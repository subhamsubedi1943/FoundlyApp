import React, { useEffect, useState } from "react";
import axios from "axios";
import "../styles/MyActivity.css";

const fallbackImage = 'https://via.placeholder.com/300x200';

const ActivityCard = ({ item, onTransactionUpdate }) => {
  const [collected, setCollected] = useState(item.requesterCompleted === true);
  const [status, setStatus] = useState(item.transactionStatus);
  const [handoverToSecurity, setHandoverToSecurity] = useState(item.handedOverToSecurity);

  useEffect(() => {
    setCollected(item.requesterCompleted === true);
    setStatus(item.transactionStatus);
    setHandoverToSecurity(item.handedOverToSecurity);
  }, [item]);

  const handleCollectedClick = async () => {
    try {
      console.log("Collected button clicked!");

      const response = await axios.put(
        `http://localhost:8080/api/transactions/requester-completed/${item.transactionId}`
      );

      if (response.data) {
        console.log("Marked as completed:", response.data);
        setCollected(true);
        setStatus(response.data.transactionStatus || "PENDING_COMPLETION");

        if (onTransactionUpdate) {
          onTransactionUpdate(); // Refresh parent component
        }
      }
    } catch (error) {
      console.error("Error marking as collected:", error);
      alert("Something went wrong. Please try again.");
    }
  };

  const getStatusClass = (status) => {
    switch (status) {
      case 'Not Found': return 'status-missing';
      case 'With Security': return 'status-security';
      case 'Received': return 'status-received';
      case 'With Finder': return 'status-finder';
      case 'Requested':
      case 'PENDING':
      case 'PENDING_COMPLETION': return 'status-pending';
      case 'COMPLETED': return 'status-completed';
      default: return 'status-missing';
    }
  };

  const getBorderClass = (type) => {
    switch (type?.toUpperCase()) {
      case 'LOST': return 'border-lost';
      case 'FOUND': return 'border-found';
      case 'CLAIM': return 'border-claim';
      case 'HANDOVER': return 'border-handover';
      default: return '';
    }
  };

  const shouldDisableButton = collected || status === 'COMPLETED' || (handoverToSecurity && status !== 'COMPLETED');

  return (
    <div className={`flip-card ${getBorderClass(item.itemType)}`}>
      <div className="flip-inner">

        {/* Front Side */}
        <div className="flip-front">
          <div className="image-container">
            <img 
              src={item.photo || item.imageUrl || fallbackImage} 
              alt="Activity Item" 
            />
            <div className={`status-tag ${getStatusClass(status)}`}>
              {status}
            </div>
          </div>
          <div className="card-info">
            <div className="card-title">{item.itemName}</div>
            <div className="card-subinfo">{item.location}</div>
          </div>
        </div>

        {/* Back Side */}
        <div className="flip-back">
          <div className="back-heading">{item.itemName}</div>

          <div className="info-row">
            <span>Category:</span> {item.category}
          </div>

          <div className="info-row">
            <span>Date:</span> {item.dateReported}
          </div>

          {item.description && (
            <div className="description">
              <span>Description:</span>
              <p>{item.description}</p>
            </div>
          )}

          {item.securityName && item.securityId && (
            <div className="info-row">
              <span>Security:</span> {item.securityName} ({item.securityId})
            </div>
          )}

          {item.pickupMessage && (
            <div className="info-row">
              <span>Pickup Message:</span> {item.pickupMessage}
            </div>
          )}

          {(item.itemType === 'CLAIM' || item.itemType === 'HANDOVER') && (
            <button
              className={`handover-btn ${shouldDisableButton ? 'disabled' : 'active'}`}
              onClick={handleCollectedClick}
              disabled={shouldDisableButton}
            >
              {collected || status === 'COMPLETED' ? "Completed" : "Mark as Collected"}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default ActivityCard;
