// API端点配置
const API_ENDPOINTS = {
    LIST_RESOURCES: '/api/resources',
    FAVORITE: '/api/resources/favorite',
    RATE: '/api/resources/rate',
    COMMENT: '/api/resources/comment',
    DOWNLOAD: '/api/resources/download',
    SHARE: '/api/resources/share'
};

// 资源类型配置
const RESOURCE_TYPES = {
    ARTICLE: '文章',
    VIDEO: '视频',
    COURSE: '课程',
    DOCUMENT: '文档',
    CODE: '代码',
    OTHER: '其他'
};

// 资源分类配置
const RESOURCE_CATEGORIES = {
    PROGRAMMING: '编程开发',
    AI: '人工智能',
    DESIGN: '设计创意',
    BUSINESS: '商业管理',
    LANGUAGE: '语言学习',
    MATH: '数学',
    SCIENCE: '科学',
    ENGINEERING: '工程技术',
    HUMANITIES: '人文社科',
    OTHER: '其他'
};

// 状态变量
let currentPage = 0;
let pageSize = 12;
let isLoading = false;

document.addEventListener('DOMContentLoaded', function() {
    initializeUI();
    loadResources();
});

function initializeUI() {
    // 初始化分类筛选
    initializeCategories();
    // 初始化资源类型筛选
    initializeResourceTypes();
    // 初始化排序选项
    initializeSortOptions();
    // 绑定事件监听
    bindEventListeners();
}

function initializeCategories() {
    const categorySelect = document.getElementById('category');
    Object.entries(RESOURCE_CATEGORIES).forEach(([value, label]) => {
        const option = new Option(label, value);
        categorySelect.appendChild(option);
    });
}

function initializeResourceTypes() {
    const typeSelect = document.getElementById('resourceType');
    Object.entries(RESOURCE_TYPES).forEach(([value, label]) => {
        const option = new Option(label, value);
        typeSelect.appendChild(option);
    });
}

function bindEventListeners() {
    // 搜索表单提交
    document.getElementById('searchForm').addEventListener('submit', function(e) {
        e.preventDefault();
        currentPage = 0;
        loadResources();
    });

    // 分类切换
    document.getElementById('category').addEventListener('change', function() {
        currentPage = 0;
        loadResources();
    });

    // 资源类型切换
    document.getElementById('resourceType').addEventListener('change', function() {
        currentPage = 0;
        loadResources();
    });

    // 排序方式切换
    document.getElementById('sortOrder').addEventListener('change', function() {
        currentPage = 0;
        loadResources();
    });

    // 添加滚动加载事件
    window.addEventListener('scroll', function() {
        if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 100) {
            if (!isLoading) {
                loadResources(currentPage + 1);
            }
        }
    });
});

/**
 * 加载资源列表
 * @param {number} page 页码，从0开始
 */
async function loadResources(page = currentPage) {
    if (isLoading) return;
    isLoading = true;
    
    const keyword = document.getElementById('keyword').value;
    const category = document.getElementById('category').value;
    const sortOrder = document.getElementById('sortOrder').value;
    const [sortBy, sortDir] = sortOrder.split(',');
    
    // 构建API URL
    let url = `${API_ENDPOINTS.LIST_RESOURCES}?page=${page}&size=${pageSize}&sortBy=${sortBy}&sortDir=${sortDir}`;
    if (category) {
        url += `&category=${category}`;
    }
    if (keyword) {
        url += `&keyword=${keyword}`;
    }
    
    // 显示加载中状态
    const loadingHtml = '<div class="col-12 text-center mt-3"><div class="spinner-border text-primary" role="status"><span class="visually-hidden">加载中...</span></div></div>';
    if (page === 0) {
        document.getElementById('resourceList').innerHTML = loadingHtml;
    } else {
        const loadingDiv = document.createElement('div');
        loadingDiv.id = 'loadingMore';
        loadingDiv.innerHTML = loadingHtml;
        document.getElementById('resourceList').appendChild(loadingDiv);
    }
    
    try {
        // 发起API请求
        const response = await fetch(url, {
            headers: {
                'Authorization': isAuthenticated() ? `Bearer ${getToken()}` : ''
            }
        });
            if (!response.ok) {
                throw new Error('网络请求失败');
            }
            const data = await response.json();
            
            if (page === 0) {
                renderResources(data);
            } else {
                appendResources(data);
            }
            
            if (data.content && data.content.length > 0) {
                currentPage = page;
                renderPagination(data, page);
            }
        } catch (error) {
            console.error('获取资源失败:', error);
            const errorHtml = '<div class="col-12 text-center"><div class="alert alert-danger">加载资源失败，请稍后重试</div></div>';
            if (page === 0) {
                document.getElementById('resourceList').innerHTML = errorHtml;
            } else {
                document.getElementById('loadingMore')?.remove();
            }
        } finally {
            isLoading = false;
            document.getElementById('loadingMore')?.remove();
        }
    } catch (error) {
        console.error('收藏操作失败:', error);
        showToast('操作失败，请稍后重试', 'error');
    }
}

