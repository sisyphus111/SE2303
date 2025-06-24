import { List } from "antd";
import "../css/MentalResource.css";


function ArticleList({ articles }) {
  return (
    <List
      itemLayout="vertical"
      dataSource={articles}
      renderItem={item => (
        <List.Item
          key={item.title}
          extra={
            <img
              alt="cover"
              src={item.cover}
              className="mental-resource-img-cover"
            />
          }
        >
          <List.Item.Meta
            title={<a href={item.link}>{item.title}</a>}
            description={item.description}
          />
        </List.Item>
      )}
    />
  );
}

export default ArticleList;