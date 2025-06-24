import React, { useState } from 'react';
import '../css/CommunityContent.css';
import PostCard from './PostCard.jsx';

// 假数据
const allPosts = [
    {
        id: 1,
        tags: ['Tag1', 'Tag2'],
        avatar: 'https://randomuser.me/api/portraits/women/1.jpg',
        username: 'user1',
        summary: '帖子简介',
        top: true,
    },
    {
        id: 2,
        tags: ['Tag2'],
        avatar: 'https://randomuser.me/api/portraits/men/2.jpg',
        username: 'user2',
        summary: '另一个帖子简介',
        top: false,
    },
    {
        id: 3,
        tags: ['Tag3'],
        avatar: 'https://randomuser.me/api/portraits/women/3.jpg',
        username: 'user3',
        summary: '更多内容...',
        top: false,
    },
];

const allTags = ['全部', ...Array.from(new Set(allPosts.flatMap(p => p.tags)))];

function CommunityContent() {
    const [selectedTag, setSelectedTag] = useState('全部');

    // 推荐置顶帖子
    const topPosts = allPosts.filter(post => post.top);
    // 标签筛选
    const filteredPosts = selectedTag === '全部'
        ? allPosts.filter(post => !post.top)
        : allPosts.filter(post => post.tags.includes(selectedTag) && !post.top);

    return (
        <div className="community-content">
            {/* 推荐置顶帖子模块 */}
            <section className="top-posts-section">
                <h1>今日心有灵犀</h1>
                {topPosts.length === 0 ? <div>暂无推荐</div> : topPosts.map(post => (
                    <PostCard post={post} key={post.id} />
                ))}
            </section>

            {/* 按标签浏览帖子模块 */}
            <section className="tag-posts-section">
                <div className="tag-filter-bar">
                    <label htmlFor="tag-select">按标签浏览：</label>
                    <select
                        id="tag-select"
                        value={selectedTag}
                        onChange={e => setSelectedTag(e.target.value)}
                    >
                        {allTags.map(tag => (
                            <option value={tag} key={tag}>{tag}</option>
                        ))}
                    </select>
                </div>
                <div className="tag-posts-list">
                    {filteredPosts.length === 0 ? <div>暂无帖子</div> : filteredPosts.map(post => (
                        <PostCard post={post} key={post.id} />
                    ))}
                </div>
            </section>
        </div>
    );
}

export default CommunityContent;