import React, { useState, useEffect } from "react";
import { 
  Typography, 
  Tabs, 
  Switch, 
  Alert,
  Modal,
  message,
  Spin
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
  LoadingOutlined
} from '@ant-design/icons';
import { useUser } from '../lib/UserContext';
import "../css/Settings.css";

const { Title, Text } = Typography;
const { confirm } = Modal;

function Settings() {
  const { userProfile, preferences, updateUserPreferences, loading: userLoading } = useUser();
  const [activeTab, setActiveTab] = useState('notifications');
  const [savingStates, setSavingStates] = useState({}); // 用于跟踪每个设置项的保存状态
  const [settings, setSettings] = useState({
    // 通知设置
    pushNotifications: true,
    emailNotifications: true,
    commentNotifications: true,
    likeNotifications: false,
    systemNotifications: true,
    
    // 个人偏好
    soundEnabled: true,
    showPostStats: true,
    enablePostRecommendation: true,
    blockSensitiveContent: true,
    
    // 心理健康设置
    crisisInterventionEnabled: true,
    moodTrackingEnabled: true,
    anonymousPosting: false,
    mentalHealthDataSharing: false
  });

  // 一次性同步 UserContext 中的偏好设置到本地 state，防止后续操作被覆盖
  const [isInitialized, setIsInitialized] = useState(false);
  useEffect(() => {
    // 当preferences不为空且包含我们期望的字段时，进行同步
    if (!isInitialized && preferences && typeof preferences === 'object') {
      const hasExpectedFields = 'pushNotifications' in preferences || 
                               'emailNotifications' in preferences || 
                               'soundEnabled' in preferences;
      
      if (hasExpectedFields) {
        console.log('📊 首次加载：同步偏好设置', preferences);
        console.log('📊 preferences字段详情:', {
          pushNotifications: preferences.pushNotifications,
          emailNotifications: preferences.emailNotifications,
          commentNotifications: preferences.commentNotifications,
          likeNotifications: preferences.likeNotifications,
          systemNotifications: preferences.systemNotifications,
          soundEnabled: preferences.soundEnabled,
          showPostStats: preferences.showPostStats,
          enablePostRecommendation: preferences.enablePostRecommendation,
          blockSensitiveContent: preferences.blockSensitiveContent,
          crisisInterventionEnabled: preferences.crisisInterventionEnabled,
          moodTrackingEnabled: preferences.moodTrackingEnabled,
          anonymousPosting: preferences.anonymousPosting,
          mentalHealthDataSharing: preferences.mentalHealthDataSharing
        });
        
        // 直接使用从后端获取的真实数据，而不是与默认值合并
        setSettings({
          pushNotifications: preferences.pushNotifications ?? true,
          emailNotifications: preferences.emailNotifications ?? true,
          commentNotifications: preferences.commentNotifications ?? true,
          likeNotifications: preferences.likeNotifications ?? false,
          systemNotifications: preferences.systemNotifications ?? true,
          soundEnabled: preferences.soundEnabled ?? true,
          showPostStats: preferences.showPostStats ?? true,
          enablePostRecommendation: preferences.enablePostRecommendation ?? true,
          blockSensitiveContent: preferences.blockSensitiveContent ?? true,
          crisisInterventionEnabled: preferences.crisisInterventionEnabled ?? true,
          moodTrackingEnabled: preferences.moodTrackingEnabled ?? true,
          anonymousPosting: preferences.anonymousPosting ?? false,
          mentalHealthDataSharing: preferences.mentalHealthDataSharing ?? false
        });
        setIsInitialized(true);
        console.log('✅ Settings状态已同步完成');
      }
    }
  }, [preferences, isInitialized]);

  // 监听activeTab变化，但只在需要时同步数据
  useEffect(() => {
    if (isInitialized && preferences) {
      console.log(`🔄 切换到${activeTab}标签页`);
      // 不再自动同步数据，保持用户的本地更改
      // 如果需要同步最新数据，用户可以使用"刷新"按钮
    }
  }, [activeTab, isInitialized]);

  // 自动保存设置的函数
  const handleSettingChange = async (key, value) => {
    console.log(`🔧 设置变更: ${key} = ${value}，开始自动保存...`);
    
    // 立即更新本地UI状态，提供即时反馈
    setSettings(prevSettings => ({
      ...prevSettings,
      [key]: value
    }));

    // 设置该项正在保存
    setSavingStates(prev => ({ ...prev, [key]: 'saving' }));

    try {
      // 调用API保存单个设置项
      const updateData = { [key]: value };
      console.log('📤 发送到后端的数据:', updateData);
      
      const result = await updateUserPreferences(updateData, true);
      console.log('📥 保存结果:', result);
      
      if (result.success) {
        // 保存成功
        setSavingStates(prev => ({ ...prev, [key]: 'success' }));
        console.log(`✅ ${key} 设置保存成功`);
        
        // 1秒后清除成功状态
        setTimeout(() => {
          setSavingStates(prev => ({ ...prev, [key]: null }));
        }, 1000);
      } else {
        throw new Error(result.error || '保存失败');
      }
    } catch (error) {
      console.error(`❌ ${key} 设置保存失败:`, error);
      
      // 保存失败，回滚本地状态
      setSettings(prevSettings => ({
        ...prevSettings,
        [key]: !value // 回滚到之前的值
      }));
      
      setSavingStates(prev => ({ ...prev, [key]: 'error' }));
      message.error(`设置保存失败: ${error.message}`);
      
      // 3秒后清除错误状态
      setTimeout(() => {
        setSavingStates(prev => ({ ...prev, [key]: null }));
      }, 3000);
    }
  };

  // 渲染设置项状态指示器
  const renderSavingIndicator = (key) => {
    const state = savingStates[key];
    if (!state) return null;

    const iconStyle = { marginLeft: 8, fontSize: 12 };
    
    switch (state) {
      case 'saving':
        return <LoadingOutlined style={{ ...iconStyle, color: '#1890ff' }} />;
      case 'success':
        return <CheckCircleOutlined style={{ ...iconStyle, color: '#52c41a' }} />;
      case 'error':
        return <ExclamationCircleOutlined style={{ ...iconStyle, color: '#ff4d4f' }} />;
      default:
        return null;
    }
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
                  {renderSavingIndicator('pushNotifications')}
                </div>
                <span>在浏览器中接收实时推送通知</span>
              </div>
              <Switch 
                checked={settings.pushNotifications} 
                onChange={(checked) => handleSettingChange('pushNotifications', checked)}
                loading={savingStates.pushNotifications === 'saving'}
              />
            </div>

            <div className="notification-switch-item">
              <div className="notification-info">
                <div className="notification-title">
                  <GlobalOutlined style={{ color: 'var(--primary-green)' }} />
                  <strong>邮件通知</strong>
                  {renderSavingIndicator('emailNotifications')}
                </div>
                <span>通过邮件接收重要通知</span>
              </div>
              <Switch 
                checked={settings.emailNotifications} 
                onChange={(checked) => handleSettingChange('emailNotifications', checked)}
                loading={savingStates.emailNotifications === 'saving'}
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
                  {renderSavingIndicator('commentNotifications')}
                </div>
                <span>有人评论您的帖子时通知您</span>
              </div>
              <Switch 
                checked={settings.commentNotifications} 
                onChange={(checked) => handleSettingChange('commentNotifications', checked)}
                loading={savingStates.commentNotifications === 'saving'}
              />
            </div>

            <div className="notification-switch-item">
              <div className="notification-info">
                <div className="notification-title">
                  <strong>点赞通知</strong>
                  {renderSavingIndicator('likeNotifications')}
                </div>
                <span>有人点赞您的内容时通知您</span>
              </div>
              <Switch 
                checked={settings.likeNotifications} 
                onChange={(checked) => handleSettingChange('likeNotifications', checked)}
                loading={savingStates.likeNotifications === 'saving'}
              />
            </div>

            <div className="notification-switch-item">
              <div className="notification-info">
                <div className="notification-title">
                  <strong>系统通知</strong>
                  {renderSavingIndicator('systemNotifications')}
                </div>
                <span>接收系统公告和重要更新</span>
              </div>
              <Switch 
                checked={settings.systemNotifications} 
                onChange={(checked) => handleSettingChange('systemNotifications', checked)}
                loading={savingStates.systemNotifications === 'saving'}
              />
            </div>
          </div>
        </div>
      </div>


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
          <h4>媒体设置</h4>
          <div className="preference-switches">
            <div className="preference-switch-item">
              <div className="preference-info">
                <div className="preference-title">
                  <SoundOutlined style={{ color: 'var(--primary-green)' }} />
                  <strong>音效</strong>
                  {renderSavingIndicator('soundEnabled')}
                </div>
                <span>开启界面操作音效和提示音</span>
              </div>
              <Switch 
                checked={settings.soundEnabled} 
                onChange={(checked) => handleSettingChange('soundEnabled', checked)}
                loading={savingStates.soundEnabled === 'saving'}
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
                  {renderSavingIndicator('showPostStats')}
                </div>
                <span>显示帖子的点赞数、评论数等统计信息</span>
              </div>
              <Switch 
                checked={settings.showPostStats} 
                onChange={(checked) => handleSettingChange('showPostStats', checked)}
                loading={savingStates.showPostStats === 'saving'}
              />
            </div>

            <div className="preference-switch-item">
              <div className="preference-info">
                <div className="preference-title">
                  <strong>智能推荐</strong>
                  {renderSavingIndicator('enablePostRecommendation')}
                </div>
                <span>根据您的兴趣推荐相关帖子和用户</span>
              </div>
              <Switch 
                checked={settings.enablePostRecommendation} 
                onChange={(checked) => handleSettingChange('enablePostRecommendation', checked)}
                loading={savingStates.enablePostRecommendation === 'saving'}
              />
            </div>

            <div className="preference-switch-item">
              <div className="preference-info">
                <div className="preference-title">
                  <strong>过滤敏感内容</strong>
                  {renderSavingIndicator('blockSensitiveContent')}
                </div>
                <span>自动隐藏可能包含敏感信息的内容</span>
              </div>
              <Switch 
                checked={settings.blockSensitiveContent} 
                onChange={(checked) => handleSettingChange('blockSensitiveContent', checked)}
                loading={savingStates.blockSensitiveContent === 'saving'}
              />
            </div>
          </div>
        </div>
      </div>


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
              {renderSavingIndicator('crisisInterventionEnabled')}
            </div>
            <span>当检测到心理危机信号时，系统将主动提供帮助资源</span>
          </div>
          <Switch 
            checked={settings.crisisInterventionEnabled} 
            onChange={(checked) => handleSettingChange('crisisInterventionEnabled', checked)}
            loading={savingStates.crisisInterventionEnabled === 'saving'}
          />
        </div>

        <div className="mental-health-switch-item">
          <div className="mental-health-info">
            <div className="mental-health-title">
              <HeartOutlined style={{ color: 'var(--primary-green)' }} />
              <strong>情绪追踪</strong>
              {renderSavingIndicator('moodTrackingEnabled')}
            </div>
            <span>允许系统记录和分析您的情绪数据，提供个性化建议</span>
          </div>
          <Switch 
            checked={settings.moodTrackingEnabled} 
            onChange={(checked) => handleSettingChange('moodTrackingEnabled', checked)}
            loading={savingStates.moodTrackingEnabled === 'saving'}
          />
        </div>

        <div className="mental-health-switch-item">
          <div className="mental-health-info">
            <div className="mental-health-title">
              <CheckCircleOutlined style={{ color: 'var(--primary-green)' }} />
              <strong>匿名发帖</strong>
              {renderSavingIndicator('anonymousPosting')}
            </div>
            <span>允许您匿名发布敏感的心理健康相关内容</span>
          </div>
          <Switch 
            checked={settings.anonymousPosting} 
            onChange={(checked) => handleSettingChange('anonymousPosting', checked)}
            loading={savingStates.anonymousPosting === 'saving'}
          />
        </div>

        <div className="mental-health-switch-item">
          <div className="mental-health-info">
            <div className="mental-health-title">
              <HeartOutlined style={{ color: 'var(--primary-green)' }} />
              <strong>心理健康数据分享</strong>
              {renderSavingIndicator('mentalHealthDataSharing')}
            </div>
            <span>允许与专业心理健康机构分享匿名统计数据，帮助改善服务</span>
          </div>
          <Switch 
            checked={settings.mentalHealthDataSharing} 
            onChange={(checked) => handleSettingChange('mentalHealthDataSharing', checked)}
            loading={savingStates.mentalHealthDataSharing === 'saving'}
          />
        </div>
      </div>


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
          <button 
            type="button" 
            className="export-btn"
            onClick={() => message.success('数据导出请求已提交')}
          >
            导出数据
          </button>
        </div>

        <div className="divider"></div>

        <div className="data-action-item danger">
          <div className="data-action-info">
            <div className="data-action-title">
              <DeleteOutlined className="data-action-icon danger" />
              <strong>删除账户</strong>
            </div>
            <p>永久删除您的账户和所有相关数据。此操作不可恢复，请谨慎操作。</p>
          </div>
          <button 
            type="button" 
            className="delete-btn"
            onClick={handleDeleteAccount}
          >
            删除账户
          </button>
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
      key: 'mentalHealth',
      label: (<span><HeartOutlined />心理健康</span>),
      children: <MentalHealthSettings />
    },
    {
      key: 'data',
      label: (<span><DatabaseOutlined />数据管理</span>),
      children: <DataManagement />
    }
  ];

  // 如果用户数据还在加载中，显示加载状态
  if (userLoading) {
    return (
      <div className="settings-page" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <Spin size="large" tip="加载用户设置中..." />
      </div>
    );
  }

  return (
    <div className="settings-page">
      <div className="settings-container">
        <div className="settings-header">
          <Title level={2} className="settings-title">系统设置</Title>
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