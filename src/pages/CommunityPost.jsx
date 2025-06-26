import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { 
  ArrowLeftOutlined,
  UserOutlined,
  TagOutlined,
  FireOutlined,
  ClockCircleOutlined
} from '@ant-design/icons';
import PostDetail from '../components/CommunityPostDetail';
import CommentInput from '../components/CommentIinput';
import PostCommentList from '../components/CommunityPostCommentList';
import "../css/CommunityPostDetail.css";

// 假数据提升到这里，后续可独立到 data 文件
const allPosts = [
    {
        id: 1,
        title: "情感与学习的成长故事",
        tags: ['情感', '学习'],
        time: "2025-06-23 11:00 PM PDT",
        user: { name: "user1", avatar: 'https://randomuser.me/api/portraits/women/1.jpg' },
        content: `在成长的道路上，情感与学习总是交织在一起。每当遇到挫折时，朋友和家人的鼓励让我重拾信心，继续前行。学习不仅仅是知识的积累，更是心灵的成长。那些深夜挑灯苦读的时光、与同伴讨论的激烈瞬间，都成为我宝贵的回忆。情感的支持和学习的动力，让我一步步走向更好的自己。`,
        views: 120,
        likes: 12,
        comments: [
            { user: { name: "小明", avatar: "https://via.placeholder.com/50" }, content: "很有共鸣！" },
        ]
    },
    {
        id: 2,
        title: "学习与健康的平衡",
        tags: ['学习', '健康'],
        time: "2025-06-22 10:00 AM PDT",
        user: { name: "user2", avatar: 'https://randomuser.me/api/portraits/men/2.jpg' },
        content: `曾经我一味追求成绩，忽视了身体健康。直到有一天因疲劳生病，才意识到健康的重要。现在我学会了劳逸结合，运动和学习并重。每天坚持晨跑、合理饮食，让我精力充沛，学习效率也提升了。健康是学习的基础，只有身心俱佳，才能走得更远。`,
        views: 80,
        likes: 8,
        comments: []
    },
    {
        id: 3,
        title: "生活中的成长点滴",
        tags: ['生活', '成长'],
        time: "2025-06-21 09:00 AM PDT",
        user: { name: "user3", avatar: 'https://randomuser.me/api/portraits/women/3.jpg' },
        content: `成长并不总是轰轰烈烈，更多的是生活中的点滴积累。一次独立完成家务、一次勇敢表达自我、一次主动帮助他人，这些小事都让我变得更加成熟。生活教会我包容、责任与感恩。每一天的经历，都是成长的养分。`,
        views: 60,
        likes: 5,
        comments: []
    },
    {
        id: 4,
        title: "健康生活分享",
        tags: ['健康'],
        time: "2025-06-20 08:00 AM PDT",
        user: { name: "user4", avatar: 'https://randomuser.me/api/portraits/men/4.jpg' },
        content: `健康的生活离不开良好的习惯。我每天坚持早睡早起，饮食清淡，多喝水，少熬夜。周末会和朋友一起打球、爬山，既锻炼身体又放松心情。健康不仅是身体的状态，更是积极乐观的生活态度。`,
        views: 40,
        likes: 2,
        comments: []
    },
    {
        id: 5,
        title: "情感成长的心路历程",
        tags: ['情感', '成长'],
        time: "2025-06-19 07:00 AM PDT",
        user: { name: "user5", avatar: 'https://randomuser.me/api/portraits/women/5.jpg' },
        content: `从青涩到成熟，情感的成长是一段漫长的旅程。曾经的我容易被情绪左右，遇到问题只会逃避。后来学会了倾听和沟通，懂得了理解和包容。每一次情感的波动，都是自我成长的机会。感谢那些陪伴我成长的人，让我学会了爱与被爱。`,
        views: 30,
        likes: 1,
        comments: []
    },
];

const CommunityPostPage = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    
    // 查找对应帖子
    const post = allPosts.find(p => String(p.id) === String(id));
    const [postData, setPostData] = useState(post);

    // 获取相关帖子（同标签或同作者）
    const relatedPosts = allPosts
        .filter(p => p.id !== parseInt(id) && (
            p.tags.some(tag => postData?.tags.includes(tag)) || 
            p.user.name === postData?.user.name
        ))
        .slice(0, 3);

    const handleNewComment = (newComment) => {
        setPostData(prevData => ({
            ...prevData,
            comments: [...(prevData.comments || []), {
                user: { name: "NewUser", avatar: "https://via.placeholder.com/50" },
                content: newComment
            }]
        }));
    };

    if (!postData) {
        return (
            <div className="post-not-found">
                <h2>帖子不存在</h2>
                <button onClick={() => navigate('/community')}>返回社区</button>
            </div>
        );
    }

    return (
        <div className="community-post-page">
            {/* 返回按钮 */}
            <div className="post-back-section">
                <button 
                    className="back-btn"
                    onClick={() => navigate(-1)}
                >
                    <ArrowLeftOutlined /> 返回
                </button>
            </div>

            <div className="post-layout">
                {/* 主内容区 */}
                <div className="post-main-content">
                    <PostDetail postData={postData} />
                    
                    {/* 评论区域 */}
                    <div className="comments-section">
                        <div className="comments-header">
                            <h3>评论区</h3>
                            <span className="comments-count">
                              {postData.comments?.length || 0} 条评论
                            </span>
                        </div>
                        <CommentInput onSubmit={handleNewComment} />
                        <PostCommentList comments={postData.comments || []} />
                    </div>
                </div>

                {/* 右侧边栏 */}
                <div className="post-sidebar">
                    {/* 作者信息 */}
                    <div className="sidebar-card author-card">
                        <div className="sidebar-header">
                            <UserOutlined className="sidebar-icon" />
                            <h4>作者信息</h4>
                        </div>
                        <div className="author-info">
                            <img 
                                src={postData.user.avatar} 
                                alt={postData.user.name}
                                className="author-avatar"
                            />
                            <div className="author-details">
                                <div className="author-name">{postData.user.name}</div>
                                <div className="author-stats">
                                    <span>帖子: 12</span>
                                    <span>获赞: 89</span>
                                </div>
                            </div>
                        </div>
                    </div>

                    {/* 相关帖子 */}
                    {relatedPosts.length > 0 && (
                        <div className="sidebar-card related-posts">
                            <div className="sidebar-header">
                                <FireOutlined className="sidebar-icon" />
                                <h4>相关帖子</h4>
                            </div>
                            <div className="related-posts-list">
                                {relatedPosts.map(relatedPost => (
                                    <div 
                                        key={relatedPost.id}
                                        className="related-post-item"
                                        onClick={() => navigate(`/post/${relatedPost.id}`)}
                                    >
                                        <h5 className="related-post-title">
                                            {relatedPost.title}
                                        </h5>
                                        <div className="related-post-meta">
                                            <span>@{relatedPost.user.name}</span>
                                            <span>{relatedPost.views} 浏览</span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}

                    {/* 快捷操作 */}
                    <div className="sidebar-card quick-actions">
                        <div className="sidebar-header">
                            <TagOutlined className="sidebar-icon" />
                            <h4>快捷操作</h4>
                        </div>
                        <div className="action-buttons">
                            <button 
                                className="action-btn"
                                onClick={() => navigate('/community')}
                            >
                                浏览更多帖子
                            </button>
                            <button 
                                className="action-btn primary"
                                onClick={() => navigate('/send_community_post')}
                            >
                                发布新帖子
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CommunityPostPage;