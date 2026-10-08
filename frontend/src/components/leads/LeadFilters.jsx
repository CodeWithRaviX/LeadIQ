import React from 'react';
import { Search, Filter, RotateCcw, CheckSquare, Square } from 'lucide-react';
import Button from '../common/Button';
import { TARGET_INDUSTRIES, LEAD_STATUSES } from '../../utils/constants';

export default function LeadFilters({ filters, onFilterChange, onReset }) {
  const handleInputChange = (field, value) => {
    onFilterChange({ [field]: value });
  };

  return (
    <div className="bg-white p-5 rounded-xl border border-slate-200 shadow-sm space-y-4">
      {/* Top Search & Primary Filters */}
      <div className="grid grid-cols-1 md:grid-cols-12 gap-3">
        {/* Search */}
        <div className="md:col-span-4 relative">
          <Search className="w-4 h-4 absolute left-3.5 top-1/2 -translate-y-1/2 text-slate-400" />
          <input
            type="text"
            placeholder="Search company, domain, contact name, or email..."
            value={filters.search || ''}
            onChange={(e) => handleInputChange('search', e.target.value)}
            className="w-full pl-9 pr-4 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-800 placeholder-slate-400 focus:outline-none focus:ring-2 focus:ring-slate-900 focus:bg-white transition-all"
          />
        </div>

        {/* Priority Filter */}
        <div className="md:col-span-2">
          <select
            value={filters.priority || ''}
            onChange={(e) => handleInputChange('priority', e.target.value)}
            className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-slate-900 focus:bg-white"
          >
            <option value="">All Priorities</option>
            <option value="HIGH">High Priority (80+)</option>
            <option value="MEDIUM">Medium Priority (60-79)</option>
            <option value="LOW">Low Priority (&lt;60)</option>
          </select>
        </div>

        {/* Industry Filter */}
        <div className="md:col-span-3">
          <select
            value={filters.industry || ''}
            onChange={(e) => handleInputChange('industry', e.target.value)}
            className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-slate-900 focus:bg-white"
          >
            <option value="">All Industries</option>
            {TARGET_INDUSTRIES.map((ind) => (
              <option key={ind} value={ind}>{ind}</option>
            ))}
          </select>
        </div>

        {/* Status Filter */}
        <div className="md:col-span-2">
          <select
            value={filters.status || ''}
            onChange={(e) => handleInputChange('status', e.target.value)}
            className="w-full px-3 py-2 bg-slate-50 border border-slate-200 rounded-lg text-sm text-slate-700 focus:outline-none focus:ring-2 focus:ring-slate-900 focus:bg-white"
          >
            <option value="">All Pipeline Statuses</option>
            {LEAD_STATUSES.map((st) => (
              <option key={st.value} value={st.value}>{st.label}</option>
            ))}
          </select>
        </div>

        {/* Reset Button */}
        <div className="md:col-span-1 flex items-center justify-end">
          <button
            onClick={onReset}
            title="Reset filters"
            className="p-2 text-slate-400 hover:text-slate-700 hover:bg-slate-100 rounded-lg transition-colors cursor-pointer border border-slate-200"
          >
            <RotateCcw className="w-4 h-4" />
          </button>
        </div>
      </div>

      {/* Secondary Criteria Bar */}
      <div className="pt-3 border-t border-slate-100 flex flex-wrap items-center justify-between gap-4 text-xs">
        <div className="flex flex-wrap items-center gap-4">
          {/* Min Score filter */}
          <div className="flex items-center gap-1.5">
            <span className="text-slate-500 font-medium">Min Score:</span>
            <input
              type="number"
              min="0"
              max="100"
              placeholder="e.g. 75"
              value={filters.minScore || ''}
              onChange={(e) => handleInputChange('minScore', e.target.value)}
              className="w-20 px-2 py-1 bg-slate-50 border border-slate-200 rounded text-xs text-slate-800 focus:outline-none focus:ring-1 focus:ring-slate-900"
            />
          </div>

          {/* Min Revenue filter */}
          <div className="flex items-center gap-1.5">
            <span className="text-slate-500 font-medium">Min Rev ($):</span>
            <input
              type="number"
              placeholder="e.g. 3000000"
              value={filters.minRevenue || ''}
              onChange={(e) => handleInputChange('minRevenue', e.target.value)}
              className="w-28 px-2 py-1 bg-slate-50 border border-slate-200 rounded text-xs text-slate-800 focus:outline-none focus:ring-1 focus:ring-slate-900"
            />
          </div>

          {/* Contact Available Toggle */}
          <button
            type="button"
            onClick={() => handleInputChange('hasContact', filters.hasContact ? '' : 'true')}
            className="flex items-center gap-1.5 px-2.5 py-1 rounded bg-slate-50 border border-slate-200 hover:bg-slate-100 text-slate-700 cursor-pointer transition-colors"
          >
            {filters.hasContact === 'true' ? (
              <CheckSquare className="w-3.5 h-3.5 text-emerald-600" />
            ) : (
              <Square className="w-3.5 h-3.5 text-slate-400" />
            )}
            <span>Decision Maker On File</span>
          </button>
        </div>

        {/* Sort Options */}
        <div className="flex items-center gap-2">
          <span className="text-slate-500 font-medium">Sort By:</span>
          <select
            value={filters.sortBy || 'score'}
            onChange={(e) => handleInputChange('sortBy', e.target.value)}
            className="px-2 py-1 bg-slate-50 border border-slate-200 rounded text-xs text-slate-700 focus:outline-none"
          >
            <option value="score">Highest Score (Default)</option>
            <option value="estimatedRevenue">Revenue</option>
            <option value="employeeCount">Employee Count</option>
            <option value="companyName">Company Name</option>
          </select>
          <select
            value={filters.sortDir || 'desc'}
            onChange={(e) => handleInputChange('sortDir', e.target.value)}
            className="px-2 py-1 bg-slate-50 border border-slate-200 rounded text-xs text-slate-700 focus:outline-none"
          >
            <option value="desc">Desc</option>
            <option value="asc">Asc</option>
          </select>
        </div>
      </div>
    </div>
  );
}
