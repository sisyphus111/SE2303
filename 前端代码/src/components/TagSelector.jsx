import React from 'react';
import '../css/TagSelector.css';

const TagSelector = ({ 
  tags = [], 
  selectedTags = [], 
  onTagToggle, 
  title = "选择标签",
  maxSelection = null,
  variant = "default", // "default" 或 "mood"
  tagIcons = {} // 标签图标映射
}) => {
  const handleTagClick = (tag) => {
    if (maxSelection && selectedTags.length >= maxSelection && !selectedTags.includes(tag)) {
      return; // 达到最大选择数量限制
    }
    onTagToggle(tag);
  };

  return (
    <div className={`tag-selector ${variant}`}>
      {title && title.trim() && <p className="tag-selector-title">{title}</p>}
      <div className="tags-grid">
        {tags.map(tag => {
          const tagValue = typeof tag === 'string' ? tag : tag.value || tag.label;
          const tagLabel = typeof tag === 'string' ? tag : tag.label || tag.value;
          
          return (
            <span
              key={tagValue}
              className={`tag ${selectedTags.includes(tagValue) ? 'selected' : ''}`}
              onClick={() => handleTagClick(tagValue)}
            >
              {tagIcons[tagValue] && <span className="tag-icon">{tagIcons[tagValue]}</span>}
              <span className="tag-text">{tagLabel}</span>
            </span>
          );
        })}
      </div>
      {maxSelection && (
        <div className="tag-limit-hint">
          最多可选择 {maxSelection} 个标签 ({selectedTags.length}/{maxSelection})
        </div>
      )}
    </div>
  );
};

export default TagSelector; 