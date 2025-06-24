import React, {useState} from 'react';
import PostDetail from '../components/post_detail';
import CommentInput from '../components/comment_input';
import PostCommentList from '../components/post_comment_list';
import {postData as initialPostData} from '../data/post_data';
import {Card, Divider, Space} from "antd";
import "../css/post.css"

const PostPage = () => {
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
        // <div>
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
        // </div>
    );
};

export default PostPage;