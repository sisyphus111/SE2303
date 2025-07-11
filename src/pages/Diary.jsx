import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../lib/UserContext';
import ContentWarningModal from '../components/ContentWarningModal';
import '../css/Diary.css';

// API服务导入
import { createDiary, getDiaryList, updateDiary, deleteDiary, getMoodStats, ContentInspectionClient } from '../service/diaryService';

export const Diary = () => {
  const navigate = useNavigate();
  const { userProfile, identity } = useUser();
  const [diaryEntries, setDiaryEntries] = useState([]);
  const [newEntry, setNewEntry] = useState({
    title: '',
    content: '',
    weather: 'sunny'
  });
  const [loading, setLoading] = useState(false);
  const [showWarningModal, setShowWarningModal] = useState(false);

  useEffect(() => {
    // 加载日记列表
    loadDiaryEntries();
  }, [userProfile]);

  const loadDiaryEntries = async () => {
    /*if (!userProfile?.userId) {
      console.log('用户未登录，使用模拟数据');
      // 使用模拟数据作为备选
      const mockEntries = [
        {
          id: 1,
          title: '充实的一天',
          date: '2024-01-15',
          content: '今天工作进展顺利，完成了几个重要的任务。下午和朋友一起喝咖啡，聊了很多有趣的话题。晚上看了一部电影，感觉很放松。',
          weather: 'sunny'
        },
        {
          id: 2,
          title: '挑战与成长',
          date: '2024-01-14',
          content: '今天遇到了一些工作上的挑战，虽然有点压力，但还是坚持下来了。通过这次经历学到了很多新东西，感觉自己在成长。',
          weather: 'cloudy'
        },
        {
          id: 3,
          title: '雨中思考',
          date: '2024-01-13',
          content: '今天下雨了，待在家里看书，思考了很多人生的问题。雨声很舒缓，让我感到内心的平静。',
          weather: 'rainy'
        }
      ];
      setDiaryEntries(mockEntries);
      return;
    }*/

    try {
      setLoading(true);
      const response = await getDiaryList(userProfile.userId, {
        startDate: '2024-01-01',
        endDate: new Date().toISOString().split('T')[0]
      });
      
      if (response && Array.isArray(response)) {
        // 将大写天气枚举值转换为小写值
        const weatherMapReverse = {
          'SUNNY': 'sunny',
          'CLOUDY': 'cloudy',
          'RAINY': 'rainy',
          'SNOWY': 'snowy',
          'WINDY': 'windy',
          'FOGGY': 'foggy'
        };
        
        // 过滤出有日记内容的记录
        const diariesWithContent = response.filter(entry => 
          entry.diaryDescription && entry.diaryDescription.trim()
        ).map(entry => ({
          id: entry.id,
          title: entry.diaryTitle || '无标题日记',
          date: entry.date,
          content: entry.diaryDescription,
          weather: weatherMapReverse[entry.weather] || 'sunny'
        }));
        setDiaryEntries(diariesWithContent);
      } else {
        setDiaryEntries([]);
      }
    } catch (error) {
      console.error('获取日记列表失败:', error);
      // 使用模拟数据作为备选
      const mockEntries = [
        {
          id: 1,
          title: '充实的一天',
          date: '2024-01-15',
          content: '今天工作进展顺利，完成了几个重要的任务。下午和朋友一起喝咖啡，聊了很多有趣的话题。晚上看了一部电影，感觉很放松。',
          weather: 'sunny'
        },
        {
          id: 2,
          title: '挑战与成长',
          date: '2024-01-14',
          content: '今天遇到了一些工作上的挑战，虽然有点压力，但还是坚持下来了。通过这次经历学到了很多新东西，感觉自己在成长。',
          weather: 'cloudy'
        },
        {
          id: 3,
          title: '雨中思考',
          date: '2024-01-13',
          content: '今天下雨了，待在家里看书，思考了很多人生的问题。雨声很舒缓，让我感到内心的平静。',
          weather: 'rainy'
        }
      ];
      setDiaryEntries(mockEntries);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    if (!newEntry.title.trim()) {
      alert('请输入日记标题');
      return;
    }
    if (!newEntry.content.trim()) {
      alert('请输入日记内容');
      return;
    }

    if (!userProfile?.userId) {
      alert('请先登录');
      return;
    }

    try {
      setLoading(true);
      
      // 内容审查
      const contentInspector = new ContentInspectionClient();
      const titleToInspect = newEntry.title;
      const contentToInspect = newEntry.content;
      
      const [titleSafe, contentSafe] = await Promise.all([
        contentInspector.validateContent(titleToInspect),
        contentInspector.validateContent(contentToInspect)
      ]);
      
      // 如果内容不安全，显示通知框并停止保存
      if (!titleSafe || !contentSafe) {
        // 创建内容审查警报
        try {
          if (!titleSafe) {
            await contentInspector.createContentAlert(userProfile.userId, titleToInspect, '标题');
            console.log('🚨 已创建标题内容审查警报');
          }
          if (!contentSafe) {
            await contentInspector.createContentAlert(userProfile.userId, contentToInspect, '内容');
            console.log('🚨 已创建内容审查警报');
          }
        } catch (alertError) {
          console.error('创建内容审查警报失败:', alertError);
        }
        
        // 显示安慰用户的模态框
        setShowWarningModal(true);
        setLoading(false);
        return; // 停止保存日记
      }
      
      // 内容安全，继续保存日记
      // 将小写天气值转换为大写枚举值
      const weatherMap = {
        sunny: 'SUNNY',
        cloudy: 'CLOUDY',
        rainy: 'RAINY',
        snowy: 'SNOWY',
        windy: 'WINDY',
        foggy: 'FOGGY'
      };
      
      const diaryData = {
        diaryTitle: newEntry.title,
        diaryDescription: newEntry.content,
        weather: weatherMap[newEntry.weather] || 'SUNNY',
        date: new Date().toISOString().split('T')[0]
      };
      
      await createDiary(diaryData, userProfile.userId, identity || false);
      setNewEntry({ title: '', content: '', weather: 'sunny' });
      alert('日记保存成功！');
      // 重新加载日记列表
      loadDiaryEntries();
    } catch (error) {
      console.error('保存日记失败:', error);
      alert('保存失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  // 更新日记的函数
  const handleUpdateDiary = async (diaryId, updatedData) => {
    try {
      await updateDiary(diaryId, updatedData);
      alert('日记更新成功！');
      loadDiaryEntries();
    } catch (error) {
      console.error('更新日记失败:', error);
      alert('更新失败，请重试');
    }
  };

  // 删除日记的函数
  const handleDeleteDiary = async (diaryId) => {
    if (window.confirm('确定要删除这篇日记吗？')) {
      try {
        await deleteDiary(diaryId);
        alert('日记删除成功！');
        loadDiaryEntries();
      } catch (error) {
        console.error('删除日记失败:', error);
        alert('删除失败，请重试');
      }
    }
  };

  // 获取心情统计的函数
  const loadMoodStats = async () => {
    if (!userProfile?.userId)
      return;
    
    try {
      const startDate = '2024-01-01';
      const endDate = new Date().toISOString().split('T')[0];
      const stats = await getMoodStats(userProfile.userId, startDate, endDate);
      // 这里可以用于显示心情统计图表
    } catch (error) {
      console.error('获取心情统计失败:', error);
    }
  };

  // 获取天气图标
  const getWeatherIcon = (weather) => {
    const weatherMap = {
      sunny: '☀️',
      cloudy: '☁️',
      rainy: '🌧️',
      snowy: '❄️',
      windy: '💨',
      foggy: '🌫️'
    };
    return weatherMap[weather] || '☀️';
  };

  // 格式化日期
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const today = new Date();
    const yesterday = new Date(today);
    yesterday.setDate(yesterday.getDate() - 1);

    if (date.toDateString() === today.toDateString()) {
      return '今天';
    } else if (date.toDateString() === yesterday.toDateString()) {
      return '昨天';
    } else {
      return date.toLocaleDateString('zh-CN', {
        month: 'long',
        day: 'numeric'
      });
    }
  };

  // 获取内容预览
  const getContentPreview = (content) => {
    return content.length > 50 ? content.substring(0, 50) + '...' : content;
  };

  const goBack = () => {
    navigate('/');
  };

  return (
    <div className="diary-container">
      <ContentWarningModal 
        isOpen={showWarningModal} 
        onClose={() => setShowWarningModal(false)} 
      />
      <div className="diary-header">
        <button onClick={goBack} className="back-btn">← 返回</button>
        <h1>文字日记</h1>
      </div>

      <div className="diary-editor">
        <h2>写新日记</h2>
        
        {/* 标题输入框 */}
        <div className="title-input-group">
          <label htmlFor="diary-title">日记标题</label>
          <input
            id="diary-title"
            type="text"
            value={newEntry.title}
            onChange={(e) => setNewEntry(prev => ({ ...prev, title: e.target.value }))}
            placeholder="给你的日记起个标题..."
            className="diary-title-input"
        />
        </div>

        {/* 天气选择器 */}
        <div className="weather-selector">
          <label>今天的天气：</label>
          <select 
            value={newEntry.weather} 
            onChange={(e) => setNewEntry(prev => ({ ...prev, weather: e.target.value }))}
            className="weather-select"
          >
            <option value="sunny">☀️ 晴天</option>
            <option value="cloudy">☁️ 多云</option>
            <option value="rainy">🌧️ 雨天</option>
            <option value="snowy">❄️ 雪天</option>
            <option value="windy">💨 风天</option>
            <option value="foggy">🌫️ 雾天</option>
          </select>
        </div>

        {/* 内容输入框 */}
        <div className="content-input-group">
          <label htmlFor="diary-content">日记内容</label>
          <textarea
            id="diary-content"
            value={newEntry.content}
            onChange={(e) => setNewEntry(prev => ({ ...prev, content: e.target.value }))}
            placeholder="记录你今天的想法和经历..."
            className="diary-textarea"
          />
        </div>
        
        <button onClick={handleSave} className="save-btn" disabled={loading}>
          {loading ? '保存中...' : '保存日记'}
        </button>
      </div>

      <div className="diary-list">
        <h2>历史日记</h2>
        {loading ? (
          <div className="diary-loading">加载中...</div>
        ) : diaryEntries.length === 0 ? (
          <div className="diary-empty">
            还没有写过日记，开始记录你的生活吧！
          </div>
        ) : (
          diaryEntries.map(entry => (
            <div 
              key={entry.id} 
              className="diary-entry clickable"
              onClick={() => navigate(`/diary/${entry.id}`)}
            >
              <div className="entry-header">
                <div className="entry-title">{entry.title}</div>
                <div className="entry-weather">{getWeatherIcon(entry.weather)}</div>
              </div>
              <div className="entry-date">{formatDate(entry.date)}</div>
              <div className="entry-preview">{getContentPreview(entry.content)}</div>
            </div>
          ))
        )}
      </div>
    </div>
  );
};
