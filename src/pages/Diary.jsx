import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../css/Diary.css';

export const Diary = () => {
  const navigate = useNavigate();
  const [diaryEntries, setDiaryEntries] = useState([]);
  const [newEntry, setNewEntry] = useState('');

  useEffect(() => {
    // 加载日记列表
    loadDiaryEntries();
  }, []);

  const loadDiaryEntries = () => {
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

  const handleSave = () => {
    if (!newEntry.trim()) {
      alert('请输入日记内容');
      return;
    }
    
    // 保存日记逻辑
    console.log('保存日记:', newEntry);
    setNewEntry('');
    alert('日记保存成功！');
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
          </div>
        ))}
      </div>
    </div>
  );
};