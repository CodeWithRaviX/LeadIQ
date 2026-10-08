import api from './api';

export const aiService = {
  async getLeadExplanation(leadId) {
    const res = await api.post(`/ai/leads/${leadId}/explanation`);
    return res.data;
  },

  async getOutreachAngle(leadId) {
    const res = await api.post(`/ai/leads/${leadId}/outreach-angle`);
    return res.data;
  }
};
