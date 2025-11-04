import { http } from './http'

export async function login(username: string, password: string): Promise<string> {
  // 根据后端实际调整
  const { data } = await http.post('/auth/login', { username, password })
  return data.accessToken || data.token || data.access_token
}