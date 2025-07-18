#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
实时消息推送测试脚本
测试WebSocket连接和HTTP API发送消息的实时推送功能
"""

import asyncio
import websockets
import json
import requests
import time
from datetime import datetime

# 配置
TALK_SERVICE_URL = "http://localhost:8090"
WS_URL = "ws://localhost:8090/ws/conn"

class ChatTester:
    def __init__(self):
        self.messages_received = []
        
    async def websocket_client(self, user_id):
        """WebSocket客户端，用于接收推送的消息"""
        uri = f"{WS_URL}?x-userId={user_id}"
        
        try:
            async with websockets.connect(uri) as websocket:
                print(f"用户 {user_id} WebSocket连接成功")
                
                # 发送心跳包
                async def send_heartbeat():
                    while True:
                        try:
                            await websocket.send("heart")
                            await asyncio.sleep(9)
                        except:
                            break
                
                # 启动心跳任务
                heartbeat_task = asyncio.create_task(send_heartbeat())
                
                # 接收消息
                try:
                    while True:
                        message = await websocket.recv()
                        data = json.loads(message)
                        timestamp = datetime.now().strftime("%H:%M:%S")
                        
                        if 'content' in data:
                            content_data = data['content']
                            print(f"[{timestamp}] 用户{user_id}收到推送消息: ID={content_data.get('id')}, "
                                  f"发送者={content_data.get('from_userId')}, 内容='{content_data.get('content')}'")
                            
                            self.messages_received.append({
                                'user_id': user_id,
                                'message': content_data,
                                'timestamp': timestamp
                            })
                        else:
                            print(f"[{timestamp}] 用户{user_id}收到其他消息: {data}")
                            
                except websockets.exceptions.ConnectionClosed:
                    print(f"用户 {user_id} WebSocket连接关闭")
                finally:
                    heartbeat_task.cancel()
                    
        except Exception as e:
            print(f"用户 {user_id} WebSocket连接失败: {e}")
    
    def send_message_via_http(self, user_id, content):
        """通过HTTP API发送消息"""
        url = f"{TALK_SERVICE_URL}/api/talk/messages"
        headers = {
            "Content-Type": "application/json",
            "X-User-Id": str(user_id)
        }
        data = {
            "content": content
        }
        
        try:
            response = requests.post(url, headers=headers, json=data)
            if response.status_code == 200:
                result = response.json()
                print(f"HTTP API发送消息成功: {result}")
                return True
            else:
                print(f"HTTP API发送消息失败: {response.status_code} - {response.text}")
                return False
        except Exception as e:
            print(f"HTTP API发送消息异常: {e}")
            return False
    
    def send_message_via_websocket(self, websocket, content):
        """通过WebSocket发送消息"""
        try:
            message = {
                "content": content
            }
            asyncio.create_task(websocket.send(json.dumps(message)))
            return True
        except Exception as e:
            print(f"WebSocket发送消息失败: {e}")
            return False

async def test_realtime_push():
    """测试实时推送功能"""
    tester = ChatTester()
    
    print("=== 实时消息推送测试 ===")
    print("1. 启动多个WebSocket客户端")
    print("2. 通过HTTP API发送消息")
    print("3. 验证所有客户端是否收到推送")
    print()
    
    # 启动多个WebSocket客户端
    clients = []
    user_ids = [1, 2, 3]
    
    for user_id in user_ids:
        client_task = asyncio.create_task(tester.websocket_client(user_id))
        clients.append(client_task)
    
    # 等待连接建立
    await asyncio.sleep(2)
    
    print("\n=== 开始发送测试消息 ===")
    
    # 测试1: 通过HTTP API发送消息
    print("\n--- 测试1: HTTP API发送消息 ---")
    test_messages = [
        "大家好！这是第一条测试消息",
        "这是第二条测试消息",
        "这是第三条测试消息"
    ]
    
    for i, message in enumerate(test_messages):
        print(f"\n发送消息 {i+1}: {message}")
        success = tester.send_message_via_http(1, message)
        
        if success:
            print("等待消息推送...")
            await asyncio.sleep(2)  # 等待推送
        else:
            print("消息发送失败")
    
    # 等待一段时间观察推送结果
    print("\n等待5秒观察推送结果...")
    await asyncio.sleep(5)
    
    # 显示接收到的消息统计
    print(f"\n=== 推送结果统计 ===")
    print(f"总共收到 {len(tester.messages_received)} 条推送消息")
    
    for msg in tester.messages_received:
        print(f"用户{msg['user_id']} 在 {msg['timestamp']} 收到消息: {msg['message']['content']}")
    
    # 取消所有客户端任务
    for client in clients:
        client.cancel()
    
    print("\n=== 测试完成 ===")

if __name__ == "__main__":
    try:
        asyncio.run(test_realtime_push())
    except KeyboardInterrupt:
        print("\n测试被用户中断")
    except Exception as e:
        print(f"测试异常: {e}") 