import React, { useState, useEffect } from 'react';
import axios from 'axios';

function ItemReportsManagement() {
  const [itemReports, setItemReports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchItemReports = async () => {
    try {
      const response = await axios.get('http://localhost:8080/api/items');
      setItemReports(response.data);
      setLoading(false);
    } catch (err) {
      setError('Failed to fetch item reports');
      setLoading(false);
    }
  };

  const deleteItemReport = async (id) => {
    if (!window.confirm('Are you sure you want to delete this item report?')) return;
    try {
      await axios.delete(`http://localhost:8080/api/items/${id}`);
      setItemReports(itemReports.filter(item => item.itemid !== id));
    } catch (err) {
      alert('Failed to delete item report');
    }
  };

  useEffect(() => {
    fetchItemReports();
  }, []);

  if (loading) return <p>Loading item reports...</p>;
  if (error) return <p>{error}</p>;

  return (
    <div>
      <h2>Item Reports Management</h2>
      {itemReports.length === 0 ? (
        <p>No item reports found.</p>
      ) : (
        <table border="1" cellPadding="5" cellSpacing="0">
          <thead>
            <tr>
              <th>ID</th>
              <th>Item Name</th>
              <th>Employee ID</th>
              <th>Description</th>
              <th>Location</th>
              <th>Status</th>
              <th>Type</th>
              <th>Date Reported</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {itemReports.map(item => (
              <tr key={item.itemid}>
                <td>{item.itemid}</td>
                <td>{item.itemName}</td>
                <td>{item.employeeId}</td>
                <td>{item.description}</td>
                <td>{item.location}</td>
                <td>{item.itemStatus}</td>
                <td>{item.type}</td>
                <td>{new Date(item.dateReported).toLocaleString()}</td>
                <td>
                  <button onClick={() => deleteItemReport(item.itemid)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default ItemReportsManagement;
