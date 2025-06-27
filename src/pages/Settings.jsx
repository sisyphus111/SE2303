import React, { useState } from "react";
import { 
  Typography, 
  Tabs, 
  Form, 
  Button, 
  Switch, 
  Radio, 
  Select, 
  Divider,
  Alert,
  Modal,
  message
} from "antd";
import {
  SettingOutlined,
  HeartOutlined,
  DatabaseOutlined,
  DeleteOutlined,
  ExclamationCircleOutlined,
  CheckCircleOutlined,
  BellOutlined,
  GlobalOutlined,
  SoundOutlined,
  EyeOutlined
} from '@ant-design/icons';
import { useUser } from '../lib/UserContext';
import "../css/Settings.css";

const { Title, Text } = Typography;
const { Option } = Select;
const { confirm } = Modal;

function Settings() {
  const { user, updatePreferences } = useUser();
  const [activeTab, setActiveTab] = useState('notifications');
  const [loading, setLoading] = useState(false);
  const [settings, setSettings] = useState({
    // 通知设置
    emailNotifications: true,
    pushNotifications: true,
    commentNotifications: true,
    likeNotifications: false,
    mentionNotifications: true,
    systemNotifications: true,
    
    // 个人偏好
    theme: 'light',
    language: 'zh-CN',
    fontSize: 'medium',
    autoPlayMedia: false,
    soundEnabled: true,
    
    // 心理健康设置
    crisisInterventionEnabled: true,
    moodTrackingEnabled: true,
    anonymousPosting: false,
    mentalHealthDataSharing: false,
    
    // 社区交互设置
    autoFollow: false,
    showPostStats: true,
    enablePostRecommendation: true,
    blockSensitiveContent: true
  });

  const handleSave = async (section, values) => {
    setLoading(true);
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      const updatedSettings = { ...settings, ...values };
      setSettings(updatedSettings);
      
      // 更新用户偏好到上下文
      if (section === 'preferences') {
        await updatePreferences(values);
      }
      
      message.success('设置保存成功！');
    } catch (error) {
      message.error('保存失败，请重试');
    } finally {
      setLoading(false);
    }
  };

  const handleSettingChange = async (key, value) => {
    const updatedSettings = { ...settings, [key]: value };
    setSettings(updatedSettings);
    
    // 实时保存特定设置
    if (['theme', 'language', 'soundEnabled'].includes(key)) {
      await updatePreferences({ [key]: value });
    }
    
    message.success('设置已更新');
  };

  const handleDeleteAccount = () => {
    confirm({
      title: '确认删除账户',
      icon: <ExclamationCircleOutlined />,
      content: '删除账户将永久删除您的所有数据，此操作不可恢复。',
      okText: '确认删除',
      okType: 'danger',
      cancelText: '取消',
      onOk() {
        message.success('账户删除请求已提交');
      },
    });
  };

  // 通知设置
  const NotificationSettings = () => (
    <div className="settings-section">
      <div className="section-header">
        <BellOutlined className="section-icon" />
        <div>
          <h3>通知设置</h3>
          <p>管理您接收通知的方式和类型</p>
        </div>
      </div>

      <div className="notification-groups">
        <div className="notification-group">
          <h4>通知方式</h4>
          <div className="notification-switches">
            <div className="notification-switch-item">
              <div className="notification-info">
                <div className="notification-title">
                  <BellOutlined style={{ color: 'var(--primary-green)' }} />
                  <strong>推送通知</strong>
                </div>
                <span>在浏览器中接收实时推送通知</span>
              </div>
              <Switch 
                checked={settings.pushNotifications} 
                onChange={(checked) => handleSettingChange('pushNotifications', checked)} 
              />
            </div>

            <div className="notification-switch-item">
              <div className="notification-info">
                <div className="notification-title">
                  <GlobalOutlined style={{ color: 'var(--primary-green)' }} />
                  <strong>邮件通知</strong>
                </div>
                <span>通过邮件接收重要通知</span>
              </div>
              <Switch 
                checked={settings.emailNotifications} 
                onChange={(checked) => handleSettingChange('emailNotifications', checked)} 
              />
            </div>
          </div>
        </div>

        <div className="notification-group">
          <h4>通知类型</h4>
          <div className="notification-switches">
            <div className="notification-switch-item">
              <div className="notification-info">
                <div className="notification-title">
                  <strong>评论通知</strong>
                </div>
                <span>有人评论您的帖子时通知您</span>
              </div>
              <Switch 
                checked={settings.commentNotifications} 
                onChange={(checked) => handleSettingChange('commentNotifications', checked)} 
              />
            </div>

            <div className="notification-switch-item">
              <div className="notification-info">
                <div className="notification-title">
                  <strong>点赞通知</strong>
                </div>
                <span>有人点赞您的内容时通知您</span>
              </div>
              <Switch 
                checked={settings.likeNotifications} 
                onChange={(checked) => handleSettingChange('likeNotifications', checked)} 
              />
            </div>

            <div className="notification-switch-item">
              <div className="notification-info">
                <div className="notification-title">
                  <strong>提及通知</strong>
                </div>
                <span>有人在帖子中提及您时通知您</span>
              </div>
              <Switch 
                checked={settings.mentionNotifications} 
                onChange={(checked) => handleSettingChange('mentionNotifications', checked)} 
              />
            </div>

            <div className="notification-switch-item">
              <div className="notification-info">
                <div className="notification-title">
                  <strong>系统通知</strong>
                </div>
                <span>接收系统公告和重要更新</span>
              </div>
              <Switch 
                checked={settings.systemNotifications} 
                onChange={(checked) => handleSettingChange('systemNotifications', checked)} 
              />
            </div>
          </div>
        </div>
      </div>

      <Button 
        type="primary" 
        onClick={() => handleSave('notifications', settings)} 
        loading={loading}
        style={{ marginTop: 24 }}
      >
        保存通知设置
      </Button>
    </div>
  );

  // 个人偏好设置
  const PreferenceSettings = () => (
    <div className="settings-section">
      <div className="section-header">
        <SettingOutlined className="section-icon" />
        <div>
          <h3>个人偏好</h3>
          <p>自定义您的使用体验</p>
        </div>
      </div>

      <div className="preference-groups">
        <div className="preference-group">
          <h4>界面设置</h4>
          <div className="preference-item">
            <div className="preference-info">
              <div className="preference-title">主题模式</div>
              <div className="preference-desc">选择您喜欢的界面主题</div>
            </div>
            <Radio.Group 
              value={settings.theme} 
              onChange={(e) => handleSettingChange('theme', e.target.value)}
            >
              <Radio value="light">浅色模式</Radio>
              <Radio value="dark">深色模式</Radio>
              <Radio value="auto">跟随系统</Radio>
            </Radio.Group>
          </div>

          <div className="preference-item">
            <div className="preference-info">
              <div className="preference-title">语言</div>
              <div className="preference-desc">选择界面显示语言</div>
            </div>
            <Select 
              value={settings.language}
              onChange={(value) => handleSettingChange('language', value)}
              style={{ width: 150 }}
            >
              <Option value="zh-CN">简体中文</Option>
              <Option value="zh-TW">繁體中文</Option>
              <Option value="en-US">English</Option>
            </Select>
          </div>

          <div className="preference-item">
            <div className="preference-info">
              <div className="preference-title">字体大小</div>
              <div className="preference-desc">调整界面文字显示大小</div>
            </div>
            <Radio.Group 
              value={settings.fontSize} 
              onChange={(e) => handleSettingChange('fontSize', e.target.value)}
            >
              <Radio value="small">小</Radio>
              <Radio value="medium">中</Radio>
              <Radio value="large">大</Radio>
            </Radio.Group>
          </div>
        </div>

        <div className="preference-group">
          <h4>媒体设置</h4>
          <div className="preference-switches">
            <div className="preference-switch-item">
              <div className="preference-info">
                <div className="preference-title">
                  <SoundOutlined style={{ color: 'var(--primary-green)' }} />
                  <strong>音效</strong>
                </div>
                <span>开启界面操作音效和提示音</span>
              </div>
              <Switch 
                checked={settings.soundEnabled} 
                onChange={(checked) => handleSettingChange('soundEnabled', checked)} 
              />
            </div>

            <div className="preference-switch-item">
              <div className="preference-info">
                <div className="preference-title">
                  <EyeOutlined style={{ color: 'var(--primary-green)' }} />
                  <strong>自动播放媒体</strong>
                </div>
                <span>自动播放帖子中的视频和音频内容</span>
              </div>
              <Switch 
                checked={settings.autoPlayMedia} 
                onChange={(checked) => handleSettingChange('autoPlayMedia', checked)} 
              />
            </div>
          </div>
        </div>

        <div className="preference-group">
          <h4>社区交互</h4>
          <div className="preference-switches">
            <div className="preference-switch-item">
              <div className="preference-info">
                <div className="preference-title">
                  <strong>显示帖子统计</strong>
                </div>
                <span>显示帖子的点赞数、评论数等统计信息</span>
              </div>
              <Switch 
                checked={settings.showPostStats} 
                onChange={(checked) => handleSettingChange('showPostStats', checked)} 
              />
            </div>

            <div className="preference-switch-item">
              <div className="preference-info">
                <div className="preference-title">
                  <strong>智能推荐</strong>
                </div>
                <span>根据您的兴趣推荐相关帖子和用户</span>
              </div>
              <Switch 
                checked={settings.enablePostRecommendation} 
                onChange={(checked) => handleSettingChange('enablePostRecommendation', checked)} 
              />
            </div>

            <div className="preference-switch-item">
              <div className="preference-info">
                <div className="preference-title">
                  <strong>过滤敏感内容</strong>
                </div>
                <span>自动隐藏可能包含敏感信息的内容</span>
              </div>
              <Switch 
                checked={settings.blockSensitiveContent} 
                onChange={(checked) => handleSettingChange('blockSensitiveContent', checked)} 
              />
            </div>
          </div>
        </div>
      </div>

      <Button 
        type="primary" 
        onClick={() => handleSave('preferences', settings)} 
        loading={loading}
        style={{ marginTop: 24 }}
      >
        保存偏好设置
      </Button>
    </div>
  );

  // 心理健康设置
  const MentalHealthSettings = () => (
    <div className="settings-section">
      <div className="section-header">
        <HeartOutlined className="section-icon" />
        <div>
          <h3>心理健康设置</h3>
          <p>管理您的心理健康相关功能</p>
        </div>
      </div>

      <Alert
        message="重要提醒"
        description="这些设置关系到您的心理健康数据安全和隐私保护，请仔细选择。"
        type="info"
        showIcon
        style={{ marginBottom: 24 }}
      />

      <div className="mental-health-switches">
        <div className="mental-health-switch-item">
          <div className="mental-health-info">
            <div className="mental-health-title">
              <CheckCircleOutlined style={{ color: 'var(--primary-green)' }} />
              <strong>危机干预功能</strong>
            </div>
            <span>当检测到心理危机信号时，系统将主动提供帮助资源</span>
          </div>
          <Switch 
            checked={settings.crisisInterventionEnabled} 
            onChange={(checked) => handleSettingChange('crisisInterventionEnabled', checked)} 
          />
        </div>

        <div className="mental-health-switch-item">
          <div className="mental-health-info">
            <div className="mental-health-title">
              <HeartOutlined style={{ color: 'var(--primary-green)' }} />
              <strong>情绪追踪</strong>
            </div>
            <span>允许系统记录和分析您的情绪数据，提供个性化建议</span>
          </div>
          <Switch 
            checked={settings.moodTrackingEnabled} 
            onChange={(checked) => handleSettingChange('moodTrackingEnabled', checked)} 
          />
        </div>

        <div className="mental-health-switch-item">
          <div className="mental-health-info">
            <div className="mental-health-title">
              <CheckCircleOutlined style={{ color: 'var(--primary-green)' }} />
              <strong>匿名发帖</strong>
            </div>
            <span>允许您匿名发布敏感的心理健康相关内容</span>
          </div>
          <Switch 
            checked={settings.anonymousPosting} 
            onChange={(checked) => handleSettingChange('anonymousPosting', checked)} 
          />
        </div>

        <div className="mental-health-switch-item">
          <div className="mental-health-info">
            <div className="mental-health-title">
              <HeartOutlined style={{ color: 'var(--primary-green)' }} />
              <strong>心理健康数据分享</strong>
            </div>
            <span>允许与专业心理健康机构分享匿名统计数据，帮助改善服务</span>
          </div>
          <Switch 
            checked={settings.mentalHealthDataSharing} 
            onChange={(checked) => handleSettingChange('mentalHealthDataSharing', checked)} 
          />
        </div>
      </div>

      <Button 
        type="primary" 
        onClick={() => handleSave('mentalHealth', settings)} 
        loading={loading} 
        style={{ marginTop: 24 }}
      >
        保存心理健康设置
      </Button>
    </div>
  );

  // 数据管理
  const DataManagement = () => (
    <div className="settings-section">
      <div className="section-header">
        <DatabaseOutlined className="section-icon" />
        <div>
          <h3>数据管理</h3>
          <p>管理您的个人数据</p>
        </div>
      </div>

      <div className="data-management-actions">
        <div className="data-action-item">
          <div className="data-action-info">
            <div className="data-action-title">
              <DatabaseOutlined className="data-action-icon" />
              <strong>导出数据</strong>
            </div>
            <p>下载您在平台上的所有数据，包括帖子、评论、个人信息等</p>
          </div>
          <Button type="primary" ghost onClick={() => message.success('数据导出请求已提交')}>导出数据</Button>
        </div>

        <Divider />

        <div className="data-action-item danger">
          <div className="data-action-info">
            <div className="data-action-title">
              <DeleteOutlined className="data-action-icon danger" />
              <strong>删除账户</strong>
            </div>
            <p>永久删除您的账户和所有相关数据。此操作不可恢复，请谨慎操作。</p>
          </div>
          <Button type="primary" danger onClick={handleDeleteAccount}>删除账户</Button>
        </div>
      </div>
    </div>
  );

  const tabItems = [
    {
      key: 'notifications',
      label: (<span><BellOutlined />通知设置</span>),
      children: <NotificationSettings />
    },
    {
      key: 'preferences',
      label: (<span><SettingOutlined />个人偏好</span>),
      children: <PreferenceSettings />
    },
    {
      key: 'mental-health',
      label: (<span><HeartOutlined />心理健康</span>),
      children: <MentalHealthSettings />
    },
    {
      key: 'data',
      label: (<span><DatabaseOutlined />数据管理</span>),
      children: <DataManagement />
    }
  ];

  return (
    <div className="settings-page">
      <div className="settings-container">
        <div className="settings-header">
          <Title level={2} className="settings-title">系统设置</Title>
          <Text type="secondary" className="settings-subtitle">自定义您的使用体验和功能偏好</Text>
        </div>

        <div className="settings-content">
          <Tabs
            activeKey={activeTab}
            onChange={setActiveTab}
            tabPosition="left"
            size="large"
            items={tabItems}
            className="settings-tabs"
          />
        </div>
      </div>
    </div>
  );
}

export default Settings;