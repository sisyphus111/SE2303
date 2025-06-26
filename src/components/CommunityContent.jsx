import React from 'react';
import '../css/CommunityContent.css';
import PostCard from './CommunityPostCard.jsx';

// 假数据（标签与侧边栏一致）
const allPosts = [
    {
        id: 1,
        tags: ['情感', '学习'],
        avatar: 'https://randomuser.me/api/portraits/women/1.jpg',
        username: 'user1',
        summary: '情感与学习的成长故事',
        top: true,
    },
    {
        id: 2,
        tags: ['学习', '健康'],
        avatar: 'https://randomuser.me/api/portraits/men/2.jpg',
        username: 'user2',
        summary: '学习与健康的平衡',
        top: false,
    },
    {
        id: 3,
        tags: ['生活', '成长'],
        avatar: 'https://randomuser.me/api/portraits/women/3.jpg',
        username: 'user3',
        summary: '生活中的成长点滴',
        top: false,
    },
    {
        id: 4,
        tags: ['健康'],
        avatar: 'https://randomuser.me/api/portraits/men/4.jpg',
        username: 'user4',
        summary: '健康生活分享',
        top: false,
    },
    {
        id: 5,
        tags: ['情感', '成长'],
        avatar: 'https://randomuser.me/api/portraits/women/5.jpg',
        username: 'user5',
        summary: '情感成长的心路历程',
        top: false,
    },
];

function CommunityContent({ selectedTag, searchValue = '' }) {
    // 所有帖子都参与筛选
    let filteredPosts = selectedTag === '全部'
        ? allPosts
        : allPosts.filter(post => post.tags.includes(selectedTag));

    // 搜索过滤
    if (searchValue && searchValue.trim() !== '') {
        filteredPosts = filteredPosts.filter(post =>
            post.summary.includes(searchValue) ||
            post.username.includes(searchValue) ||
            post.tags.some(tag => tag.includes(searchValue))
        );
    }

    return (
        <div className="community-content">
            <section className="tag-posts-section">
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