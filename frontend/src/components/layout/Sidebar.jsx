import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Building2,
  UploadCloud,
  Sliders,
  ShieldCheck,
  TrendingUp,
  Sparkles
} from 'lucide-react';

const navItems = [
  { name: 'Dashboard', path: '/', icon: LayoutDashboard },
  { name: 'Prioritized Leads', path: '/leads', icon: Building2 },
  { name: 'Import Leads', path: '/import', icon: UploadCloud },
  { name: 'Acquisition Buy Box', path: '/settings', icon: Sliders },
];

export default function Sidebar() {
  return (
    <aside className="w-64 bg-slate-900 text-slate-300 flex flex-col shrink-0 border-r border-slate-800 select-none">
      {/* Brand Header */}
      <div className="h-16 flex items-center px-6 gap-3 border-b border-slate-800/80 bg-slate-950/40">
        <div className="w-9 h-9 rounded-lg bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-emerald-400 font-bold shadow-inner">
          <TrendingUp className="w-5 h-5 text-emerald-400" />
        </div>
        <div>
          <div className="flex items-center gap-2">
            <span className="text-base font-bold text-white tracking-tight">LeadIQ</span>
            <span className="text-[10px] uppercase tracking-wider font-semibold px-1.5 py-0.5 rounded bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">M&A</span>
          </div>
          <p className="text-[11px] text-slate-400">Caprae Capital Intelligence</p>
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        <div className="px-3 pb-2 text-[11px] font-semibold tracking-wider text-slate-500 uppercase">
          Pipeline
        </div>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-all ${
                  isActive
                    ? 'bg-emerald-600 text-white shadow-sm shadow-emerald-950/30 font-semibold'
                    : 'text-slate-300 hover:text-white hover:bg-slate-800/60'
                }`
              }
            >
              <Icon className="w-4 h-4" />
              <span>{item.name}</span>
            </NavLink>
          );
        })}
      </nav>

      {/* Acquisition Thesis Card */}
      <div className="p-4 m-3 rounded-xl bg-slate-800/60 border border-slate-700/60">
        <div className="flex items-center gap-2 text-xs font-semibold text-slate-200 mb-1">
          <Sparkles className="w-3.5 h-3.5 text-amber-400" />
          <span>Active Buy Box</span>
        </div>
        <p className="text-[11px] text-slate-400 leading-relaxed">
          $3M – $15M ARR • B2B SaaS & Tech • Owner-Led • Scalable Teams
        </p>
        <div className="mt-2.5 pt-2 border-t border-slate-700/50 flex items-center justify-between text-[11px] text-slate-400">
          <span className="flex items-center gap-1.5">
            <span className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
            PostgreSQL 18
          </span>
          <span className="font-mono text-[10px] text-slate-400">Live DB</span>
        </div>
      </div>

      {/* User / Workspace info */}
      <div className="p-4 border-t border-slate-800/80 flex items-center gap-3 bg-slate-950/20">
        <div className="w-8 h-8 rounded-full bg-slate-800 border border-slate-700 flex items-center justify-center text-xs font-semibold text-slate-200">
          CC
        </div>
        <div className="flex-1 min-w-0">
          <p className="text-xs font-medium text-slate-200 truncate">Caprae Investment Fund</p>
          <p className="text-[11px] text-slate-500 truncate">Origination Workspace</p>
        </div>
      </div>
    </aside>
  );
}
