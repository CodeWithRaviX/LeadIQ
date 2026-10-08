import React from 'react';
import { CheckCircle2, ShieldCheck, HelpCircle } from 'lucide-react';
import LeadScoreBadge from './LeadScoreBadge';

export default function LeadReasoning({ lead }) {
  const scoreDimensions = [
    { label: 'Revenue Fit', score: lead.revenueScore || 0, max: 20 },
    { label: 'Industry Fit', score: lead.industryScore || 0, max: 20 },
    { label: 'Decision Maker', score: lead.decisionMakerScore || 0, max: 15 },
    { label: 'Geography', score: lead.locationScore || 0, max: 10 },
    { label: 'Headcount', score: lead.employeeScore || 0, max: 10 },
    { label: 'Contact Availability', score: lead.contactScore || 0, max: 10 },
    { label: 'Data Quality', score: lead.dataQualityScore || 0, max: 10 },
    { label: 'Website Domain', score: lead.websiteScore || 0, max: 5 },
  ];

  const reasons = lead.scoreReasons || [];

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-slate-100 pb-4">
        <div>
          <h3 className="text-sm font-semibold text-slate-900 flex items-center gap-2">
            <ShieldCheck className="w-4 h-4 text-emerald-600" />
            Explainable Prioritization Formula
          </h3>
          <p className="text-xs text-slate-500">Transparent, deterministic scoring against fund acquisition criteria</p>
        </div>
        <LeadScoreBadge score={lead.score} priority={lead.priority} size="lg" />
      </div>

      {/* Breakdown Progress Bars */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {scoreDimensions.map((dim) => {
          const pct = Math.round((dim.score / dim.max) * 100);
          return (
            <div key={dim.label} className="p-3 bg-slate-50/70 rounded-lg border border-slate-100">
              <div className="flex justify-between items-center text-xs mb-1.5">
                <span className="font-medium text-slate-700">{dim.label}</span>
                <span className="font-semibold text-slate-900">{dim.score}/{dim.max} pts</span>
              </div>
              <div className="w-full bg-slate-200 h-1.5 rounded-full overflow-hidden">
                <div
                  className={`h-full rounded-full transition-all duration-500 ${
                    pct >= 80 ? 'bg-emerald-600' : pct >= 50 ? 'bg-amber-500' : 'bg-slate-400'
                  }`}
                  style={{ width: `${pct}%` }}
                />
              </div>
            </div>
          );
        })}
      </div>

      {/* Rationale Bullet List */}
      <div className="pt-2">
        <h4 className="text-xs font-semibold uppercase tracking-wider text-slate-500 mb-3">
          Detailed Scoring Audit Rationale
        </h4>
        <div className="space-y-2">
          {reasons.length > 0 ? (
            reasons.map((reason, idx) => (
              <div key={idx} className="flex items-start gap-2.5 text-xs text-slate-700 bg-emerald-50/40 p-2.5 rounded-lg border border-emerald-100/60">
                <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0 mt-0.5" />
                <span className="leading-relaxed">{reason}</span>
              </div>
            ))
          ) : (
            <p className="text-xs text-slate-400 italic">No specific rationale records available.</p>
          )}
        </div>
      </div>
    </div>
  );
}
