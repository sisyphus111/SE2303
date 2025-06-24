import { BrowserRouter, Route, Routes } from "react-router-dom";
import PostDetailPage from "../pages/post"
import SendPostPage from "../pages/send_post"

export default function AppRouter() {
    return (
        <BrowserRouter>
            <Routes>
                <Route index element={<PostDetailPage />} />
                <Route path="/post_details" element={<PostDetailPage />} />
                {/*<Route path="/send_post" element={<PostDetailPage />} />*/}
            </Routes>
        </BrowserRouter>
    );
}