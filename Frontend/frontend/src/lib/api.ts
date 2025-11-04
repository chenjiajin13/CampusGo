import { Configuration } from '@/services/api/runtime'
import { http } from '@/lib/http'

export const apiConfig = new Configuration({ basePath: '' })
// 例：
// import { UsersApi } from '@/services/api/apis/UsersApi'
// const usersApi = new UsersApi(apiConfig, '', http)