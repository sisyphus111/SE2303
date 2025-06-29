import { handleApiResponse } from '../lib/ErrorHandler';

export async function get(url) {
    let res = await fetch(`${PREFIX}${url}`, { 
        method: "GET", 
        credentials: "include" // 包含cookie进行认证
    });
    const response = await res.json(); // 反序列化
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
    return handleApiResponse(response);
}

export async function post(url, data) {
    let opts = {
        method: "POST",
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: "include" // 包含cookie进行认证
    };
    let res = await fetch(`${PREFIX}${url}`, opts);
    const response = await res.json();
    return handleApiResponse(response);
}

export const BASEURL = 'http://localhost:8080';
export const PREFIX = `${BASEURL}/api`;
