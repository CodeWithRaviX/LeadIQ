import { useState, useEffect, useCallback } from 'react';
import { leadService } from '../services/leadService';

export function useLeads(initialFilters = {}) {
  const [leads, setLeads] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filters, setFilters] = useState(initialFilters);

  const fetchLeads = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await leadService.getLeads(filters);
      setLeads(data.content || []);
      setTotalPages(data.totalPages || 0);
      setTotalElements(data.totalElements || 0);
    } catch (err) {
      setError(err.message || 'Failed to fetch leads');
    } finally {
      setLoading(false);
    }
  }, [filters]);

  useEffect(() => {
    fetchLeads();
  }, [fetchLeads]);

  const updateFilters = (newFilters) => {
    setFilters((prev) => ({ ...prev, ...newFilters, page: newFilters.page !== undefined ? newFilters.page : 0 }));
  };

  const resetFilters = () => {
    setFilters({ page: 0, size: 15, sortBy: 'score', sortDir: 'desc' });
  };

  return {
    leads,
    totalPages,
    totalElements,
    loading,
    error,
    filters,
    updateFilters,
    resetFilters,
    refreshLeads: fetchLeads
  };
}
