import React from 'react';
import { Pagination } from 'antd';

const AdminCrisisPagination = ({ pagination, onPageChange }) => (
  pagination.total > 0 && (
    <div className="admin-pagination">
      <Pagination
        current={pagination.current + 1}
        pageSize={pagination.pageSize}
        total={pagination.total}
        onChange={onPageChange}
        showSizeChanger
        showQuickJumper
        showTotal={(total, range) => `第 ${range[0]}-${range[1]} 条，共 ${total} 条`}
      />
    </div>
  )
);

export default AdminCrisisPagination; 