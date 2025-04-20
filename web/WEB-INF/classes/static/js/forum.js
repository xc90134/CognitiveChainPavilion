// forum.js - 论坛功能实现

document.addEventListener('DOMContentLoaded', function() {
    // 初始化加载帖子列表
    loadPosts();
    
    // 绑定发帖表单提交事件
    const postForm = document.getElementById('postForm');
    if (postForm) {
        postForm.addEventListener('submit', function(e) {
            e.preventDefault();
            if (!isAuthenticated()) {
                alert('请先登录后再发帖');
                window.location.href = '/login.html';
                return;
            }
            
            const title = postForm.querySelector('input[type="text"]').value;
            const content = postForm.querySelector('textarea').value;
            
            createPost(title, content);
        });
    }
});

/**
 * 加载帖子列表
 * @param {number} page 页码，从0开始
 */
function loadPosts(page = 0) {
    const url = `/api/posts?page=${page}&size=10`;
    
    // 显示加载中状态
    document.getElementById('postList').innerHTML = '<div class="text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">加载中...</span></div></div>';
    
    // 发起API请求
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('网络请求失败');
            }
            return response.json();
        })
        .then(data => {
            renderPosts(data);
        })
        .catch(error => {
            console.error('获取帖子失败:', error);
            document.getElementById('postList').innerHTML = '<div class="alert alert-danger">加载帖子失败，请稍后重试</div>';
        });
}

/**
 * 渲染帖子列表
 * @param {Object} data 帖子数据
 */
function renderPosts(data) {
    const postList = document.getElementById('postList');
    postList.innerHTML = '';
    
    if (data.content && data.content.length > 0) {
        data.content.forEach(post => {
            const postElement = document.createElement('div');
            postElement.className = 'card post-card mb-3';
            postElement.innerHTML = `
                <div class="card-body">
                    <h5><a href="/post/${post.id}" class="text-decoration-none">${post.title}</a></h5>
                    <p class="text-muted">作者：${post.author.username} · 发布于 ${formatDate(post.createdAt)}</p>
                    <p>${post.content.length > 200 ? post.content.substring(0, 200) + '...' : post.content}</p>
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <span class="me-3"><i class="bi bi-eye"></i> ${post.viewCount || 0}</span>
                            <span class="me-3"><i class="bi bi-chat"></i> ${post.commentCount || 0}</span>
                            <span><i class="bi bi-hand-thumbs-up"></i> ${post.likeCount || 0}</span>
                        </div>
                        <a href="/post/${post.id}" class="btn btn-sm btn-outline-primary">查看详情</a>
                    </div>
                </div>
            `;
            
            postList.appendChild(postElement);
        });
        
        // 添加分页组件
        const paginationElement = document.createElement('div');
        paginationElement.className = 'mt-4';
        paginationElement.innerHTML = `
            <nav aria-label="Page navigation">
                <ul class="pagination justify-content-center">
                    <li class="page-item ${data.first ? 'disabled' : ''}">
                        <a class="page-link" href="#" data-page="${data.number - 1}">上一页</a>
                    </li>
                    <li class="page-item active">
                        <span class="page-link">${data.number + 1}</span>
                    </li>
                    <li class="page-item ${data.last ? 'disabled' : ''}">
                        <a class="page-link" href="#" data-page="${data.number + 1}">下一页</a>
                    </li>
                </ul>
            </nav>
        `;
        
        postList.appendChild(paginationElement);
        
        // 绑定分页点击事件
        document.querySelectorAll('.pagination a.page-link').forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const page = parseInt(this.getAttribute('data-page'));
                loadPosts(page);
            });
        });
    } else {
        postList.innerHTML = '<div class="alert alert-info">暂无帖子，来发表第一个帖子吧！</div>';
    }
}

/**
 * 创建新帖子
 * @param {string} title 帖子标题
 * @param {string} content 帖子内容
 */
function createPost(title, content) {
    if (!isAuthenticated()) {
        alert('请先登录');
        return;
    }
    
    fetch('/api/posts', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + getToken()
        },
        body: JSON.stringify({
            title: title,
            content: content
        })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => {
                throw new Error(err.message || '发布失败');
            });
        }
        return response.json();
    })
    .then(data => {
        // 重置表单
        document.getElementById('postForm').reset();
        
        // 重新加载帖子列表
        loadPosts();
        
        alert('发布成功');
    })
    .catch(error => {
        console.error('发布帖子失败:', error);
        alert(error.message || '发布失败，请稍后重试');
    });
}

/**
 * 添加评论
 * @param {number} postId 帖子ID
 * @param {string} content 评论内容
 * @param {function} callback 回调函数
 */
function addComment(postId, content, callback) {
    if (!isAuthenticated()) {
        alert('请先登录');
        return;
    }
    
    fetch(`/api/posts/${postId}/comments`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + getToken()
        },
        body: JSON.stringify({
            content: content
        })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => {
                throw new Error(err.message || '评论失败');
            });
        }
        return response.json();
    })
    .then(data => {
        if (callback) {
            callback(data);
        }
    })
    .catch(error => {
        console.error('评论失败:', error);
        alert(error.message || '评论失败，请稍后重试');
    });
}

/**
 * 格式化日期
 * @param {string} dateString 日期字符串
 * @return {string} 格式化后的日期
 */
function formatDate(dateString) {
    if (!dateString) return '';
    
    const date = new Date(dateString);
    const now = new Date();
    const diff = Math.floor((now - date) / 1000);
    
    if (diff < 60) {
        return `${diff}秒前`;
    } else if (diff < 3600) {
        return `${Math.floor(diff / 60)}分钟前`;
    } else if (diff < 86400) {
        return `${Math.floor(diff / 3600)}小时前`;
    } else if (diff < 2592000) {
        return `${Math.floor(diff / 86400)}天前`;
    } else {
        return date.toLocaleDateString('zh-CN');
    }
}

/**
 * 检查用户是否已登录
 * @return {boolean} 是否已登录
 */
function isAuthenticated() {
    return localStorage.getItem('token') !== null;
}

/**
 * 获取用户令牌
 * @return {string|null} 用户令牌
 */
function getToken() {
    return localStorage.getItem('token');
} 