export const LEAD_PRIORITIES = {
  HIGH: { label: 'High Priority', color: 'emerald', bg: 'bg-emerald-50', text: 'text-emerald-700', border: 'border-emerald-200' },
  MEDIUM: { label: 'Medium Priority', color: 'amber', bg: 'bg-amber-50', text: 'text-amber-700', border: 'border-amber-200' },
  LOW: { label: 'Low Priority', color: 'slate', bg: 'bg-slate-100', text: 'text-slate-600', border: 'border-slate-200' }
};

export const RECOMMENDED_ACTIONS = {
  CALL: { label: 'Direct Call', bg: 'bg-blue-50', text: 'text-blue-700', border: 'border-blue-200', icon: 'Phone' },
  EMAIL: { label: 'Executive Email', bg: 'bg-indigo-50', text: 'text-indigo-700', border: 'border-indigo-200', icon: 'Mail' },
  RESEARCH: { label: 'Deep Research', bg: 'bg-purple-50', text: 'text-purple-700', border: 'border-purple-200', icon: 'Search' },
  VERIFY: { label: 'Verify Data', bg: 'bg-amber-50', text: 'text-amber-700', border: 'border-amber-200', icon: 'CheckCircle' },
  SKIP: { label: 'Pass / Monitor', bg: 'bg-slate-100', text: 'text-slate-500', border: 'border-slate-200', icon: 'XCircle' }
};

export const LEAD_STATUSES = [
  { value: 'NEW', label: 'New Lead' },
  { value: 'REVIEWED', label: 'Reviewed' },
  { value: 'CONTACTED', label: 'Contacted' },
  { value: 'QUALIFIED', label: 'Qualified' },
  { value: 'DISQUALIFIED', label: 'Disqualified' }
];

export const TARGET_INDUSTRIES = [
  'B2B SaaS',
  'HealthTech',
  'FinTech',
  'CyberSecurity',
  'Logistics Tech',
  'GovTech',
  'Compliance & Legal Tech',
  'Enterprise Software',
  'Cloud Software',
  'Managed Services',
  'Digital Agency'
];
