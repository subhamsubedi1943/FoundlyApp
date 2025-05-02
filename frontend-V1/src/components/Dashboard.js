import React, { useEffect, useState } from 'react';
import { 
  LineChart, Line, BarChart, Bar, PieChart, Pie, Cell,
  XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer 
} from 'recharts';
import axios from 'axios';
import '../styles/Dashboard.css';
import { AlertCircle, CheckCircle, Database, Shield } from 'lucide-react';

const Dashboard = () => {
  const [stats, setStats] = useState([]);
  const [monthlyData, setMonthlyData] = useState([]);
  const [categoryData, setCategoryData] = useState([]);
  const [locationData, setLocationData] = useState([]);
  const [claimStatusData, setClaimStatusData] = useState([]);

  const COLORS = ['#0088FE', '#FF8042', '#82ca9d', '#8884d8'];
  const LOST_COLOR = '#ff6b6b';
  const FOUND_COLOR = '#69db7c';

  useEffect(() => {
    fetchStats();
    fetchMonthlyTrends();
    fetchCategories();
    fetchLocations();
    fetchClaimStatus();
  }, []);

  const fetchStats = async () => {
    try {
      const [lostRes, foundRes, allTransactionsRes] = await Promise.all([
        axios.get('http://localhost:8080/api/items/lost-items'),
        axios.get('http://localhost:8080/api/items/found-items'),
        axios.get('http://localhost:8080/api/transactions')
      ]);

      const totalLost = lostRes.data.length;
      const totalFound = foundRes.data.length;
      const totalReturned = allTransactionsRes.data.filter(t => 
        t.transactionStatus === 'COMPLETED' && t.transactionType === 'HANDOVER'
      ).length;

      setStats([
        { 
          id: 1, 
          title: 'Total Lost Items Reported', 
          count: totalLost, 
          icon: <AlertCircle className="stat-icon lost-icon" />, 
          change: '+8% this week', 
          type: 'lost' 
        },
        { 
          id: 2, 
          title: 'Total Found Items Reported', 
          count: totalFound, 
          icon: <CheckCircle className="stat-icon found-icon" />, 
          change: '+12% this week', 
          type: 'found' 
        },
        { 
          id: 3, 
          title: 'Items Successfully Returned', 
          count: totalReturned, 
          icon: <Database className="stat-icon neutral-icon" />, 
          change: '', 
          type: 'neutral' 
        }
      ]);
    } catch (error) {
      console.error('Error fetching stats:', error);
    }
  };

  const fetchMonthlyTrends = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/items');
      const data = res.data;

      const monthlyCounts = {};

      data.forEach(item => {
        const month = new Date(item.dateReported).toLocaleString('default', { month: 'short' });
        if (!monthlyCounts[month]) monthlyCounts[month] = { lost: 0, found: 0 };
        if (item.type === 'LOST') monthlyCounts[month].lost += 1;
        if (item.type === 'FOUND') monthlyCounts[month].found += 1;
      });

      const formatted = Object.keys(monthlyCounts).map(month => ({
        name: month,
        ...monthlyCounts[month]
      }));

      setMonthlyData(formatted);
    } catch (error) {
      console.error('Error fetching monthly trends:', error);
    }
  };

  const fetchCategories = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/items');
      const data = res.data;

      const categoryCounts = {};

      data.forEach(item => {
        const category = item.category ? item.category.name : 'Others';
        if (!categoryCounts[category]) categoryCounts[category] = 0;
        categoryCounts[category]++;
      });

      const formatted = Object.keys(categoryCounts).map(category => ({
        name: category,
        value: categoryCounts[category]
      }));

      setCategoryData(formatted);
    } catch (error) {
      console.error('Error fetching categories:', error);
    }
  };

  const fetchLocations = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/items');
      const data = res.data;

      const locationCounts = {};

      data.forEach(item => {
        const location = item.location || 'Unknown';
        if (!locationCounts[location]) locationCounts[location] = 0;
        locationCounts[location]++;
      });

      const formatted = Object.keys(locationCounts).map(location => ({
        name: location,
        value: locationCounts[location]
      }));

      setLocationData(formatted);
    } catch (error) {
      console.error('Error fetching locations:', error);
    }
  };

  const fetchClaimStatus = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/transactions');
      const data = res.data;

      const claimCounts = { Requested: 0, Pending: 0, Completed: 0, HandedOver: 0 };

      data.forEach(txn => {
        if (txn.transactionStatus === 'REQUESTED') claimCounts.Requested++;
        else if (txn.transactionStatus === 'PENDING') claimCounts.Pending++;
        else if (txn.transactionStatus === 'COMPLETED') claimCounts.Completed++;
        else if (txn.transactionStatus === 'HANDOVER') claimCounts.HandedOver++;
      });

      const formatted = Object.keys(claimCounts).map(status => ({
        name: status,
        value: claimCounts[status]
      }));

      setClaimStatusData(formatted);
    } catch (error) {
      console.error('Error fetching claim status:', error);
    }
  };

  const renderStatCards = () => {
    return stats.map(stat => (
      <div key={stat.id} className={`stat-card ${stat.type}-card`}>
        <div className="stat-icon-container">
          {stat.icon}
        </div>
        <div className="stat-content">
          <h2 className="stat-count">{stat.count}</h2>
          <p className="stat-title">{stat.title}</p>
          {stat.change && <p className="stat-change">{stat.change}</p>}
        </div>
      </div>
    ));
  };

  return (
    <div className="dashboard-container">
      {/* Stats */}
      <div className="stats-container">
        {renderStatCards()}
        <div className="stat-card security-card">
          <div className="security-content">
            <Shield className="shield-icon" />
            <h2>Backed by Security Team</h2>
            <p>30 Verified handovers</p>
          </div>
        </div>
      </div>

      {/* Charts */}
      <div className="charts-container">
        {/* Monthly Trends */}
        <div className="chart-card">
          <h3>Monthly Activity Trends</h3>
          <ResponsiveContainer width="100%" height={250}>
            <LineChart data={monthlyData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Line type="monotone" dataKey="lost" stroke={LOST_COLOR} activeDot={{ r: 8 }} name="Lost Items" />
              <Line type="monotone" dataKey="found" stroke={FOUND_COLOR} name="Found Items" />
            </LineChart>
          </ResponsiveContainer>
        </div>

        {/* Categories */}
        <div className="chart-card">
          <h3>Item Categories Distribution</h3>
          <ResponsiveContainer width="100%" height={250}>
            <PieChart>
              <Pie
                data={categoryData}
                cx="50%"
                cy="50%"
                labelLine={false}
                outerRadius={80}
                innerRadius={40}
                fill="#8884d8"
                dataKey="value"
                label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
              >
                {categoryData.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip />
              <Legend />
            </PieChart>
          </ResponsiveContainer>
        </div>

        {/* Locations */}
        <div className="chart-card">
          <h3>Top Locations with Most Reports</h3>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={locationData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Bar dataKey="value" fill="#4dabf7" />
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Claim Status */}
        <div className="chart-card">
          <h3>Claim Status Distribution</h3>
          <ResponsiveContainer width="100%" height={250}>
            <BarChart data={claimStatusData}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="value" fill="#4dabf7" />
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
