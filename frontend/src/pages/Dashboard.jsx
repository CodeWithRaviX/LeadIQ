import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Building2,
  CheckCircle,
  TrendingUp,
  Database,
  ArrowUpRight,
  UploadCloud,
  RefreshCw
} from 'lucide-react';
import PageContainer from '../components/layout/PageContainer';
import MetricCard from '../components/dashboard/MetricCard';
import ScoreDistribution from '../components/dashboard/ScoreDistribution';
import LeadOverview from '../components/dashboard/LeadOverview';
import Loading from '../components/common/Loading';
import ErrorMessage from '../components/common/ErrorMessage';
import Button from '../components/common/Button';
import { leadService } from '../services/leadService';

export default function Dashboard() {
  const navigate = useNavigate();
  const [summary, setSummary] = useState(null);
  const [topLeads, setTopLeads] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [sumData, leadsData] = await Promise.all([
        leadService.getDashboardSummary(),
        leadService.getLeads({ sortBy: 'score', sortDir: 'desc', size: 6 })
      ]);
      setSummary(sumData);
      setTopLeads(leadsData.content || []);
    } catch (err) {
      setError(err.message || 'Failed to load executive dashboard');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  if (loading) {
    return (
      <PageContainer>
        <Loading message="Synthesizing portfolio intelligence..." />
      </PageContainer>
    );
  }

  if (error) {
    return (
      <PageContainer>
        <ErrorMessage
          title="Intelligence Pipeline Unavailable"
          message={error}
          onRetry={loadData}
        />
      </PageContainer>
    );
  }

  const totalLeads = summary?.totalLeads || 0;
  const qualified = summary?.qualifiedLeads || 0;
  const highPriority = summary?.highPriority || 0;
  const dataQuality = summary?.dataQuality || 0;
  const avgScore = summary?.avgScore || 0;

  return (
    <PageContainer>
      {/* Top Welcome & Mission Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-slate-900 text-white p-6 rounded-2xl border border-slate-800 shadow-md">
        <div>
          <span className="text-[11px] font-bold uppercase tracking-widest text-emerald-400">
            Caprae Capital • LeadIQ Platform
          </span>
          <h2 className="text-xl font-bold tracking-tight text-white mt-1">
            M&A Acquisition Decision Intelligence
          </h2>
          <p className="text-xs text-slate-300 mt-1 max-w-2xl leading-relaxed">
            Prioritize the leads most likely to fit the acquisition thesis — before spending senior partner time on outreach.
          </p>
        </div>
        <div className="flex items-center gap-2.5">
          <Button
            variant="ghost"
            size="sm"
            onClick={loadData}
            icon={RefreshCw}
            className="text-slate-300 hover:text-white hover:bg-slate-800"
          >
            Refresh
          </Button>
          <Button
            variant="success"
            size="sm"
            onClick={() => navigate('/import')}
            icon={UploadCloud}
          >
            Import CSV
          </Button>
        </div>
      </div>

      {/* 4 Core KPI Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <MetricCard
          title="Total Opportunities"
          value={totalLeads.toLocaleString()}
          subvalue="Active database records"
          icon={Database}
          badgeText="Active Database"
          badgeColor="slate"
        />

        <MetricCard
          title="Qualified Opportunities"
          value={qualified.toLocaleString()}
          subvalue="Score ≥ 60 within buy-box"
          icon={CheckCircle}
          badgeText={`${totalLeads > 0 ? Math.round((qualified / totalLeads) * 100) : 0}% Qualified`}
          badgeColor="blue"
        />

        <MetricCard
          title="High Priority Candidates"
          value={highPriority.toLocaleString()}
          subvalue="Score ≥ 80 • Immediate outreach"
          icon={TrendingUp}
          badgeText={`${totalLeads > 0 ? Math.round((highPriority / totalLeads) * 100) : 0}% High Priority`}
          badgeColor="emerald"
          highlight={true}
        />

        <MetricCard
          title="Data Integrity Index"
          value={`${dataQuality}%`}
          subvalue={`Data Integrity Index — ${dataQuality}% Complete`}
          icon={Building2}
          badgeText="Complete"
          badgeColor="amber"
        />
      </div>

      {/* Visual Analytics Distribution Charts */}
      {totalLeads > 0 && (
        <ScoreDistribution
          priorityDistribution={summary?.priorityDistribution}
          scoreDistribution={summary?.scoreDistribution}
        />
      )}

      {/* Highest Priority Opportunities Table Preview */}
      <LeadOverview leads={topLeads} />
    </PageContainer>
  );
}
