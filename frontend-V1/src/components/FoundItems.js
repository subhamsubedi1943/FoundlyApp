import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../styles/FoundItems.css';
import ClaimModal from './ClaimModal';
import { FiFilter } from 'react-icons/fi';

const FoundItems = () => {
  const [items, setItems] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [filterOpen, setFilterOpen] = useState(false);
  const [filters, setFilters] = useState({
    category: '',
    location: '',
    date: '',
  });
  const user = JSON.parse(localStorage.getItem("user"));
  const requesterId = user?.requesterId;

  const [selectedItem, setSelectedItem] = useState(null);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    const fetchFoundItems = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/items/found-items');
        // Sort items by dateReported descending to show latest first
        const sortedItems = response.data.sort((a, b) => new Date(b.dateReported) - new Date(a.dateReported));
        setItems(sortedItems);
      } catch (error) {
        console.error('Error fetching found items:', error);
      }
    };

    fetchFoundItems();
  }, []);

  const handleFilterChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value });
  };

  const applyFilters = () => {
    setFilterOpen(false);
  };

  const handleClaimClick = (item) => {
    setSelectedItem(item);
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setSelectedItem(null);
    setShowModal(false);
  };

  const filteredItems = items.filter((item) => {
    const matchesSearch = item.itemName.toLowerCase().includes(searchQuery.toLowerCase());
    const matchesCategory = !filters.category || item.categoryName === filters.category;
    const matchesLocation = !filters.location || item.location === filters.location;
    const matchesDate = !filters.date || item.dateReported === filters.date;
    return matchesSearch && matchesCategory && matchesLocation && matchesDate;
  });

  return (
    <div className="items-page">
      <h1 className="title">Found Item Reports</h1>

      <div className="search-container">
        <div className="search-icon"></div>
        <input
          type="text"
          placeholder="Search found items..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          className="search-input"
        />
        <button
          onClick={() => setFilterOpen(!filterOpen)}
          className="filter-icon-button"
          title="Toggle filters"
        >
          <FiFilter className="filter-icon" />
        </button>
      </div>

      {filterOpen && (
        <div className="filters-inline">
          <select name="category" value={filters.category} onChange={handleFilterChange}>
            <option value="">Category</option>
            <option value="Phone">Phone</option>
            <option value="Wallet">Wallet</option>
            <option value="Watch">Watch</option>
            <option value="Bags">Bags</option>
            <option value="Electronics">Electronics</option>
            <option value="Documents">Documents</option>
            <option value="Keys">Keys</option>
            <option value="Fashion accessories">Fashion accessories</option>
            <option value="Jewellery">Jewellery</option>
            <option value="Others">Others</option>
          </select>
          <input
            type="text"
            name="location"
            placeholder="Location"
            value={filters.location}
            onChange={handleFilterChange}
          />
          <input
            type="date"
            name="date"
            value={filters.date}
            onChange={handleFilterChange}
          />
          <button onClick={applyFilters}>Apply</button>
        </div>
      )}

      <div className="grid-container">
        {filteredItems.slice(0, 10).map((item, index) => (
          <div className="flip-card" key={index}>
            <div className="flip-card-inner">
              <div className="flip-card-front">
                {item.imageUrl && (
                  <img src={item.imageUrl} alt={item.itemName} className="card-image" />
                )}
                <h2 className="card-title">{item.itemName}</h2>
              </div>
              <div className="flip-card-back">
                <table className="item-details-table">
                  <tbody>
                    <tr>
                      <td><strong>Category</strong></td>
                      <td>{item.categoryName}</td>
                    </tr>
                    <tr>
                      <td><strong>Location</strong></td>
                      <td>{item.location}</td>
                    </tr>
                    <tr>
                      <td><strong>Date</strong></td>
                      <td>{new Date(item.dateReported).toLocaleDateString()}</td>
                    </tr>
                    <tr>
                      <td><strong>Time</strong></td>
                      <td>{new Date(item.dateReported).toLocaleTimeString()}</td>
                    </tr>
                    <tr>
                      <td colSpan="2"><strong>Description:</strong><br />{item.description}</td>
                    </tr>
                    <tr>
                      <td colSpan="2" style={{ textAlign: 'center' }}>
                        <button className="claim-button" onClick={() => handleClaimClick(item)}>
                          Claim
                        </button>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        ))}
      </div>

      {showModal && selectedItem && (
        <ClaimModal
          itemId={selectedItem.itemId}
          requesterId={selectedItem.requesterId}
          onClose={handleCloseModal}
        />
      )}
    </div>
  );
};

export default FoundItems;