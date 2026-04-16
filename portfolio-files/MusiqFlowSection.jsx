import React, { useState, useEffect } from 'react';
import './MusiqFlowSection.css';

const MusiqFlowSection = () => {
  const [downloadCount, setDownloadCount] = useState(0);

  useEffect(() => {
    // Simulate download count (you can connect to real analytics later)
    setDownloadCount(Math.floor(Math.random() * 500) + 100);
  }, []);

  const shareApp = () => {
    if (navigator.share) {
      navigator.share({
        title: 'MusiqFlow Lite - Music Streaming App',
        text: 'Check out this awesome music streaming app by Sudhanshu! 🎵',
        url: window.location.href
      });
    } else {
      navigator.clipboard.writeText(window.location.href);
      alert('Link copied to clipboard! 📋');
    }
  };

  const handleDownload = () => {
    // Track download (you can add analytics here)
    console.log('APK Download started');
  };

  return (
    <section className="musiqflow-section" id="musiqflow-app">
      <div className="container">
        <div className="section-header">
          <h2>🎵 MusiqFlow Lite</h2>
          <p className="section-subtitle">Your Personal Music Streaming Companion</p>
        </div>
        
        <div className="app-showcase">
          <div className="app-info">
            <div className="app-description">
              <h3>✨ Features</h3>
              <ul className="features-list">
                <li>🎵 <strong>YouTube Music Streaming</strong> - Access millions of songs</li>
                <li>🔍 <strong>Smart Search</strong> - Find songs, artists, and albums instantly</li>
                <li>📱 <strong>Material Design UI</strong> - Clean and intuitive interface</li>
                <li>⚡ <strong>Fast & Lightweight</strong> - Optimized performance</li>
                <li>🎧 <strong>Background Playback</strong> - Music continues while multitasking</li>
                <li>🔄 <strong>Auto Updates</strong> - Always get the latest features</li>
              </ul>
            </div>
            
            <div className="app-stats">
              <div className="stat">
                <span className="stat-number">{downloadCount}+</span>
                <span className="stat-label">Downloads</span>
              </div>
              <div className="stat">
                <span className="stat-number">4.8★</span>
                <span className="stat-label">Rating</span>
              </div>
              <div className="stat">
                <span className="stat-number">4.3MB</span>
                <span className="stat-label">Size</span>
              </div>
            </div>
            
            <div className="download-section">
              <a 
                href="/downloads/musiqflow-lite-v1.0.0.apk" 
                className="download-btn primary"
                download="MusiqFlow-Lite.apk"
                onClick={handleDownload}
              >
                📱 Download APK
                <span className="btn-subtitle">Android 8.0+</span>
              </a>
              
              <button className="share-btn secondary" onClick={shareApp}>
                🔗 Share App
              </button>
            </div>
            
            <div className="tech-stack">
              <h4>🛠️ Built With</h4>
              <div className="tech-tags">
                <span className="tech-tag">Kotlin</span>
                <span className="tech-tag">Jetpack Compose</span>
                <span className="tech-tag">ExoPlayer</span>
                <span className="tech-tag">Material3</span>
                <span className="tech-tag">Coroutines</span>
              </div>
            </div>
          </div>
          
          <div className="app-preview">
            <div className="phone-mockup">
              <div className="phone-frame">
                <div className="phone-screen">
                  <img 
                    src="/images/musiqflow-screen1.jpg" 
                    alt="MusiqFlow Home Screen" 
                    className="screenshot active"
                  />
                </div>
              </div>
            </div>
            
            <div className="screenshot-gallery">
              <img 
                src="/images/musiqflow-screen1.jpg" 
                alt="Home Screen" 
                className="thumbnail"
              />
              <img 
                src="/images/musiqflow-screen2.jpg" 
                alt="Player Screen" 
                className="thumbnail"
              />
            </div>
          </div>
        </div>

        <div className="app-footer">
          <p className="disclaimer">
            ⚠️ <strong>Note:</strong> This app is for educational purposes. 
            Enable "Install from Unknown Sources" in Android settings to install.
          </p>
        </div>
      </div>
    </section>
  );
};

export default MusiqFlowSection;