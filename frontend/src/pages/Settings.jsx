import React, { useState } from 'react';
import { Sliders, ShieldCheck, Database, Sparkles, CheckCircle2, RotateCcw } from 'lucide-react';
import PageContainer from '../components/layout/PageContainer';
import Button from '../components/common/Button';
import { leadService } from '../services/leadService';

export default function Settings() {
  const [scoringAll, setScoringAll] = useState(false);
  const [rescoreMsg, setRescoreMsg] = useState('');

  const criteria = [
    { title: 'Revenue Sweet Spot', weight: '20 pts', target: '$3.0M – $15.0M Estimated Revenue', desc: 'Lower middle-market revenue scale suitable for acquisition.' },
    { title: 'Target Verticals', weight: '20 pts', target: 'B2B SaaS, HealthTech, FinTech, CyberSecurity, GovTech', desc: 'High-margin, mission-critical workflow software.' },
    { title: 'Owner / Decision Maker', weight: '15 pts', target: 'Founder, CEO, President, Owner', desc: 'Identified equity owner for direct proprietary acquisition outreach.' },
    { title: 'Operational Headcount', weight: '10 pts', target: '20 – 100 Employees', desc: 'Efficient organizational scale with key management layer.' },
    { title: 'Contact Direct Channels', weight: '10 pts', target: 'Executive Email + Phone Available', desc: 'Enables rapid senior partner outreach without friction.' },
    { title: 'Data Completeness Index', weight: '10 pts', target: '≥ 80% Field Completeness', desc: 'Penalizes sparse or unverified data imports.' },
    { title: 'Geographic Focus', weight: '10 pts', target: 'US & North American Hubs', desc: 'Core legal jurisdiction and favorable business climates.' },
    { title: 'Domain & Web Presence', weight: '5 pts', target: 'Verified Active Domain', desc: 'Active digital presence confirming operational viability.' },
  ];

  const handleScoreAll = async () => {
    setScoringAll(true);
    setRescoreMsg('');
    try {
      const res = await leadService.scoreAllLeads();
      setRescoreMsg(`Successfully re-evaluated and scored ${res.reScoredCount || 'all'} leads against the buy-box criteria.`);
    } catch (err) {
      alert('Re-scoring failed: ' + err.message);
    } finally {
      setScoringAll(false);
    }
  };

  return (
    <PageContainer>
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold tracking-tight text-slate-900">
            Acquisition Thesis & Buy-Box Criteria
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Deterministic rule weights and parameters governing the 100-point LeadIQ opportunity scoring engine.
          </p>
        </div>

        <Button
          variant="secondary"
          size="sm"
          icon={RotateCcw}
          loading={scoringAll}
          onClick={handleScoreAll}
        >
          {scoringAll ? 'Re-scoring Pipeline...' : 'Re-score All Leads in DB'}
        </Button>
      </div>

      {rescoreMsg && (
        <div className="p-3 text-xs bg-emerald-50 text-emerald-800 rounded-lg border border-emerald-200 flex items-center gap-2">
          <CheckCircle2 className="w-4 h-4 text-emerald-600 shrink-0" />
          <span>{rescoreMsg}</span>
        </div>
      )}

      {/* Criteria Breakdown Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        {criteria.map((c) => (
          <div key={c.title} className="p-5 bg-white rounded-xl border border-slate-200 shadow-sm space-y-2">
            <div className="flex items-center justify-between">
              <h3 className="text-sm font-bold text-slate-900">{c.title}</h3>
              <span className="text-xs font-mono font-bold px-2 py-0.5 rounded bg-emerald-50 text-emerald-700 border border-emerald-200">
                {c.weight}
              </span>
            </div>
            <div className="text-xs font-semibold text-slate-700 bg-slate-50 p-2 rounded border border-slate-100">
              Target: <span className="text-slate-900">{c.target}</span>
            </div>
            <p className="text-xs text-slate-500 leading-relaxed">
              {c.desc}
            </p>
          </div>
        ))}
      </div>

      {/* Architecture & Infrastructure Status */}
      <div className="p-6 bg-slate-900 text-white rounded-xl border border-slate-800 space-y-4">
        <h3 className="text-xs font-bold uppercase tracking-wider text-emerald-400 flex items-center gap-2">
          <Database className="w-4 h-4 text-emerald-400" />
          Environment & Persistence Status
        </h3>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-xs">
          <div className="p-3 rounded-lg bg-slate-800/80 border border-slate-700/60">
            <span className="text-slate-400 block">Database Server</span>
            <span className="font-semibold text-white mt-1 block">PostgreSQL 18 (Relational)</span>
            <span className="text-[11px] text-emerald-400 mt-1 block">● Connected (lead_intelligence)</span>
          </div>

          <div className="p-3 rounded-lg bg-slate-800/80 border border-slate-700/60">
            <span className="text-slate-400 block">Scoring Engine</span>
            <span className="font-semibold text-white mt-1 block">Deterministic & Explainable</span>
            <span className="text-[11px] text-emerald-400 mt-1 block">● 8 Modular Rules (Max 100)</span>
          </div>

          <div className="p-3 rounded-lg bg-slate-800/80 border border-slate-700/60">
            <span className="text-slate-400 block">AI Intelligence Layer</span>
            <span className="font-semibold text-white mt-1 block">Google Gemini + Offline Fallback</span>
            <span className="text-[11px] text-emerald-400 mt-1 block">● Core prioritization remains available when AI is offline</span>
          </div>
        </div>
      </div>
    </PageContainer>
  );
}
