// auth.js - 用户身份验证相关功能

document.addEventListener('DOMContentLoaded', function() {
    // 检查登录状态并更新导航栏
    updateNavbar();
    
    // 绑定登出按钮事件
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function(e) {
            e.preventDefault();
            logout();
        });
    }
    
    // 绑定登录表单提交事件
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            login(username, password);
        });
    }
    
    // 绑定注册表单提交事件
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            register(username, email, password);
        });
    }
});

/**
 * 更新导航栏显示
 */
function updateNavbar() {
    const navAuth = document.getElementById('navAuth');
    const navUser = document.getElementById('navUser');
    const username = document.getElementById('username');
    
    if (isAuthenticated()) {
        // 用户已登录
        if (navAuth) navAuth.classList.add('d-none');
        if (navUser) {
            navUser.classList.remove('d-none');
            const user = getCurrentUser();
            if (username && user) {
                username.textContent = user.username;
            }
        }
    } else {
        // 用户未登录
        if (navAuth) navAuth.classList.remove('d-none');
        if (navUser) navUser.classList.add('d-none');
    }
}

/**
 * 用户登录
 */
function login(username, password) {
    fetch('/api/auth/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            password: password
        })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => {
                throw new Error(err.message || '用户名或密码错误');
            });
        }
        return response.json();
    })
    .then(data => {
        // 保存令牌和用户信息
        localStorage.setItem('token', data.token);
        localStorage.setItem('user', JSON.stringify(data.user));
        
        // 显示成功消息并跳转到首页
        alert('登录成功');
        window.location.href = '/';
    })
    .catch(error => {
        console.error('登录失败:', error);
        alert(error.message || '登录失败，请稍后重试');
    });
}

/**
 * 用户注册
 */
function register(username, email, password) {
    fetch('/api/auth/register', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            username: username,
            email: email,
            password: password
        })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => {
                throw new Error(err.message || '注册失败');
            });
        }
        return response.json();
    })
    .then(data => {
        // 显示成功消息并跳转到登录页
        alert('注册成功，请登录');
        window.location.href = '/login.html';
    })
    .catch(error => {
        console.error('注册失败:', error);
        alert(error.message || '注册失败，请稍后重试');
    });
}

/**
 * 用户登出
 */
function logout() {
    // 清除本地存储的令牌和用户信息
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    
    // 跳转到首页
    window.location.href = '/';
}

/**
 * 检查用户是否已登录
 */
function isAuthenticated() {
    return getToken() !== null;
}

/**
 * 获取当前用户信息
 */
function getCurrentUser() {
    const userJson = localStorage.getItem('user');
    return userJson ? JSON.parse(userJson) : null;
}

/**
 * 获取用户令牌
 */
function getToken() {
    return localStorage.getItem('token');
} 