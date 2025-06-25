import React, {useState} from 'react';
import '../css/PostCard.css';
import {Link} from "react-router-dom";

function PostCard({post}) {
    const [liked, setLiked] = useState(false);

    return (

        <div className="post-card">
            <div className="post-tags">
                {post.tags.map(tag => (
                    <span className="post-tag" key={tag}>#{tag}</span>
                ))}
            </div>
            <div className="post-main">
                <div className="post-user">
                    <img className="post-avatar" src={post.avatar} alt="avatar"/>
                    <span className="post-username">{post.username}</span>
                </div>
                <Link to={`/post/${post.id}`} className={`post-card a`}>
                    <div className="post-summary">{post.summary}</div>
                </Link>
                <button
                    className={`post-like-btn${liked ? ' liked' : ''}`}
                    onClick={() => setLiked(l => !l)}
                >
                    <span
                        className="post-like-icon"
                        role="img"
                        aria-label="like"
                        style={{
                            color: liked ? '#fff' : '#fa3e5f',
                            background: liked ? '#fa3e5f' : '#fff',
                            borderColor: liked ? '#fa3e5f' : '#b2f2c9'
                        }}
                    >❤️</span>
                </button>
            </div>
        </div>


    );
}

export default PostCard;
