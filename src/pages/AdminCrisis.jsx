import React, { useState, useEffect } from 'react';
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
    const [searchTerm, setSearchTerm] = useState('');
    const [filterLevel, setFilterLevel] = useState('all');
    const [filterStatus, setFilterStatus] = useState('all');
    const [operatingUserId, setOperatingUserId] = useState(null);

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
        const matchesSearch = user.username?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                             user.nickname?.toLowerCase().includes(searchTerm.toLowerCase());
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
                            type="text"
                            placeholder="搜索用户名..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="admin-search-input"
                        />
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

                {/* 错误提示 */}
                {error && (
                    <div className="admin-error-message">
                        {error}
                    </div>
                )}

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