import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../css/Diary.css';

// API服务导入注释
// import { createDiary, getDiaryList, updateDiary, deleteDiary, getMoodStats } from '../service/diaryService';

export const Diary = () => {
  const navigate = useNavigate();
  const [diaryEntries, setDiaryEntries] = useState([]);
  const [newEntry, setNewEntry] = useState({
    title: '',
    content: '',
    weather: 'sunny'
  });

  useEffect(() => {
    // 加载日记列表
    loadDiaryEntries();
  }, []);

  const loadDiaryEntries = async () => {
    // API调用注释：获取日记列表
    // try {
    //   const response = await getDiaryList({
    //     startDate: '2024-01-01',
    //     endDate: new Date().toISOString().split('T')[0]
    //   });
    //   setDiaryEntries(response.data);
    // } catch (error) {
    //   console.error('获取日记列表失败:', error);
    //   // 使用模拟数据作为备选
    //   const mockEntries = [
    //     {
    //       id: 1,
    //       date: '2024-01-15',
    //       content: '今天心情不错，工作进展顺利...',
    //       mood: 4
    //     },
    //     {
    //       id: 2,
    //       date: '2024-01-14',
    //       content: '有点压力，但还是坚持下来了...',
    //       mood: 3
    //     }
    //   ];
    //   setDiaryEntries(mockEntries);
    // }

    // 示例数据
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
    
    // API调用注释：创建日记
    // try {
    //   const diaryData = {
    //     title: newEntry.title,
    //     content: newEntry.content,
    //     weather: newEntry.weather,
    //     date: new Date().toISOString().split('T')[0]
    //   };
    //   await createDiary(diaryData);
    //   setNewEntry({ title: '', content: '', weather: 'sunny' });
    //   alert('日记保存成功！');
    //   // 重新加载日记列表
    //   loadDiaryEntries();
    // } catch (error) {
    //   console.error('保存日记失败:', error);
    //   alert('保存失败，请重试');
    // }

    // 保存日记逻辑
    const newDiary = {
      id: Date.now(),
      title: newEntry.title,
      content: newEntry.content,
      weather: newEntry.weather,
      date: new Date().toISOString().split('T')[0]
    };
    
    setDiaryEntries(prev => [newDiary, ...prev]);
    setNewEntry({ title: '', content: '', weather: 'sunny' });
    alert('日记保存成功！');
  };

  // 更新日记的函数
  const handleUpdateDiary = async (diaryId, updatedData) => {
    // API调用注释：更新日记
    // try {
    //   await updateDiary(diaryId, updatedData);
    //   alert('日记更新成功！');
    //   loadDiaryEntries();
    // } catch (error) {
    //   console.error('更新日记失败:', error);
    //   alert('更新失败，请重试');
    // }
  };

  // 删除日记的函数
  const handleDeleteDiary = async (diaryId) => {
    // API调用注释：删除日记
    // if (window.confirm('确定要删除这篇日记吗？')) {
    //   try {
    //     await deleteDiary(diaryId);
    //     alert('日记删除成功！');
    //     loadDiaryEntries();
    //   } catch (error) {
    //     console.error('删除日记失败:', error);
    //     alert('删除失败，请重试');
    //   }
    // }
  };

  // 获取心情统计的函数
  const loadMoodStats = async () => {
    // API调用注释：获取心情统计
    // try {
    //   const startDate = '2024-01-01';
    //   const endDate = new Date().toISOString().split('T')[0];
    //   const stats = await getMoodStats(startDate, endDate);
    //   console.log('心情统计数据:', stats.data);
    //   // 这里可以用于显示心情统计图表
    // } catch (error) {
    //   console.error('获取心情统计失败:', error);
    // }
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
        
        <button onClick={handleSave} className="save-btn">
          保存日记
        </button>
      </div>

      <div className="diary-list">
        <h2>历史日记</h2>
        {diaryEntries.length === 0 ? (
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
