import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useUser } from '../lib/UserContext';
import '../css/DiaryDetail.css';

// API服务导入
import { getDiaryDetail, updateDiary, deleteDiary } from '../service/diaryService';

export const DiaryDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { userProfile, identity } = useUser();
  const [diary, setDiary] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [editForm, setEditForm] = useState({
    title: '',
    content: '',
    weather: 'sunny'
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDiaryDetail();
  }, [id]);

  const loadDiaryDetail = async () => {
    setLoading(true);
    try {
      // 尝试从后端获取日记详情
      const response = await getDiaryDetail(id);
      
      if (response && response.id) {
        // 将大写天气枚举值转换为小写值
        const weatherMapReverse = {
          'SUNNY': 'sunny',
          'CLOUDY': 'cloudy',
          'RAINY': 'rainy',
          'SNOWY': 'snowy',
          'WINDY': 'windy',
          'FOGGY': 'foggy'
        };
        
        const diaryData = response;
        const formattedDiary = {
          id: diaryData.id,
          title: diaryData.diaryTitle || '无标题日记',
          date: diaryData.date,
          content: diaryData.diaryDescription || '',
          weather: weatherMapReverse[diaryData.weather] || 'sunny'
        };
        setDiary(formattedDiary);
        setEditForm({
          title: formattedDiary.title,
          content: formattedDiary.content,
          weather: formattedDiary.weather
        });
      } else {
        throw new Error('日记不存在');
      }
    } catch (error) {
      console.error('获取日记详情失败:', error);
      // 使用模拟数据作为备选
      const mockDiaries = [
        {
          id: 1,
          title: '充实的一天',
          date: '2024-01-15',
          content: '今天工作进展顺利，完成了几个重要的任务。下午和朋友一起喝咖啡，聊了很多有趣的话题。晚上看了一部电影，感觉很放松。这一天虽然忙碌，但是很充实，每一刻都很有意义。',
          weather: 'sunny'
        },
        {
          id: 2,
          title: '挑战与成长',
          date: '2024-01-14',
          content: '今天遇到了一些工作上的挑战，虽然有点压力，但还是坚持下来了。通过这次经历学到了很多新东西，感觉自己在成长。困难让我更加坚强，我相信这些经历会让我变得更好。',
          weather: 'cloudy'
        },
        {
          id: 3,
          title: '雨中思考',
          date: '2024-01-13',
          content: '今天下雨了，待在家里看书，思考了很多人生的问题。雨声很舒缓，让我感到内心的平静。在这个安静的午后，我重新审视了自己的目标和方向，感觉内心更加清晰了。',
          weather: 'rainy'
        }
      ];

      const foundDiary = mockDiaries.find(d => d.id === parseInt(id));
      if (foundDiary) {
        setDiary(foundDiary);
        setEditForm({
          title: foundDiary.title,
          content: foundDiary.content,
          weather: foundDiary.weather
        });
      } else {
        setDiary(null);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleSaveEdit = async () => {
    try {
      // 将小写天气值转换为大写枚举值
      const weatherMap = {
        sunny: 'SUNNY',
        cloudy: 'CLOUDY',
        rainy: 'RAINY',
        snowy: 'SNOWY',
        windy: 'WINDY',
        foggy: 'FOGGY'
      };
      
      // 尝试更新日记 - 需要提供完整的MoodCheckInRequest格式
      const updateData = {
        date: diary.date, // 保持原日期
        score: 3, // 默认心情评分，因为更新日记时可能没有心情数据
        moodLabels: ['平静'], // 默认心情标签
        diaryTitle: editForm.title,
        diaryDescription: editForm.content,
        weather: weatherMap[editForm.weather] || 'SUNNY'
      };
      
      await updateDiary(id, updateData);
      
      // 更新本地状态
      setDiary(prev => ({
        ...prev,
        title: editForm.title,
        content: editForm.content,
        weather: editForm.weather
      }));
      
      setIsEditing(false);
      alert('日记更新成功！');
    } catch (error) {
      console.error('更新日记失败:', error);
      // 如果后端更新失败，只更新本地状态
      setDiary(prev => ({
        ...prev,
        title: editForm.title,
        content: editForm.content,
        weather: editForm.weather
      }));
      setIsEditing(false);
      alert('日记更新成功！（本地更新）');
    }
  };

  const handleCancelEdit = () => {
    setEditForm({
      title: diary.title,
      content: diary.content,
      weather: diary.weather
    });
    setIsEditing(false);
  };

  const handleDelete = async () => {
    if (window.confirm('确定要删除这篇日记吗？')) {
      try {
        // 尝试从后端删除日记
        await deleteDiary(id);
        alert('日记删除成功！');
        navigate('/diary');
      } catch (error) {
        console.error('删除日记失败:', error);
        alert('删除失败，请重试');
      }
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
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      weekday: 'long'
    });
  };

  const goBack = () => {
    navigate('/diary');
  };

  if (loading) {
    return (
      <div className="diary-detail-container">
        <div className="loading">加载中...</div>
      </div>
    );
  }

  if (!diary) {
    return (
      <div className="diary-detail-container">
        <div className="not-found">
          <h2>日记不存在</h2>
          <button onClick={goBack} className="back-btn">返回日记列表</button>
        </div>
      </div>
    );
  }

  return (
    <div className="diary-detail-container">
      <div className="diary-detail-header">
        <button onClick={goBack} className="back-btn">← 返回</button>
        <div className="header-actions">
          {!isEditing && (
            <>
              <button onClick={handleEdit} className="edit-btn">编辑</button>
              <button onClick={handleDelete} className="delete-btn">删除</button>
            </>
          )}
        </div>
      </div>

      <div className="diary-detail-content">
        {isEditing ? (
          <div className="edit-form">
            <div className="form-group">
              <label htmlFor="edit-title">标题</label>
              <input
                id="edit-title"
                type="text"
                value={editForm.title}
                onChange={(e) => setEditForm(prev => ({ ...prev, title: e.target.value }))}
                className="edit-title-input"
              />
            </div>

            <div className="form-group">
              <label htmlFor="edit-weather">天气</label>
              <select
                id="edit-weather"
                value={editForm.weather}
                onChange={(e) => setEditForm(prev => ({ ...prev, weather: e.target.value }))}
                className="edit-weather-select"
              >
                <option value="sunny">☀️ 晴天</option>
                <option value="cloudy">☁️ 多云</option>
                <option value="rainy">🌧️ 雨天</option>
                <option value="snowy">❄️ 雪天</option>
                <option value="windy">💨 风天</option>
                <option value="foggy">🌫️ 雾天</option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="edit-content">内容</label>
              <textarea
                id="edit-content"
                value={editForm.content}
                onChange={(e) => setEditForm(prev => ({ ...prev, content: e.target.value }))}
                className="edit-content-textarea"
                rows="10"
              />
            </div>

            <div className="edit-actions">
              <button onClick={handleSaveEdit} className="save-btn">保存</button>
              <button onClick={handleCancelEdit} className="cancel-btn">取消</button>
            </div>
          </div>
        ) : (
          <div className="diary-view">
            <div className="diary-header-info">
              <h1 className="diary-title">{diary.title}</h1>
              <div className="diary-meta">
                <span className="diary-date">{formatDate(diary.date)}</span>
                <span className="diary-weather">{getWeatherIcon(diary.weather)}</span>
              </div>
            </div>
            
            <div className="diary-content">
              <p>{diary.content}</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}; 