import { List } from "antd";
import "../css/MentalResource.css";


function ArticleList({ articles, onArticleClick }) {
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
              onClick={() => onArticleClick && onArticleClick(item.id)}
            />
          }
        >
          <List.Item.Meta
            title={
              <span
                className="mental-resource-article-title"
                onClick={() => onArticleClick && onArticleClick(item.id)}
              >
                {item.title}
              </span>
            }
            description={item.description}
          />
        </List.Item>
      )}
    />
  );
}

export default ArticleList;