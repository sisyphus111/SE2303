import React, { useState, useEffect } from 'react';
import { 
  UserOutlined, 
  CheckCircleOutlined, 
  StopOutlined,
  SearchOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import { getAllUsers, updateUserStatus } from '../service/adminService';
import '../css/AdminUsers.css';

const AdminUsers = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterStatus, setFilterStatus] = useState('all'); // all, active, inactive
    const [operatingUserId, setOperatingUserId] = useState(null);

    // 获取用户列表
    const fetchUsers = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await getAllUsers();
            setUsers(response);
            
        } catch (err) {
            console.error('获取用户列表失败:', err);
            setError('获取用户列表失败');
        } finally {
            setLoading(false);
        }
    };

    // 页面加载时获取用户列表
    useEffect(() => {
        fetchUsers();
    }, []);

    // 启用/禁用用户
    const handleUserStatusChange = async (userId, newStatus) => {
        try {
            setOperatingUserId(userId);
            const response = await updateUserStatus(userId, newStatus);
            if (response) {
                // 更新本地状态 - 修复数据结构不一致问题
                setUsers(users.map(user => 
                    user.id === userId 
                        ? { ...user, valid: newStatus }
                        : user
                ));
                console.log(`用户 ${userId} 状态已更新为: ${newStatus ? '启用' : '禁用'}`);
            } else {
                setError(response.message || '更新用户状态失败');
            }
        } catch (err) {
            console.error('更新用户状态失败:', err);
            setError('更新用户状态失败');
        } finally {
            setOperatingUserId(null);
        }
    };

    // 过滤用户列表
    const filteredUsers = users.filter(user => {
        const matchesSearch = user.username?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                             user.nickname?.toLowerCase().includes(searchTerm.toLowerCase()) ||
                             user.sjtuId?.toLowerCase().includes(searchTerm.toLowerCase());
        
        const matchesFilter = filterStatus === 'all' || 
                             (filterStatus === 'active' && user.valid) ||
                             (filterStatus === 'inactive' && !user.valid);
        
        return matchesSearch && matchesFilter;
    });

    // 获取用户头像
    const getUserAvatar = (user) => {
        if (user.avatar && user.avatar.includes('http')) {
            return user.avatar;
        }
        return user.username?.[0]?.toUpperCase() || 'U';
    };

    // 格式化注册时间
    const formatDate = (dateString) => {
        if (!dateString) return '未知';
        return new Date(dateString).toLocaleDateString('zh-CN');
    };

    return (
        <div className="admin-users-container">
            <div className="admin-card">
                <div className="admin-card-title">
                    <UserOutlined />
                    用户管理
                </div>
                
                {/* 搜索和过滤 */}
                <div className="admin-users-controls">
                    <div className="admin-search-box">
                        <SearchOutlined className="admin-search-icon" />
                        <input
                            type="text"
                            placeholder="搜索用户名、昵称或学号..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="admin-search-input"
                        />
                    </div>
                    
                    <div className="admin-filter-box">
                        <select 
                            value={filterStatus} 
                            onChange={(e) => setFilterStatus(e.target.value)}
                            className="admin-filter-select"
                        >
                            <option value="all">全部用户</option>
                            <option value="active">启用用户</option>
                            <option value="inactive">禁用用户</option>
                        </select>
                    </div>
                    
                    <button 
                        onClick={fetchUsers}
                        disabled={loading}
                        className="admin-btn admin-btn-primary"
                    >
                        <ReloadOutlined />
                        刷新
                    </button>
                </div>

                {/* 用户统计 */}
                <div className="admin-users-stats">
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">总用户数:</span>
                        <span className="admin-stat-value">{users.length}</span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">启用用户:</span>
                        <span className="admin-stat-value admin-stat-active">
                            {users.filter(u => u.valid).length}
                        </span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">禁用用户:</span>
                        <span className="admin-stat-value admin-stat-inactive">
                            {users.filter(u => !u.valid).length}
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
                <div className="admin-users-list">
                    {loading ? (
                        <div className="admin-loading">
                            <div className="admin-loading-spinner"></div>
                            <span>加载中...</span>
                        </div>
                    ) : filteredUsers.length === 0 ? (
                        <div className="admin-empty-state">
                            <UserOutlined />
                            <p>没有找到匹配的用户</p>
                        </div>
                    ) : (
                        filteredUsers.map(user => (
                            <div key={user.id} className="admin-user-item">
                                <div className="admin-user-avatar">
                                    {typeof getUserAvatar(user) === 'string' && getUserAvatar(user).includes('http') ? (
                                        <img src={getUserAvatar(user)} alt="用户头像" />
                                    ) : (
                                        <span className="admin-user-avatar-text">
                                            {getUserAvatar(user)}
                                        </span>
                                    )}
                                </div>
                                
                                <div className="admin-user-info">
                                    <div className="admin-user-name">
                                        <span className="admin-user-username">{user.username}</span>
                                        {user.nickname && (
                                            <span className="admin-user-nickname">({user.nickname})</span>
                                        )}
                                    </div>
                                    <div className="admin-user-details">
                                        <span className="admin-user-detail">
                                            学号: {user.sjtuId || '未知'}
                                        </span>
                                        <span className="admin-user-detail">
                                            注册时间: {formatDate(user.registrationDate)}
                                        </span>
                                        <span className="admin-user-detail">
                                            身份: {user.isUser ? '普通用户' : '管理员'}
                                        </span>
                                    </div>
                                </div>
                                
                                <div className="admin-user-status">
                                    <span className={`admin-status-badge ${user.valid ? 'active' : 'inactive'}`}>
                                        {user.valid ? '启用' : '禁用'}
                                    </span>
                                </div>
                                
                                <div className="admin-user-actions">
                                    {user.isUser && ( // 只有普通用户可以被启用/禁用
                                        <button
                                            onClick={() => handleUserStatusChange(user.id, !user.valid)}
                                            disabled={operatingUserId === user.id}
                                            className={`admin-btn ${user.valid ? 'admin-btn-danger' : 'admin-btn-success'}`}
                                        >
                                            {operatingUserId === user.id ? (
                                                <div className="admin-btn-loading"></div>
                                            ) : user.valid ? (
                                                <>
                                                    <StopOutlined />
                                                    禁用
                                                </>
                                            ) : (
                                                <>
                                                    <CheckCircleOutlined />
                                                    启用
                                                </>
                                            )}
                                        </button>
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

export default AdminUsers; 