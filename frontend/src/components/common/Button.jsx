import React from 'react';

export default function Button({
  children,
  variant = 'primary',
  size = 'md',
  onClick,
  disabled = false,
  loading = false,
  icon: Icon,
  className = '',
  type = 'button'
}) {
  const base = "inline-flex items-center justify-center font-medium transition-all rounded-lg focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed cursor-pointer";

  const sizes = {
    sm: "px-3 py-1.5 text-xs gap-1.5",
    md: "px-4 py-2 text-sm gap-2",
    lg: "px-5 py-2.5 text-base gap-2.5"
  };

  const variants = {
    primary: "bg-slate-900 text-white hover:bg-slate-800 focus:ring-slate-900 shadow-sm",
    secondary: "bg-white text-slate-700 hover:bg-slate-50 border border-slate-300 focus:ring-slate-400 shadow-sm",
    success: "bg-emerald-600 text-white hover:bg-emerald-700 focus:ring-emerald-500 shadow-sm",
    danger: "bg-rose-600 text-white hover:bg-rose-700 focus:ring-rose-500 shadow-sm",
    ghost: "bg-transparent text-slate-600 hover:bg-slate-100 focus:ring-slate-300"
  };

  return (
    <button
      type={type}
      disabled={disabled || loading}
      onClick={onClick}
      className={`${base} ${sizes[size]} ${variants[variant]} ${className}`}
    >
      {loading ? (
        <svg className="animate-spin -ml-0.5 mr-2 h-4 w-4 text-current" fill="none" viewBox="0 0 24 24">
          <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
          <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
        </svg>
      ) : Icon && (
        <Icon className={size === 'sm' ? "w-3.5 h-3.5" : "w-4 h-4"} />
      )}
      {children}
    </button>
  );
}
