import React, { useState } from 'react';
import PostDetail from '../components/CommunityPostDetail';
import CommentInput from '../components/CommentIinput';
import PostCommentList from '../components/CommunityPostCommentList';
import { Card, Divider, Space, Button, Input } from "antd";
import "../css/post.css"
import Layout from "../components/Layout";
import { useParams, useNavigate } from 'react-router-dom';
import { SearchOutlined, UserOutlined } from '@ant-design/icons';

const allTags = ['全部', '情感', '学习', '生活', '健康', '成长'];

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
    const [selectedTag, setSelectedTag] = useState('全部');
    const [searchValue, setSearchValue] = useState('');
    // 查找对应帖子
    const post = allPosts.find(p => String(p.id) === String(id));
    const [postData, setPostData] = useState(post);

    const handleNewComment = (newComment) => {
        setPostData(prevData => ({
            ...prevData,
            comments: [...(prevData.comments || []), {
                user: { name: "NewUser", avatar: "https://via.placeholder.com/50" },
                content: newComment
            }]
        }));
    };

    // 保证侧边栏和主页一致
    return (
        <div className="community-homepage-layout">
            <aside className="sidebar custom-sidebar">
                <Button
                    type="text"
                    style={{ width: '100%', marginBottom: 18, borderRadius: 12, fontWeight: 700, fontSize: 20, color: '#4e9a9a', background: 'none', letterSpacing: 2 }}
                    onClick={() => navigate('/community')}
                >
                    广场
                </Button>
                <Button
                    icon={<UserOutlined />}
                    type="primary"
                    style={{ width: '100%', marginBottom: 24, borderRadius: 12 }}
                    onClick={() => navigate('/community_user_page')}
                >
                    我的帖子
                </Button>
                <div className="sidebar-title">按标签筛选</div>
                <ul className="sidebar-tag-list">
                    {allTags.map(tag => (
                        <li
                            key={tag}
                            className={`sidebar-tag-item${selectedTag === tag ? ' active' : ''}`}
                            onClick={() => {
                                navigate(`/community?tag=${encodeURIComponent(tag)}`);
                            }}
                        >
                            {tag}
                        </li>
                    ))}
                </ul>
            </aside>
            <Card className="card-container">
                <Space direction="vertical" style={{ width: "100%" }}>
                    <PostDetail postData={postData} />
                    <div style={{ margin: 20 }}>
                        <Divider>评论</Divider>
                        <CommentInput onSubmit={handleNewComment} />
                        <PostCommentList comments={postData.comments || []} />
                    </div>
                </Space>
            </Card>
        </div>
    );
};

export default CommunityPostPage;