import React, {useState, useEffect, useRef} from 'react';
import '../css/CommunityHeader.css';
import {useNavigate} from 'react-router-dom';

function CommunityHeader() {
    const [showSearch, setShowSearch] = useState(false);
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const searchRef = useRef(null);
    const navigate = useNavigate();

    const handleSearch = (query) => {
        setSearchQuery(query);
        // 这里可以添加实际的搜索逻辑
        // 模拟搜索结果
        if (query.trim()) {
            setSearchResults([
                {id: 1, title: '关于树洞社区的使用指南', content: '欢迎来到树洞社区...'},
                {id: 2, title: '分享你的故事', content: '在这里分享你的想法...'},
                {id: 3, title: '社区规则', content: '请遵守社区规则...'}
            ]);
        } else {
            setSearchResults([]);
        }
    };

    const toggleSearch = () => {
        setShowSearch(!showSearch);
        if (!showSearch) {
            setSearchQuery('');
            setSearchResults([]);
        }
    };

    const closeSearch = () => {
        setShowSearch(false);
        setSearchQuery('');
        setSearchResults([]);
    };

    // 监听点击事件，点击搜索区域外关闭搜索栏
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (searchRef.current && !searchRef.current.contains(event.target)) {
                closeSearch();
            }
        };

        if (showSearch) {
            document.addEventListener('mousedown', handleClickOutside);
        }

        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [showSearch]);

    return (
        <header className="community-header">
            <div className="header-content">
                {/* 只保留右侧区域：搜索和头像 */}
                <div className="right-section">
                    <div className="search-section" ref={searchRef}>
                        <button
                            className="search-toggle-btn"
                            onClick={toggleSearch}
                        >
                            🔍
                        </button>

                        {showSearch && (
                            <div className="search-container">
                                <input
                                    type="text"
                                    placeholder="搜索帖子..."
                                    value={searchQuery}
                                    onChange={(e) => handleSearch(e.target.value)}
                                    className="search-input"
                                />

                                {searchResults.length > 0 && (
                                    <div className="search-results">
                                        {searchResults.map((result) => (
                                            <div key={result.id} className="search-result-item">
                                                <h4>{result.title}</h4>
                                                <p>{result.content}</p>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        )}
                    </div>

                    <div className="avatar" onClick={() => navigate('/community_user_page')} style={{cursor: 'pointer'}}>
                        <div className="avatar-img">👤</div>
                    </div>
                </div>
            </div>
        </header>
    );
}

export default CommunityHeader;