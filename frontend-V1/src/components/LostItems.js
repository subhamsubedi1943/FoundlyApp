import React, { useEffect, useState } from 'react';
import axios from 'axios';
import '../styles/LostItems.css';
import { HandoverModal } from './HandoverModal';
import { FiFilter } from 'react-icons/fi';

const LostItems = () => {
  const [items, setItems] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [filterOpen, setFilterOpen] = useState(false);
  const [filters, setFilters] = useState({
    category: '',
    location: '',
    date: ''
  });

  const user = JSON.parse(localStorage.getItem("user"));
  const requesterId = user?.id;

  const [selectedItem, setSelectedItem] = useState(null);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    const fetchLostItems = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/items/lost-items');
        const data = response.data.map(item => ({
          id: item.itemId,
          itemName: item.itemName,
          description: item.description,
          location: item.location,
          categoryName: item.category?.categoryName || 'Uncategorized',
          dateReported: item.dateReported,
          dateLostOrFound: item.dateLostOrFound,
          imageUrl: item.imageUrl,
          itemStatus: item.itemStatus
        }));
        setItems(data);
      } catch (error) {
        console.error('Error fetching lost items:', error);
      }
    };

    fetchLostItems();
  }, []);

  const handleFilterChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value });
  };

  const applyFilters = () => {
    setFilterOpen(false);
  };

  const handleHandoverClick = (item) => {
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
    const matchesLocation = !filters.location || item.location.toLowerCase().includes(filters.location.toLowerCase());
    const matchesDate = !filters.date || item.dateReported.startsWith(filters.date);

    return matchesSearch && matchesCategory && matchesLocation && matchesDate;
  });

  return (
    <div className="items-page">
      <h1 className="title">Lost Item Reports</h1>

      <div className="search-container">
        <input
          type="text"
          placeholder="Search lost items..."
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
            <option value="Fashion accessories">Fashion Accessories</option>
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

      {filteredItems.length > 0 ? (
        <div className="grid-container">
          {filteredItems.slice(0, 10).map((item, index) => (
            <div className="flip-card" key={index}>
              <div className="flip-card-inner">
                <div className="flip-card-front">
                {item.imageUrl && (
                  <img src={item.imageUrl} alt={item.itemName} className="card-image" />
                )}
                  <p className="card-category">{item.categoryName}</p>
                  <h2 className="card-title">{item.itemName}</h2>
                  <p className="card-location">{item.location}</p>
                  <p className="card-date">{new Date(item.dateReported).toLocaleDateString()}</p>
                </div>
                <div className="flip-card-back">
                  <p className="card-description">{item.description}</p>
                  <button className="claim-button" onClick={() => handleHandoverClick(item)}>
                    Handover
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <p className="no-results">No items match your search or filters.</p>
      )}

      {showModal && selectedItem && (
        <HandoverModal
          isOpen={showModal}
          onClose={handleCloseModal}
          onSubmit={handleCloseModal}
          itemId={selectedItem.id}
          requesterId={requesterId}
        />
      )}
    </div>
  );
};

export default LostItems;
