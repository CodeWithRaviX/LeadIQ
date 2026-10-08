import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, RefreshCw, Trash2, CheckCircle2 } from 'lucide-react';
import PageContainer from '../components/layout/PageContainer';
import LeadDetail from '../components/leads/LeadDetail';
import LeadReasoning from '../components/leads/LeadReasoning';
import NextBestAction from '../components/leads/NextBestAction';
import Loading from '../components/common/Loading';
import ErrorMessage from '../components/common/ErrorMessage';
import Button from '../components/common/Button';
import { leadService } from '../services/leadService';

export default function LeadDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [lead, setLead] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [reScoring, setReScoring] = useState(false);

  const fetchLead = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await leadService.getLeadById(id);
      setLead(data);
    } catch (err) {
      setError(err.message || 'Failed to load lead details');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLead();
  }, [id]);

  const handleReScore = async () => {
    setReScoring(true);
    try {
      await leadService.scoreLead(id);
      await fetchLead();
    } catch (err) {
      alert('Re-scoring failed: ' + err.message);
    } finally {
      setReScoring(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this lead record?')) return;
    try {
      await leadService.deleteLead(id);
      navigate('/leads');
    } catch (err) {
      alert('Delete failed: ' + err.message);
    }
  };

  const handleStatusChange = async (newStatus) => {
    try {
      const updated = await leadService.updateLeadStatus(id, newStatus);
      setLead(updated);
    } catch (err) {
      alert('Failed to update status: ' + err.message);
    }
  };

  if (loading) {
    return (
      <PageContainer>
        <Loading message="Assembling company intelligence dossier..." />
      </PageContainer>
    );
  }

  if (error || !lead) {
    return (
      <PageContainer>
        <ErrorMessage
          title="Lead Dossier Not Found"
          message={error || "The requested lead could not be located in PostgreSQL."}
          onRetry={fetchLead}
        />
      </PageContainer>
    );
  }

  return (
    <PageContainer>
      {/* Top Action Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2 border-b border-slate-200">
        <button
          onClick={() => navigate(-1)}
          className="inline-flex items-center gap-1.5 text-xs font-semibold text-slate-500 hover:text-slate-900 transition-colors cursor-pointer"
        >
          <ArrowLeft className="w-4 h-4" /> Back to Prioritized Leads
        </button>

        <div className="flex items-center gap-3">
          {/* Status selector */}
          <div className="flex items-center gap-2">
            <span className="text-xs text-slate-400 font-medium">Status:</span>
            <select
              value={lead.status || 'NEW'}
              onChange={(e) => handleStatusChange(e.target.value)}
              className="text-xs font-semibold bg-white border border-slate-200 rounded-lg px-2.5 py-1.5 text-slate-800 focus:outline-none focus:ring-1 focus:ring-slate-900"
            >
              <option value="NEW">New</option>
              <option value="REVIEWED">Reviewed</option>
              <option value="CONTACTED">Contacted</option>
              <option value="QUALIFIED">Qualified</option>
              <option value="DISQUALIFIED">Disqualified</option>
            </select>
          </div>

          <Button
            variant="secondary"
            size="sm"
            icon={RefreshCw}
            loading={reScoring}
            onClick={handleReScore}
          >
            Re-Score
          </Button>

          <Button
            variant="ghost"
            size="sm"
            icon={Trash2}
            onClick={handleDelete}
            className="text-rose-600 hover:bg-rose-50 hover:text-rose-700"
          >
            Delete
          </Button>
        </div>
      </div>

      {/* Main Dossier Sections */}
      <div className="space-y-6">
        {/* 1. Firmographics & Contact Profile */}
        <LeadDetail lead={lead} />

        {/* 2. Deterministic Action Directive & AI Analysis */}
        <NextBestAction lead={lead} onUpdate={(updated) => setLead(updated)} />

        {/* 3. 100-Point Scoring Breakdown & Audit Rationale */}
        <LeadReasoning lead={lead} />
      </div>
    </PageContainer>
  );
}
