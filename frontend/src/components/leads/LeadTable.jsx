import React from 'react';
import { ChevronLeft, ChevronRight, ChevronsUpDown } from 'lucide-react';
import LeadRow from './LeadRow';
import Loading from '../common/Loading';
import EmptyState from '../common/EmptyState';
import Button from '../common/Button';

export default function LeadTable({
  leads = [],
  loading = false,
  page = 0,
  totalPages = 0,
  totalElements = 0,
  pageSize = 15,
  onPageChange,
  onStatusChange,
  onImportClick
}) {
  if (loading) {
    return (
      <div className="bg-white rounded-xl border border-slate-200 p-8 shadow-sm">
        <Loading message="Fetching prioritized acquisition opportunities..." />
      </div>
    );
  }

  if (leads.length === 0) {
    return (
      <EmptyState
        title="No opportunities match your criteria"
        description="Try adjusting your filters or import a fresh batch of company records to identify potential targets."
        actionText="Import Leads"
        onAction={onImportClick}
      />
    );
  }

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm overflow-hidden flex flex-col">
      <div className="overflow-x-auto">
        <table className="w-full text-left">
          <thead className="bg-slate-50/90 text-slate-500 font-medium text-xs border-b border-slate-200">
            <tr>
              <th className="py-3 px-5">Company</th>
              <th className="py-3 px-5">Industry</th>
              <th className="py-3 px-5">Revenue</th>
              <th className="py-3 px-5">Employees</th>
              <th className="py-3 px-5">Location</th>
              <th className="py-3 px-5">Decision Maker</th>
              <th className="py-3 px-5 text-center">Score</th>
              <th className="py-3 px-5">Next Action</th>
              <th className="py-3 px-5 text-right">Status</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-slate-100">
            {leads.map((lead) => (
              <LeadRow
                key={lead.id}
                lead={lead}
                onStatusChange={onStatusChange}
              />
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination Footer */}
      <div className="px-5 py-3 border-t border-slate-200 bg-slate-50/60 flex items-center justify-between text-xs text-slate-600">
        <div>
          Showing <span className="font-semibold text-slate-800">{page * pageSize + 1}</span> to{' '}
          <span className="font-semibold text-slate-800">{Math.min((page + 1) * pageSize, totalElements)}</span> of{' '}
          <span className="font-semibold text-slate-800">{totalElements}</span> opportunities
        </div>

        <div className="flex items-center gap-2">
          <Button
            variant="secondary"
            size="sm"
            disabled={page === 0}
            onClick={() => onPageChange(page - 1)}
            icon={ChevronLeft}
          >
            Prev
          </Button>
          <span className="px-2 font-medium">
            Page {page + 1} of {Math.max(1, totalPages)}
          </span>
          <Button
            variant="secondary"
            size="sm"
            disabled={page >= totalPages - 1}
            onClick={() => onPageChange(page + 1)}
          >
            Next
            <ChevronRight className="w-3.5 h-3.5 ml-1" />
          </Button>
        </div>
      </div>
    </div>
  );
}
