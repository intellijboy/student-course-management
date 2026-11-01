// ============================================
// 通用请求工具模块
// 封装 fetch 调用后端 REST API 与统一错误处理
// ============================================

export async function request(url, method, body) {
  method = method || 'GET';
  const options = { method: method, headers: { 'Content-Type': 'application/json' } };
  if (body !== undefined && method !== 'GET') {
    options.body = JSON.stringify(body);
  }
  const res = await fetch(url, options);
  if (!res.ok) {
    let message = '请求失败 (HTTP ' + res.status + ')';
    try {
      const data = await res.json();
      if (data && (data.message || data.error)) {
        message = data.message || data.error;
      }
    } catch (e) { /* 忽略非 JSON 响应 */ }
    throw new Error(message);
  }
  if (res.status === 204) return null;
  return res.json();
}
