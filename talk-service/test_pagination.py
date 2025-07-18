#!/usr/bin/env python3
"""
测试talk-service分页修复的脚本
验证第二页是否显示在第一页下方
"""

import requests
import json
import time

# 配置
BASE_URL = "http://localhost:8083"  # talk-service的端口
API_BASE = f"{BASE_URL}/api/talk"

def test_pagination():
    """测试分页功能"""
    print("=== 测试talk-service分页修复 ===")
    
    # 设置请求头
    headers = {
        "X-User-Id": "1",
        "Content-Type": "application/json"
    }
    
    try:
        # 1. 先发送一些测试消息
        print("\n1. 发送测试消息...")
        for i in range(1, 25):  # 发送24条消息
            message_data = {
                "content": f"测试消息 {i}"
            }
            response = requests.post(f"{API_BASE}/messages", 
                                   json=message_data, 
                                   headers=headers)
            if response.status_code == 200:
                print(f"   发送消息 {i} 成功")
            else:
                print(f"   发送消息 {i} 失败: {response.text}")
            time.sleep(0.1)  # 避免消息时间戳相同
        
        # 等待一下确保消息都保存完成
        time.sleep(1)
        
        # 2. 测试分页
        print("\n2. 测试分页功能...")
        
        # 获取第一页（每页10条）
        print("\n   获取第一页 (page=1, size=10):")
        response1 = requests.get(f"{API_BASE}/messages?page=1&size=10", headers=headers)
        if response1.status_code == 200:
            data1 = response1.json()
            messages1 = data1.get('data', [])
            print(f"   第一页消息数量: {len(messages1)}")
            for i, msg in enumerate(messages1):
                print(f"   [{i+1}] ID: {msg.get('id')}, 内容: {msg.get('content')}, 时间: {msg.get('createdAt')}")
        else:
            print(f"   获取第一页失败: {response1.text}")
            return
        
        # 获取第二页（每页10条）
        print("\n   获取第二页 (page=2, size=10):")
        response2 = requests.get(f"{API_BASE}/messages?page=2&size=10", headers=headers)
        if response2.status_code == 200:
            data2 = response2.json()
            messages2 = data2.get('data', [])
            print(f"   第二页消息数量: {len(messages2)}")
            for i, msg in enumerate(messages2):
                print(f"   [{i+1}] ID: {msg.get('id')}, 内容: {msg.get('content')}, 时间: {msg.get('createdAt')}")
        else:
            print(f"   获取第二页失败: {response2.text}")
            return
        
        # 3. 验证分页顺序
        print("\n3. 验证分页顺序...")
        if messages1 and messages2:
            # 检查第一页最后一条消息的ID是否小于第二页第一条消息的ID
            last_msg_page1 = messages1[-1]
            first_msg_page2 = messages2[0]
            
            id1 = last_msg_page1.get('id')
            id2 = first_msg_page2.get('id')
            
            print(f"   第一页最后一条消息ID: {id1}, 内容: {last_msg_page1.get('content')}")
            print(f"   第二页第一条消息ID: {id2}, 内容: {first_msg_page2.get('content')}")
            
            if id1 < id2:
                print("   ✅ 分页顺序正确：第二页显示在第一页下方")
            else:
                print("   ❌ 分页顺序错误：第二页显示在第一页上方")
                
            # 额外验证：检查消息ID的连续性
            print("\n   验证消息ID连续性:")
            all_ids_page1 = [msg.get('id') for msg in messages1]
            all_ids_page2 = [msg.get('id') for msg in messages2]
            print(f"   第一页ID列表: {all_ids_page1}")
            print(f"   第二页ID列表: {all_ids_page2}")
            
            # 检查第一页的ID是否都小于第二页的ID
            max_id_page1 = max(all_ids_page1) if all_ids_page1 else 0
            min_id_page2 = min(all_ids_page2) if all_ids_page2 else float('inf')
            
            if max_id_page1 < min_id_page2:
                print("   ✅ ID连续性正确：第一页所有ID都小于第二页所有ID")
            else:
                print("   ❌ ID连续性错误：存在ID交叉")
        else:
            print("   ⚠️  无法验证分页顺序：消息数据不足")
        
        # 4. 测试用户消息分页
        print("\n4. 测试用户消息分页...")
        response_user = requests.get(f"{API_BASE}/user/messages?page=1&size=10", headers=headers)
        if response_user.status_code == 200:
            data_user = response_user.json()
            messages_user = data_user.get('data', [])
            print(f"   用户消息数量: {len(messages_user)}")
            for i, msg in enumerate(messages_user[:5]):  # 只显示前5条
                print(f"   [{i+1}] ID: {msg.get('id')}, 内容: {msg.get('content')}")
        else:
            print(f"   获取用户消息失败: {response_user.text}")
        
        # 5. 测试完整的历史记录
        print("\n5. 测试完整历史记录...")
        response_all = requests.get(f"{API_BASE}/messages?page=1&size=50", headers=headers)
        if response_all.status_code == 200:
            data_all = response_all.json()
            messages_all = data_all.get('data', [])
            print(f"   总消息数量: {len(messages_all)}")
            
            # 显示前10条和后10条
            print("   前10条消息:")
            for i, msg in enumerate(messages_all[:10]):
                print(f"   [{i+1}] ID: {msg.get('id')}, 内容: {msg.get('content')}")
            
            if len(messages_all) > 10:
                print("   后10条消息:")
                for i, msg in enumerate(messages_all[-10:]):
                    print(f"   [{len(messages_all)-9+i}] ID: {msg.get('id')}, 内容: {msg.get('content')}")
        else:
            print(f"   获取完整历史记录失败: {response_all.text}")
        
        print("\n=== 测试完成 ===")
        
    except requests.exceptions.ConnectionError:
        print("❌ 无法连接到talk-service，请确保服务正在运行")
    except Exception as e:
        print(f"❌ 测试过程中发生错误: {e}")

if __name__ == "__main__":
    test_pagination() 