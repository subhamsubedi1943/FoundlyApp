import React, { useState, useEffect } from 'react';
import '../styles/NotificationPanel.css';

function NotificationPanel() {
  const [open, setOpen] = useState(false);
  const [selectedNote, setSelectedNote] = useState(null);
  const [activeTab, setActiveTab] = useState('all');
  const [notificationData, setNotificationData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user?.userId) {
      setError('User not logged in.');
      setLoading(false);
      return;
    }

    const userId = user.userId;

    fetch(`http://localhost:8080/api/transactions/notifications/${userId}`)
      .then((res) => {
        if (!res.ok) throw new Error('Failed to fetch notifications');
        return res.json();
      })
      .then((data) => {
        setNotificationData(data);
        setError(null);
      })
      .catch((err) => {
        console.error('Error fetching notifications:', err);
        setError('Could not load notifications.');
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  const openDetails = (note) => setSelectedNote(note);
  const closeDetails = () => setSelectedNote(null);

  const filteredNotifications = notificationData.filter((note) => {
    if (activeTab === 'all') return true;
    return note.type?.toLowerCase() === activeTab;
  });

  return (
    <div className="notification-wrapper">
      {/* Bell Button */}
      <button className="bell-icon" onClick={() => setOpen(true)}>
        <svg width="22" height="22" viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 2C10.343 2 9 3.343 9 5V6.068C6.163 7.142 4 9.97 4 13V18L2 20V21H22V20L20 18V13C20 9.97 17.837 7.142 15 6.068V5C15 3.343 13.657 2 12 2ZM6 13C6 10.243 8.243 8 11 8H13C15.757 8 18 10.243 18 13V18H6V13ZM12 23C13.657 23 15 21.657 15 20H9C9 21.657 10.343 23 12 23Z" />
        </svg>
        <span className="dot" />
      </button>

      {/* Notification Panel */}
      <div className={`panel ${open ? 'open' : ''}`}>
        <div className="panel-header">
          <h2>Notifications</h2>
          <button onClick={() => setOpen(false)} className="close-btn">✖</button>
        </div>

        {/* Tabs */}
        <div className="tabs">
          <button className={activeTab === 'all' ? 'active-tab' : ''} onClick={() => setActiveTab('all')}>All</button>
          <button className={activeTab === 'claim' ? 'active-tab' : ''} onClick={() => setActiveTab('claim')}>Claims</button>
          <button className={activeTab === 'handover' ? 'active-tab' : ''} onClick={() => setActiveTab('handover')}>Handovers</button>
        </div>

        {/* Notification Cards */}
        <div className="notifications-list">
          {loading && <p>Loading notifications...</p>}
          {error && <p className="error">{error}</p>}
          {!loading && !error && filteredNotifications.length === 0 && (
            <p>No notifications found.</p>
          )}
          {!loading && !error && filteredNotifications.map((note, index) => (
            <div key={note.id || index} className="notification-card">
              <h3>{note.title || 'Notification'}</h3>
              <p>{note.message}</p>
              <div className="card-footer">
                <button className="view-btn" onClick={() => openDetails(note)}>View Details</button>
                <span className="time">{note.time}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Overlay */}
      {open && <div className="overlay" onClick={() => setOpen(false)} />}

      {/* Details Modal */}
      {selectedNote && (
        <div className="modal-overlay" onClick={closeDetails}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h2>{selectedNote.title || 'Notification Details'}</h2>

            {selectedNote.message && (
              <p><strong>Message:</strong> {selectedNote.message}</p>
            )}
            {selectedNote.description && (
              <p><strong>Description:</strong> {selectedNote.description}</p>
            )}

            {selectedNote.type?.toLowerCase() === 'handover' && selectedNote.itemStatus && (
              <p><strong>Item Status:</strong> {selectedNote.itemStatus}</p>
            )}

            {selectedNote.type?.toLowerCase() === 'claim' && (
              <>
                {selectedNote.employeeId && (
                  <p><strong>Employee ID:</strong> {selectedNote.employeeId}</p>
                )}
                {selectedNote.employeeName && (
                  <p><strong>Employee Name:</strong> {selectedNote.employeeName}</p>
                )}
              </>
            )}

            {selectedNote.type?.toLowerCase() === 'handover' && (
              <>
                {selectedNote.pickupMessage && (
                  <p><strong>Pickup Message:</strong> {selectedNote.pickupMessage}</p>
                )}
                {selectedNote.securityId && (
                  <p><strong>Security ID:</strong> {selectedNote.securityId}</p>
                )}
                {selectedNote.securityName && (
                  <p><strong>Security Name:</strong> {selectedNote.securityName}</p>
                )}
              </>
            )}

            {selectedNote.photo && (
              <img src={selectedNote.photo} alt="Proof" className="proof-image" />
            )}

            <button className="view-btn" onClick={closeDetails}>OK</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default NotificationPanel;
