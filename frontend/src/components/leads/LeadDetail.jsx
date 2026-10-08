import React from 'react';
import { Building2, Globe, MapPin, Users, DollarSign, Mail, Phone, Linkedin, ExternalLink } from 'lucide-react';
import { formatCurrency } from '../../utils/formatters';

export default function LeadDetail({ lead }) {
  const comp = lead.company || {};
  const cont = lead.contact || {};

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
      {/* Company Profile Card */}
      <div className="p-6 bg-white rounded-xl border border-slate-200 shadow-sm space-y-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-lg bg-emerald-50 border border-emerald-200 flex items-center justify-center text-emerald-600">
            <Building2 className="w-5 h-5" />
          </div>
          <div>
            <h2 className="text-lg font-bold text-slate-900">{comp.companyName || 'Unknown Entity'}</h2>
            {comp.domain && (
              <a
                href={comp.website?.startsWith('http') ? comp.website : `https://${comp.domain}`}
                target="_blank"
                rel="noreferrer"
                className="text-xs text-emerald-600 hover:underline flex items-center gap-1 font-mono"
              >
                {comp.domain} <ExternalLink className="w-3 h-3" />
              </a>
            )}
          </div>
        </div>

        {comp.description && (
          <p className="text-xs text-slate-600 leading-relaxed bg-slate-50 p-3 rounded-lg border border-slate-100">
            {comp.description}
          </p>
        )}

        <div className="grid grid-cols-2 gap-4 pt-2 border-t border-slate-100 text-xs">
          <div>
            <span className="text-slate-400 block font-medium">Industry Vertical</span>
            <span className="font-semibold text-slate-800">{comp.industry || '—'}</span>
          </div>
          <div>
            <span className="text-slate-400 block font-medium">Estimated ARR</span>
            <span className="font-bold text-emerald-700 text-sm">{formatCurrency(comp.estimatedRevenue)}</span>
          </div>
          <div>
            <span className="text-slate-400 block font-medium">Headcount</span>
            <span className="font-semibold text-slate-800">{comp.employeeCount ? `${comp.employeeCount} full-time` : '—'}</span>
          </div>
          <div>
            <span className="text-slate-400 block font-medium">Headquarters</span>
            <span className="font-semibold text-slate-800 flex items-center gap-1">
              <MapPin className="w-3 h-3 text-slate-400" />
              {comp.location || '—'}
            </span>
          </div>
        </div>
      </div>

      {/* Decision Maker Card */}
      <div className="p-6 bg-white rounded-xl border border-slate-200 shadow-sm space-y-4">
        <div className="flex items-center justify-between">
          <h3 className="text-sm font-semibold text-slate-900 flex items-center gap-2">
            <Users className="w-4 h-4 text-slate-500" />
            Decision Maker Profile
          </h3>
          {cont.firstName && (
            <span className="text-[11px] font-semibold px-2 py-0.5 rounded bg-emerald-50 text-emerald-700 border border-emerald-200">
              Verified Executive
            </span>
          )}
        </div>

        {cont.firstName ? (
          <div className="space-y-3">
            <div>
              <div className="text-base font-bold text-slate-900">
                {cont.firstName} {cont.lastName}
              </div>
              <div className="text-xs text-slate-500 font-medium">
                {cont.jobTitle || 'Executive'}
              </div>
            </div>

            <div className="space-y-2 pt-2 border-t border-slate-100 text-xs">
              {cont.email && (
                <div className="flex items-center justify-between p-2 rounded-lg bg-slate-50 border border-slate-100">
                  <span className="text-slate-500 flex items-center gap-2">
                    <Mail className="w-3.5 h-3.5 text-slate-400" />
                    {cont.email}
                  </span>
                  <a
                    href={`mailto:${cont.email}`}
                    className="text-[11px] font-semibold text-emerald-600 hover:underline"
                  >
                    Send Email →
                  </a>
                </div>
              )}

              {cont.phone && (
                <div className="flex items-center justify-between p-2 rounded-lg bg-slate-50 border border-slate-100">
                  <span className="text-slate-500 flex items-center gap-2">
                    <Phone className="w-3.5 h-3.5 text-slate-400" />
                    {cont.phone}
                  </span>
                  <a
                    href={`tel:${cont.phone}`}
                    className="text-[11px] font-semibold text-blue-600 hover:underline"
                  >
                    Direct Dial →
                  </a>
                </div>
              )}

              {cont.linkedinUrl && (
                <div className="flex items-center justify-between p-2 rounded-lg bg-slate-50 border border-slate-100">
                  <span className="text-slate-500 flex items-center gap-2">
                    <Linkedin className="w-3.5 h-3.5 text-blue-500" />
                    LinkedIn Dossier
                  </span>
                  <a
                    href={cont.linkedinUrl}
                    target="_blank"
                    rel="noreferrer"
                    className="text-[11px] font-semibold text-blue-600 hover:underline flex items-center gap-1"
                  >
                    Open Profile <ExternalLink className="w-3 h-3" />
                  </a>
                </div>
              )}
            </div>
          </div>
        ) : (
          <div className="py-8 text-center bg-slate-50 rounded-lg border border-dashed border-slate-200">
            <p className="text-xs text-slate-500">No primary decision maker attached to this record.</p>
            <p className="text-[11px] text-slate-400 mt-1">Recommended action: Deeper firmographic research</p>
          </div>
        )}
      </div>
    </div>
  );
}
