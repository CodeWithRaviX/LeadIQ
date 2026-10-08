import React from 'react';
import { AlertTriangle, RefreshCw } from 'lucide-react';
import Button from './Button';

export default function ErrorMessage({
  title = "Failed to load data",
  message = "An error occurred while connecting to the intelligence server.",
  onRetry
}) {
  return (
    <div className="rounded-xl border border-rose-200 bg-rose-50/50 p-6 text-center">
      <div className="w-10 h-10 rounded-full bg-rose-100 text-rose-600 flex items-center justify-center mx-auto mb-3">
        <AlertTriangle className="w-5 h-5" />
      </div>
      <h3 className="text-sm font-semibold text-rose-900 mb-1">{title}</h3>
      <p className="text-xs text-rose-700 max-w-md mx-auto mb-4">{message}</p>
      {onRetry && (
        <Button onClick={onRetry} variant="secondary" size="sm" icon={RefreshCw}>
          Retry Connection
        </Button>
      )}
    </div>
  );
}
