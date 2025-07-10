import React, { useState, useEffect } from "react";
import { Button, Card, Typography, Divider, Switch, Space, Alert } from "antd";
import { useUser } from '../lib/UserContext';

const { Title, Text } = Typography;

export default function SettingsDebugger() {
  const { preferences, updateUserPrefs, setPreferences } = useUser();
  const [localSettings, setLocalSettings] = useState({
    // Notifications
    pushNotifications: true,
    emailNotifications: true,
    commentNotifications: true,
    // Preferences  
    soundEnabled: true,
    showPostStats: true,
    // Mental Health
    crisisInterventionEnabled: true,
    moodTrackingEnabled: true
  });

  // 同步preferences到本地state
  useEffect(() => {
    if (preferences && typeof preferences === 'object') {
      console.log('🔄 SettingsDebugger同步数据:', preferences);
      setLocalSettings(prev => ({
        ...prev,
        pushNotifications: preferences.pushNotifications ?? prev.pushNotifications,
        emailNotifications: preferences.emailNotifications ?? prev.emailNotifications,
        commentNotifications: preferences.commentNotifications ?? prev.commentNotifications,
        soundEnabled: preferences.soundEnabled ?? prev.soundEnabled,
        showPostStats: preferences.showPostStats ?? prev.showPostStats,
        crisisInterventionEnabled: preferences.crisisInterventionEnabled ?? prev.crisisInterventionEnabled,
        moodTrackingEnabled: preferences.moodTrackingEnabled ?? prev.moodTrackingEnabled
      }));
    }
  }, [preferences]);

  const handleChange = (key, value) => {
    console.log(`🔧 本地更改: ${key} = ${value}`);
    setLocalSettings(prev => ({ ...prev, [key]: value }));
  };

  const testSaveSection = async (section) => {
    console.log(`\n=== 测试保存${section}分区 ===`);
    
    const sectionFields = {
      notifications: ['pushNotifications', 'emailNotifications', 'commentNotifications'],
      preferences: ['soundEnabled', 'showPostStats'],
      mentalHealth: ['crisisInterventionEnabled', 'moodTrackingEnabled']
    };

    const fieldsToSave = sectionFields[section] || [];
    const dataToSave = {};
    fieldsToSave.forEach(key => {
      dataToSave[key] = localSettings[key];
    });

    console.log(`📤 ${section}分区要保存的数据:`, dataToSave);
    console.log(`📊 保存前完整preferences:`, preferences);

    try {
      const result = await updateUserPrefs(dataToSave);
      console.log(`✅ ${section}分区保存结果:`, result);
      
      if (result.success) {
        console.log(`📥 ${section}分区后端返回的完整数据:`, result.data);
        
        // 验证：检查是否只更新了当前section的字段
        const updatedPrefs = result.data;
        console.log(`🔍 ${section}分区保存后验证:`);
        fieldsToSave.forEach(field => {
          console.log(`  ${field}: 本地=${localSettings[field]}, 后端返回=${updatedPrefs[field]}`);
        });
        
        // 检查其他section的字段是否被意外修改
        const otherSections = Object.keys(sectionFields).filter(s => s !== section);
        otherSections.forEach(otherSection => {
          console.log(`🔍 检查${otherSection}分区是否被影响:`);
          sectionFields[otherSection].forEach(field => {
            const originalValue = preferences[field];
            const newValue = updatedPrefs[field];
            if (originalValue !== newValue) {
              console.warn(`⚠️  ${field}被意外修改: ${originalValue} -> ${newValue}`);
            } else {
              console.log(`✅ ${field}未被影响: ${originalValue}`);
            }
          });
        });
      }
    } catch (error) {
      console.error(`❌ ${section}分区保存失败:`, error);
    }
  };

  return (
    <Card title="Settings Debug Panel" style={{ margin: '20px' }}>
      <Alert 
        message="此组件用于调试Settings保存逻辑，检查分区保存是否互相影响" 
        type="info" 
        style={{ marginBottom: '20px' }}
      />
      
      <Title level={4}>当前状态</Title>
      <div style={{ background: '#f5f5f5', padding: '10px', marginBottom: '20px' }}>
        <Text strong>本地State:</Text>
        <pre>{JSON.stringify(localSettings, null, 2)}</pre>
        <Text strong>UserContext Preferences:</Text>
        <pre>{JSON.stringify(preferences, null, 2)}</pre>
      </div>

      <Title level={4}>通知设置</Title>
      <Space direction="vertical">
        <div>
          <Text>推送通知: </Text>
          <Switch 
            checked={localSettings.pushNotifications} 
            onChange={(value) => handleChange('pushNotifications', value)}
          />
        </div>
        <div>
          <Text>邮件通知: </Text>
          <Switch 
            checked={localSettings.emailNotifications} 
            onChange={(value) => handleChange('emailNotifications', value)}
          />
        </div>
        <div>
          <Text>评论通知: </Text>
          <Switch 
            checked={localSettings.commentNotifications} 
            onChange={(value) => handleChange('commentNotifications', value)}
          />
        </div>
        <Button 
          type="primary" 
          onClick={() => testSaveSection('notifications')}
          style={{ marginTop: '10px' }}
        >
          保存通知设置
        </Button>
      </Space>

      <Divider />

      <Title level={4}>个人偏好</Title>
      <Space direction="vertical">
        <div>
          <Text>声音开启: </Text>
          <Switch 
            checked={localSettings.soundEnabled} 
            onChange={(value) => handleChange('soundEnabled', value)}
          />
        </div>
        <div>
          <Text>显示帖子统计: </Text>
          <Switch 
            checked={localSettings.showPostStats} 
            onChange={(value) => handleChange('showPostStats', value)}
          />
        </div>
        <Button 
          type="primary" 
          onClick={() => testSaveSection('preferences')}
          style={{ marginTop: '10px' }}
        >
          保存个人偏好
        </Button>
      </Space>

      <Divider />

      <Title level={4}>心理健康设置</Title>
      <Space direction="vertical">
        <div>
          <Text>危机干预: </Text>
          <Switch 
            checked={localSettings.crisisInterventionEnabled} 
            onChange={(value) => handleChange('crisisInterventionEnabled', value)}
          />
        </div>
        <div>
          <Text>心情追踪: </Text>
          <Switch 
            checked={localSettings.moodTrackingEnabled} 
            onChange={(value) => handleChange('moodTrackingEnabled', value)}
          />
        </div>
        <Button 
          type="primary" 
          onClick={() => testSaveSection('mentalHealth')}
          style={{ marginTop: '10px' }}
        >
          保存心理健康设置
        </Button>
      </Space>
    </Card>
  );
}
