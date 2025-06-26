import React from 'react';
import '../css/PostCard.css';
import {Link} from "react-router-dom";

function PostCard({post}) {
    return (
        <div className="post-card new-format">
            <div className="post-card-left">
                <Link to={`/post/${post.id}`} className="post-title-link">
                    <div className="post-title">{post.summary}</div>
                </Link>
                <div className="post-tags">
                    {post.tags.map(tag => (
                        <span className="post-tag" key={tag}>#{tag}</span>
                    ))}
                </div>
            </div>
            <div className="post-card-right">
                <img className="post-avatar" src={post.avatar} alt="avatar"/>
                <span className="post-username">{post.username}</span>
            </div>
        </div>
    );
}

export default PostCard;
