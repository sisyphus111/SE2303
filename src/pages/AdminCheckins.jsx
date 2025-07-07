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

  // 模拟打卡数据
  const mockCheckins = [
    {
      id: 1,
      userId: 101,
      username: "张三",
      mood: "happy",
      moodValue: 8,
      content: "今天感觉很好，完成了所有的学习任务！",
      tags: ["学习", "充实", "开心"],
      createdAt: "2024-01-15 09:30:00",
      location: "图书馆",
      weather: "晴天",
      activities: ["学习", "运动"],
      reflection: "今天的学习效率很高，心情也很愉快。"
    },
    {
      id: 2,
      userId: 102,
      username: "李四",
      mood: "neutral",
      moodValue: 5,
      content: "普通的一天，没有什么特别的。",
      tags: ["平常", "日常"],
      createdAt: "2024-01-15 08:15:00",
      location: "宿舍",
      weather: "阴天",
      activities: ["休息", "看书"],
      reflection: "今天比较平淡，但也挺放松的。"
    },
    {
      id: 3,
      userId: 103,
      username: "王五",
      mood: "sad",
      moodValue: 3,
      content: "今天遇到了一些困难，感觉有点沮丧。",
      tags: ["困难", "挫折", "需要帮助"],
      createdAt: "2024-01-14 20:45:00",
      location: "教室",
      weather: "雨天",
      activities: ["学习", "思考"],
      reflection: "今天遇到了一些学习上的困难，需要找老师请教。"
    },
    {
      id: 4,
      userId: 104,
      username: "赵六",
      mood: "happy",
      moodValue: 9,
      content: "参加了社团活动，认识了很多新朋友！",
      tags: ["社交", "活动", "开心"],
      createdAt: "2024-01-14 18:20:00",
      location: "活动中心",
      weather: "晴天",
      activities: ["社团活动", "交友"],
      reflection: "今天的社团活动很有趣，结识了很多志同道合的朋友。"
    },
    {
      id: 5,
      userId: 105,
      username: "孙七",
      mood: "anxious",
      moodValue: 4,
      content: "明天要考试了，感觉很紧张。",
      tags: ["考试", "紧张", "压力"],
      createdAt: "2024-01-14 15:10:00",
      location: "自习室",
      weather: "晴天",
      activities: ["复习", "准备考试"],
      reflection: "考试前的紧张感让我有点焦虑，需要调整心态。"
    },
    {
      id: 6,
      userId: 106,
      username: "周八",
      mood: "excited",
      moodValue: 9,
      content: "收到了心仪公司的面试邀请！",
      tags: ["面试", "机会", "兴奋"],
      createdAt: "2024-01-14 14:30:00",
      location: "宿舍",
      weather: "晴天",
      activities: ["准备面试", "查资料"],
      reflection: "终于等到了这个机会，要好好准备面试。"
    },
    {
      id: 7,
      userId: 107,
      username: "吴九",
      mood: "tired",
      moodValue: 4,
      content: "熬夜做项目，感觉很疲惫。",
      tags: ["疲惫", "项目", "熬夜"],
      createdAt: "2024-01-14 12:00:00",
      location: "实验室",
      weather: "阴天",
      activities: ["编程", "项目开发"],
      reflection: "项目进度有点紧张，需要合理安排时间。"
    },
    {
      id: 8,
      userId: 108,
      username: "郑十",
      mood: "happy",
      moodValue: 7,
      content: "和朋友一起吃了美食，心情很棒！",
      tags: ["美食", "朋友", "开心"],
      createdAt: "2024-01-14 11:45:00",
      location: "餐厅",
      weather: "晴天",
      activities: ["聚餐", "聊天"],
      reflection: "和朋友在一起总是很开心，美食也很棒。"
    }
  ];

  useEffect(() => {
    // 模拟API调用
    const fetchCheckins = async () => {
      setLoading(true);
      try {
        // TODO: 调用实际API获取打卡数据
        // const response = await fetch('/api/admin/checkins');
        // const data = await response.json();
        // setCheckins(data);
        
        // 模拟网络延迟
        setTimeout(() => {
          setCheckins(mockCheckins);
          setLoading(false);
        }, 1000);
      } catch (error) {
        console.error('获取打卡数据失败:', error);
        setLoading(false);
      }
    };

    fetchCheckins();
  }, []);

  // 筛选打卡记录
  const filteredCheckins = checkins.filter(checkin => {
    const matchesSearch = checkin.username.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         checkin.content.toLowerCase().includes(searchTerm.toLowerCase()) ||
                         checkin.tags.some(tag => tag.toLowerCase().includes(searchTerm.toLowerCase()));
    
    const matchesMood = moodFilter === 'all' || checkin.mood === moodFilter;
    
    let matchesDate = true;
    if (dateFilter !== 'all') {
      const checkinDate = new Date(checkin.createdAt);
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
    total: checkins.length,
    today: checkins.filter(c => {
      const today = new Date().toDateString();
      const checkinDate = new Date(c.createdAt).toDateString();
      return today === checkinDate;
    }).length,
    happy: checkins.filter(c => c.mood === 'happy' || c.mood === 'excited').length,
    sad: checkins.filter(c => c.mood === 'sad' || c.mood === 'anxious').length,
    averageMood: checkins.length > 0 ? 
      (checkins.reduce((sum, c) => sum + c.moodValue, 0) / checkins.length).toFixed(1) : 0
  };

  // 心情分布统计
  const moodDistribution = {
    happy: checkins.filter(c => c.mood === 'happy').length,
    excited: checkins.filter(c => c.mood === 'excited').length,
    neutral: checkins.filter(c => c.mood === 'neutral').length,
    tired: checkins.filter(c => c.mood === 'tired').length,
    anxious: checkins.filter(c => c.mood === 'anxious').length,
    sad: checkins.filter(c => c.mood === 'sad').length
  };

  // 操作函数
  const handleViewCheckin = (checkin) => {
    setSelectedCheckin(checkin);
    setShowModal(true);
  };

  const handleDeleteCheckin = async (checkinId) => {
    if (window.confirm('确定要删除这条打卡记录吗？此操作不可恢复。')) {
      try {
        // TODO: 调用API删除打卡记录
        // await fetch(`/api/admin/checkins/${checkinId}`, { method: 'DELETE' });
        
        setCheckins(checkins.filter(checkin => checkin.id !== checkinId));
        console.log('打卡记录已删除:', checkinId);
      } catch (error) {
        console.error('删除打卡记录失败:', error);
      }
    }
  };

  // 获取心情图标
  const getMoodIcon = (mood) => {
    switch (mood) {
      case 'happy':
      case 'excited':
        return <SmileOutlined style={{ color: '#52c41a' }} />;
      case 'neutral':
        return <MehOutlined style={{ color: '#faad14' }} />;
      case 'sad':
      case 'anxious':
      case 'tired':
        return <FrownOutlined style={{ color: '#ff4d4f' }} />;
      default:
        return <MehOutlined style={{ color: '#d9d9d9' }} />;
    }
  };

  // 获取心情显示文本
  const getMoodText = (mood) => {
    switch (mood) {
      case 'happy':
        return '开心';
      case 'excited':
        return '兴奋';
      case 'neutral':
        return '平静';
      case 'sad':
        return '难过';
      case 'anxious':
        return '焦虑';
      case 'tired':
        return '疲惫';
      default:
        return '未知';
    }
  };

  // 格式化时间
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN');
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
              placeholder="搜索用户、内容或标签..."
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
              <option value="happy">开心</option>
              <option value="excited">兴奋</option>
              <option value="neutral">平静</option>
              <option value="sad">难过</option>
              <option value="anxious">焦虑</option>
              <option value="tired">疲惫</option>
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
                    {getMoodIcon(mood)}
                    <span>{getMoodText(mood)}</span>
                  </div>
                  <div className="admin-mood-bar">
                    <div 
                      className="admin-mood-fill"
                      style={{ 
                        width: `${stats.total > 0 ? (count / stats.total) * 100 : 0}%`,
                        backgroundColor: mood === 'happy' || mood === 'excited' ? '#52c41a' :
                                       mood === 'neutral' ? '#faad14' : '#ff4d4f'
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
                    <span className="admin-checkin-username">{checkin.username}</span>
                    <span className="admin-checkin-mood">
                      {getMoodIcon(checkin.mood)}
                      {getMoodText(checkin.mood)} ({checkin.moodValue}/10)
                    </span>
                  </div>
                  <div className="admin-checkin-time">
                    <CalendarOutlined /> {formatDate(checkin.createdAt)}
                  </div>
                </div>
                
                <div className="admin-checkin-content">
                  <div className="admin-checkin-info">
                    <p className="admin-checkin-text">{checkin.content}</p>
                    
                    <div className="admin-checkin-details">
                      <div className="admin-checkin-location">
                        <strong>位置:</strong> {checkin.location}
                      </div>
                      <div className="admin-checkin-weather">
                        <strong>天气:</strong> {checkin.weather}
                      </div>
                      <div className="admin-checkin-activities">
                        <strong>活动:</strong> {checkin.activities.join(', ')}
                      </div>
                    </div>
                    
                    <div className="admin-checkin-tags">
                      {checkin.tags.map((tag, index) => (
                        <span key={index} className="admin-checkin-tag">#{tag}</span>
                      ))}
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
                <strong>用户:</strong> {selectedCheckin.username} (ID: {selectedCheckin.userId})
              </div>
              
              <div className="admin-detail-item">
                <strong>心情:</strong> 
                <span className="admin-mood-display">
                  {getMoodIcon(selectedCheckin.mood)}
                  {getMoodText(selectedCheckin.mood)} ({selectedCheckin.moodValue}/10)
                </span>
              </div>
              
              <div className="admin-detail-item">
                <strong>打卡时间:</strong> {formatDate(selectedCheckin.createdAt)}
              </div>
              
              <div className="admin-detail-item">
                <strong>位置:</strong> {selectedCheckin.location}
              </div>
              
              <div className="admin-detail-item">
                <strong>天气:</strong> {selectedCheckin.weather}
              </div>
              
              <div className="admin-detail-item">
                <strong>活动:</strong> {selectedCheckin.activities.join(', ')}
              </div>
              
              <div className="admin-detail-item">
                <strong>标签:</strong>
                <div className="admin-checkin-tags">
                  {selectedCheckin.tags.map((tag, index) => (
                    <span key={index} className="admin-checkin-tag">#{tag}</span>
                  ))}
                </div>
              </div>
              
              <div className="admin-detail-item">
                <strong>内容:</strong>
                <div className="admin-checkin-full-content">
                  {selectedCheckin.content}
                </div>
              </div>
              
              <div className="admin-detail-item">
                <strong>反思:</strong>
                <div className="admin-checkin-reflection">
                  {selectedCheckin.reflection}
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