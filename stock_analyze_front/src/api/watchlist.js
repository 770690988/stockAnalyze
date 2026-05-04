import request from '../utils/request'

// ==================== 板块类型接口 ====================

export const getBkTypeList = () => request.get('/watchlist/bk/type/list')

export const addBkType = (data) => request.post('/watchlist/bk/type/add', data)

export const updateBkType = (data) => request.post('/watchlist/bk/type/update', data)

export const deleteBkType = (id) => request.post(`/watchlist/bk/type/delete/${id}`)

// ==================== 板块接口 ====================

export const getBkList = () => request.get('/watchlist/bk/list')

export const addBk = (data) => request.post('/watchlist/bk/add', data)

export const updateBk = (data) => request.post('/watchlist/bk/update', data)

export const deleteBk = (id) => request.post(`/watchlist/bk/delete/${id}`)

export const getMoneyFlowHistory = (bkId, periodDay) =>
    request.post('/watchlist/bk/stock/period', { id: bkId, periodDay })

// ==================== 股票接口 ====================

export const getStockList = (bkId) => request.get(`/watchlist/bk/stock/list/${bkId}`)

export const addStock = (data) => request.post('/watchlist/bk/stock/add', data)

export const addBatchStock = (data) => request.post('/watchlist/bk/stock/addBatch', data)

export const updateStock = (data) => request.post('/watchlist/bk/stock/update', data)

export const deleteStock = (id) => request.post(`/watchlist/bk/stock/delete/${id}`)

export const deleteBatchStock = (ids) => request.post('/watchlist/bk/stock/deleteBatch', ids)

export const getMoneyFlow = (bkId) => request.get(`/watchlist/bk/stock/moneyFlow/${bkId}`)