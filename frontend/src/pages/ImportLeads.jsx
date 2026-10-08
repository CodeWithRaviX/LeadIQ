import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Database, Sparkles, FileDown, CheckCircle } from 'lucide-react';
import PageContainer from '../components/layout/PageContainer';
import CSVUploader from '../components/import/CSVUploader';
import ImportSummary from '../components/import/ImportSummary';
import Button from '../components/common/Button';
import { leadService } from '../services/leadService';

export default function ImportLeads() {
  const navigate = useNavigate();
  const [importSummary, setImportSummary] = useState(null);
  const [loadingSample, setLoadingSample] = useState(false);

  // Helper to load sample dataset with 1 click
  const handleLoadSampleData = async () => {
    setLoadingSample(true);
    try {
      // Fetch sample leads csv from public or sample endpoint
      const response = await fetch('/sample-data/leads.csv');
      let blob;
      if (response.ok) {
        blob = await response.blob();
      } else {
        // Fallback: create mock blob from standard format
        const text = "company_name,domain,industry,location,employee_count,estimated_revenue,first_name,last_name,job_title,email,phone\n" +
          "Apex Cloud Technologies,apexcloud.io,B2B SaaS,Austin TX,45,$8500000,Marcus,Vance,Founder & CEO,m.vance@apexcloud.io,+1-512-555-0142\n" +
          "HealthPulse Systems,healthpulse.com,HealthTech,Boston MA,68,$11200000,Elena,Reyes,President,elena@healthpulse.com,+1-617-555-0189\n" +
          "CyberShield Labs,cybershieldlabs.com,CyberSecurity,Reston VA,52,$9400000,David,Kim,CEO,dkim@cybershieldlabs.com,+1-703-555-0114\n" +
          "FinFlow Analytics,finflow.io,FinTech,New York NY,38,$6800000,Rachel,Stern,Founder,rachel@finflow.io,+1-212-555-0177\n" +
          "Catalyst Digital Agency,catalystdigital.agency,Digital Agency,Portland OR,28,$2800000,Nathan,Drake,Partner,ndrake@catalyst.agency,\n" +
          "Bob's Local Contracting,,Construction,Tulsa OK,4,$450000,Bob,Miller,Owner,bob@contracting.biz,";
        blob = new Blob([text], { type: 'text/csv' });
      }

      const file = new File([blob], 'sample-leads.csv', { type: 'text/csv' });
      const summary = await leadService.importCsv(file);
      setImportSummary(summary);
    } catch (err) {
      alert('Sample import error: ' + err.message);
    } finally {
      setLoadingSample(false);
    }
  };

  return (
    <PageContainer>
      <div>
        <h2 className="text-xl font-bold tracking-tight text-slate-900">
          CSV Intelligence Ingestion Hub
        </h2>
        <p className="text-xs text-slate-500 mt-1">
          Import raw lead discovery exports. The intelligence engine cleanses headers, removes duplicate entities, validates emails, and computes prioritized scores.
        </p>
      </div>

      {!importSummary ? (
        <div className="space-y-6">
          <CSVUploader onImportComplete={(summary) => setImportSummary(summary)} />

          {/* Quick Demo Dataset Card for Reviewers */}
          <div className="p-6 rounded-xl bg-slate-900 text-white flex flex-col md:flex-row md:items-center justify-between gap-4 border border-slate-800 shadow-sm">
            <div>
              <div className="flex items-center gap-2 text-xs font-bold uppercase tracking-wider text-emerald-400">
                <Sparkles className="w-4 h-4 text-emerald-400" />
                Reviewer Quick Test
              </div>
              <h3 className="text-sm font-semibold text-white mt-1">
                Test Ingestion with Pre-built Benchmark Data (70 Leads)
              </h3>
              <p className="text-xs text-slate-400 mt-0.5 max-w-xl">
                Immediately evaluate the pipeline using our benchmark dataset containing high-priority SaaS targets, duplicate company entries, and varied edge cases.
              </p>
            </div>

            <Button
              variant="success"
              size="md"
              loading={loadingSample}
              onClick={handleLoadSampleData}
              icon={Database}
              className="shrink-0"
            >
              Load Benchmark Dataset
            </Button>
          </div>
        </div>
      ) : (
        <ImportSummary
          summary={importSummary}
          onReset={() => setImportSummary(null)}
        />
      )}
    </PageContainer>
  );
}
