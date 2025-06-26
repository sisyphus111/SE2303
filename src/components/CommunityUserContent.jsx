import React, { useState } from 'react';
import '../css/CommunityUserContent.css';
import PostCard from './CommunityPostCard.jsx';

// 假数据：个人帖子
const userPosts = [
    {
        id: 1,
        tags: ['生活', '成长'],
        avatar: 'https://randomuser.me/api/portraits/women/1.jpg',
        username: 'user1',
        summary: '今天的心情不错！',
    },
    {
        id: 2,
        tags: ['学习', '健康'],
        avatar: 'https://randomuser.me/api/portraits/women/1.jpg',
        username: 'user1',
        summary: 'React真的很有趣。',
    },
    {
        id: 3,
        tags: ['情感', '生活'],
        avatar: 'https://randomuser.me/api/portraits/women/1.jpg',
        username: 'user1',
        summary: '周末去爬山啦！',
    },
];

const allTags = ['全部', '情感', '学习', '生活', '健康', '成长'];

function CommunityUserContent() {
    const [selectedTag, setSelectedTag] = useState('全部');
    const filteredPosts = selectedTag === '全部'
        ? userPosts
        : userPosts.filter(post => post.tags.includes(selectedTag));

    return (
        <div className="user-content">
            <h1>我的帖子</h1>
            <div className="user-tag-filter-bar">
                <label htmlFor="user-tag-select">按标签筛选：</label>
                <select
                    id="user-tag-select"
                    value={selectedTag}
                    onChange={e => setSelectedTag(e.target.value)}
                >
                    {allTags.map(tag => (
                        <option value={tag} key={tag}>{tag}</option>
                    ))}
                </select>
            </div>
            <div className="user-posts-list">
                {filteredPosts.length === 0 ? <div>暂无帖子</div> : filteredPosts.map(post => (
                    <PostCard post={post} key={post.id} />
                ))}
            </div>
        </div>
    );
}

export default CommunityUserContent;