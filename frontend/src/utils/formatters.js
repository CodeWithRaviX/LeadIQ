export function formatCurrency(amount) {
  if (amount === null || amount === undefined || isNaN(amount)) {
    return 'N/A';
  }
  const val = Number(amount);
  if (val >= 1_000_000_000) {
    return `$${(val / 1_000_000_000).toFixed(1)}B`;
  }
  if (val >= 1_000_000) {
    return `$${(val / 1_000_000).toFixed(1)}M`;
  }
  if (val >= 1_000) {
    return `$${(val / 1_000).toFixed(0)}K`;
  }
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD', maximumFractionDigits: 0 }).format(val);
}

export function formatNumber(val) {
  if (val === null || val === undefined || isNaN(val)) {
    return '0';
  }
  return new Intl.NumberFormat('en-US').format(Number(val));
}

export function formatDate(isoString) {
  if (!isoString) return 'N/A';
  try {
    const d = new Date(isoString);
    return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
  } catch {
    return isoString;
  }
}
