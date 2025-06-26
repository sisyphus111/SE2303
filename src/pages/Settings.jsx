import React, { useState, useEffect } from "react";
import SettingsForm from "../components/SettingsForm";
import { Card, Typography } from "antd";
import "../css/Settings.css";

// API服务导入注释
// import { getUserInfo, updateUserInfo, getUserSettings, updateUserSettings } from '../service/userService';

const { Title, Text } = Typography;

function Settings() {
  const [userInfo, setUserInfo] = useState(null);
  const [userSettings, setUserSettings] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadUserData();
  }, []);

  // 加载用户数据的函数
  const loadUserData = async () => {
    try {
      setLoading(true);
      
      // API调用注释：获取用户信息
      // const userRes = await getUserInfo();
      // setUserInfo(userRes.data);
      
      // API调用注释：获取用户设置
      // const settingsRes = await getUserSettings();
      // setUserSettings(settingsRes.data);
      
    } catch (error) {
      console.error('加载用户数据失败:', error);
    } finally {
      setLoading(false);
    }
  };

  // 更新用户信息的函数
  const handleUpdateUserInfo = async (userData) => {
    // API调用注释：更新用户信息
    // try {
    //   const response = await updateUserInfo(userData);
    //   setUserInfo(response.data);
    //   alert('用户信息更新成功！');
    // } catch (error) {
    //   console.error('更新用户信息失败:', error);
    //   alert('更新失败，请重试');
    // }
  };

  // 更新用户设置的函数
  const handleUpdateSettings = async (settings) => {
    // API调用注释：更新用户设置
    // try {
    //   const response = await updateUserSettings(settings);
    //   setUserSettings(response.data);
    //   alert('设置更新成功！');
    // } catch (error) {
    //   console.error('更新设置失败:', error);
    //   alert('更新失败，请重试');
    // }
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div className="settings-bg">
      <Card className="settings-card settings-card-body">
        <div className="settings-title">
          <Title level={2} className="settings-title-main">
            个人设置
          </Title>
          <Text type="secondary" className="settings-title-sub">
            完善你的个人信息，打造专属形象
          </Text>
        </div>
        <SettingsForm 
          userInfo={userInfo}
          userSettings={userSettings}
          onUpdateUserInfo={handleUpdateUserInfo}
          onUpdateSettings={handleUpdateSettings}
        />
      </Card>
    </div>
  );
}
export default Settings;