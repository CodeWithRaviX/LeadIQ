import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { UploadCloud, Download, Sparkles, Filter } from 'lucide-react';
import Button from '../common/Button';
import { leadService } from '../../services/leadService';

export default function Header() {
  const navigate = useNavigate();
  const location = useLocation();

  const handleExport = async () => {
    try {
      await leadService.exportLeads();
    } catch (err) {
      alert('Export failed: ' + err.message);
    }
  };

  return (
    <header className="h-16 bg-white border-b border-slate-200 px-8 flex items-center justify-between shrink-0">
      <div className="flex items-center gap-4">
        <h1 className="text-base font-semibold text-slate-900 tracking-tight">
          {location.pathname === '/' && 'Executive Overview'}
          {location.pathname === '/leads' && 'Prioritized Opportunities'}
          {location.pathname === '/import' && 'CSV Intelligence Ingestion'}
          {location.pathname === '/settings' && 'Acquisition Thesis & Rules'}
          {location.pathname.startsWith('/leads/') && 'Opportunity Dossier'}
        </h1>
        <span className="hidden md:inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium bg-slate-100 text-slate-600 border border-slate-200">
          <Sparkles className="w-3 h-3 text-emerald-600" />
          Deterministic Prioritization
        </span>
      </div>

      <div className="flex items-center gap-3">
        {location.pathname !== '/import' && (
          <Button
            variant="secondary"
            size="sm"
            icon={UploadCloud}
            onClick={() => navigate('/import')}
          >
            Import Leads
          </Button>
        )}
        <Button
          variant="secondary"
          size="sm"
          icon={Download}
          onClick={handleExport}
        >
          Export CSV
        </Button>
      </div>
    </header>
  );
}
