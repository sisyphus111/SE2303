import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../css/Diary.css';

// API服务导入注释
// import { createDiary, getDiaryList, updateDiary, deleteDiary, getMoodStats } from '../service/diaryService';

export const Diary = () => {
  const navigate = useNavigate();
  const [diaryEntries, setDiaryEntries] = useState([]);
  const [newEntry, setNewEntry] = useState('');
  const [moodRating, setMoodRating] = useState(3); // 心情评分

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
        date: '2024-01-15',
        content: '今天心情不错，工作进展顺利...',
        mood: 4
      },
      {
        id: 2,
        date: '2024-01-14',
        content: '有点压力，但还是坚持下来了...',
        mood: 3
      }
    ];
    setDiaryEntries(mockEntries);
  };

  const handleSave = async () => {
    if (!newEntry.trim()) {
      alert('请输入日记内容');
      return;
    }
    
    // API调用注释：创建日记
    // try {
    //   const diaryData = {
    //     content: newEntry,
    //     mood: moodRating,
    //     date: new Date().toISOString().split('T')[0]
    //   };
    //   await createDiary(diaryData);
    //   setNewEntry('');
    //   setMoodRating(3);
    //   alert('日记保存成功！');
    //   // 重新加载日记列表
    //   loadDiaryEntries();
    // } catch (error) {
    //   console.error('保存日记失败:', error);
    //   alert('保存失败，请重试');
    // }

    // 保存日记逻辑
    console.log('保存日记:', newEntry);
    setNewEntry('');
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
        <textarea
          value={newEntry}
          onChange={(e) => setNewEntry(e.target.value)}
          placeholder="记录你今天的心情和想法..."
          className="diary-textarea"
        />
        {/* 心情评分选择器 */}
        <div className="mood-rating">
          <label>今天的心情：</label>
          <select 
            value={moodRating} 
            onChange={(e) => setMoodRating(Number(e.target.value))}
          >
            <option value={1}>很差</option>
            <option value={2}>不好</option>
            <option value={3}>一般</option>
            <option value={4}>不错</option>
            <option value={5}>很好</option>
          </select>
        </div>
        <button onClick={handleSave} className="save-btn">
          保存日记
        </button>
      </div>

      <div className="diary-list">
        <h2>历史日记</h2>
        {diaryEntries.map(entry => (
          <div key={entry.id} className="diary-entry">
            <div className="entry-date">{entry.date}</div>
            <div className="entry-content">{entry.content}</div>
            <div className="entry-mood">心情: {'★'.repeat(entry.mood)}</div>
            {/* 编辑和删除按钮 */}
            {/* <div className="entry-actions">
              <button onClick={() => handleUpdateDiary(entry.id, { content: '更新的内容' })}>
                编辑
              </button>
              <button onClick={() => handleDeleteDiary(entry.id)}>
                删除
              </button>
            </div> */}
          </div>
        ))}
      </div>
    </div>
  );
};
