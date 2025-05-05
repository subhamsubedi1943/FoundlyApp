import React from 'react';
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
        <Link to="/employees" className="dashboard-card">
          <div className="card-icon">🧑‍💼</div>
          <h3>Manage Employees</h3>
          <p>Add, edit, and manage employee list</p>
        </Link>
        <Link to="/categories" className="dashboard-card">
          <div className="card-icon">📂</div>
          <h3>Manage Categories</h3>
          <p>Create and organize product categories</p>
        </Link>
        <Link to="/transactions" className="dashboard-card">
          <div className="card-icon">💰</div>
          <h3>Transaction Management</h3>
          <p>View and delete transactions</p>
        </Link>
        <Link to="/item-reports" className="dashboard-card">
          <div className="card-icon">📋</div>
          <h3>Item Reports Management</h3>
          <p>View and delete item reports</p>
        </Link>
      </div>
    </div>
  );
};

export default AdminDashboard;
