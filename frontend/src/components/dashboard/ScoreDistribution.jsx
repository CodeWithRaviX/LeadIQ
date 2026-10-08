import React from 'react';
import {
  BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer,
  PieChart, Pie, Cell, Legend
} from 'recharts';

export default function ScoreDistribution({ priorityDistribution = {}, scoreDistribution = {} }) {
  const priorityData = [
    { name: 'High Priority (80+)', value: priorityDistribution.HIGH || 0, color: '#10b981' },
    { name: 'Medium Priority (60-79)', value: priorityDistribution.MEDIUM || 0, color: '#f59e0b' },
    { name: 'Low Priority (<60)', value: priorityDistribution.LOW || 0, color: '#94a3b8' }
  ];

  const scoreData = [
    { range: '90-100', count: scoreDistribution['90-100'] || 0 },
    { range: '80-89', count: scoreDistribution['80-89'] || 0 },
    { range: '70-79', count: scoreDistribution['70-79'] || 0 },
    { range: '60-69', count: scoreDistribution['60-69'] || 0 },
    { range: '<60', count: scoreDistribution['below60'] || 0 }
  ];

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
      {/* Chart 1: Opportunity Prioritization Breakdown */}
      <div className="p-6 bg-white rounded-xl border border-slate-200 shadow-sm">
        <div className="mb-4">
          <h3 className="text-sm font-semibold text-slate-900">Priority Tier Distribution</h3>
          <p className="text-xs text-slate-500">Distribution across High, Medium, and Low acquisition priority</p>
        </div>
        <div className="h-64 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <PieChart>
              <Pie
                data={priorityData}
                dataKey="value"
                nameKey="name"
                cx="50%"
                cy="50%"
                innerRadius={55}
                outerRadius={85}
                paddingAngle={4}
              >
                {priorityData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Pie>
              <Tooltip
                contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderRadius: '8px', color: '#fff', fontSize: '12px' }}
                itemStyle={{ color: '#fff' }}
              />
              <Legend
                verticalAlign="bottom"
                iconType="circle"
                wrapperStyle={{ fontSize: '12px', paddingTop: '12px' }}
              />
            </PieChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Chart 2: Score Distribution Histogram */}
      <div className="p-6 bg-white rounded-xl border border-slate-200 shadow-sm">
        <div className="mb-4">
          <h3 className="text-sm font-semibold text-slate-900">100-Point Score Frequency</h3>
          <p className="text-xs text-slate-500">Frequency of leads across deterministic scoring bands</p>
        </div>
        <div className="h-64 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={scoreData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
              <XAxis dataKey="range" tick={{ fontSize: 11, fill: '#64748b' }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 11, fill: '#64748b' }} axisLine={false} tickLine={false} />
              <Tooltip
                cursor={{ fill: '#f1f5f9' }}
                contentStyle={{ backgroundColor: '#0f172a', borderColor: '#1e293b', borderRadius: '8px', color: '#fff', fontSize: '12px' }}
                formatter={(val) => [`${val} Leads`, 'Volume']}
              />
              <Bar dataKey="count" fill="#10b981" radius={[4, 4, 0, 0]} />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
}
