import { handleApiResponse } from '../lib/ErrorHandler';

export async function get(url) {
    let res = await fetch(url, { 
        method: "GET", 
        // credentials: "include" 
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
        // credentials: "include"
    };
    let res = await fetch(url, opts);
    const response = await res.json();
    return handleApiResponse(response);
}

export async function del(url, data) {
    let opts = {
        method: "DELETE",
        // credentials: "include"
    };
    
    if (data) {
        opts.body = JSON.stringify(data);
        opts.headers = {
            'Content-Type': 'application/json'
        };
    }
    
    let res = await fetch(url, opts);
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
        // credentials: "include"
    };
    let res = await fetch(url, opts);
    const response = await res.json();
    return handleApiResponse(response);
}

export const BASEURL = 'http://localhost:8080';
export const PREFIX = `${BASEURL}/api`;
