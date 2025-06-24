import React from 'react';
import CommunityHeader from '../components/CommunityHeader.jsx';
import CommunityContent from '../components/CommunityContent.jsx';
import CreatePosts from '../components/CreatePosts.jsx';
import '../css/CommunityHomepage.css';
import Layout from "../components/Layout";
import {Card} from "antd";

function CommunityHomepage() {
    return (
        <div className="main-content">
            <CommunityHeader />
            <Card className="card-container">
                <CommunityContent />
                <CreatePosts />
            </Card>
        </div>
    );
}

export default CommunityHomepage;