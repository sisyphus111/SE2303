import React, { useState, useEffect, useRef } from 'react';
import { 
  UserOutlined, 
  CheckCircleOutlined, 
  StopOutlined,
  SearchOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import { Avatar } from 'antd';
import { getAllUsers, updateUserStatus } from '../service/adminService';
import '../css/AdminUsers.css';

const AdminUsers = () => {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState(''); // 输入框的值
    const [activeSearchTerm, setActiveSearchTerm] = useState(''); // 实际用于搜索的值
    const [filterStatus, setFilterStatus] = useState('all'); // all, active, inactive
    const [operatingUserId, setOperatingUserId] = useState(null);
    const searchInputRef = useRef(null);
    const clearButtonRef = useRef(null);

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

    // 过滤用户列表
    const filteredUsers = users.filter(user => {
        const matchesSearch = user.username?.toLowerCase().includes(activeSearchTerm.toLowerCase()) ||
                             user.nickname?.toLowerCase().includes(activeSearchTerm.toLowerCase()) ||
                             user.sjtuId?.toLowerCase().includes(activeSearchTerm.toLowerCase());
        
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
                            ref={searchInputRef}
                            type="text"
                            placeholder="搜索用户名、昵称或学号..."
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
                        <span className="admin-stat-label">总用户数</span>
                        <span className="admin-stat-value">{users.length}</span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">启用用户</span>
                        <span className="admin-stat-value admin-stat-active">
                            {users.filter(u => u.valid).length}
                        </span>
                    </div>
                    <div className="admin-stat-item">
                        <span className="admin-stat-label">禁用用户</span>
                        <span className="admin-stat-value admin-stat-inactive">
                            {users.filter(u => !u.valid).length}
                        </span>
                    </div>
                </div>

                {/* 搜索状态 */}
                {activeSearchTerm && (
                    <div className="admin-search-status">
                        正在搜索: "{activeSearchTerm}"
                    </div>
                )}

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
                                    {user.avatar && user.avatar.includes('http') ? (
                                        <img 
                                            src={user.avatar} 
                                            alt="用户头像"
                                            onError={(e) => {
                                                // 图片加载失败时，隐藏img元素，显示Avatar组件
                                                e.target.style.display = 'none';
                                                e.target.nextSibling.style.display = 'block';
                                            }}
                                        />
                                    ) : null}
                                    {(!user.avatar || !user.avatar.includes('http')) ? (
                                        <Avatar 
                                            size={64} 
                                            icon={<UserOutlined />}
                                            style={{ backgroundColor: '#1890ff' }}
                                        >
                                            {user.username?.[0]?.toUpperCase() || 'U'}
                                        </Avatar>
                                    ) : (
                                        <Avatar 
                                            size={64} 
                                            icon={<UserOutlined />}
                                            style={{ 
                                                backgroundColor: '#1890ff',
                                                display: 'none' // 默认隐藏，只在图片加载失败时显示
                                            }}
                                        >
                                            {user.username?.[0]?.toUpperCase() || 'U'}
                                        </Avatar>
                                    )}
                                </div>
                                
                                <div className="admin-user-info">
                                    <div className="admin-user-name">
                                        <span className="admin-user-username">{`${user.username}（真实姓名:${user.realname} 手机号码:${user.phone}）`}</span>
                                        {user.nickname && (
                                            <span className="admin-user-nickname">({user.nickname})</span>
                                        )}
                                    </div>
                                    <div className="admin-user-details">
                                        <span className="admin-user-detail">
                                            学号: {user.sjtuId || '未知'}
                                        </span>
                                        <span className="admin-user-detail">
                                            Jaccount: {user.jaccount || '未知'}
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