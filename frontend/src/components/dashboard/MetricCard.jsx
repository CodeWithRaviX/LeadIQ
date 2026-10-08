import React from 'react';

export default function MetricCard({
  title,
  value,
  subvalue,
  icon: Icon,
  badgeText,
  badgeColor = 'emerald',
  highlight = false
}) {
  const badgeStyles = {
    emerald: 'bg-emerald-50 text-emerald-700 border-emerald-200',
    amber: 'bg-amber-50 text-amber-700 border-amber-200',
    blue: 'bg-blue-50 text-blue-700 border-blue-200',
    slate: 'bg-slate-100 text-slate-700 border-slate-200'
  };

  return (
    <div className={`p-6 rounded-xl border transition-all ${
      highlight ? 'bg-gradient-to-br from-white to-emerald-50/40 border-emerald-300/80 shadow-sm' : 'bg-white border-slate-200 shadow-sm'
    }`}>
      <div className="flex items-center justify-between mb-3">
        <span className="text-xs font-semibold uppercase tracking-wider text-slate-500">{title}</span>
        {Icon && (
          <div className="w-8 h-8 rounded-lg bg-slate-50 border border-slate-100 flex items-center justify-center text-slate-600">
            <Icon className="w-4 h-4" />
          </div>
        )}
      </div>

      <div className="flex items-baseline gap-2">
        <span className="text-3xl font-bold tracking-tight text-slate-900">{value}</span>
        {badgeText && (
          <span className={`text-[11px] font-semibold px-2 py-0.5 rounded-full border ${badgeStyles[badgeColor]}`}>
            {badgeText}
          </span>
        )}
      </div>

      {subvalue && (
        <p className="text-xs text-slate-500 mt-2 flex items-center gap-1.5 font-medium">
          {subvalue}
        </p>
      )}
    </div>
  );
}
