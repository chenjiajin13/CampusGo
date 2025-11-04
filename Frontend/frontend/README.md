# frontend-admin (React + Vite + TS + AntD + React Query)

通过 `http://localhost:8080/v3/api-docs` 生成 typescript-axios 客户端。

## Quick Start
```bash
pnpm i
pnpm openapi:gen
pnpm dev
```

- 开发：`VITE_API_BASE_URL=http://localhost:8080`（网关）。
- 生产：构建后将 dist/ 托管到 Nginx 或复制到 gateway 静态目录。