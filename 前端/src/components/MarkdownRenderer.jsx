import React from 'react';
import ReactMarkdown from 'react-markdown';
import '../css/MarkdownRenderer.css';

const MarkdownRenderer = ({ content, className = '' }) => {
  // 自定义组件渲染器
  const components = {
    // 代码块
    code: ({ node, inline, className, children, ...props }) => {
      const match = /language-(\w+)/.exec(className || '');
      const language = match ? match[1] : '';
      
      if (!inline) {
        return (
          <div className="code-block-container">
            {language && (
              <div className="code-block-header">
                <span className="code-language">{language}</span>
                <button 
                  className="copy-button"
                  onClick={() => navigator.clipboard.writeText(String(children))}
                  title="复制代码"
                >
                  📋
                </button>
              </div>
            )}
            <pre className="code-block">
              <code className={className} {...props}>
                {children}
              </code>
            </pre>
          </div>
        );
      }
      
      return (
        <code className="inline-code" {...props}>
          {children}
        </code>
      );
    },
    
    // 链接
    a: ({ href, children, ...props }) => (
      <a 
        href={href} 
        target="_blank" 
        rel="noopener noreferrer"
        className="markdown-link"
        {...props}
      >
        {children}
      </a>
    ),
    
    // 引用块
    blockquote: ({ children, ...props }) => (
      <blockquote className="markdown-blockquote" {...props}>
        {children}
      </blockquote>
    ),
    
    // 表格
    table: ({ children, ...props }) => (
      <div className="table-container">
        <table className="markdown-table" {...props}>
          {children}
        </table>
      </div>
    ),
    
    // 列表
    ul: ({ children, ...props }) => (
      <ul className="markdown-list" {...props}>
        {children}
      </ul>
    ),
    
    ol: ({ children, ...props }) => (
      <ol className="markdown-list ordered" {...props}>
        {children}
      </ol>
    ),
    
    // 标题
    h1: ({ children, ...props }) => (
      <h1 className="markdown-heading h1" {...props}>
        {children}
      </h1>
    ),
    
    h2: ({ children, ...props }) => (
      <h2 className="markdown-heading h2" {...props}>
        {children}
      </h2>
    ),
    
    h3: ({ children, ...props }) => (
      <h3 className="markdown-heading h3" {...props}>
        {children}
      </h3>
    ),
    
    h4: ({ children, ...props }) => (
      <h4 className="markdown-heading h4" {...props}>
        {children}
      </h4>
    ),
    
    h5: ({ children, ...props }) => (
      <h5 className="markdown-heading h5" {...props}>
        {children}
      </h5>
    ),
    
    h6: ({ children, ...props }) => (
      <h6 className="markdown-heading h6" {...props}>
        {children}
      </h6>
    ),
    
    // 段落
    p: ({ children, ...props }) => (
      <p className="markdown-paragraph" {...props}>
        {children}
      </p>
    ),
    
    // 强调
    strong: ({ children, ...props }) => (
      <strong className="markdown-bold" {...props}>
        {children}
      </strong>
    ),
    
    em: ({ children, ...props }) => (
      <em className="markdown-italic" {...props}>
        {children}
      </em>
    ),
    
    // 分割线
    hr: ({ ...props }) => (
      <hr className="markdown-divider" {...props} />
    )
  };

  return (
    <div className={`markdown-renderer ${className}`}>
      <ReactMarkdown components={components}>
        {content}
      </ReactMarkdown>
    </div>
  );
};

export default MarkdownRenderer; 