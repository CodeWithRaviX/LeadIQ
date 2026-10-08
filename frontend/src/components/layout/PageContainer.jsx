import React from 'react';

export default function PageContainer({ children, className = '' }) {
  return (
    <div className={`p-8 max-w-7xl mx-auto w-full space-y-8 animate-fadeIn ${className}`}>
      {children}
    </div>
  );
}
