import React, { useState, useEffect } from 'react';
import CommunityHeader from '../components/CommunityHeader.jsx';
import CommunityUserContent from '../components/CommunityUserContent.jsx';
import CreatePosts from '../components/CreatePosts.jsx';
import '../css/CommunityUserPage.css';
import Layout from "../components/Layout";

// API服务导入注释
// import { getUserInfo } from '../service/userService';
// import { getPostList, createPost } from '../service/communityService';

function CommunityUserPage() {
    const [userInfo, setUserInfo] = useState(null);
    const [userPosts, setUserPosts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadUserData();
    }, []);

    // 加载用户数据的函数
    const loadUserData = async () => {
        try {
            setLoading(true);
            
            // API调用注释：获取用户信息
            // const userRes = await getUserInfo();
            // setUserInfo(userRes.data);
            
            // API调用注释：获取用户发布的帖子
            // const postsRes = await getPostList({
            //   page: 1,
            //   size: 20,
            //   authorId: userRes.data.id // 获取当前用户的帖子
            // });
            // setUserPosts(postsRes.data);
            
        } catch (error) {
            console.error('加载用户数据失败:', error);
        } finally {
            setLoading(false);
        }
    };

    // 创建新帖子的函数
    const handleCreatePost = async (postData) => {
        // API调用注释：创建帖子
        // try {
        //   const response = await createPost(postData);
        //   console.log('帖子发布成功:', response.data);
        //   // 重新加载用户帖子列表
        //   loadUserData();
        // } catch (error) {
        //   console.error('发布帖子失败:', error);
        //   throw error;
        // }
    };

    if (loading) {
        return <div className="loading">加载中...</div>;
    }

    return (
        <div className="main-content">
            <CommunityHeader />
            <CommunityUserContent userInfo={userInfo} userPosts={userPosts} onRefresh={loadUserData} />
            <CreatePosts onCreatePost={handleCreatePost} />
        </div>
    );
}

export default CommunityUserPage;