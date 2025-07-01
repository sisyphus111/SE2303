import React, { useState, useEffect } from "react";
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
  EyeOutlined
} from '@ant-design/icons';
import { useUser } from '../lib/UserContext';
import { getUserPreferences } from '../service/userService';
import "../css/Settings.css";

const { Title, Text } = Typography;
const { Option } = Select;
const { confirm } = Modal;

function Settings() {
  const { userProfile, preferences, updateUserPrefs, setPreferences, loading: userLoading } = useUser();
  const [activeTab, setActiveTab] = useState('notifications');
  const [loading, setLoading] = useState(false);
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

  // 刷新偏好设置数据的方法
  const refreshPreferences = async () => {
    try {
      setLoading(true);
      console.log('🔄 手动刷新偏好设置数据...');
      const freshPrefs = await getUserPreferences();
      console.log('📥 从后端获取的最新偏好设置:', freshPrefs);
      
      // 更新UserContext中的preferences
      setPreferences(freshPrefs);
      
      // 立即同步到本地settings
      setSettings({
        pushNotifications: freshPrefs.pushNotifications ?? true,
        emailNotifications: freshPrefs.emailNotifications ?? true,
        commentNotifications: freshPrefs.commentNotifications ?? true,
        likeNotifications: freshPrefs.likeNotifications ?? false,
        systemNotifications: freshPrefs.systemNotifications ?? true,
        soundEnabled: freshPrefs.soundEnabled ?? true,
        showPostStats: freshPrefs.showPostStats ?? true,
        enablePostRecommendation: freshPrefs.enablePostRecommendation ?? true,
        blockSensitiveContent: freshPrefs.blockSensitiveContent ?? true,
        crisisInterventionEnabled: freshPrefs.crisisInterventionEnabled ?? true,
        moodTrackingEnabled: freshPrefs.moodTrackingEnabled ?? true,
        anonymousPosting: freshPrefs.anonymousPosting ?? false,
        mentalHealthDataSharing: freshPrefs.mentalHealthDataSharing ?? false
      });
      
      message.success('偏好设置已刷新');
    } catch (error) {
      console.error('❌ 刷新偏好设置失败:', error);
      message.error('刷新失败: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async (section, updatedFields = null) => {
    setLoading(true);
    try {
      console.log(`🔄 handleSave被调用，section=${section}，updatedFields=`, updatedFields);
      console.log('当前settings状态:', settings);
      
      // 组装要保存的数据
      let dataToSave = updatedFields;
      if (!dataToSave) {
        const map = {
          notifications: ['pushNotifications','emailNotifications','commentNotifications','likeNotifications','systemNotifications'],
          preferences: ['soundEnabled','showPostStats','enablePostRecommendation','blockSensitiveContent'],
          mentalHealth: ['crisisInterventionEnabled','moodTrackingEnabled','anonymousPosting','mentalHealthDataSharing']
        };
        dataToSave = {};
        map[section].forEach(key => { dataToSave[key] = settings[key]; });
      }
      console.log('📤 即将发送到后端的数据:', dataToSave);
      
      // 使用 Context 方法保存并获取后端返回的更新字段
      console.log('🚀 开始调用updateUserPrefs...');
      const result = await updateUserPrefs(dataToSave, true); // 第二个参数表示只更新指定字段
      console.log('📥 updateUserPrefs返回结果:', result);
      
      if (result.success) {
        const updatedPrefs = result.data;
        console.log('✅ 保存成功，后端返回的完整数据:', updatedPrefs);
        
        // UserContext已经只更新了指定字段，这里只需要更新本地settings
        setSettings(prev => ({ ...prev, ...dataToSave }));
        
        message.success('设置保存成功');
      } else {
        throw new Error(result.error || '保存失败');
      }
    } catch (error) {
      console.error('❌ 保存失败:', error);
      message.error('保存失败: ' + error.message);
    } finally {
      setLoading(false);
    }
  };

  // 检查是否有未保存的更改
  const hasUnsavedChanges = (section) => {
    if (!preferences || !isInitialized) return false;
    
    const fieldMap = {
      notifications: ['pushNotifications','emailNotifications','commentNotifications','likeNotifications','systemNotifications'],
      preferences: ['soundEnabled','showPostStats','enablePostRecommendation','blockSensitiveContent'],
      mentalHealth: ['crisisInterventionEnabled','moodTrackingEnabled','anonymousPosting','mentalHealthDataSharing']
    };
    
    const fields = fieldMap[section] || [];
    return fields.some(field => {
      const currentValue = settings[field];
      const originalValue = preferences[field];
      return currentValue !== originalValue;
    });
  };

  // 取消更改，恢复到保存的状态
  const handleCancel = (section) => {
    console.log(`🔄 取消${section}的更改，恢复到保存状态`);
    
    const fieldMap = {
      notifications: ['pushNotifications','emailNotifications','commentNotifications','likeNotifications','systemNotifications'],
      preferences: ['soundEnabled','showPostStats','enablePostRecommendation','blockSensitiveContent'],
      mentalHealth: ['crisisInterventionEnabled','moodTrackingEnabled','anonymousPosting','mentalHealthDataSharing']
    };
    
    const fields = fieldMap[section] || [];
    setSettings(prev => {
      const newSettings = { ...prev };
      fields.forEach(field => {
        newSettings[field] = preferences[field] ?? prev[field];
      });
      return newSettings;
    });
    
    message.info('已取消更改');
  };

  const handleSettingChange = (key, value) => {
    console.log(`🔧 设置变更: ${key} = ${value} (仅更新本地状态，不自动保存)`);
    
    // 只更新本地UI状态，不自动保存到后端
    setSettings(prevSettings => ({
      ...prevSettings,
      [key]: value
    }));
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

      <div style={{ display: 'flex', gap: '12px', marginTop: 24 }}>
        <Button 
          type="primary" 
          onClick={() => handleSave('notifications')} 
          loading={loading}
          disabled={!hasUnsavedChanges('notifications')}
        >
          保存通知设置
          {hasUnsavedChanges('notifications') && ' *'}
        </Button>
        
        {hasUnsavedChanges('notifications') && (
          <Button onClick={() => handleCancel('notifications')}>
            取消更改
          </Button>
        )}
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
                </div>
                <span>开启界面操作音效和提示音</span>
              </div>
              <Switch 
                checked={settings.soundEnabled} 
                onChange={(checked) => handleSettingChange('soundEnabled', checked)} 
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

      <div style={{ display: 'flex', gap: '12px', marginTop: 24 }}>
        <Button 
          type="primary" 
          onClick={() => handleSave('preferences')} 
          loading={loading}
          disabled={!hasUnsavedChanges('preferences')}
        >
          保存偏好设置
          {hasUnsavedChanges('preferences') && ' *'}
        </Button>
        
        {hasUnsavedChanges('preferences') && (
          <Button onClick={() => handleCancel('preferences')}>
            取消更改
          </Button>
        )}
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

      <div style={{ display: 'flex', gap: '12px', marginTop: 24 }}>
        <Button 
          type="primary" 
          onClick={() => handleSave('mentalHealth')} 
          loading={loading}
          disabled={!hasUnsavedChanges('mentalHealth')}
        >
          保存心理健康设置
          {hasUnsavedChanges('mentalHealth') && ' *'}
        </Button>
        
        {hasUnsavedChanges('mentalHealth') && (
          <Button onClick={() => handleCancel('mentalHealth')}>
            取消更改
          </Button>
        )}
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
          <Text type="secondary" className="settings-subtitle">自定义您的使用体验和功能偏好</Text>
          {userProfile && (
            <Text type="secondary" style={{ marginLeft: 10 }}>
              当前用户: {userProfile.username || userProfile.nickname}
            </Text>
          )}
          <Button 
            type="link" 
            size="small" 
            onClick={refreshPreferences}
            loading={loading}
            style={{ marginLeft: 10 }}
          >
            🔄 刷新数据
          </Button>
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