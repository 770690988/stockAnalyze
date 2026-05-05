package com.biubiu.stock.stockanalyze.service;

public interface RootService {

    String getBKInfo() throws InterruptedException;
    String getStockInfo() throws InterruptedException;
    void freshStockData() throws InterruptedException;
    void freshStockMoneyFlowDataAll() throws InterruptedException;
    void freshStockPriceDataAll() throws InterruptedException;
    void getWxBkAnalyzeMessage();
    void getWxAnalyzeMessage();
    void getWxSelectedAnalyzeMessage();
    void changeSelectedStock();
    String getCurrentTradeTime();
    void saveDatabaseSql();
}
