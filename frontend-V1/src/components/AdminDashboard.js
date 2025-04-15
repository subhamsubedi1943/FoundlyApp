import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import '../styles/AdminStyles.css';

// Main Dashboard Component
function AdminDashboard() {
  return (
    <div className="dashboard-container">
      <h1 className="dashboard-title">Admin Dashboard</h1>
      <div className="dashboard-menu">
        <Link to="/users" className="dashboard-card">
          <div className="card-icon">👥</div>
          <h3>Manage Users</h3>
          <p>Add, edit, and manage system users</p>
        </Link>
        <Link to="/categories" className="dashboard-card">
          <div className="card-icon">📂</div>
          <h3>Manage Categories</h3>
          <p>Create and organize product categories</p>
        </Link>
        {/* Additional cards can be added here */}
      </div>
    </div>
  );
};
export default AdminDashboard;