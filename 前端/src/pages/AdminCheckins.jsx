import React, { useState, useEffect } from 'react';
import { 
  CheckCircleOutlined, 
  SearchOutlined, 
  EyeOutlined, 
  DeleteOutlined,
  CalendarOutlined,
  UserOutlined,
  FilterOutlined,
  BarChartOutlined,
  SmileOutlined,
  MehOutlined,
  FrownOutlined,
  ClockCircleOutlined,
  TrophyOutlined
} from '@ant-design/icons';
import { fetchAllCheckins, fetchMoodStats, fetchMoodLabels, getMoodLabelDisplay, getMoodCategoryStats, deleteCheckin } from '../service/adminCheckInService';
import '../css/AdminCheckins.css';

const AdminCheckins = () => {
  const [checkins, setCheckins] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [moodFilter, setMoodFilter] = useState('all');
  const [dateFilter, setDateFilter] = useState('all');
  const [selectedCheckin, setSelectedCheckin] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [checkinsPerPage] = useState(15);
  const [showStats, setShowStats] = useState(false);
  const [moodLabels, setMoodLabels] = useState([]);



  useEffect(() => {
    // 调用后端API获取打卡数据和心情标签
    const fetchData = async () => {
      setLoading(true);
      try {
        // 并行获取打卡数据和心情标签
        const [checkinsData, labelsData] = await Promise.all([
          fetchAllCheckins(),
          fetchMoodLabels()
        ]);
        
        setCheckins(checkinsData || []);
        setMoodLabels(labelsData || []);
      } catch (error) {
        console.error('获取数据失败:', error);
        setCheckins([]);
        setMoodLabels([]);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  // 筛选打卡记录
  const filteredCheckins = checkins.filter(checkin => {
    const matchesSearch = checkin.userId.toString().includes(searchTerm) ||
                         (checkin.diaryDescription && checkin.diaryDescription.toLowerCase().includes(searchTerm.toLowerCase()));
    
    // 根据moodLabels判断心情类型 - 匹配任何一个标签，没有心情时不匹配任何筛选
    const checkinMoods = checkin.moodLabels && checkin.moodLabels.length > 0 ? checkin.moodLabels : [];
    const matchesMood = moodFilter === 'all' || checkinMoods.includes(moodFilter);
    
    let matchesDate = true;
    if (dateFilter !== 'all') {
      const checkinDate = new Date(checkin.date);
      const today = new Date();
      const daysDiff = Math.floor((today - checkinDate) / (1000 * 60 * 60 * 24));
      
      switch (dateFilter) {
        case 'today':
          matchesDate = daysDiff === 0;
          break;
        case 'week':
          matchesDate = daysDiff <= 7;
          break;
        case 'month':
          matchesDate = daysDiff <= 30;
          break;
        default:
          matchesDate = true;
      }
    }
    
    return matchesSearch && matchesMood && matchesDate;
  });

  // 分页
  const indexOfLastCheckin = currentPage * checkinsPerPage;
  const indexOfFirstCheckin = indexOfLastCheckin - checkinsPerPage;
  const currentCheckins = filteredCheckins.slice(indexOfFirstCheckin, indexOfLastCheckin);
  const totalPages = Math.ceil(filteredCheckins.length / checkinsPerPage);

  // 统计数据
  const stats = {
    total: filteredCheckins.length,
    today: (() => {
      const today = new Date();
      const todayStart = new Date(today.getFullYear(), today.getMonth(), today.getDate());
      const todayEnd = new Date(today.getFullYear(), today.getMonth(), today.getDate() + 1);
      return filteredCheckins.filter(c => {
        const checkinDate = new Date(c.date || c.checkinDate || c.createdAt);
        return checkinDate >= todayStart && checkinDate < todayEnd;
      }).length;
    })(),
    happy: filteredCheckins.filter(c => {
      const moods = c.moodLabels && c.moodLabels.length > 0 ? c.moodLabels : [];
      return moods.some(mood => ['开心', '兴奋', '满足', '宁静', '感恩', '自信', '充满活力', '乐观'].includes(mood));
    }).length,
    sad: filteredCheckins.filter(c => {
      const moods = c.moodLabels && c.moodLabels.length > 0 ? c.moodLabels : [];
      return moods.some(mood => ['悲伤', '愤怒', '焦虑', '压力', '疲惫', '孤独', '沮丧', '担心', '失望', '烦躁', '困惑'].includes(mood));
    }).length,
    averageMood: (() => {
      // 只计算有评分的打卡记录
      const scoredCheckins = filteredCheckins.filter(c => c.score && c.score > 0);
      return scoredCheckins.length > 0 ? 
        (scoredCheckins.reduce((sum, c) => sum + c.score, 0) / scoredCheckins.length).toFixed(1) : 0;
    })()
  };

  // 心情分布统计 - 动态统计所有出现的心情标签（基于筛选结果）
  const moodDistribution = {};
  filteredCheckins.forEach(checkin => {
    if (checkin.moodLabels && checkin.moodLabels.length > 0) {
      checkin.moodLabels.forEach(mood => {
        moodDistribution[mood] = (moodDistribution[mood] || 0) + 1;
      });
    }
  });

  // 操作函数
  const handleViewCheckin = (checkin) => {
    setSelectedCheckin(checkin);
    setShowModal(true);
  };

  const handleDeleteCheckin = async (checkinId) => {
    if (window.confirm('确定要删除这条打卡记录吗？此操作不可恢复。')) {
      try {
        await deleteCheckin(checkinId);
        setCheckins(checkins.filter(checkin => checkin.id !== checkinId));
        console.log('打卡记录已删除:', checkinId);
      } catch (error) {
        alert('删除打卡记录失败，请重试！');
        console.error('删除打卡记录失败:', error);
      }
    }
  };

  // 获取心情图标
  const getMoodIcon = (mood) => {
    // 根据中文心情标签返回对应图标
    if (typeof mood === 'string') {
      // 积极情绪
      if (['开心', '兴奋', '满足', '充满活力', '快乐', '愉悦'].includes(mood)) {
        return <SmileOutlined style={{ color: '#52c41a' }} />;
      }
      // 中性情绪
      if (['平静', '一般', '普通', '正常'].includes(mood)) {
        return <MehOutlined style={{ color: '#faad14' }} />;
      }
      // 消极情绪
      if (['难过', '焦虑', '疲惫', '沮丧', '担心', '紧张'].includes(mood)) {
        return <FrownOutlined style={{ color: '#ff4d4f' }} />;
      }
    }
    
    // 默认图标
    return <MehOutlined style={{ color: '#d9d9d9' }} />;
  };

  // 获取心情显示文本
  const getMoodText = (mood) => {
    // 直接返回心情标签，不做转换
    return mood || '未知';
  };

  // 格式化时间
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN');
  };

  // 新增：只显示日期部分，严格YYYY-MM-DD格式
  const formatDateOnly = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    const y = date.getFullYear();
    const m = String(date.getMonth() + 1).padStart(2, '0');
    const d = String(date.getDate()).padStart(2, '0');
    return `${y}-${m}-${d}`;
  };

  if (loading) {
    return (
      <div className="admin-loading">
        <div className="admin-loading-spinner"></div>
        <p>正在加载打卡数据...</p>
      </div>
    );
  }

  return (
    <div className="admin-checkins-container">
      <div className="admin-card">
        <div className="admin-card-title">
          <CheckCircleOutlined />
          打卡数据管理
        </div>
        
        {/* 控制面板 */}
        <div className="admin-checkins-controls">
          <div className="admin-search-box">
            <SearchOutlined className="admin-search-icon" />
            <input
              type="text"
              placeholder="搜索用户ID..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="admin-search-input"
            />
          </div>
          
          <div className="admin-filter-box">
            <FilterOutlined className="admin-filter-icon" />
            <select
              value={moodFilter}
              onChange={(e) => setMoodFilter(e.target.value)}
              className="admin-filter-select"
            >
              <option value="all">所有心情</option>
              {Object.values(moodLabels).flat().map((label, index) => (
                <option key={index} value={label}>
                  {label}
                </option>
              ))}
            </select>
          </div>
          
          <div className="admin-filter-box">
            <select
              value={dateFilter}
              onChange={(e) => setDateFilter(e.target.value)}
              className="admin-filter-select"
            >
              <option value="all">所有时间</option>
              <option value="today">今天</option>
              <option value="week">最近一周</option>
              <option value="month">最近一月</option>
            </select>
          </div>
          
          <button
            onClick={() => setShowStats(!showStats)}
            className="admin-btn admin-btn-primary"
          >
            <BarChartOutlined />
            {showStats ? '隐藏统计' : '显示统计'}
          </button>
        </div>

        {/* 统计信息 */}
        <div className="admin-checkins-stats">
          <div className="admin-stat-item">
            <div className="admin-stat-label">总打卡数</div>
            <div className="admin-stat-value">{stats.total}</div>
          </div>
          <div className="admin-stat-item">
            <div className="admin-stat-label">今日打卡</div>
            <div className="admin-stat-value admin-stat-today">{stats.today}</div>
          </div>
          <div className="admin-stat-item">
            <div className="admin-stat-label">积极情绪</div>
            <div className="admin-stat-value admin-stat-happy">{stats.happy}</div>
          </div>
          <div className="admin-stat-item">
            <div className="admin-stat-label">消极情绪</div>
            <div className="admin-stat-value admin-stat-sad">{stats.sad}</div>
          </div>
          <div className="admin-stat-item">
            <div className="admin-stat-label">平均心情</div>
            <div className="admin-stat-value admin-stat-average">{stats.averageMood}</div>
          </div>
        </div>

        {/* 心情分布统计 */}
        {showStats && (
          <div className="admin-mood-stats">
            <h4>心情分布统计</h4>
            <div className="admin-mood-chart">
              {Object.entries(moodDistribution).map(([mood, count]) => (
                <div key={mood} className="admin-mood-item">
                  <div className="admin-mood-label">
                    {/* Removed getMoodIcon(mood) */}
                    <span>{getMoodText(mood)}</span>
                  </div>
                  <div className="admin-mood-bar">
                    <div 
                      className="admin-mood-fill"
                      style={{ 
                        width: `${stats.total > 0 ? (count / stats.total) * 100 : 0}%`,
                        backgroundColor: ['开心', '兴奋', '满足', '宁静', '感恩', '自信', '充满活力', '乐观'].includes(mood) ? '#52c41a' :
                                       ['平静', '冷静', '专注', '放松'].includes(mood) ? '#faad14' : '#ff4d4f'
                      }}
                    ></div>
                  </div>
                  <div className="admin-mood-count">{count}</div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* 打卡记录列表 */}
        <div className="admin-checkins-list">
          {currentCheckins.length === 0 ? (
            <div className="admin-empty-state">
              <CheckCircleOutlined />
              <p>暂无打卡数据</p>
            </div>
          ) : (
            currentCheckins.map((checkin) => (
              <div key={checkin.id} className="admin-checkin-item">
                <div className="admin-checkin-header">
                  <div className="admin-checkin-user">
                    <UserOutlined />
                    <span className="admin-checkin-username">用户ID: {checkin.userId}</span>
                    <span className="admin-checkin-mood">
                      {checkin.moodLabels && checkin.moodLabels.length > 0 ? (
                        <span className="admin-mood-tag">
                          {getMoodLabelDisplay(checkin.moodLabels)}
                        </span>
                      ) : (
                        <span className="admin-mood-tag admin-mood-empty">
                          无心情记录
                        </span>
                      )}
                      <span style={{ marginLeft: '8px', fontSize: '12px', color: '#666' }}>
                        ({checkin.score || 0}/5)
                      </span>
                    </span>
                  </div>
                  <div className="admin-checkin-time">
                    <CalendarOutlined /> {formatDateOnly(checkin.date || checkin.checkinDate || checkin.createdAt)}
                  </div>
                </div>
                
                <div className="admin-checkin-content">
                  <div className="admin-checkin-info">
                    <p className="admin-checkin-text">{checkin.diaryDescription || '无日记内容'}</p>
                    
                    <div className="admin-checkin-details">
                      <div className="admin-checkin-weather">
                        <strong>天气:</strong> {checkin.weather || '未知'}
                      </div>
                    </div>
                  </div>
                  
                  <div className="admin-checkin-actions">
                    <button
                      onClick={() => handleViewCheckin(checkin)}
                      className="admin-btn admin-btn-primary"
                    >
                      <EyeOutlined /> 查看详情
                    </button>
                    
                    <button
                      onClick={() => handleDeleteCheckin(checkin.id)}
                      className="admin-btn admin-btn-danger"
                    >
                      <DeleteOutlined /> 删除
                    </button>
                  </div>
                </div>
              </div>
            ))
          )}
        </div>

        {/* 分页 */}
        {totalPages > 1 && (
          <div className="admin-pagination">
            <button
              onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
              disabled={currentPage === 1}
              className="admin-btn admin-btn-secondary"
            >
              上一页
            </button>
            
            <span className="admin-page-info">
              第 {currentPage} 页，共 {totalPages} 页
            </span>
            
            <button
              onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
              disabled={currentPage === totalPages}
              className="admin-btn admin-btn-secondary"
            >
              下一页
            </button>
          </div>
        )}
      </div>

      {/* 打卡详情模态框 */}
      {showModal && selectedCheckin && (
        <div className="admin-modal-overlay" onClick={() => setShowModal(false)}>
          <div className="admin-modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="admin-modal-header">
              <h3>打卡详情</h3>
              <button 
                onClick={() => setShowModal(false)}
                className="admin-modal-close"
              >
                ×
              </button>
            </div>
            
            <div className="admin-modal-body">
              <div className="admin-detail-item">
                <strong>用户:</strong> 用户ID: {selectedCheckin.userId}
              </div>
              
              <div className="admin-detail-item">
                <strong>心情:</strong> 
                <span className="admin-mood-display">
                  {selectedCheckin.moodLabels && selectedCheckin.moodLabels.length > 0 ? (
                    selectedCheckin.moodLabels.map((mood, index) => (
                      <span key={index} className="admin-mood-tag">
                        {getMoodText(mood)}
                      </span>
                    ))
                  ) : (
                    <span className="admin-mood-tag admin-mood-empty">
                      无心情记录
                    </span>
                  )}
                  <span style={{ marginLeft: '8px', fontSize: '12px', color: '#666' }}>
                    ({selectedCheckin.score || 5}/5)
                  </span>
                </span>
              </div>
              
              <div className="admin-detail-item">
                <strong>打卡时间:</strong> {formatDateOnly(selectedCheckin.date || selectedCheckin.checkinDate || selectedCheckin.createdAt)}
              </div>
              
              <div className="admin-detail-item">
                <div className="admin-checkin-weather">
                  <strong>天气:</strong> {selectedCheckin.weather || '未知'}
                </div>
              </div>
              
              <div className="admin-detail-item">
                <strong>内容:</strong>
                <div className="admin-checkin-full-content">
                  {selectedCheckin.diaryDescription || '无日记内容'}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminCheckins; 