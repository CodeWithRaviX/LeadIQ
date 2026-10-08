import React from 'react';
import { useNavigate } from 'react-router-dom';
import { CheckCircle2, ArrowRight, Layers, CopyX, AlertCircle, TrendingUp, RotateCcw } from 'lucide-react';
import Button from '../common/Button';

export default function ImportSummary({ summary, onReset }) {
  const navigate = useNavigate();
  const processingSeconds = summary.processingTimeMs ? (summary.processingTimeMs / 1000).toFixed(1) : '1.5';
  const finalOpportunities = summary.uniqueOpportunities ?? (summary.newOpportunities + summary.updatedRecords);

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-8 space-y-8 animate-fadeIn">
      {/* Banner */}
      <div className="flex items-center gap-4 p-4 rounded-xl bg-emerald-50 border border-emerald-200">
        <div className="w-12 h-12 rounded-xl bg-emerald-100 flex items-center justify-center text-emerald-700 shrink-0">
          <CheckCircle2 className="w-7 h-7" />
        </div>
        <div>
          <h2 className="text-base font-bold text-emerald-950">INTELLIGENCE INGESTION COMPLETE</h2>
          <p className="text-xs text-emerald-800 mt-0.5">
            <span className="font-semibold">{summary.totalRows} records</span> processed in <span className="font-semibold">{processingSeconds}s</span>. Normalized, validated, deduplicated, and scored. All records persisted to PostgreSQL.
          </p>
        </div>
      </div>

      {/* Metrics Breakdown Grid */}
      <div className="grid grid-cols-2 md:grid-cols-5 gap-3">
        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200/80">
          <span className="text-[11px] text-slate-500 font-semibold uppercase tracking-wider block">Input Rows</span>
          <span className="text-2xl font-black text-slate-900 mt-1 block">{summary.totalRows}</span>
          <span className="text-[10px] text-slate-400 mt-0.5 block">Raw CSV records</span>
        </div>

        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200/80">
          <span className="text-[11px] text-slate-500 font-semibold uppercase tracking-wider block">Valid Rows</span>
          <span className="text-2xl font-black text-emerald-600 mt-1 block">{summary.validRows ?? summary.totalRows}</span>
          <span className="text-[10px] text-emerald-700 mt-0.5 block">Format validated</span>
        </div>

        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200/80">
          <span className="text-[11px] text-slate-500 font-semibold uppercase tracking-wider block">New Opportunities</span>
          <span className="text-2xl font-black text-blue-600 mt-1 block">{summary.newOpportunities ?? 0}</span>
          <span className="text-[10px] text-blue-700 mt-0.5 block">New DB entities created</span>
        </div>

        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200/80">
          <span className="text-[11px] text-slate-500 font-semibold uppercase tracking-wider block">Existing Matched</span>
          <span className="text-2xl font-black text-indigo-600 mt-1 block">{summary.updatedRecords ?? 0}</span>
          <span className="text-[10px] text-indigo-700 mt-0.5 block">Pre-existing DB matches</span>
        </div>

        <div className="p-3.5 rounded-xl bg-slate-50 border border-slate-200/80">
          <span className="text-[11px] text-slate-500 font-semibold uppercase tracking-wider block">Duplicates</span>
          <span className="text-2xl font-black text-amber-600 mt-1 block">{summary.duplicatesRemoved}</span>
          <span className="text-[10px] text-amber-700 mt-0.5 block">Intra-file removed</span>
        </div>
      </div>

      {/* Priority Distribution */}
      <div className="p-6 rounded-xl bg-slate-50/70 border border-slate-200 space-y-4">
        <h3 className="text-xs font-bold uppercase tracking-wider text-slate-500 flex items-center gap-2">
          <TrendingUp className="w-4 h-4 text-emerald-600" />
          Acquisition Prioritization Breakdown ({finalOpportunities} Unique Opportunities)
        </h3>

        <div className="grid grid-cols-3 gap-4 text-center">
          <div className="p-4 rounded-xl bg-white border border-emerald-200 shadow-2xs">
            <span className="text-3xl font-extrabold text-emerald-600 block">{summary.highPriority}</span>
            <span className="text-xs font-bold text-slate-800 mt-1 block">High Priority (80+)</span>
            <span className="text-[11px] text-slate-500 mt-0.5 block">Direct Outreach Ready</span>
          </div>

          <div className="p-4 rounded-xl bg-white border border-amber-200 shadow-2xs">
            <span className="text-3xl font-extrabold text-amber-600 block">{summary.mediumPriority}</span>
            <span className="text-xs font-bold text-slate-800 mt-1 block">Medium Priority (60-79)</span>
            <span className="text-[11px] text-slate-500 mt-0.5 block">Research & Enrich</span>
          </div>

          <div className="p-4 rounded-xl bg-white border border-slate-200 shadow-2xs">
            <span className="text-3xl font-extrabold text-slate-500 block">{summary.lowPriority}</span>
            <span className="text-xs font-bold text-slate-800 mt-1 block">Low Priority (&lt;60)</span>
            <span className="text-[11px] text-slate-500 mt-0.5 block">Pass / Passive Monitor</span>
          </div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-4 pt-2">
        <Button variant="secondary" onClick={onReset} icon={RotateCcw} size="md">
          Import Another Dataset
        </Button>

        <Button
          variant="success"
          onClick={() => navigate('/leads?priority=HIGH')}
          size="lg"
          className="w-full sm:w-auto"
        >
          View Prioritized Opportunities ({finalOpportunities}) <ArrowRight className="w-4 h-4 ml-2" />
        </Button>
      </div>
    </div>
  );
}
