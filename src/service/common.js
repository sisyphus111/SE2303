import { handleApiResponse } from '../lib/ErrorHandler';



export async function get(url, options = {}) {
    let fullUrl = `${PREFIX}${url}`;
    if (options.params) {
        const searchParams = new URLSearchParams();
        Object.entries(options.params).forEach(([key, value]) => {
            if (value !== undefined && value !== null) {
                searchParams.append(key, value);
            }
        });
        const queryString = searchParams.toString();
        console.log("get请求：",queryString);
        if (queryString) {
            fullUrl += `?${queryString}`;
        }
    }

    console.log(`🌐 GET请求: ${fullUrl}`);

    let res = await fetch(fullUrl, {
        method: "GET",
        credentials: "include" // 包含cookie进行认证
    });
    const response = await res.json();

    // 如果HTTP状态码是401但响应体没有errorCode，添加errorCode字段
    if (res.status === 401 && !response.errorCode) {
        response.errorCode = 401;
    }

    return handleApiResponse(response);
}

export async function put(url, data) {
    let opts = {
        method: "PUT",
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: "include" // 包含cookie进行认证
    };
    let res = await fetch(`${PREFIX}${url}`, opts);
    const response = await res.json();

    // 如果HTTP状态码是401但响应体没有errorCode，添加errorCode字段
    if (res.status === 401 && !response.errorCode) {
        response.errorCode = 401;
    }

    return handleApiResponse(response);
}

export async function del(url, data) {
    let opts = {
        method: "DELETE",
        credentials: "include" // 包含cookie进行认证
    };

    if (data) {
        opts.body = JSON.stringify(data);
        opts.headers = {
            'Content-Type': 'application/json'
        };
    }

    let res = await fetch(`${PREFIX}${url}`, opts);
    const response = await res.json();

    // 如果HTTP状态码是401但响应体没有errorCode，添加errorCode字段
    if (res.status === 401 && !response.errorCode) {
        response.errorCode = 401;
    }

    return handleApiResponse(response);
}

export async function post(url, data, options = {}) {
    let fullUrl = `${PREFIX}${url}`;

    // 如果有查询参数，添加到URL中
    if (options.params) {
        const searchParams = new URLSearchParams();
        Object.entries(options.params).forEach(([key, value]) => {
            if (value !== undefined && value !== null) {
                searchParams.append(key, value);
            }
        });
        const queryString = searchParams.toString();
        if (queryString) {
            fullUrl += `?${queryString}`;
        }
    }

    console.log(`🌐 POST请求: ${fullUrl}`);
    console.log('📤 请求数据:', data);

    let opts = {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json',
            ...options.headers // 合并额外的请求头
        },
        credentials: "include" // 包含cookie进行认证
    };

    let res = await fetch(fullUrl, opts);
    console.log(`📥 HTTP响应状态: ${res.status} ${res.statusText}`);

    const response = await res.json();
    console.log('📥 响应数据:', response);

    // 如果HTTP状态码是401但响应体没有errorCode，添加errorCode字段
    if (res.status === 401 && !response.errorCode) {
        response.errorCode = 401;
    }

    return handleApiResponse(response);
}

export const BASEURL = 'https://16f66c5b.r5.cpolar.top';
export const PREFIX = `${BASEURL}/api`;
