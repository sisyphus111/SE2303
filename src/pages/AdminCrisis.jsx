import React, { useState, useEffect, useRef } from 'react';
import { 
  WarningOutlined, 
  PhoneOutlined, 
  CheckCircleOutlined,
  EyeOutlined,
  SearchOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import { getCrisisUsers, markCrisisHandled } from '../service/adminService';
import '../css/AdminCrisis.css';

const AdminCrisis = () => {
    const [crisisUsers, setCrisisUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState(''); // 输入框的值
    const [activeSearchTerm, setActiveSearchTerm] = useState(''); // 实际用于搜索的值
    const [filterLevel, setFilterLevel] = useState('all');
    const [filterStatus, setFilterStatus] = useState('all');
    const [operatingUserId, setOperatingUserId] = useState(null);
    const searchInputRef = useRef(null);
    const clearButtonRef = useRef(null);

    // 处理搜索回车键
    const handleSearchKeyPress = (e) => {
        if (e.key === 'Enter') {
            setActiveSearchTerm(searchTerm.trim());
        }
    };

    // 清除搜索
    const handleClearSearch = () => {
        setSearchTerm('');
        setActiveSearchTerm('');
    };

    // 动态调整清除按钮位置
    const updateClearButtonPosition = () => {
        if (searchInputRef.current && clearButtonRef.current && activeSearchTerm) {
            const input = searchInputRef.current;
            const clearBtn = clearButtonRef.current;
            
            // 创建临时元素来测量文字宽度
            const canvas = document.createElement('canvas');
            const context = canvas.getContext('2d');
            const computedStyle = window.getComputedStyle(input);
            context.font = `${computedStyle.fontSize} ${computedStyle.fontFamily}`;
            
            // 使用当前输入框中显示的文字宽度
            const displayedText = searchTerm || activeSearchTerm;
            const textWidth = context.measureText(displayedText).width;
            
            // 计算按钮位置（左边距 + 搜索图标宽度 + 文字宽度 + 一点间隙）
            const leftPosition = 40 + textWidth + 8;
            
            // 确保按钮不超出输入框右边界
            const maxLeft = input.offsetWidth - 32;
            const finalLeft = Math.min(leftPosition, maxLeft);
            
            clearBtn.style.left = `${finalLeft}px`;
        }
    };

    // 监听搜索词变化，更新清除按钮位置
    useEffect(() => {
        updateClearButtonPosition();
    }, [searchTerm, activeSearchTerm]);

    // 获取危险用户列表
    const fetchCrisisUsers = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await getCrisisUsers();
            if (response.success) {
                setCrisisUsers(response.data || []);
            } else {
                setError(response.message || '获取危险用户列表失败');
            }
        } catch (err) {
            console.error('获取危险用户列表失败:', err);
            setError('获取危险用户列表失败');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCrisisUsers();
    }, []);

    // 标记为已处理
    const handleMarkHandled = async (userId, action) => {
        try {
            setOperatingUserId(userId);
            const response = await markCrisisHandled(userId, action);
            if (response.success) {
                setCrisisUsers(crisisUsers.map(user => 
                    user.id === userId 
                        ? { ...user, status: action, handledAt: new Date().toISOString() }
                        : user
                ));
                console.log(`用户 ${userId} 已标记为${action === 'contacted' ? '已联系' : '已解决'}`);
            } else {
                setError(response.message || '更新状态失败');
            }
        } catch (err) {
            console.error('更新状态失败:', err);
            setError('更新状态失败');
        } finally {
            setOperatingUserId(null);
        }
    };

    // 过滤用户列表
    const filteredUsers = crisisUsers.filter(user => {
        const matchesSearch = user.username?.toLowerCase().includes(activeSearchTerm.toLowerCase()) ||
                             user.nickname?.toLowerCase().includes(activeSearchTerm.toLowerCase());
        const matchesLevel = filterLevel === 'all' || user.riskLevel === filterLevel;
        const matchesStatus = filterStatus === 'all' || user.status === filterStatus;
        return matchesSearch && matchesLevel && matchesStatus;
    });

    return (
        <div className="admin-crisis-container">
            <div className="admin-card">
                <div className="admin-card-title">
                    <WarningOutlined />
                    危险用户监控
                </div>
                
                {/* 控制区域 */}
                <div className="admin-crisis-controls">
                    <div className="admin-search-box">
                        <SearchOutlined className="admin-search-icon" />
                        <input
                            ref={searchInputRef}
                            type="text"
                            placeholder="搜索用户名... (按回车键搜索)"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            onKeyPress={handleSearchKeyPress}
                            className="admin-search-input"
                        />
                        {activeSearchTerm && (
                            <button 
                                ref={clearButtonRef}
                                onClick={handleClearSearch}
                                className="admin-search-clear-btn"
                                title="清除搜索"
                            >
                                ×
                            </button>
                        )}
                    </div>
                    
                    <div className="admin-filter-box">
                        <select 
                            value={filterLevel} 
                            onChange={(e) => setFilterLevel(e.target.value)}
                            className="admin-filter-select"
                        >
                            <option value="all">全部风险等级</option>
                            <option value="high">高风险</option>
                            <option value="medium">中风险</option>
                            <option value="low">低风险</option>
                        </select>
                    </div>
                    
                    <div className="admin-filter-box">
                        <select 
                            value={filterStatus} 
                            onChange={(e) => setFilterStatus(e.target.value)}
                            className="admin-filter-select"
                        >
                            <option value="all">全部状态</option>
                            <option value="pending">待处理</option>
                            <option value="contacted">已联系</option>
                            <option value="resolved">已解决</option>
                        </select>
                    </div>
                    
                    <button 
                        onClick={fetchCrisisUsers}
                        disabled={loading}
                        className="admin-btn admin-btn-primary"
                    >
                        <ReloadOutlined />
                        刷新
                    </button>
                </div>

                {/* 统计信息 */}
                <div className="admin-crisis-stats">
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">总计:</span>
                        <span className="admin-stat-value">{crisisUsers.length}</span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">高风险:</span>
                        <span className="admin-stat-value admin-stat-high">
                            {crisisUsers.filter(u => u.riskLevel === 'high').length}
                        </span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">待处理:</span>
                        <span className="admin-stat-value admin-stat-pending">
                            {crisisUsers.filter(u => u.status === 'pending').length}
                        </span>
                    </div>
                </div>

                {/* 用户列表 */}
                <div className="admin-crisis-list">
                    {loading ? (
                        <div className="admin-loading">
                            <div className="admin-loading-spinner"></div>
                            <span>加载中...</span>
                        </div>
                    ) : filteredUsers.length === 0 ? (
                        <div className="admin-empty-state">
                            <WarningOutlined />
                            <p>没有找到匹配的危险用户</p>
                        </div>
                    ) : (
                        filteredUsers.map(user => (
                            <div key={user.id} className="admin-crisis-item">
                                <div className="admin-crisis-user">
                                    <span className="admin-user-username">{user.username}</span>
                                    <span className={`admin-risk-badge ${user.riskLevel}`}>
                                        {user.riskLevel === 'high' ? '高风险' : 
                                         user.riskLevel === 'medium' ? '中风险' : '低风险'}
                                    </span>
                                </div>
                                
                                <div className="admin-crisis-info">
                                    <div>风险评分: {user.riskScore}/100</div>
                                    <div>检测时间: {new Date(user.detectedAt).toLocaleDateString()}</div>
                                </div>
                                
                                <div className="admin-crisis-actions">
                                    {user.status === 'pending' && (
                                        <>
                                            <button
                                                onClick={() => handleMarkHandled(user.id, 'contacted')}
                                                disabled={operatingUserId === user.id}
                                                className="admin-btn admin-btn-primary"
                                            >
                                                <PhoneOutlined />
                                                已联系
                                            </button>
                                            <button
                                                onClick={() => handleMarkHandled(user.id, 'resolved')}
                                                disabled={operatingUserId === user.id}
                                                className="admin-btn admin-btn-success"
                                            >
                                                <CheckCircleOutlined />
                                                已解决
                                            </button>
                                        </>
                                    )}
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
};


export default AdminCrisis; 