// 下载资源
async function downloadResource(resourceId) {
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }

    try {
        const response = await fetch(`${API_ENDPOINTS.DOWNLOAD}/${resourceId}`, {
            headers: {
                'Authorization': 'Bearer ' + getToken()
            }
        });

        if (!response.ok) {
            throw new Error('下载失败');
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = response.headers.get('Content-Disposition').split('filename=')[1];
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        showToast('下载开始');
    } catch (error) {
        console.error('下载失败:', error);
        showToast('下载失败，请稍后重试', 'error');
    }
}

// 评分功能
async function rateResource(resourceId, rating) {
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }

    try {
        const response = await fetch(`${API_ENDPOINTS.RATE}/${resourceId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getToken()
            },
            body: JSON.stringify({ rating })
        });

        if (!response.ok) {
            throw new Error('评分失败');
        }

        showToast('评分成功');
        // 重新加载资源以更新评分显示
        loadResources(currentPage);
    } catch (error) {
        console.error('评分失败:', error);
        showToast('评分失败，请稍后重试', 'error');
    }
}

// 显示提示消息
function showToast(message, type = 'success') {
    const toastContainer = document.getElementById('toastContainer') || createToastContainer();
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type === 'success' ? 'success' : 'danger'} border-0`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;

    toastContainer.appendChild(toast);
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();

    toast.addEventListener('hidden.bs.toast', () => {
        toast.remove();
    });
}

// 创建Toast容器
function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
    document.body.appendChild(container);
    return container;
}

// 检查认证状态
function isAuthenticated() {
    return getToken() !== null;
}

// 获取认证Token
function getToken() {
    return localStorage.getItem('token');
}
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

    if (!data.content?.length) {
        showEmptyState();
        return;
    }

    data.content.forEach(resource => {
        const card = createResourceCard(resource);
        resourceList.appendChild(card);
    });
}

function createResourceCard(resource) {
    const card = document.createElement('div');
    card.className = 'col-md-4 mb-4';
    card.innerHTML = `
        <div class="card h-100">
            ${resource.coverImage ? `<img src="${resource.coverImage}" class="card-img-top" alt="${resource.title}">` : ''}
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-start mb-2">
                    <span class="badge bg-primary">${RESOURCE_TYPES[resource.type] || '其他'}</span>
                    <span class="badge bg-secondary">${RESOURCE_CATEGORIES[resource.category] || '其他'}</span>
                </div>
                <h5 class="card-title">${resource.title}</h5>
                <p class="card-text text-muted">${resource.description}</p>
                <div class="resource-meta text-muted small mb-3">
                    <i class="bi bi-eye"></i> ${resource.viewCount || 0}
                    <i class="bi bi-heart ms-2"></i> ${resource.favoriteCount || 0}
                    <i class="bi bi-chat ms-2"></i> ${resource.commentCount || 0}
                </div>
                <div class="d-flex justify-content-between align-items-center">
                    <a href="${resource.url}" class="btn btn-primary btn-sm" target="_blank">查看详情</a>
                    <div class="btn-group">
                        <button onclick="toggleFavorite(${resource.id}, this)" class="btn btn-outline-danger btn-sm ${resource.isFavorited ? 'active' : ''}">
                            <i class="bi bi-heart${resource.isFavorited ? '-fill' : ''}"></i>
                        </button>
                        <button onclick="shareResource(${resource.id})" class="btn btn-outline-secondary btn-sm">
                            <i class="bi bi-share"></i>
                        </button>
                    </div>
                </div>
            </div>
            <div class="card-footer text-muted small">
                <i class="bi bi-clock"></i> ${formatDate(resource.createdAt)}
                <span class="ms-2">${resource.sourceWebsite}</span>
            </div>
        </div>
    `;
    return card;
}

