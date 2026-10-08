import React from 'react';

export default function Loading({ message = "Loading...", submessage = "" }) {
  return (
    <div className="flex flex-col items-center justify-center py-16 px-4">
      <div className="relative flex items-center justify-center mb-4">
        <div className="w-10 h-10 border-4 border-slate-200 border-t-emerald-600 rounded-full animate-spin"></div>
      </div>
      <p className="text-sm font-medium text-slate-800">{message}</p>
      {submessage && (
        <p className="text-xs text-slate-500 mt-1 animate-pulse">{submessage}</p>
      )}
    </div>
  );
}
