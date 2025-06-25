import React, { useState, useEffect } from 'react';
import CommunityHeader from '../components/CommunityHeader.jsx';
import CommunityContent from '../components/CommunityContent.jsx';
import CreatePosts from '../components/CreatePosts.jsx';
import '../css/CommunityHomepage.css';
import Layout from "../components/Layout";
import {Card} from "antd";

// API服务导入注释
// import { getPostList, createPost } from '../service/communityService';

function CommunityHomepage() {
    const [posts, setPosts] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadPosts();
    }, []);

    // 加载帖子列表的函数
    const loadPosts = async () => {
        try {
            setLoading(true);
            
            // API调用注释：获取帖子列表
            // const response = await getPostList({
            //   page: 1,
            //   size: 20,
            //   sortBy: '最新'
            // });
            // setPosts(response.data);
            
        } catch (error) {
            console.error('获取帖子列表失败:', error);
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
        //   // 重新加载帖子列表
        //   loadPosts();
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
            <Card className="card-container">
                <CommunityContent posts={posts} onRefresh={loadPosts} />
                <CreatePosts onCreatePost={handleCreatePost} />
            </Card>
        </div>
    );
}

export default CommunityHomepage;