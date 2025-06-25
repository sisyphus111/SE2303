import React, {useState, useEffect} from 'react';
import { useParams } from 'react-router-dom';
import PostDetail from '../components/CommunityPostDetail';
import CommentInput from '../components/CommentIinput';
import PostCommentList from '../components/CommunityPostCommentList';
import {postData as initialPostData} from '../data/post_data';
import {Card, Divider, Space} from "antd";
import "../css/post.css"
import CommunityHeader from "../components/CommunityHeader";
import Layout from "../components/Layout";

// API服务导入注释
// import { getPostDetail, createComment } from '../service/communityService';

const CommunityPostPage = () => {
    const { postId } = useParams();
    const [postData, setPostData] = useState(initialPostData);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        loadPostDetail();
    }, [postId]);

    // 加载帖子详情的函数
    const loadPostDetail = async () => {
        try {
            setLoading(true);
            
            // API调用注释：获取帖子详情
            // const response = await getPostDetail(postId);
            // setPostData(response.data);
            
            // 使用模拟数据作为备选
            setPostData(initialPostData);
            
        } catch (error) {
            console.error('获取帖子详情失败:', error);
            // 使用模拟数据作为备选
            setPostData(initialPostData);
        } finally {
            setLoading(false);
        }
    };

    const handleNewComment = async (newComment) => {
        // API调用注释：创建评论
        // try {
        //   const commentData = {
        //     postId: postId,
        //     content: newComment,
        //     author: '当前用户', // 从用户状态获取
        //     authorAvatar: '👤' // 从用户状态获取
        //   };
        //   
        //   const response = await createComment(commentData);
        //   console.log('评论发布成功:', response.data);
        //   
        //   // 重新加载帖子详情以获取最新评论
        //   loadPostDetail();
        // } catch (error) {
        //   console.error('发布评论失败:', error);
        //   alert('评论发布失败，请重试');
        // }

        // 使用本地状态更新作为备选
        setPostData(prevData => ({
            ...prevData,
            comments: [...prevData.comments, {
                user: {name: "NewUser", avatar: "https://via.placeholder.com/50"},
                content: newComment
            }]
        }));
    };

    if (loading) {
        return <div className="loading">加载中...</div>;
    }

    return (
        <div>
            <CommunityHeader />
            <Card className="card-container">
                <Space direction="vertical" style={{width: "100%"}}>
                    <PostDetail postData={postData}/>
                    <div style={{margin: 20}}>
                        <Divider>评论</Divider>
                        <CommentInput onSubmit={handleNewComment}/>
                        <PostCommentList comments={postData.comments}/>
                    </div>
                </Space>
            </Card>
        </div>
    );
};

export default CommunityPostPage;