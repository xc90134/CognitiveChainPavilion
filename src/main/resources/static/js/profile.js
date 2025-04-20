/**
 * 用户个人中心脚本
 */

// 当页面加载完成时执行
document.addEventListener('DOMContentLoaded', function() {
    // 检查用户是否已登录
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }
    
    // 加载用户信息
    loadUserProfile();
    
    // 加载用户收藏
    loadUserFavorites();
    
    // 监听标签页切换事件
    document.querySelectorAll('#profileTabs a').forEach(tab => {
        tab.addEventListener('shown.bs.tab', function(event) {
            const target = event.target.getAttribute('href');
            
            if (target === '#favorites') {
                loadUserFavorites();
            } else if (target === '#history') {
                loadViewHistory();
            } else if (target === '#activities') {
                loadUserActivities();
            }
        });
    });
});

/**
 * 加载用户个人资料
 */
function loadUserProfile() {
    // 从localStorage获取用户信息
    const user = getCurrentUser();
    
    if (user) {
        document.getElementById('profileUsername').textContent = user.username;
        document.getElementById('profileEmail').textContent = user.email || '未设置邮箱';
    }
}

/**
 * 加载用户收藏列表
 */
function loadUserFavorites(page = 0) {
    fetch(`/api/favorites?page=${page}&size=6`, {
        headers: {
            'Authorization': `Bearer ${getToken()}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('加载收藏失败');
        }
        return response.json();
    })
    .then(data => {
        renderFavorites(data.content);
        renderPagination(data, page, 'favoritesPagination', loadUserFavorites);
    })
    .catch(error => {
        console.error('Error loading favorites:', error);
        document.getElementById('favoritesList').innerHTML = `
            <div class="col-12 text-center py-5">
                <h4>加载收藏失败</h4>
                <p class="text-muted">请稍后再试</p>
            </div>
        `;
    });
}

/**
 * 渲染收藏列表
 */
function renderFavorites(favorites) {
    const favoritesList = document.getElementById('favoritesList');
    favoritesList.innerHTML = '';
    
    if (!favorites || favorites.length === 0) {
        favoritesList.innerHTML = `
            <div class="col-12 text-center py-5">
                <h4>暂无收藏</h4>
                <p class="text-muted">您还没有收藏任何资源</p>
            </div>
        `;
        return;
    }
    
    const template = document.getElementById('resourceCardTemplate');
    
    favorites.forEach(favorite => {
        const resource = favorite.resource;
        const clone = template.content.cloneNode(true);
        
        // 设置资源信息
        clone.querySelector('.resource-title').textContent = resource.title;
        clone.querySelector('.resource-description').textContent = 
            resource.description.length > 100 ? resource.description.substring(0, 100) + '...' : resource.description;
        clone.querySelector('.resource-category').textContent = getCategoryName(resource.category);
        clone.querySelector('.resource-views').textContent = resource.viewCount;
        clone.querySelector('.resource-date').textContent = formatDate(favorite.createdAt);
        clone.querySelector('.resource-link').href = resource.url;
        
        // 设置取消收藏按钮
        const unfavoriteBtn = clone.querySelector('.unfavorite-btn');
        unfavoriteBtn.dataset.id = resource.id;
        unfavoriteBtn.addEventListener('click', function() {
            removeFavorite(resource.id, this);
        });
        
        favoritesList.appendChild(clone);
    });
}

/**
 * 取消收藏
 */
function removeFavorite(resourceId, button) {
    fetch(`/api/favorites/${resourceId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${getToken()}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('取消收藏失败');
        }
        
        // 移除资源卡片
        const card = button.closest('.col-md-4');
        card.remove();
        
        // 检查是否还有收藏
        const favoritesList = document.getElementById('favoritesList');
        if (favoritesList.children.length === 0) {
            favoritesList.innerHTML = `
                <div class="col-12 text-center py-5">
                    <h4>暂无收藏</h4>
                    <p class="text-muted">您还没有收藏任何资源</p>
                </div>
            `;
        }
    })
    .catch(error => {
        console.error('Error removing favorite:', error);
        alert('取消收藏失败，请稍后再试');
    });
}

/**
 * 加载浏览历史
 */
function loadViewHistory(page = 0) {
    fetch(`/api/activities/views?page=${page}&size=10`, {
        headers: {
            'Authorization': `Bearer ${getToken()}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('加载浏览历史失败');
        }
        return response.json();
    })
    .then(data => {
        renderHistory(data.content);
        renderPagination(data, page, 'historyPagination', loadViewHistory);
    })
    .catch(error => {
        console.error('Error loading view history:', error);
        document.getElementById('historyList').innerHTML = `
            <div class="text-center py-5">
                <h4>加载浏览历史失败</h4>
                <p class="text-muted">请稍后再试</p>
            </div>
        `;
    });
}

/**
 * 渲染浏览历史
 */
function renderHistory(activities) {
    const historyList = document.getElementById('historyList');
    historyList.innerHTML = '';
    
    if (!activities || activities.length === 0) {
        historyList.innerHTML = `
            <div class="text-center py-5">
                <h4>暂无浏览历史</h4>
                <p class="text-muted">您还没有浏览任何资源</p>
            </div>
        `;
        return;
    }
    
    const template = document.getElementById('activityItemTemplate');
    
    activities.forEach(activity => {
        const resource = activity.resource;
        const clone = template.content.cloneNode(true);
        
        // 设置活动信息
        clone.querySelector('.activity-title').textContent = resource.title;
        clone.querySelector('.activity-description').textContent = 
            resource.description.length > 100 ? resource.description.substring(0, 100) + '...' : resource.description;
        clone.querySelector('.activity-time').textContent = formatDate(activity.createdAt);
        clone.querySelector('.activity-type').textContent = '浏览';
        
        // 设置链接
        const link = clone.querySelector('a');
        link.href = resource.url;
        link.target = '_blank';
        
        historyList.appendChild(clone);
    });
}

/**
 * 加载用户活动记录
 */
function loadUserActivities(page = 0) {
    fetch(`/api/activities?page=${page}&size=10`, {
        headers: {
            'Authorization': `Bearer ${getToken()}`
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('加载活动记录失败');
        }
        return response.json();
    })
    .then(data => {
        renderActivities(data.content);
        renderPagination(data, page, 'activitiesPagination', loadUserActivities);
    })
    .catch(error => {
        console.error('Error loading activities:', error);
        document.getElementById('activitiesList').innerHTML = `
            <div class="text-center py-5">
                <h4>加载活动记录失败</h4>
                <p class="text-muted">请稍后再试</p>
            </div>
        `;
    });
}

/**
 * 渲染活动记录
 */
function renderActivities(activities) {
    const activitiesList = document.getElementById('activitiesList');
    activitiesList.innerHTML = '';
    
    if (!activities || activities.length === 0) {
        activitiesList.innerHTML = `
            <div class="text-center py-5">
                <h4>暂无活动记录</h4>
                <p class="text-muted">您还没有任何活动</p>
            </div>
        `;
        return;
    }
    
    const template = document.getElementById('activityItemTemplate');
    
    activities.forEach(activity => {
        const resource = activity.resource;
        const clone = template.content.cloneNode(true);
        
        // 设置活动信息
        clone.querySelector('.activity-title').textContent = resource.title;
        clone.querySelector('.activity-description').textContent = 
            resource.description.length > 100 ? resource.description.substring(0, 100) + '...' : resource.description;
        clone.querySelector('.activity-time').textContent = formatDate(activity.createdAt);
        
        // 设置活动类型
        const activityTypeText = activity.activityType === 'VIEW' ? '浏览' : 
                                 activity.activityType === 'FAVORITE' ? '收藏' : 
                                 activity.activityType;
        clone.querySelector('.activity-type').textContent = activityTypeText;
        
        // 设置链接
        const link = clone.querySelector('a');
        link.href = resource.url;
        link.target = '_blank';
        
        activitiesList.appendChild(clone);
    });
}

/**
 * 渲染分页
 */
function renderPagination(data, currentPage, paginationId, loadFunction) {
    const pagination = document.getElementById(paginationId);
    pagination.innerHTML = '';
    
    const totalPages = data.totalPages;
    
    if (totalPages <= 1) {
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
        prevLink.addEventListener('click', function(e) {
            e.preventDefault();
            loadFunction(currentPage - 1);
        });
    }
    prevLi.appendChild(prevLink);
    pagination.appendChild(prevLi);
    
    // 页码按钮
    for (let i = 0; i < totalPages; i++) {
        if (totalPages <= 5 || i === 0 || i === totalPages - 1 || Math.abs(i - currentPage) <= 1) {
            const pageLi = document.createElement('li');
            pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
            const pageLink = document.createElement('a');
            pageLink.className = 'page-link';
            pageLink.href = '#';
            pageLink.textContent = i + 1;
            pageLink.addEventListener('click', function(e) {
                e.preventDefault();
                loadFunction(i);
            });
            pageLi.appendChild(pageLink);
            pagination.appendChild(pageLi);
        } else if (Math.abs(i - currentPage) === 2) {
            const ellipsisLi = document.createElement('li');
            ellipsisLi.className = 'page-item disabled';
            const ellipsisSpan = document.createElement('span');
            ellipsisSpan.className = 'page-link';
            ellipsisSpan.textContent = '...';
            ellipsisLi.appendChild(ellipsisSpan);
            pagination.appendChild(ellipsisLi);
        }
    }
    
    // 下一页按钮
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${currentPage === totalPages - 1 ? 'disabled' : ''}`;
    const nextLink = document.createElement('a');
    nextLink.className = 'page-link';
    nextLink.href = '#';
    nextLink.textContent = '下一页';
    if (currentPage < totalPages - 1) {
        nextLink.addEventListener('click', function(e) {
            e.preventDefault();
            loadFunction(currentPage + 1);
        });
    }
    nextLi.appendChild(nextLink);
    pagination.appendChild(nextLi);
}

/**
 * 获取分类名称
 */
function getCategoryName(category) {
    const categoryMap = {
        'programming': '编程',
        'ai': '人工智能',
        'design': '设计',
        'business': '商业',
        'language': '语言学习',
        'PROGRAMMING': '编程',
        'AI': '人工智能',
        'DESIGN': '设计',
        'BUSINESS': '商业',
        'LANGUAGE': '语言学习'
    };
    
    return categoryMap[category] || category;
}

/**
 * 格式化日期
 */
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
} 