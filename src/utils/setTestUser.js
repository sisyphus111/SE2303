// 设置测试用户数据到localStorage
// 在浏览器控制台中执行此脚本来模拟用户登录

const testUser = {
    id: 1,
    userId: 1,
    username: '测试用户',
    email: 'test@example.com',
    token: 'REDACTED'
};

// 存储用户信息到localStorage
localStorage.setItem('user', JSON.stringify(testUser));

// 验证存储成功
console.log('测试用户数据已设置:', JSON.parse(localStorage.getItem('user')));

// 刷新页面以应用更改
window.location.reload();
