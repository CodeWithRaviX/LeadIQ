import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowUpRight, Phone, Mail, Search, CheckCircle, XCircle } from 'lucide-react';
import { formatCurrency } from '../../utils/formatters';
import LeadScoreBadge from '../leads/LeadScoreBadge';
import { RECOMMENDED_ACTIONS } from '../../utils/constants';

export default function LeadOverview({ leads = [] }) {
  const navigate = useNavigate();

  const getActionIcon = (action) => {
    switch (action) {
      case 'CALL': return <Phone className="w-3.5 h-3.5 text-blue-600" />;
      case 'EMAIL': return <Mail className="w-3.5 h-3.5 text-indigo-600" />;
      case 'RESEARCH': return <Search className="w-3.5 h-3.5 text-purple-600" />;
      case 'VERIFY': return <CheckCircle className="w-3.5 h-3.5 text-amber-600" />;
      default: return <XCircle className="w-3.5 h-3.5 text-slate-400" />;
    }
  };

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden">
      <div className="p-6 border-b border-slate-100 flex items-center justify-between">
        <div>
          <h3 className="text-sm font-semibold text-slate-900">Highest Priority Opportunities</h3>
          <p className="text-xs text-slate-500">Target candidates ranked by acquisition buy-box criteria</p>
        </div>
        <button
          onClick={() => navigate('/leads?priority=HIGH')}
          className="text-xs font-semibold text-emerald-600 hover:text-emerald-700 flex items-center gap-1 cursor-pointer transition-colors"
        >
          View all high priority <ArrowUpRight className="w-3.5 h-3.5" />
        </button>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full text-left text-xs">
          <thead className="bg-slate-50/80 text-slate-500 font-medium border-b border-slate-100">
            <tr>
              <th className="py-3 px-6">Company</th>
              <th className="py-3 px-6">Industry</th>
              <th className="py-3 px-6">Revenue</th>
              <th className="py-3 px-6">Headcount</th>
              <th className="py-3 px-6">Key Decision Maker</th>
              <th className="py-3 px-6 text-center">Score</th>
              <th className="py-3 px-6">Next Best Action</th>
              <th className="py-3 px-6 text-right">Action</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {leads.length === 0 ? (
              <tr>
                <td colSpan="8" className="py-8 text-center text-slate-400">
                  No prioritized leads available. Import a CSV to generate rankings.
                </td>
              </tr>
            ) : (
              leads.slice(0, 6).map((lead) => {
                const actionConf = RECOMMENDED_ACTIONS[lead.recommendedAction] || RECOMMENDED_ACTIONS.RESEARCH;
                return (
                  <tr
                    key={lead.id}
                    onClick={() => navigate(`/leads/${lead.id}`)}
                    className="hover:bg-slate-50/70 transition-colors cursor-pointer group"
                  >
                    <td className="py-3.5 px-6 font-semibold text-slate-900 group-hover:text-emerald-600 transition-colors">
                      {lead.company?.companyName || 'N/A'}
                      <div className="text-[11px] font-normal text-slate-400 font-mono">
                        {lead.company?.domain || lead.company?.website || ''}
                      </div>
                    </td>
                    <td className="py-3.5 px-6 text-slate-600">{lead.company?.industry || 'N/A'}</td>
                    <td className="py-3.5 px-6 font-medium text-slate-900">
                      {formatCurrency(lead.company?.estimatedRevenue)}
                    </td>
                    <td className="py-3.5 px-6 text-slate-600">
                      {lead.company?.employeeCount ? `${lead.company.employeeCount} team` : 'N/A'}
                    </td>
                    <td className="py-3.5 px-6">
                      {lead.contact ? (
                        <div>
                          <div className="font-medium text-slate-900">{lead.contact.firstName} {lead.contact.lastName}</div>
                          <div className="text-[11px] text-slate-400 truncate max-w-[140px]">{lead.contact.jobTitle || 'Executive'}</div>
                        </div>
                      ) : (
                        <span className="text-slate-400 italic">Unassigned</span>
                      )}
                    </td>
                    <td className="py-3.5 px-6 text-center">
                      <LeadScoreBadge score={lead.score} priority={lead.priority} />
                    </td>
                    <td className="py-3.5 px-6">
                      <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-[11px] font-semibold border ${actionConf.bg} ${actionConf.text} ${actionConf.border}`}>
                        {getActionIcon(lead.recommendedAction)}
                        {actionConf.label}
                      </span>
                    </td>
                    <td className="py-3.5 px-6 text-right">
                      <span className="text-slate-400 group-hover:text-slate-900 transition-colors font-medium text-xs">
                        Inspect →
                      </span>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
