import React, { useRef, useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Testimonials from '../components/Testimonials';
import Navbar from '../components/Navbar';
import '../styles/HomePage.css';
import '../styles/About.css';
import backgroundImage from '../assets/img.png';

import signupImg from '../assets/signup.jpg';
import notifyImg from '../assets/notify.jpg';
import listoutImg from '../assets/listout.jpg';
import teamImg from '../assets/team.jpg';

function HomePage() {
  const navigate = useNavigate();
  const location = useLocation();
  const { isLoggedIn, setShowAuthBox } = useAuth();
  const [isScrolledUp, setIsScrolledUp] = useState(false);
  const [activeSection, setActiveSection] = useState('home');
  const [activeTab, setActiveTab] = useState('lost-items');
  const tabRefs = useRef([]);

  const aboutRef = useRef(null);

  const scrollToAbout = () => {
    if (aboutRef.current) {
      aboutRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  };

  const handleProtectedClick = (path) => {
    if (isLoggedIn) {
      setIsScrolledUp(true);
      setTimeout(() => {
        navigate(path);
        setIsScrolledUp(false);
      }, 600);
    } else {
      setShowAuthBox(true);
    }
  };

  const handleTabClick = (tabName, path) => {
    setActiveTab(tabName);
    handleProtectedClick(path);
  };

  useEffect(() => {
    const activeTabIndex = ['lost-items', 'found-items', 'my-activity'].indexOf(activeTab);
    const activeTabElement = tabRefs.current[activeTabIndex];

    if (activeTabElement) {
      const tabWidth = activeTabElement.offsetWidth;
      const tabLeft = activeTabElement.offsetLeft;

      const slider = document.querySelector('.tab-slider');
      if (slider) {
        slider.style.width = `${tabWidth}px`;
        slider.style.left = `${tabLeft}px`;
      }
    }
  }, [activeTab]);

  return (
    <div className={`homepage-container ${isScrolledUp ? 'scrolled-up' : ''}`}>
      <header
        className="App-header"
        style={{
          backgroundImage: `url(${backgroundImage})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
        }}
      >
        <Navbar onAboutClick={scrollToAbout} activeSection={activeSection} />

        <div className="hero-text">
          <h1 className="main-title">Foundly</h1>
          <div className="subtitle">
            <span className="dot-label lost">Lost It</span>
            <span className="dot-label list">List It</span>
            <span className="dot-label find">Find It</span>
          </div>
          <div className="center-report">
            <button className="report-btn" onClick={() => handleProtectedClick('/report')}>
              Report ➜
            </button>
          </div>
        </div>

        <div className="tab-buttons">
          <button
            ref={(el) => (tabRefs.current[0] = el)}
            className={`tab ${activeTab === 'lost-items' ? 'active' : ''}`}
            onClick={() => handleTabClick('lost-items', '/lost-items')}
          >
            Lost Items
          </button>
          <button
            ref={(el) => (tabRefs.current[1] = el)}
            className={`tab ${activeTab === 'found-items' ? 'active' : ''}`}
            onClick={() => handleTabClick('found-items', '/found-items')}
          >
            Found Items
          </button>
          <button
            ref={(el) => (tabRefs.current[2] = el)}
            className={`tab ${activeTab === 'my-activity' ? 'active' : ''}`}
            onClick={() => handleTabClick('my-activity', '/my-activity')}
          >
            My Activity
          </button>

          <div className="tab-slider"></div>
        </div>
      </header>

      <section ref={aboutRef} className="about-us-page">
        <div className="about-flow">
          <div className="text-section right">
            <h2>What is Foundly?</h2>
            <p>
              Foundly is a smart and secure lost & found item management system built to reconnect people with their misplaced belongings.
            </p>
            <h2>Why Foundly?</h2>
            <p>
              Losing something shouldn’t feel like the end of the world — and finding something shouldn’t be a dead end.
            </p>
          </div>

          <div className="roadmap-vertical">
            <div className="roadmap-step">
              <img src={signupImg} alt="Sign up" />
              <div className="step-label">Sign up</div>
              <div className="curve-line vertical"></div>
            </div>
            <div className="roadmap-step">
              <img src={listoutImg} alt="List out" />
              <div className="step-label">List out</div>
              <div className="curve-line vertical"></div>
            </div>
            <div className="roadmap-step">
              <img src={notifyImg} alt="Get notified" />
              <div className="step-label">Get notified</div>
            </div>
          </div>

          <div className="features">
            <h2>Key Features</h2>
            <ul>
              <li>📋 Report Items</li>
              <li>👥 Role-Based Access</li>
              <li>🔔 Smart Notifications</li>
              <li>🤝 Claim & Handover</li>
              <li>📊 Dashboard Analytics</li>
              <li>🔍 Filter & Search</li>
            </ul>
          </div>

          <div className="team">
            <h2>Meet the Team</h2>
            <p>
              We are a group of passionate developers and problem-solvers...
            </p>
            <img src={teamImg} alt="Meet the Team" />
          </div>
        </div>
      </section>

      <section className="content-section">
        <h2 className="section-heading">What users say</h2>
        <Testimonials />
      </section>

      <footer className="footer">
        <div className="footer-content">
          <div className="footer-logo">
            <span className="logo-f">F</span>
            <span className="logo-icon">🔍</span>
            <span className="logo-rest">ound</span>
            <span className="logo-l">l</span>
            <span className="logo-rest">y</span>
          </div>

          <div className="footer-links">
            <a href="/">Home</a>
            <a href="/dashboard">Dashboard</a>
            <a href="#about">About us</a>
            <a href="/notifications">Notifications</a>
          </div>

          <div className="footer-contact">
            <p>Contact : +91 8734629540</p>
            <p>Email : support@insightglobal.com</p>
          </div>
        </div>
      </footer>
    </div>
  );
}

export default HomePage;
