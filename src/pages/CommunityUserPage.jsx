import React from 'react';
import CommunityUserContent from '../components/CommunityUserContent.jsx';
import CreatePosts from '../components/CreatePosts.jsx';
import '../css/CommunityUserPage.css';
import Layout from "../components/Layout";
import { Button } from 'antd';
import { ArrowLeftOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

function CommunityUserPage() {
    const navigate = useNavigate();
    return (
        <div className="main-content">
            <div style={{ maxWidth: 900, margin: '0 auto' }}>
                <Button
                    type="link"
                    icon={<ArrowLeftOutlined />}
                    onClick={() => navigate('/community')}
                    style={{ margin: '16px 0' }}
                >
                    返回首页
                </Button>
                <CommunityUserContent />
                <CreatePosts />
            </div>
        </div>
    );
}

export default CommunityUserPage;