function showEmptyState() {
    document.getElementById('resourceList').innerHTML = `
        <div class="col-12 text-center">
            <div class="alert alert-info">
                <i class="bi bi-info-circle me-2"></i>暂无相关资源
            </div>
        </div>
    `;
    } else {
        resourceList.innerHTML = '<div class="col-12 text-center"><div class="alert alert-warning">没有找到相关资源</div></div>';
    }
}

/**
 * 渲染分页控件
 * @param {Object} data 分页数据
 * @param {number} currentPage 当前页码
 */
function appendResources(data) {
    if (!data.content || data.content.length === 0) {
        return;
    }
    
    const resourceList = document.getElementById('resourceList');
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
            
            checkFavoriteStatus(resource.id, favoriteBtn);
        } else {
            favoriteBtn.onclick = function() {
                window.location.href = '/login.html';
                return false;
            };
        }
        
        resourceList.appendChild(resourceCard);
    });
}

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
// 资源操作函数
async function toggleFavorite(resourceId, button) {
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }

    try {
    
        const isFavorited = button.classList.contains('btn-danger');
        const method = isFavorited ? 'DELETE' : 'POST';
        const url = `${API_ENDPOINTS.FAVORITE}/${resourceId}`;
        
        const response = await fetch(url, {
        method: method,
        headers: {
            'Authorization': 'Bearer ' + getToken()
        }
    })
            if (!response.ok) {
            throw new Error('操作失败');
        }

        // 更新UI
        button.classList.toggle('btn-danger');
        button.classList.toggle('btn-outline-danger');
        
        const countSpan = button.querySelector('.favorite-count');
        const currentCount = parseInt(countSpan.textContent);
        countSpan.textContent = isFavorited ? currentCount - 1 : currentCount + 1;

        showToast(isFavorited ? '已取消收藏' : '收藏成功');
    } catch (error) {
        console.error('收藏操作失败:', error);
        showToast('操作失败，请稍后重试', 'error');
    }
}

// 下载资源
async function downloadResource(resourceId) {
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }

    try {
        const response = await fetch(`${API_ENDPOINTS.DOWNLOAD}/${resourceId}`, {
            headers: {
                'Authorization': 'Bearer ' + getToken()
            }
        });

        if (!response.ok) {
            throw new Error('下载失败');
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = response.headers.get('Content-Disposition').split('filename=')[1];
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        showToast('下载开始');
    } catch (error) {
        console.error('下载失败:', error);
        showToast('下载失败，请稍后重试', 'error');
    }
}

// 评分功能
async function rateResource(resourceId, rating) {
    if (!isAuthenticated()) {
        window.location.href = '/login.html';
        return;
    }

    try {
        const response = await fetch(`${API_ENDPOINTS.RATE}/${resourceId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + getToken()
            },
            body: JSON.stringify({ rating })
        });

        if (!response.ok) {
            throw new Error('评分失败');
        }

        showToast('评分成功');
        // 重新加载资源以更新评分显示
        loadResources(currentPage);
    } catch (error) {
        console.error('评分失败:', error);
        showToast('评分失败，请稍后重试', 'error');
    }
}

// 显示提示消息
function showToast(message, type = 'success') {
    const toastContainer = document.getElementById('toastContainer') || createToastContainer();
    const toast = document.createElement('div');
    toast.className = `toast align-items-center text-white bg-${type === 'success' ? 'success' : 'danger'} border-0`;
    toast.setAttribute('role', 'alert');
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');

    toast.innerHTML = `
        <div class="d-flex">
            <div class="toast-body">${message}</div>
            <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button>
        </div>
    `;

    toastContainer.appendChild(toast);
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();

    toast.addEventListener('hidden.bs.toast', () => {
        toast.remove();
    });
}

// 创建Toast容器
function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toastContainer';
    container.className = 'toast-container position-fixed bottom-0 end-0 p-3';
    document.body.appendChild(container);
    return container;
}

// 检查认证状态
function isAuthenticated() {
    return getToken() !== null;
}

// 获取认证Token
function getToken() {
    return localStorage.getItem('token');
}
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