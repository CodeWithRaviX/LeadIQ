import React from 'react';
import { useNavigate } from 'react-router-dom';
import { Phone, Mail, Search, CheckCircle, XCircle } from 'lucide-react';
import { formatCurrency } from '../../utils/formatters';
import LeadScoreBadge from './LeadScoreBadge';
import { RECOMMENDED_ACTIONS } from '../../utils/constants';

export default function LeadRow({ lead, onStatusChange }) {
  const navigate = useNavigate();
  const comp = lead.company || {};
  const cont = lead.contact || {};
  const actionConf = RECOMMENDED_ACTIONS[lead.recommendedAction] || RECOMMENDED_ACTIONS.RESEARCH;

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
    <tr
      onClick={() => navigate(`/leads/${lead.id}`)}
      className="hover:bg-slate-50/70 transition-colors cursor-pointer text-xs group"
    >
      {/* Company */}
      <td className="py-3.5 px-5 font-semibold text-slate-900 group-hover:text-emerald-600 transition-colors">
        <div className="flex items-center gap-2">
          <span>{comp.companyName || 'Unknown Entity'}</span>
        </div>
        <div className="text-[11px] font-mono text-slate-400 font-normal">
          {comp.domain || comp.website || 'No domain'}
        </div>
      </td>

      {/* Industry */}
      <td className="py-3.5 px-5 text-slate-600">
        <span className="inline-block max-w-[140px] truncate" title={comp.industry}>
          {comp.industry || '—'}
        </span>
      </td>

      {/* Revenue */}
      <td className="py-3.5 px-5 font-semibold text-slate-900">
        {formatCurrency(comp.estimatedRevenue)}
      </td>

      {/* Headcount */}
      <td className="py-3.5 px-5 text-slate-600">
        {comp.employeeCount ? `${comp.employeeCount}` : '—'}
      </td>

      {/* Location */}
      <td className="py-3.5 px-5 text-slate-600 max-w-[120px] truncate" title={comp.location}>
        {comp.location || '—'}
      </td>

      {/* Contact */}
      <td className="py-3.5 px-5">
        {cont.firstName ? (
          <div>
            <div className="font-medium text-slate-900">{cont.firstName} {cont.lastName}</div>
            <div className="text-[11px] text-slate-400 truncate max-w-[130px]">{cont.jobTitle || 'Executive'}</div>
          </div>
        ) : (
          <span className="text-slate-400 italic">None on file</span>
        )}
      </td>

      {/* Score */}
      <td className="py-3.5 px-5 text-center">
        <LeadScoreBadge score={lead.score} priority={lead.priority} size="sm" />
      </td>

      {/* Next Action */}
      <td className="py-3.5 px-5">
        <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-md text-[11px] font-semibold border ${actionConf.bg} ${actionConf.text} ${actionConf.border}`}>
          {getActionIcon(lead.recommendedAction)}
          {actionConf.label}
        </span>
      </td>

      {/* Status */}
      <td className="py-3.5 px-5 text-right" onClick={(e) => e.stopPropagation()}>
        <select
          value={lead.status || 'NEW'}
          onChange={(e) => onStatusChange(lead.id, e.target.value)}
          className="text-[11px] font-medium bg-white border border-slate-200 rounded px-2 py-1 text-slate-700 focus:outline-none focus:ring-1 focus:ring-slate-900"
        >
          <option value="NEW">New</option>
          <option value="REVIEWED">Reviewed</option>
          <option value="CONTACTED">Contacted</option>
          <option value="QUALIFIED">Qualified</option>
          <option value="DISQUALIFIED">Disqualified</option>
        </select>
      </td>
    </tr>
  );
}
