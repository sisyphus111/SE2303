#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
WebSocket监控功能测试脚本
用于测试bot-service的WebSocket通信时长监控功能
"""

import asyncio
import websockets
import json
import time
import requests
from datetime import datetime, timedelta

class WebSocketMonitoringTester:
    def __init__(self, bot_service_url="ws://localhost:8080/ws/chat", 
                 alert_service_url="http://localhost:8083"):
        self.bot_service_url = bot_service_url
        self.alert_service_url = alert_service_url
        self.session_id = None
        self.connection_start_time = None
        
    async def connect_and_chat(self, duration_minutes=1):
        """建立WebSocket连接并进行聊天，持续指定时间"""
        print(f"开始WebSocket连接测试，持续时间: {duration_minutes} 分钟")
        
        try:
            async with websockets.connect(self.bot_service_url) as websocket:
                self.connection_start_time = datetime.now()
                print(f"WebSocket连接建立成功，时间: {self.connection_start_time}")
                
                # 发送初始消息
                initial_message = {
                    "id": "test-msg-1",
                    "type": "user",
                    "role": "user",
                    "content": "你好，我是测试用户",
                    "timestamp": int(time.time() * 1000)
                }
                
                await websocket.send(json.dumps(initial_message))
                print("发送初始消息")
                
                # 接收欢迎消息
                response = await websocket.recv()
                print(f"收到响应: {response}")
                
                # 持续发送消息以保持连接活跃
                message_count = 0
                end_time = datetime.now() + timedelta(minutes=duration_minutes)
                
                while datetime.now() < end_time:
                    message_count += 1
                    message = {
                        "id": f"test-msg-{message_count}",
                        "type": "user",
                        "role": "user",
                        "content": f"这是第{message_count}条测试消息，时间: {datetime.now()}",
                        "timestamp": int(time.time() * 1000)
                    }
                    
                    await websocket.send(json.dumps(message))
                    print(f"发送第{message_count}条消息")
                    
                    # 接收AI响应
                    try:
                        response = await asyncio.wait_for(websocket.recv(), timeout=10.0)
                        print(f"收到AI响应: {response[:100]}...")
                    except asyncio.TimeoutError:
                        print("等待AI响应超时")
                    
                    # 等待一段时间再发送下一条消息
                    await asyncio.sleep(30)  # 每30秒发送一条消息
                
                print(f"测试完成，总共发送了{message_count}条消息")
                
        except Exception as e:
            print(f"WebSocket连接测试出错: {e}")
    
    def check_session_monitoring_api(self):
        """检查会话监控API"""
        print("\n=== 检查会话监控API ===")
        
        try:
            # 检查活跃会话统计
            response = requests.get(f"{self.alert_service_url.replace('8083', '8080')}/api/session-monitoring/active-count")
            if response.status_code == 200:
                data = response.json()
                print(f"活跃会话数量: {data.get('activeSessionCount', 0)}")
                print(f"长时间会话阈值: {data.get('longSessionThreshold', 0)} 分钟")
            else:
                print(f"获取活跃会话统计失败: {response.status_code}")
            
            # 检查长时间会话记录
            response = requests.get(f"{self.alert_service_url.replace('8083', '8080')}/api/session-monitoring/long-sessions")
            if response.status_code == 200:
                long_sessions = response.json()
                print(f"长时间会话记录数量: {len(long_sessions)}")
                for session in long_sessions[:3]:  # 只显示前3条
                    print(f"  - 会话ID: {session.get('sessionId')}, 持续时间: {session.get('durationMinutes')} 分钟")
            else:
                print(f"获取长时间会话记录失败: {response.status_code}")
                
        except Exception as e:
            print(f"检查会话监控API出错: {e}")
    
    def check_alert_service_api(self):
        """检查警报服务API"""
        print("\n=== 检查警报服务API ===")
        
        try:
            # 检查警报统计
            response = requests.get(f"{self.alert_service_url}/api/alerts/statistics")
            if response.status_code == 200:
                data = response.json()
                print(f"总警报数量: {data.get('totalAlerts', 0)}")
                print(f"新警报数量: {data.get('newAlerts', 0)}")
                print(f"长时间会话警报数量: {data.get('longSessionAlerts', 0)}")
                print(f"最近24小时长时间会话警报: {data.get('recentLongSessionAlerts', 0)}")
            else:
                print(f"获取警报统计失败: {response.status_code}")
            
            # 检查长时间会话警报
            response = requests.get(f"{self.alert_service_url}/api/alerts/long-session")
            if response.status_code == 200:
                alerts = response.json()
                print(f"长时间会话警报数量: {len(alerts)}")
                for alert in alerts[:3]:  # 只显示前3条
                    print(f"  - 警报ID: {alert.get('id')}, 会话ID: {alert.get('sessionId')}, 持续时间: {alert.get('durationMinutes')} 分钟")
            else:
                print(f"获取长时间会话警报失败: {response.status_code}")
                
        except Exception as e:
            print(f"检查警报服务API出错: {e}")
    
    def test_alert_creation(self):
        """测试创建警报"""
        print("\n=== 测试创建警报 ===")
        
        try:
            test_alert = {
                "sessionId": "test-session-123",
                "serviceName": "bot-service",
                "description": "测试长时间会话警报",
                "durationMinutes": 55,
                "connectionStartTime": (datetime.now() - timedelta(minutes=55)).isoformat(),
                "connectionEndTime": datetime.now().isoformat()
            }
            
            response = requests.post(
                f"{self.alert_service_url}/api/alerts/long-session",
                json=test_alert,
                headers={"Content-Type": "application/json"}
            )
            
            if response.status_code == 200:
                data = response.json()
                print(f"测试警报创建成功: {data}")
            else:
                print(f"测试警报创建失败: {response.status_code} - {response.text}")
                
        except Exception as e:
            print(f"测试创建警报出错: {e}")

async def main():
    """主测试函数"""
    print("=== WebSocket监控功能测试 ===")
    print(f"测试开始时间: {datetime.now()}")
    
    tester = WebSocketMonitoringTester()
    
    # 1. 检查服务状态
    print("\n1. 检查服务状态...")
    tester.check_session_monitoring_api()
    tester.check_alert_service_api()
    
    # 2. 测试创建警报
    print("\n2. 测试创建警报...")
    tester.test_alert_creation()
    
    # 3. 进行WebSocket连接测试（短时间）
    print("\n3. 进行WebSocket连接测试...")
    await tester.connect_and_chat(duration_minutes=1)
    
    # 4. 再次检查服务状态
    print("\n4. 再次检查服务状态...")
    tester.check_session_monitoring_api()
    tester.check_alert_service_api()
    
    print(f"\n测试结束时间: {datetime.now()}")
    print("=== 测试完成 ===")

if __name__ == "__main__":
    # 运行测试
    asyncio.run(main()) 