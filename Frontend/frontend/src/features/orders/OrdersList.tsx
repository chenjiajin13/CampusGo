import { useQuery } from '@tanstack/react-query'
import { Table } from 'antd'

export default function OrdersList() {
  const q = useQuery({
    queryKey: ['orders'],
    queryFn: async () => {
      // 替换为实际 OpenAPI 调用
      return []
    }
  })

  return (
    <Table
      rowKey={(r:any) => r.id ?? r.orderId}
      loading={q.isLoading}
      dataSource={q.data ?? []}
      columns={[
        { title: 'Order ID', dataIndex: 'id' },
        { title: 'User ID', dataIndex: 'userId' },
        { title: 'Amount', dataIndex: 'amount' },
        { title: 'Status', dataIndex: 'status' },
      ]}
    />
  )
}