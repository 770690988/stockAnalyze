import axios from 'axios'

// ==================== 板块类型接口 ====================

export const getBkTypeList = () => axios.get('/watchlist/bk/type/list')

export const addBkType = (data) => axios.post('/watchlist/bk/type/add', data)

export const updateBkType = (data) => axios.post('/watchlist/bk/type/update', data)

export const deleteBkType = (id) => axios.post(`/watchlist/bk/type/delete/${id}`)

// ==================== 板块接口 ====================

export const getBkList = () => axios.get('/watchlist/bk/list')

export const addBk = (data) => axios.post('/watchlist/bk/add', data)

export const updateBk = (data) => axios.post('/watchlist/bk/update', data)

export const deleteBk = (id) => axios.post(`/watchlist/bk/delete/${id}`)

// ==================== 股票接口 ====================

export const getStockList = (bkId) => axios.get(`/watchlist/bk/stock/list/${bkId}`)

export const addStock = (data) => axios.post('/watchlist/bk/stock/add', data)

export const updateStock = (data) => axios.post('/watchlist/bk/stock/update', data)

export const deleteStock = (id) => axios.post(`/watchlist/bk/stock/delete/${id}`)

export const getMoneyFlow = (bkId) => axios.get(`/watchlist/bk/stock/moneyFlow/${bkId}`)
