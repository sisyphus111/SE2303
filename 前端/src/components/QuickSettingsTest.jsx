import React, { useState, useEffect } from "react";
import { Button, Typography, Space, Alert, Card, Switch } from "antd";
import { useUser } from '../lib/UserContext';

const { Title, Text } = Typography;

export default function QuickSettingsTest() {
  const { preferences, updateUserPrefs } = useUser();
  const [testResults, setTestResults] = useState([]);

  const addTestResult = (test, result, details) => {
    setTestResults(prev => [...prev, {
      id: Date.now(),
      test,
      result,
      details,
      timestamp: new Date().toLocaleTimeString()
    }]);
  };

  const testSectionSaving = async () => {
    setTestResults([]);
    
    try {
      // 测试1: 保存通知设置
      console.log('\n=== 测试1: 保存通知设置 ===');
      const notificationData = {
        pushNotifications: false,
        emailNotifications: false
      };
      
      const result1 = await updateUserPrefs(notificationData, true);
      addTestResult('保存通知设置', result1.success ? '✅ 成功' : '❌ 失败', 
        `推送:${notificationData.pushNotifications}, 邮件:${notificationData.emailNotifications}`);
      
      // 等待一下
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // 测试2: 保存个人偏好（应该不影响通知设置）
      console.log('\n=== 测试2: 保存个人偏好 ===');
      const preferencesData = {
        soundEnabled: false,
        showPostStats: false
      };
      
      const result2 = await updateUserPrefs(preferencesData, true);
      addTestResult('保存个人偏好', result2.success ? '✅ 成功' : '❌ 失败',
        `声音:${preferencesData.soundEnabled}, 统计:${preferencesData.showPostStats}`);
      
      // 等待一下
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // 测试3: 验证通知设置是否被影响
      console.log('\n=== 测试3: 验证数据完整性 ===');
      const currentPrefs = preferences;
      const notificationStillCorrect = 
        currentPrefs.pushNotifications === false && 
        currentPrefs.emailNotifications === false;
      const preferencesCorrect = 
        currentPrefs.soundEnabled === false && 
        currentPrefs.showPostStats === false;
      
      addTestResult('通知设置完整性', notificationStillCorrect ? '✅ 未被影响' : '❌ 被覆盖',
        `推送:${currentPrefs.pushNotifications}, 邮件:${currentPrefs.emailNotifications}`);
      addTestResult('偏好设置完整性', preferencesCorrect ? '✅ 正确保存' : '❌ 保存失败',
        `声音:${currentPrefs.soundEnabled}, 统计:${currentPrefs.showPostStats}`);
      
    } catch (error) {
      addTestResult('测试异常', '❌ 错误', error.message);
    }
  };

  return (
    <Card title="Settings 分区保存测试" style={{ margin: '20px', maxWidth: '800px' }}>
      <Alert 
        message="此组件用于测试Settings分区保存是否互相影响" 
        type="info" 
        style={{ marginBottom: '20px' }}
      />
      
      <Space direction="vertical" style={{ width: '100%' }}>
        <Title level={4}>当前偏好设置</Title>
        <div style={{ background: '#f5f5f5', padding: '15px', borderRadius: '6px' }}>
          <Text strong>通知设置:</Text>
          <div style={{ marginLeft: '20px', marginTop: '5px' }}>
            <div>推送通知: {preferences?.pushNotifications ? '✅ 开启' : '❌ 关闭'}</div>
            <div>邮件通知: {preferences?.emailNotifications ? '✅ 开启' : '❌ 关闭'}</div>
          </div>
          
          <Text strong style={{ display: 'block', marginTop: '10px' }}>个人偏好:</Text>
          <div style={{ marginLeft: '20px', marginTop: '5px' }}>
            <div>声音开启: {preferences?.soundEnabled ? '✅ 开启' : '❌ 关闭'}</div>
            <div>显示统计: {preferences?.showPostStats ? '✅ 开启' : '❌ 关闭'}</div>
          </div>
        </div>

        <Button 
          type="primary" 
          size="large"
          onClick={testSectionSaving}
          style={{ marginTop: '20px' }}
        >
          开始分区保存测试
        </Button>

        {testResults.length > 0 && (
          <div style={{ marginTop: '20px' }}>
            <Title level={4}>测试结果</Title>
            {testResults.map(result => (
              <div key={result.id} style={{ 
                padding: '10px', 
                border: '1px solid #d9d9d9', 
                borderRadius: '4px',
                marginBottom: '5px',
                background: result.result.includes('✅') ? '#f6ffed' : '#fff2f0'
              }}>
                <div style={{ fontWeight: 'bold' }}>
                  [{result.timestamp}] {result.test}: {result.result}
                </div>
                <div style={{ fontSize: '12px', color: '#666', marginTop: '2px' }}>
                  {result.details}
                </div>
              </div>
            ))}
          </div>
        )}
      </Space>
    </Card>
  );
}
