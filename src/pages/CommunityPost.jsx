import React, {useState} from 'react';
import PostDetail from '../components/CommunityPostDetail';
import CommentInput from '../components/CommentIinput';
import PostCommentList from '../components/CommunityPostCommentList';
import {postData as initialPostData} from '../data/post_data';
import {Card, Divider, Space} from "antd";
import "../css/post.css"
import CommunityHeader from "../components/CommunityHeader";
import Layout from "../components/Layout";

const CommunityPostPage = () => {
    const [postData, setPostData] = useState(initialPostData);

    const handleNewComment = (newComment) => {
        setPostData(prevData => ({
            ...prevData,
            comments: [...prevData.comments, {
                user: {name: "NewUser", avatar: "https://via.placeholder.com/50"},
                content: newComment
            }]
        }));
    };

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