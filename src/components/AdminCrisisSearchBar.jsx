import React from 'react';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';

const AdminCrisisSearchBar = ({
  searchTerm, filterLevel, filterType, filterStatus,
  alertLevels, alertTypes, alertStatuses,
  onSearchTermChange, onFilterLevelChange, onFilterTypeChange, onFilterStatusChange,
  onRefresh, loading, pagination
}) => (
  <div className="admin-crisis-controls">
    <div className="admin-search-box">
      <SearchOutlined className="admin-search-icon" />
      <input
        type="text"
        placeholder="搜索用户ID..."
        value={searchTerm}
        onChange={onSearchTermChange}
        className="admin-search-input"
      />
    </div>
    <div className="admin-filter-box">
      <select value={filterLevel} onChange={onFilterLevelChange} className="admin-filter-select">
        <option value="all">全部警报等级</option>
        {alertLevels && typeof alertLevels === 'object' && Object.entries(alertLevels).map(([key, value]) => (
          <option key={key} value={key}>{value}</option>
        ))}
      </select>
    </div>
    <div className="admin-filter-box">
      <select value={filterType} onChange={onFilterTypeChange} className="admin-filter-select">
        <option value="all">全部警报类型</option>
        {alertTypes && typeof alertTypes === 'object' && Object.entries(alertTypes).map(([key, value]) => (
          <option key={key} value={key}>{value}</option>
        ))}
      </select>
    </div>
    <div className="admin-filter-box">
      <select value={filterStatus} onChange={onFilterStatusChange} className="admin-filter-select">
        <option value="all">全部状态</option>
        {alertStatuses && typeof alertStatuses === 'object' && Object.entries(alertStatuses).map(([key, value]) => (
          <option key={key} value={key}>{value}</option>
        ))}
      </select>
    </div>
    <button 
      onClick={() => onRefresh(pagination.current, pagination.pageSize)}
      disabled={loading}
      className="admin-btn admin-btn-primary"
    >
      <ReloadOutlined />
      刷新
    </button>
  </div>
);

export default AdminCrisisSearchBar; 