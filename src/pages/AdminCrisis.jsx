import React, { useState, useEffect } from 'react';
import { Pagination } from 'antd';
import { 
  WarningOutlined, 
  PhoneOutlined, 
  CheckCircleOutlined,
  EyeOutlined,
  SearchOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import { 
    getAlertList, 
    updateAlertStatus, 
    addFollowUpNotes,
    getAlertStatistics,
    getAlertTypes,
    getAlertLevels,
    getAlertStatuses
} from '../service/adminAlertService';
import { getUserAdminInfo } from '../service/userService';
import { Modal, Spin } from 'antd';
import '../css/AdminCrisis.css';
import AdminCrisisSearchBar from '../components/AdminCrisisSearchBar';
import AdminCrisisStats from '../components/AdminCrisisStats';
import AdminCrisisList from '../components/AdminCrisisList';
import AdminCrisisPagination from '../components/AdminCrisisPagination';

const AdminCrisis = () => {
    const [alerts, setAlerts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [filterLevel, setFilterLevel] = useState('all');
    const [filterStatus, setFilterStatus] = useState('all');
    const [filterType, setFilterType] = useState('all');
    const [operatingAlertId, setOperatingAlertId] = useState(null);
    const [pagination, setPagination] = useState({
        current: 0,
        pageSize: 10,
        total: 0
    });
    const [alertTypes, setAlertTypes] = useState({});
    const [alertLevels, setAlertLevels] = useState({});
    const [alertStatuses, setAlertStatuses] = useState({});
    const [userDetailModal, setUserDetailModal] = useState({ visible: false, loading: false, user: null });

    // 获取警报列表
    const fetchAlerts = async (page = 0, pageSize = 10) => {
        try {
            setLoading(true);
            setError(null); // 每次请求前清空错误
            
            const params = {
                page,
                size: pageSize,
                ...(searchTerm && { userId: searchTerm }),
                ...(filterStatus !== 'all' && { status: filterStatus }),
                ...(filterLevel !== 'all' && { alertLevel: filterLevel }),
                ...(filterType !== 'all' && { alertType: filterType })
            };
            
            const response = await getAlertList(params);
            const data = response.data || response.object || response;
            if (data && Array.isArray(data.content)) {
                setAlerts(data.content);
                setPagination({
                    current: data.number,
                    pageSize: data.size,
                    total: data.totalElements
                });
            } else {
                setAlerts([]);
                setPagination({
                    current: 0,
                    pageSize: pageSize,
                    total: 0
                });
            }
        } catch (err) {
            setAlerts([]); // 异常时清空
            setError('获取警报列表失败');
        } finally {
            setLoading(false);
        }
    };

    // 获取枚举数据
    const fetchEnums = async () => {
        try {
            const [typesRes, levelsRes, statusesRes] = await Promise.all([
                getAlertTypes(),
                getAlertLevels(),
                getAlertStatuses()
            ]);
            setAlertTypes(typesRes);
            setAlertLevels(levelsRes);
            setAlertStatuses(statusesRes);
        } catch (err) {
            console.error('获取枚举数据失败:', err);
        }
    };

    useEffect(() => {
        fetchEnums();
        fetchAlerts();
    }, []);

    // 当搜索条件或筛选条件改变时重新获取数据
    useEffect(() => {
        fetchAlerts(0, pagination.pageSize);
    }, [searchTerm, filterLevel, filterStatus, filterType]);

    // 更新警报状态
    const handleUpdateStatus = async (alertId, status) => {
        try {
            setOperatingAlertId(alertId);
            const response = await updateAlertStatus(alertId, status);
            if (response.success) {
                setAlerts(alerts.map(alert => 
                    alert.id === alertId 
                        ? { ...alert, status: status }
                        : alert
                ));
            } else {
                setError(response.message || '更新状态失败');
            }
        } catch (err) {
            console.error('更新状态失败:', err);
            setError('更新状态失败');
        } finally {
            setOperatingAlertId(null);
        }
    };

    // 添加回访备注
    const handleAddFollowUpNotes = async (alertId, notes) => {
        try {
            setOperatingAlertId(alertId);
            const response = await addFollowUpNotes(alertId, notes);
            if (response.success) {
                setAlerts(alerts.map(alert => 
                    alert.id === alertId 
                        ? { ...alert, followUpNotes: notes, followUpDate: new Date().toISOString() }
                        : alert
                ));
            } else {
                setError(response.message || '添加回访备注失败');
            }
        } catch (err) {
            console.error('添加回访备注失败:', err);
            setError('添加回访备注失败');
        } finally {
            setOperatingAlertId(null);
        }
    };

    // 查看用户详情
    const handleViewUserDetail = async (userId) => {
        setUserDetailModal({ visible: true, loading: true, user: null });
        try {
            const user = await getUserAdminInfo(userId);
            setUserDetailModal({ visible: true, loading: false, user });
        } catch (err) {
            setUserDetailModal({ visible: true, loading: false, user: { error: '获取用户信息失败' } });
        }
    };

    // 处理分页变化
    const handlePageChange = (page, pageSize) => {
        fetchAlerts(page - 1, pageSize); // 后端从0开始，前端从1开始
    };

    return (
        <div className="admin-crisis-container">
            <div className="admin-card">
                <div className="admin-card-title">
                    <WarningOutlined />
                    危险用户监控
                </div>
                {/* 搜索栏和筛选条件 */}
                <AdminCrisisSearchBar
                    searchTerm={searchTerm}
                    filterLevel={filterLevel}
                    filterType={filterType}
                    filterStatus={filterStatus}
                    alertLevels={alertLevels}
                    alertTypes={alertTypes}
                    alertStatuses={alertStatuses}
                    onSearchTermChange={e => setSearchTerm(e.target.value)}
                    onFilterLevelChange={e => setFilterLevel(e.target.value)}
                    onFilterTypeChange={e => setFilterType(e.target.value)}
                    onFilterStatusChange={e => setFilterStatus(e.target.value)}
                    onRefresh={fetchAlerts}
                    loading={loading}
                    pagination={pagination}
                />
                {/* 统计信息 */}
                <AdminCrisisStats pagination={pagination} alerts={alerts} />
                {/* 警报列表/空状态/加载中/错误 */}
                <AdminCrisisList 
                    alerts={alerts} 
                    loading={loading} 
                    error={error}
                    onUpdateStatus={handleUpdateStatus}
                    onAddFollowUpNotes={handleAddFollowUpNotes}
                    operatingAlertId={operatingAlertId}
                    onViewUserDetail={handleViewUserDetail}
                />
                {/* 分页组件 */}
                <AdminCrisisPagination pagination={pagination} onPageChange={handlePageChange} />
            </div>
            <Modal
              open={userDetailModal.visible}
              title="用户详细信息"
              onCancel={() => setUserDetailModal({ visible: false, loading: false, user: null })}
              footer={null}
              width={400}
            >
              {userDetailModal.loading ? (
                <div style={{ textAlign: 'center', padding: '32px 0' }}><Spin /> 加载中...</div>
              ) : userDetailModal.user && !userDetailModal.user.error ? (
                <div style={{ fontSize: 14 }}>
                  <div><b>用户名：</b>{userDetailModal.user.username}</div>
                  <div><b>真实姓名：</b>{userDetailModal.user.realname}</div>
                  <div><b>手机号：</b>{userDetailModal.user.phone}</div>
                  <div><b>学号：</b>{userDetailModal.user.sjtuId}</div>
                  <div><b>Jaccount：</b>{userDetailModal.user.jaccount}</div>
                  <div><b>注册时间：</b>{userDetailModal.user.registrationDate ? new Date(userDetailModal.user.registrationDate).toLocaleString() : '-'}</div>
                  <div><b>身份：</b>{userDetailModal.user.isUser ? '普通用户' : '管理员'}</div>
                  <div><b>状态：</b>{userDetailModal.user.valid ? '启用' : '禁用'}</div>
                </div>
              ) : (
                <div style={{ color: 'red', textAlign: 'center' }}>{userDetailModal.user?.error || '未找到用户信息'}</div>
              )}
            </Modal>
        </div>
    );
};

export default AdminCrisis; 