document.addEventListener('DOMContentLoaded', function() {
    // 初始化页面
    loadResources();
    
    // 绑定搜索事件
    document.getElementById('searchForm').addEventListener('submit', function(e) {
        e.preventDefault();
        loadResources();
    });
    
    // 绑定分类切换事件
    document.getElementById('category').addEventListener('change', function() {
        loadResources();
    });
    
    // 绑定排序切换事件
    document.getElementById('sortOrder').addEventListener('change', function() {
        loadResources();
    });
});

/**
 * 加载资源列表
 * @param {number} page 页码，从0开始
 */
function loadResources(page = 0) {
    const keyword = document.getElementById('keyword').value;
    const category = document.getElementById('category').value;
    const sortOrder = document.getElementById('sortOrder').value;
    const sortParts = sortOrder.split(',');
    const sortBy = sortParts[0];
    const sortDir = sortParts[1];
    
    // 构建API URL
    let url = `/api/resources?page=${page}&size=12&sortBy=${sortBy}&sortDir=${sortDir}`;
    if (category) {
        url += `&category=${category}`;
    }
    if (keyword) {
        url += `&keyword=${keyword}`;
    }
    
    // 显示加载中状态
    document.getElementById('resourceList').innerHTML = '<div class="col-12 text-center"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">加载中...</span></div></div>';
    
    // 发起API请求
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('网络请求失败');
            }
            return response.json();
        })
        .then(data => {
            renderResources(data);
            renderPagination(data, page);
        })
        .catch(error => {
            console.error('获取资源失败:', error);
            document.getElementById('resourceList').innerHTML = '<div class="col-12 text-center"><div class="alert alert-danger">加载资源失败，请稍后重试</div></div>';
        });
}

/**
 * 渲染资源列表
 * @param {Object} data 资源数据
 */
function renderResources(data) {
    const resourceList = document.getElementById('resourceList');
    resourceList.innerHTML = '';
    
    if (data.content && data.content.length > 0) {
        data.content.forEach(resource => {
            const template = document.getElementById('resourceCardTemplate');
            const resourceCard = template.content.cloneNode(true);
            
            resourceCard.querySelector('.resource-title').textContent = resource.title;
            resourceCard.querySelector('.resource-description').textContent = resource.description;
            resourceCard.querySelector('.resource-category').textContent = getCategoryName(resource.category);
            resourceCard.querySelector('.resource-views').textContent = resource.viewCount;
            resourceCard.querySelector('.resource-date').textContent = formatDate(resource.createdAt);
            resourceCard.querySelector('.favorite-count').textContent = resource.favoriteCount || 0;
            
            const favoriteBtn = resourceCard.querySelector('.favorite-btn');
            const resourceLink = resourceCard.querySelector('.resource-link');
            resourceLink.href = `/resource/${resource.id}`;
            
            if (isAuthenticated()) {
                favoriteBtn.onclick = function() {
                    toggleFavorite(resource.id, favoriteBtn);
                    return false;
                };
                
                // 检查资源是否已收藏
                checkFavoriteStatus(resource.id, favoriteBtn);
            } else {
                favoriteBtn.onclick = function() {
                    window.location.href = '/login.html';
                    return false;
                };
            }
            
            resourceList.appendChild(resourceCard);
        });
    } else {
        resourceList.innerHTML = '<div class="col-12 text-center"><div class="alert alert-warning">没有找到相关资源</div></div>';
    }
}

/**
 * 渲染分页控件
 * @param {Object} data 分页数据
 * @param {number} currentPage 当前页码
 */
function renderPagination(data, currentPage) {
    const pagination = document.getElementById('pagination');
    pagination.innerHTML = '';
    
    if (data.totalPages <= 1) {
        return;
    }
    
    // 上一页按钮
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${currentPage === 0 ? 'disabled' : ''}`;
    const prevLink = document.createElement('a');
    prevLink.className = 'page-link';
    prevLink.href = '#';
    prevLink.textContent = '上一页';
    if (currentPage > 0) {
        prevLink.onclick = function() {
            loadResources(currentPage - 1);
            return false;
        };
    }
    prevLi.appendChild(prevLink);
    pagination.appendChild(prevLi);
    
    // 页码按钮
    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(data.totalPages - 1, currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
        const pageLi = document.createElement('li');
        pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
        const pageLink = document.createElement('a');
        pageLink.className = 'page-link';
        pageLink.href = '#';
        pageLink.textContent = i + 1;
        if (i !== currentPage) {
            pageLink.onclick = function() {
                loadResources(i);
                return false;
            };
        }
        pageLi.appendChild(pageLink);
        pagination.appendChild(pageLi);
    }
    
    // 下一页按钮
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === data.totalPages - 1 ? 'disabled' : ''}`;
    const nextLink = document.createElement('a');
    nextLink.className = 'page-link';
    nextLink.href = '#';
    nextLink.textContent = '下一页';
    if (currentPage < data.totalPages - 1) {
        nextLink.onclick = function() {
            loadResources(currentPage + 1);
            return false;
        };
    }
    nextLi.appendChild(nextLink);
    pagination.appendChild(nextLi);
}

/**
 * 切换收藏状态
 * @param {number} resourceId 资源ID
 * @param {HTMLElement} button 收藏按钮
 */
function toggleFavorite(resourceId, button) {
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }
    
    const isFavorited = button.classList.contains('btn-danger');
    const method = isFavorited ? 'DELETE' : 'POST';
    const url = `/api/resources/${resourceId}/favorite`;
    
    fetch(url, {
        method: method,
        headers: {
            'Authorization': 'Bearer ' + getToken()
        }
    })
    .then(response => {
        if (response.ok) {
            const countSpan = button.querySelector('.favorite-count');
            let count = parseInt(countSpan.textContent);
            
            if (isFavorited) {
                button.classList.remove('btn-danger');
                button.classList.add('btn-outline-danger');
                countSpan.textContent = Math.max(0, count - 1);
            } else {
                button.classList.remove('btn-outline-danger');
                button.classList.add('btn-danger');
                countSpan.textContent = count + 1;
            }
        }
    })
    .catch(error => {
        console.error('收藏操作失败:', error);
    });
}

/**
 * 检查资源是否已收藏
 * @param {number} resourceId 资源ID
 * @param {HTMLElement} button 收藏按钮
 */
function checkFavoriteStatus(resourceId, button) {
    if (!isAuthenticated()) {
        return;
    }
    
    fetch(`/api/resources/${resourceId}/favorite/check`, {
        headers: {
            'Authorization': 'Bearer ' + getToken()
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data === true) {
            button.classList.remove('btn-outline-danger');
            button.classList.add('btn-danger');
        }
    })
    .catch(error => {
        console.error('检查收藏状态失败:', error);
    });
}

/**
 * 获取分类名称
 * @param {string} categoryCode 分类代码
 * @return {string} 分类名称
 */
function getCategoryName(categoryCode) {
    const categories = {
        'programming': '编程',
        'ai': '人工智能',
        'design': '设计',
        'business': '商业',
        'language': '语言学习'
    };
    
    return categories[categoryCode] || categoryCode;
}

/**
 * 格式化日期
 * @param {string} dateString 日期字符串
 * @return {string} 格式化后的日期
 */
function formatDate(dateString) {
    if (!dateString) return '';
    
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN');
}

/**
 * 检查用户是否已登录
 * @return {boolean} 是否已登录
 */
function isAuthenticated() {
    return getToken() !== null;
}

/**
 * 获取用户令牌
 * @return {string|null} 用户令牌
 */
function getToken() {
    return localStorage.getItem('token');
} 