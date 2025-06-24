import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../css/PostDetail.css';

const PostDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [isLiked, setIsLiked] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  // 模拟帖子数据
  const mockPost = {
    id: parseInt(id),
    title: '如何缓解工作压力？',
    content: `最近工作压力很大，想和大家分享一下我的减压方法，也希望能听到大家的建议。

首先，我想说的是，工作压力是每个人都会遇到的问题，这很正常。关键是要学会如何应对。

我的减压方法包括：

1. **深呼吸练习**
每天工作前，我会花5-10分钟做深呼吸练习。找一个安静的地方，闭上眼睛，慢慢吸气4秒，屏住呼吸4秒，然后慢慢呼气6秒。重复几次，整个人就会感觉轻松很多。

2. **运动放松**
下班后，我会去健身房或者在家做一些简单的运动。运动能释放内啡肽，这是天然的"快乐激素"。

3. **时间管理**
学会合理安排时间，把重要的事情优先处理，避免拖延带来的焦虑。

4. **寻求支持**
当压力特别大的时候，我会和朋友或家人聊天，有时候倾诉本身就是一种很好的减压方式。

5. **培养兴趣爱好**
工作之外，我会做一些自己喜欢的事情，比如看书、听音乐、画画等。

当然，每个人的情况不同，适合的方法也不同。我想听听大家都是怎么应对工作压力的，有什么好的建议吗？

记住，压力不是敌人，适当的压力能让我们成长。关键是要学会与压力共处，找到适合自己的平衡点。

希望我的分享能帮助到有类似经历的朋友，也欢迎大家分享自己的经验！`,
    author: '小明',
    authorAvatar: '👨',
    category: '压力管理',
    tags: ['工作压力', '减压方法', '心理健康', '时间管理'],
    likes: 24,
    comments: 8,
    views: 156,
    createdAt: '2024-01-15 14:30'
  };

  // 模拟评论数据
  const mockComments = [
    {
      id: 1,
      author: '小红',
      authorAvatar: '👩',
      content: '谢谢分享！我也经常用深呼吸的方法，真的很有效。另外我觉得冥想也很不错，推荐大家试试。',
      createdAt: '2024-01-15 15:20',
      likes: 5
    },
    {
      id: 2,
      author: '小李',
      authorAvatar: '👨‍🦱',
      content: '运动确实是最好的减压方式！我每天跑步30分钟，感觉整个人都轻松了。',
      createdAt: '2024-01-15 16:10',
      likes: 3
    },
    {
      id: 3,
      author: '小王',
      authorAvatar: '👩‍🦰',
      content: '时间管理真的很重要，我用番茄工作法，25分钟专注工作，然后休息5分钟，效率提高了很多。',
      createdAt: '2024-01-15 17:05',
      likes: 7
    }
  ];

  useEffect(() => {
    // 检查登录状态
    const loginStatus = localStorage.getItem('isLoggedIn');
    if (loginStatus === 'true') {
      setIsLoggedIn(true);
    }

    // 模拟加载数据
    setTimeout(() => {
      setPost(mockPost);
      setComments(mockComments);
      setIsLoading(false);
    }, 500);
  }, [id]);

  const handleLike = () => {
    if (!isLoggedIn) {
      alert('请先登录');
      navigate('/login');
      return;
    }

    setIsLiked(!isLiked);
    setPost(prev => ({
      ...prev,
      likes: isLiked ? prev.likes - 1 : prev.likes + 1
    }));
  };

  const handleComment = () => {
    if (!isLoggedIn) {
      alert('请先登录');
      navigate('/login');
      return;
    }

    if (!newComment.trim()) {
      alert('请输入评论内容');
      return;
    }

    const comment = {
      id: Date.now(),
      author: localStorage.getItem('username') || '用户',
      authorAvatar: '👤',
      content: newComment,
      createdAt: new Date().toLocaleString(),
      likes: 0
    };

    setComments(prev => [comment, ...prev]);
    setPost(prev => ({
      ...prev,
      comments: prev.comments + 1
    }));
    setNewComment('');
  };

  const formatTime = (timeString) => {
    const now = new Date();
    const postTime = new Date(timeString);
    const diffInHours = Math.floor((now - postTime) / (1000 * 60 * 60));
    
    if (diffInHours < 1) return '刚刚';
    if (diffInHours < 24) return `${diffInHours}小时前`;
    if (diffInHours < 48) return '昨天';
    return postTime.toLocaleDateString();
  };

  if (isLoading) {
    return (
      <div className="post-detail-container">
        <div className="loading-state">
          <div className="loading-spinner">
            <span></span>
            <span></span>
            <span></span>
          </div>
          <p>加载中...</p>
        </div>
      </div>
    );
  }

  if (!post) {
    return (
      <div className="post-detail-container">
        <div className="error-state">
          <div className="error-icon">❌</div>
          <p>帖子不存在</p>
          <button onClick={() => navigate('/community')}>返回社区</button>
        </div>
      </div>
    );
  }

  return (
    <div className="post-detail-container">
      <div className="post-detail-header">
        <button 
          className="back-btn"
          onClick={() => navigate('/community')}
        >
          ← 返回社区
        </button>
      </div>

      <div className="post-detail-content">
        <div className="post-main">
          <div className="post-header">
            <div className="post-author">
              <div className="author-avatar">
                <span>{post.authorAvatar}</span>
              </div>
              <div className="author-info">
                <span className="author-name">{post.author}</span>
                <span className="post-time">{formatTime(post.createdAt)}</span>
              </div>
            </div>
            <div className="post-category">
              <span className="category-tag">{post.category}</span>
            </div>
          </div>

          <h1 className="post-title">{post.title}</h1>

          <div className="post-tags">
            {post.tags.map(tag => (
              <span key={tag} className="tag">{tag}</span>
            ))}
          </div>

          <div className="post-body">
            {post.content.split('\n\n').map((paragraph, index) => (
              <p key={index} className="post-paragraph">
                {paragraph}
              </p>
            ))}
          </div>

          <div className="post-actions">
            <button 
              className={`like-btn ${isLiked ? 'liked' : ''}`}
              onClick={handleLike}
            >
              <span>❤️</span>
              <span>{post.likes}</span>
            </button>
            <div className="stat-item">
              <span>💬</span>
              <span>{post.comments}</span>
            </div>
            <div className="stat-item">
              <span>👁️</span>
              <span>{post.views}</span>
            </div>
          </div>
        </div>

        <div className="comments-section">
          <h2>评论 ({comments.length})</h2>
          
          {isLoggedIn && (
            <div className="comment-form">
              <textarea
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder="写下你的评论..."
                rows="3"
              />
              <button 
                className="comment-btn"
                onClick={handleComment}
                disabled={!newComment.trim()}
              >
                发表评论
              </button>
            </div>
          )}

          <div className="comments-list">
            {comments.length === 0 ? (
              <div className="no-comments">
                <p>还没有评论，来发表第一条评论吧！</p>
              </div>
            ) : (
              comments.map(comment => (
                <div key={comment.id} className="comment-item">
                  <div className="comment-author">
                    <div className="comment-avatar">
                      <span>{comment.authorAvatar}</span>
                    </div>
                    <div className="comment-info">
                      <span className="comment-author-name">{comment.author}</span>
                      <span className="comment-time">{formatTime(comment.createdAt)}</span>
                    </div>
                  </div>
                  <div className="comment-content">
                    {comment.content}
                  </div>
                  <div className="comment-actions">
                    <button className="comment-like-btn">
                      <span>👍</span>
                      <span>{comment.likes}</span>
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default PostDetail; 