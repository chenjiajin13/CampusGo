import { useQuery } from '@tanstack/react-query'
import { Table } from 'antd'
// 生成后按需引入：
// import { UsersApi } from '@/services/api/apis/UsersApi'
// import { apiConfig } from '@/lib/api'
// import { http } from '@/lib/http'
// const usersApi = new UsersApi(apiConfig, '', http)

export default function UsersList() {
  const q = useQuery({
    queryKey: ['users'],
    queryFn: async () => {
      // 替换为实际 OpenAPI 调用
      return []
    }
  })

  return (
    <Table
      rowKey={(r:any) => r.id ?? r.userId}
      loading={q.isLoading}
      dataSource={q.data ?? []}
      columns={[
        { title: 'ID', dataIndex: 'id' },
        { title: 'Username', dataIndex: 'username' },
        { title: 'Email', dataIndex: 'email' },
      ]}
    />
  )
}