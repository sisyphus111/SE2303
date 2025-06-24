<<<<<<< HEAD
<<<<<<< HEAD
import React, { useState } from 'react';
// import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import AppRouter from "./components/router";
import { ConfigProvider, theme } from "antd";
=======
import React, {useState} from 'react';
// import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import AppRouter from "./components/router";
import {ConfigProvider,theme} from "antd";
>>>>>>> lhy

function App() {
    const themeToken = {
        colorPrimary: "#1DA57A",
        colorInfo: "#1DA57A",
    };
<<<<<<< HEAD
    // const [user, setUser] = useState(null); 

    return (
        <ConfigProvider
            theme={{
                algorithm: theme.defaultAlgorithm,
                token: themeToken
            }}
        >
            <AppRouter />
        </ConfigProvider>
=======
    // const [user, setUser] = useState(null); // 全局用户状态

    return (
            <ConfigProvider
                theme={{
                    algorithm: theme.defaultAlgorithm,
                    token: themeToken
                }}
            >
                <AppRouter/>
            </ConfigProvider>
>>>>>>> lhy
    );
}

export default App;