import request from '../utils/request'

const login = (data) => request.post('/api/auth/login', data)

const register = (data) => request.post('/api/auth/register', data)

const getMe = () => request.get('/api/auth/me')

export { login, register, getMe }