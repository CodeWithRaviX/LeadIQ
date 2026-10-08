import React from 'react';
import { Database, Plus } from 'lucide-react';
import Button from './Button';

export default function EmptyState({
  title = "No acquisition opportunities match your filters",
  description = "Try adjusting your search criteria or clearing active pipeline filters.",
  actionText = "Import Leads",
  onAction,
  icon: Icon = Database
}) {
  return (
    <div className="flex flex-col items-center justify-center p-10 text-center bg-white rounded-xl border border-dashed border-slate-300 animate-fadeIn">
      <div className="w-12 h-12 rounded-xl bg-slate-100 flex items-center justify-center text-slate-500 mb-3">
        <Icon className="w-6 h-6" />
      </div>
      <h3 className="text-base font-semibold text-slate-900 mb-1">{title}</h3>
      <p className="text-xs text-slate-500 max-w-sm mb-4 leading-relaxed">{description}</p>

      <div className="bg-slate-50 p-4 rounded-lg border border-slate-200/80 text-left text-xs text-slate-600 mb-6 space-y-1.5 w-full max-w-xs">
        <span className="font-semibold text-slate-700 block mb-1">Recommended steps:</span>
        <div className="flex items-center gap-2">
          <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
          <span>Lowering minimum score threshold</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
          <span>Removing industry vertical filter</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="w-1.5 h-1.5 rounded-full bg-emerald-500"></span>
          <span>Clearing status filter</span>
        </div>
      </div>

      {onAction && actionText && (
        <Button onClick={onAction} variant="primary" icon={Plus}>
          {actionText}
        </Button>
      )}
    </div>
  );
}
