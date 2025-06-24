import { List, Avatar, Button } from "antd";
import "../css/MentalResource.css";

function MusicList({ musics }) {
  return (
    <List
      itemLayout="horizontal"
      dataSource={musics}
      renderItem={item => (
        <List.Item
          actions={[
            <Button type="link" href={item.url} target="_blank" className="mental-resource-music-link">
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