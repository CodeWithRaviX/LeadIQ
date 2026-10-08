import api from './api';

export const leadService = {
  async getDashboardSummary() {
    const res = await api.get('/dashboard/summary');
    return res.data;
  },

  async getLeads(filters = {}) {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, val]) => {
      if (val !== undefined && val !== null && val !== '') {
        params.append(key, val);
      }
    });
    const res = await api.get(`/leads?${params.toString()}`);
    return res.data;
  },

  async getLeadById(id) {
    const res = await api.get(`/leads/${id}`);
    return res.data;
  },

  async updateLeadStatus(id, status) {
    const res = await api.put(`/leads/${id}/status`, { status });
    return res.data;
  },

  async deleteLead(id) {
    const res = await api.delete(`/leads/${id}`);
    return res.data;
  },

  async scoreLead(id) {
    const res = await api.post(`/leads/${id}/score`);
    return res.data;
  },

  async scoreAllLeads() {
    const res = await api.post('/leads/score-all');
    return res.data;
  },

  async importCsv(file) {
    const formData = new FormData();
    formData.append('file', file);
    const res = await api.post('/import/csv', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      timeout: 120000
    });
    return res.data;
  },

  async exportLeads(filters = {}) {
    const params = new URLSearchParams();
    Object.entries(filters).forEach(([key, val]) => {
      if (val !== undefined && val !== null && val !== '') {
        params.append(key, val);
      }
    });
    const res = await api.get(`/leads/export?${params.toString()}`, {
      responseType: 'blob',
      timeout: 120000
    });
    const blob = new Blob([res.data], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `prioritized-leads-${new Date().toISOString().slice(0, 10)}.csv`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  }
};
