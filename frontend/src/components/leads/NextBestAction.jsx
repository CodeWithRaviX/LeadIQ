import React, { useState } from 'react';
import { Phone, Mail, Search, CheckCircle, XCircle, Sparkles, Send, RefreshCw, MessageSquare } from 'lucide-react';
import Button from '../common/Button';
import { RECOMMENDED_ACTIONS } from '../../utils/constants';
import { aiService } from '../../services/aiService';

export default function NextBestAction({ lead, onUpdate }) {
  const [loadingAi, setLoadingAi] = useState(false);
  const [loadingAngle, setLoadingAngle] = useState(false);
  const [explanation, setExplanation] = useState(lead.aiReasoning || '');
  const [outreachAngle, setOutreachAngle] = useState(lead.aiOutreachAngle || '');
  const [error, setError] = useState('');

  const actionConf = RECOMMENDED_ACTIONS[lead.recommendedAction] || RECOMMENDED_ACTIONS.RESEARCH;

  const getActionIcon = (action) => {
    switch (action) {
      case 'CALL': return <Phone className="w-5 h-5 text-blue-600" />;
      case 'EMAIL': return <Mail className="w-5 h-5 text-indigo-600" />;
      case 'RESEARCH': return <Search className="w-5 h-5 text-purple-600" />;
      case 'VERIFY': return <CheckCircle className="w-5 h-5 text-amber-600" />;
      default: return <XCircle className="w-5 h-5 text-slate-400" />;
    }
  };

  const handleGenerateExplanation = async () => {
    setLoadingAi(true);
    setError('');
    try {
      const res = await aiService.getLeadExplanation(lead.id);
      setExplanation(res.content);
      if (onUpdate) onUpdate({ ...lead, aiReasoning: res.content });
    } catch (err) {
      setError(err.message || 'AI generation failed');
    } finally {
      setLoadingAi(false);
    }
  };

  const handleGenerateAngle = async () => {
    setLoadingAngle(true);
    setError('');
    try {
      const res = await aiService.getOutreachAngle(lead.id);
      setOutreachAngle(res.content);
      if (onUpdate) onUpdate({ ...lead, aiOutreachAngle: res.content });
    } catch (err) {
      setError(err.message || 'AI outreach angle generation failed');
    } finally {
      setLoadingAngle(false);
    }
  };

  return (
    <div className="bg-white rounded-xl border border-slate-200 shadow-sm p-6 space-y-6">
      {/* Deterministic Action Recommendation */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-4 rounded-xl bg-slate-50 border border-slate-200/80">
        <div className="flex items-center gap-3.5">
          <div className={`w-12 h-12 rounded-xl flex items-center justify-center border shadow-xs ${actionConf.bg} ${actionConf.border}`}>
            {getActionIcon(lead.recommendedAction)}
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-xs uppercase tracking-wider font-bold text-slate-400">Next Best Action:</span>
              <span className={`text-sm font-bold ${actionConf.text}`}>{actionConf.label}</span>
            </div>
            <p className="text-xs text-slate-600 mt-0.5 max-w-xl leading-relaxed">
              {lead.actionReason || 'Proceed with standard executive origination protocol.'}
            </p>
          </div>
        </div>

        {/* Action Action Trigger */}
        <div className="shrink-0 flex items-center gap-2">
          {lead.recommendedAction === 'CALL' && lead.contact?.phone && (
            <a href={`tel:${lead.contact.phone}`}>
              <Button variant="primary" size="sm" icon={Phone}>
                Initiate Call
              </Button>
            </a>
          )}
          {lead.recommendedAction === 'EMAIL' && lead.contact?.email && (
            <a href={`mailto:${lead.contact.email}`}>
              <Button variant="primary" size="sm" icon={Mail}>
                Compose Email
              </Button>
            </a>
          )}
        </div>
      </div>

      {error && (
        <div className="p-3 text-xs bg-rose-50 text-rose-700 rounded-lg border border-rose-200">
          {error}
        </div>
      )}

      {/* AI Qualitative Intelligence Section */}
      <div className="space-y-4 pt-2">
        <div className="flex items-center justify-between">
          <div>
            <h3 className="text-sm font-semibold text-slate-900 flex items-center gap-2">
              <Sparkles className="w-4 h-4 text-emerald-600" />
              AI Qualitative Intelligence Layer
            </h3>
            <p className="text-xs text-slate-500">
              Generates executive investment rationale and discrete owner outreach angles
            </p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* Card 1: Investment Rationale */}
          <div className="p-4 rounded-xl bg-slate-50/70 border border-slate-200/70 flex flex-col justify-between space-y-3">
            <div>
              <div className="flex items-center justify-between mb-2">
                <span className="text-xs font-semibold text-slate-800 flex items-center gap-1.5">
                  <MessageSquare className="w-3.5 h-3.5 text-slate-500" />
                  M&A Thesis Rationale
                </span>
                {explanation && (
                  <span className="text-[10px] uppercase font-semibold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                    Generated
                  </span>
                )}
              </div>
              {explanation ? (
                <p className="text-xs text-slate-700 leading-relaxed italic bg-white p-3 rounded-lg border border-slate-100">
                  "{explanation}"
                </p>
              ) : (
                <p className="text-xs text-slate-400 italic">
                  Generate a concise 2–3 sentence analysis of this candidate's fundamental fit.
                </p>
              )}
            </div>

            <Button
              variant="secondary"
              size="sm"
              loading={loadingAi}
              onClick={handleGenerateExplanation}
              icon={explanation ? RefreshCw : Sparkles}
              className="w-full text-xs"
            >
              {loadingAi ? 'Analyzing lead fundamentals...' : explanation ? 'Regenerate Analysis' : 'Explain This Lead'}
            </Button>
          </div>

          {/* Card 2: Tailored Outreach Angle */}
          <div className="p-4 rounded-xl bg-slate-50/70 border border-slate-200/70 flex flex-col justify-between space-y-3">
            <div>
              <div className="flex items-center justify-between mb-2">
                <span className="text-xs font-semibold text-slate-800 flex items-center gap-1.5">
                  <Send className="w-3.5 h-3.5 text-slate-500" />
                  Owner Strategic Outreach Angle
                </span>
                {outreachAngle && (
                  <span className="text-[10px] uppercase font-semibold text-emerald-700 bg-emerald-50 px-2 py-0.5 rounded border border-emerald-200">
                    Strategic Angle
                  </span>
                )}
              </div>
              {outreachAngle ? (
                <p className="text-xs text-slate-700 leading-relaxed italic bg-white p-3 rounded-lg border border-slate-100">
                  "{outreachAngle}"
                </p>
              ) : (
                <p className="text-xs text-slate-400 italic">
                  Synthesize a discrete 2–4 sentence conversation angle for speaking directly with the business owner.
                </p>
              )}
            </div>

            <Button
              variant="secondary"
              size="sm"
              loading={loadingAngle}
              onClick={handleGenerateAngle}
              icon={outreachAngle ? RefreshCw : Sparkles}
              className="w-full text-xs"
            >
              {loadingAngle ? 'Crafting discrete angle...' : outreachAngle ? 'Regenerate Outreach Angle' : 'Generate Outreach Angle'}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
