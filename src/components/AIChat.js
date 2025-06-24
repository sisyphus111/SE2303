import React, { useState, useRef, useEffect } from 'react';
import './AIChat.css';

const AIChat = () => {
  const [messages, setMessages] = useState([
    {
      id: 1,
      text: "你好！我是你的AI陪伴助手，很高兴认识你！有什么想聊的吗？",
      sender: 'ai',
      timestamp: new Date()
    }
  ]);
  const [inputText, setInputText] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // 模拟AI回复
  const generateAIResponse = (userMessage) => {
    const responses = [
      "我理解你的感受，能具体说说吗？",
      "这确实是一个值得思考的问题。",
      "听起来很有趣，请继续分享。",
      "我在这里陪伴着你，随时可以聊天。",
      "你的想法很有见地，我很想听听更多。",
      "生活中总会有起起落落，重要的是保持积极的心态。",
      "每个人都有自己的节奏，不用着急。",
      "你的感受是正常的，我完全理解。",
      "让我们一起面对这个挑战吧。",
      "你的坚持让我很感动。"
    ];
    
    // 根据用户消息内容选择更合适的回复
    if (userMessage.includes('难过') || userMessage.includes('伤心') || userMessage.includes('不开心')) {
      return "我感受到你现在的心情不太好。记住，每个人都会有情绪低落的时候，这是完全正常的。你想聊聊具体发生了什么吗？我在这里倾听。";
    } else if (userMessage.includes('开心') || userMessage.includes('高兴') || userMessage.includes('快乐')) {
      return "太好了！看到你心情不错我也很开心。能分享是什么让你这么开心吗？";
    } else if (userMessage.includes('压力') || userMessage.includes('焦虑') || userMessage.includes('紧张')) {
      return "压力确实会让人感到困扰。试着深呼吸，一步一步来。你愿意和我分享是什么让你感到压力吗？";
    } else if (userMessage.includes('谢谢') || userMessage.includes('感谢')) {
      return "不用谢！能陪伴你是我的荣幸。希望我们的对话能给你带来一些温暖和力量。";
    } else {
      return responses[Math.floor(Math.random() * responses.length)];
    }
  };

  const handleSendMessage = async () => {
    if (!inputText.trim()) return;

    const userMessage = {
      id: messages.length + 1,
      text: inputText,
      sender: 'user',
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputText('');
    setIsLoading(true);

    // 模拟AI思考时间
    setTimeout(() => {
      const aiResponse = {
        id: messages.length + 2,
        text: generateAIResponse(inputText),
        sender: 'ai',
        timestamp: new Date()
      };
      setMessages(prev => [...prev, aiResponse]);
      setIsLoading(false);
    }, 1000 + Math.random() * 2000); // 1-3秒的随机延迟
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  const formatTime = (timestamp) => {
    return timestamp.toLocaleTimeString('zh-CN', { 
      hour: '2-digit', 
      minute: '2-digit' 
    });
  };

  return (
    <div className="ai-chat-container">
      <div className="chat-header">
        <div className="ai-avatar">
          <span>🤖</span>
        </div>
        <div className="header-info">
          <h2>AI陪伴助手</h2>
          <p>在线 • 随时陪伴</p>
        </div>
      </div>

      <div className="messages-container">
        {messages.map((message) => (
          <div
            key={message.id}
            className={`message ${message.sender === 'user' ? 'user-message' : 'ai-message'}`}
          >
            <div className="message-content">
              <div className="message-text">{message.text}</div>
              <div className="message-time">{formatTime(message.timestamp)}</div>
            </div>
          </div>
        ))}
        
        {isLoading && (
          <div className="message ai-message">
            <div className="message-content">
              <div className="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        )}
        
        <div ref={messagesEndRef} />
      </div>

      <div className="input-container">
        <div className="input-wrapper">
          <textarea
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="输入你的消息..."
            disabled={isLoading}
            rows="1"
          />
          <button
            onClick={handleSendMessage}
            disabled={!inputText.trim() || isLoading}
            className="send-button"
          >
            <span>发送</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default AIChat; 