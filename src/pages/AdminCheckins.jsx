import React, { useState, useEffect, useRef } from 'react';
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
import AdminCheckinList from '../components/AdminCheckinList';
import AdminCheckinModal from '../components/AdminCheckinModal';
import AdminCheckinsStats from '../components/AdminCheckinsStats';
import AdminMoodStats from '../components/AdminMoodStats';

const AdminCheckins = () => {
  const [checkins, setCheckins] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState(''); // 输入框的值
  const [activeSearchTerm, setActiveSearchTerm] = useState(''); // 实际用于搜索的值
  const [moodFilter, setMoodFilter] = useState('all');
  const [dateFilter, setDateFilter] = useState('all');
  const [selectedCheckin, setSelectedCheckin] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [checkinsPerPage] = useState(15);
  const [showStats, setShowStats] = useState(false);
  const [moodLabels, setMoodLabels] = useState([]);
  const searchInputRef = useRef(null);
  const clearButtonRef = useRef(null);



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
        console.log("checkins:", checkins);
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
    const matchesSearch = checkin.userId.toString().includes(activeSearchTerm) ||
                         (checkin.diaryDescription && checkin.diaryDescription.toLowerCase().includes(activeSearchTerm.toLowerCase()));
    
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

  // 处理搜索回车键
  const handleSearchKeyPress = (e) => {
    if (e.key === 'Enter') {
      setActiveSearchTerm(searchTerm.trim());
      setCurrentPage(1); // 重置到第一页
    }
  };

  // 清除搜索
  const handleClearSearch = () => {
    setSearchTerm('');
    setActiveSearchTerm('');
    setCurrentPage(1);
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
              ref={searchInputRef}
              type="text"
              placeholder="搜索用户ID..."
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
        <AdminCheckinsStats stats={stats} />

        {/* 心情分布统计 */}
        <AdminMoodStats showStats={showStats} moodDistribution={moodDistribution} stats={stats} getMoodText={getMoodText} />

        {/* 打卡记录列表 */}
        <AdminCheckinList
          currentCheckins={currentCheckins}
          onView={handleViewCheckin}
          onDelete={handleDeleteCheckin}
          getMoodLabelDisplay={getMoodLabelDisplay}
          getMoodText={getMoodText}
          formatDateOnly={formatDateOnly}
        />

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
      <AdminCheckinModal
        show={showModal}
        checkin={selectedCheckin}
        onClose={() => setShowModal(false)}
        getMoodText={getMoodText}
        formatDateOnly={formatDateOnly}
      />
    </div>
  );
};

export default AdminCheckins;