import { List, Avatar, Button } from "antd";
import "../css/MentalResource.css";
import { useAudio } from "../lib/GlobalAudioPlayer";

function MusicList({ musics }) {
    const {play} = useAudio();

    return (
        <List
        itemLayout="horizontal"
        dataSource={musics}
        renderItem={item => (
            <List.Item
            actions={[
                <Button
                type="link"
                className="mental-resource-music-link"
                onClick={() => play(item)}
                >
                听一听
                </Button>,
            ]}
            >
            <List.Item.Meta
                avatar={<Avatar shape="square" size={64} src={item.cover} />}
                title={item.title}
                description={item.artist}
            />
            </List.Item>
        )}
        />
    );
}

export default MusicList;