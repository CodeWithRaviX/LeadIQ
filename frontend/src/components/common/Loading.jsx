import React, { useState, useEffect } from 'react';

export default function Loading({ message = "Loading...", submessage = "" }) {
  const [showColdStartNotice, setShowColdStartNotice] = useState(false);

  useEffect(() => {
    const timer = setTimeout(() => {
      setShowColdStartNotice(true);
    }, 3500);
    return () => clearTimeout(timer);
  }, []);

  return (
    <div className="flex flex-col items-center justify-center py-16 px-4">
      <div className="relative flex items-center justify-center mb-4">
        <div className="w-10 h-10 border-4 border-slate-200 border-t-emerald-600 rounded-full animate-spin"></div>
      </div>
      <p className="text-sm font-medium text-slate-800">{message}</p>
      {submessage && (
        <p className="text-xs text-slate-500 mt-1 animate-pulse">{submessage}</p>
      )}
      {showColdStartNotice && (
        <div className="mt-4 px-3.5 py-2 rounded-xl bg-amber-50 border border-amber-200/80 text-amber-900 text-xs flex items-center gap-2 max-w-md text-center animate-fadeIn shadow-2xs">
          <span>⚡ <strong>Render Cold Start</strong>: Waking up free-tier server (~30s). Thank you for your patience!</span>
        </div>
      )}
    </div>
  );
}
