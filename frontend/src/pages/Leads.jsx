import React, { useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import PageContainer from '../components/layout/PageContainer';
import LeadFilters from '../components/leads/LeadFilters';
import LeadTable from '../components/leads/LeadTable';
import ErrorMessage from '../components/common/ErrorMessage';
import { useLeads } from '../hooks/useLeads';
import { useDebounce } from '../hooks/useDebounce';
import { leadService } from '../services/leadService';

export default function Leads() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const initialPriority = searchParams.get('priority') || '';

  const {
    leads,
    totalPages,
    totalElements,
    loading,
    error,
    filters,
    updateFilters,
    resetFilters,
    refreshLeads
  } = useLeads({
    priority: initialPriority,
    page: 0,
    size: 15,
    sortBy: 'score',
    sortDir: 'desc'
  });

  const handleStatusChange = async (leadId, newStatus) => {
    try {
      await leadService.updateLeadStatus(leadId, newStatus);
      refreshLeads();
    } catch (err) {
      alert('Status update failed: ' + err.message);
    }
  };

  const handlePageChange = (newPage) => {
    updateFilters({ page: newPage });
  };

  return (
    <PageContainer>
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold tracking-tight text-slate-900">
            Prioritized Acquisition Opportunities
          </h2>
          <p className="text-xs text-slate-500 mt-1">
            Algorithmic ranking ordered by deterministic fit against fund investment criteria.
          </p>
        </div>
        <div className="text-xs text-slate-500 font-medium">
          Total in view: <span className="font-bold text-slate-900">{totalElements}</span> records
        </div>
      </div>

      {error ? (
        <ErrorMessage
          title="Failed to retrieve lead data"
          message={error}
          onRetry={refreshLeads}
        />
      ) : (
        <div className="space-y-6">
          <LeadFilters
            filters={filters}
            onFilterChange={updateFilters}
            onReset={resetFilters}
          />

          <LeadTable
            leads={leads}
            loading={loading}
            page={filters.page || 0}
            totalPages={totalPages}
            totalElements={totalElements}
            pageSize={filters.size || 15}
            onPageChange={handlePageChange}
            onStatusChange={handleStatusChange}
            onImportClick={() => navigate('/import')}
          />
        </div>
      )}
    </PageContainer>
  );
}
