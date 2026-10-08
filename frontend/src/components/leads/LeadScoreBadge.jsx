import React from 'react';

export default function LeadScoreBadge({ score = 0, priority = 'LOW', size = 'md' }) {
  const getColors = () => {
    if (score >= 80 || priority === 'HIGH') {
      return {
        bg: 'bg-emerald-50',
        text: 'text-emerald-700',
        border: 'border-emerald-200',
        dot: 'bg-emerald-500'
      };
    }
    if (score >= 60 || priority === 'MEDIUM') {
      return {
        bg: 'bg-amber-50',
        text: 'text-amber-700',
        border: 'border-amber-200',
        dot: 'bg-amber-500'
      };
    }
    return {
      bg: 'bg-slate-100',
      text: 'text-slate-600',
      border: 'border-slate-200',
      dot: 'bg-slate-400'
    };
  };

  const style = getColors();

  const sizes = {
    sm: 'text-xs px-2 py-0.5 font-semibold',
    md: 'text-xs px-2.5 py-1 font-bold',
    lg: 'text-base px-3.5 py-1.5 font-bold'
  };

  return (
    <span className={`inline-flex items-center gap-1.5 rounded-full border shadow-2xs ${sizes[size]} ${style.bg} ${style.text} ${style.border}`}>
      <span className={`w-1.5 h-1.5 rounded-full ${style.dot}`} />
      <span>{score}</span>
      <span className="text-[10px] font-medium opacity-70">/ 100</span>
    </span>
  );
}
