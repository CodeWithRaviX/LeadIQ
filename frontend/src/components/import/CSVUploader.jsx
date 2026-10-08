import React, { useState, useRef } from 'react';
import { UploadCloud, FileText, AlertCircle, CheckCircle, ArrowRight } from 'lucide-react';
import Button from '../common/Button';
import { leadService } from '../../services/leadService';

export default function CSVUploader({ onImportComplete }) {
  const [file, setFile] = useState(null);
  const [dragging, setDragging] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState('');
  const [error, setError] = useState('');
  const fileInputRef = useRef(null);

  const handleDragOver = (e) => {
    e.preventDefault();
    setDragging(true);
  };

  const handleDragLeave = () => {
    setDragging(false);
  };

  const handleDrop = (e) => {
    e.preventDefault();
    setDragging(false);
    const droppedFile = e.dataTransfer.files[0];
    validateAndSetFile(droppedFile);
  };

  const handleFileChange = (e) => {
    const selected = e.target.files[0];
    validateAndSetFile(selected);
  };

  const validateAndSetFile = (f) => {
    setError('');
    if (!f) return;
    if (!f.name.toLowerCase().endsWith('.csv')) {
      setError('Please upload a valid .csv file.');
      return;
    }
    if (f.size > 10 * 1024 * 1024) {
      setError('File size exceeds the 10MB maximum limit.');
      return;
    }
    setFile(f);
  };

  const handleUpload = async () => {
    if (!file) return;
    setUploading(true);
    setError('');
    setUploadProgress('Normalizing headers and parsing rows...');

    try {
      setTimeout(() => setUploadProgress('Evaluating deduplication and validating emails...'), 400);
      setTimeout(() => setUploadProgress('Calculating deterministic 100-point scores and actions...'), 800);

      const result = await leadService.importCsv(file);
      onImportComplete(result);
    } catch (err) {
      setError(err.message || 'Failed to process CSV file.');
    } finally {
      setUploading(false);
      setUploadProgress('');
    }
  };

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-8 space-y-6">
      <div className="text-center max-w-lg mx-auto">
        <h2 className="text-lg font-bold text-slate-900 tracking-tight">Import Lead Dataset</h2>
        <p className="text-xs text-slate-500 mt-1">
          Upload raw business records from CSV. Our pipeline normalizes headers, merges duplicates, validates contact channels, and scores each opportunity against your buy box.
        </p>
      </div>

      {/* Drag & Drop Zone */}
      <div
        onDragOver={handleDragOver}
        onDragLeave={handleDragLeave}
        onDrop={handleDrop}
        onClick={() => fileInputRef.current?.click()}
        className={`border-2 border-dashed rounded-xl p-8 text-center cursor-pointer transition-all ${
          dragging
            ? 'border-emerald-500 bg-emerald-50/50 scale-[1.01]'
            : file
            ? 'border-emerald-300 bg-emerald-50/20'
            : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50/50'
        }`}
      >
        <input
          type="file"
          ref={fileInputRef}
          onChange={handleFileChange}
          accept=".csv"
          className="hidden"
        />

        <div className="w-12 h-12 rounded-full bg-slate-100 flex items-center justify-center mx-auto mb-3 text-slate-600">
          <UploadCloud className="w-6 h-6 text-slate-500" />
        </div>

        {file ? (
          <div className="space-y-1">
            <p className="text-sm font-semibold text-slate-900 flex items-center justify-center gap-2">
              <FileText className="w-4 h-4 text-emerald-600" />
              {file.name}
            </p>
            <p className="text-xs text-slate-500 font-mono">
              {(file.size / 1024).toFixed(1)} KB • Ready for processing
            </p>
          </div>
        ) : (
          <div className="space-y-1">
            <p className="text-sm font-medium text-slate-700">
              Drag and drop your lead CSV here, or <span className="text-emerald-600 font-semibold underline">browse</span>
            </p>
            <p className="text-xs text-slate-400">
              Supports standard column aliases (Company Name, Domain, Industry, Revenue, Employees, Email, Phone, etc.)
            </p>
          </div>
        )}
      </div>

      {error && (
        <div className="p-3 text-xs bg-rose-50 text-rose-700 rounded-lg border border-rose-200 flex items-center gap-2">
          <AlertCircle className="w-4 h-4 shrink-0" />
          <span>{error}</span>
        </div>
      )}

      {/* Upload Button & Progress */}
      <div className="flex flex-col items-center gap-3 w-full">
        <Button
          variant="primary"
          size="lg"
          disabled={!file || uploading}
          loading={uploading}
          onClick={handleUpload}
          className="w-full sm:w-64"
        >
          {uploading ? 'Processing Dataset...' : 'Process & Score Dataset'}
        </Button>

        {uploading && (
          <div className="w-full max-w-md space-y-2 pt-2 animate-fadeIn">
            <div className="flex justify-between items-center text-xs font-medium text-slate-600">
              <span className="animate-pulse">{uploadProgress || 'Analyzing records...'}</span>
              <span className="font-mono text-emerald-600 font-bold">Processing</span>
            </div>
            <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden border border-slate-200">
              <div className="h-full bg-gradient-to-r from-emerald-500 to-teal-600 rounded-full animate-pulse w-3/4 transition-all duration-500" />
            </div>
            <div className="grid grid-cols-3 text-[10px] text-slate-400 font-medium text-center pt-1">
              <span className="text-emerald-600 font-semibold">✓ Normalizing</span>
              <span className="text-emerald-600 font-semibold">✓ Deduplicating</span>
              <span className="text-emerald-600 font-semibold">✓ Scoring</span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
