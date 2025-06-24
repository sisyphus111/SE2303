import React, {useState} from 'react';
// import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import AppRouter from "./components/router";
import {ConfigProvider,theme} from "antd";

function App() {
    const themeToken = {
        colorPrimary: "#1DA57A",
        colorInfo: "#1DA57A",
    };
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
    );
}

export default App;
