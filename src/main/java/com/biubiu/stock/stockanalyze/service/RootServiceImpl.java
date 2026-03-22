package com.biubiu.stock.stockanalyze.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.biubiu.stock.stockanalyze.enums.UnitEnum;
import com.biubiu.stock.stockanalyze.mapper.SelectedStockMapper;
import com.biubiu.stock.stockanalyze.mapper.StockBkMapper;
import com.biubiu.stock.stockanalyze.mapper.StockMoneyFlowMapper;
import com.biubiu.stock.stockanalyze.model.SelectedStock;
import com.biubiu.stock.stockanalyze.model.StockBk;
import com.biubiu.stock.stockanalyze.model.StockMoneyFlow;
import com.biubiu.stock.stockanalyze.utils.WxPostUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RootServiceImpl implements RootService {
    private final StockBkMapper stockBkMapper;

    private final SelectedStockMapper selectedStockMapper;

    private final StockMoneyFlowMapper stockMoneyFlowMapper;

    List<String> errorStockNo = new ArrayList<>();

    String currentStockNo = null;


    public String getBKInfo() throws InterruptedException {
        log.info("testInfo called");
        // 业务逻辑写这里
        List<StockBk> list = new ArrayList<>();
        for (int num = 1; num <= 5; num++) {
            Thread.sleep(10000);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://push2delay.eastmoney.com/api/qt/clist/get?cb=biubiu&pn=" + num + "&pz=100&po=1&np=1&fields=f12,f13,f14,f62&fid=f62&fs=m:90+t:2&ut=b2884a393a59ad64002292a3e90d46a5&_=")
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();

                // 去掉 JSONP 外层包装 jinhaiyuejiang(...)
                String json = result.substring(result.indexOf("(") + 1, result.lastIndexOf(")"));

                // 解析 JSON
                JSONObject jsonObject = JSONObject.parseObject(json);

                // 取出 data 里的 diff 列表
                JSONArray diffList = jsonObject.getJSONObject("data").getJSONArray("diff");

                // 遍历转成对象
                for (int i = 0; i < diffList.size(); i++) {
                    JSONObject item = diffList.getJSONObject(i);
                    StockBk stockBk = new StockBk();
                    stockBk.setCode(item.getString("f12"));
                    stockBk.setName(item.getString("f14"));
                    stockBk.setHotNum(new BigDecimal(item.getInteger("f62")));
                    stockBk.setType(1);  // 固定值，板块类型
                    list.add(stockBk);
                }

                System.out.println(list);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        list.forEach(stockBkMapper::insert);

//        stockBkMapper.selectList(null);
        return "hello";
    }

    public String getStockInfo() throws InterruptedException {
        log.info("testInfo called");
        // 业务逻辑写这里
        List<StockBk> list = new ArrayList<>();
        for (int num = 1; num <= 55; num++) {
            Thread.sleep(5000);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://push2.eastmoney.com/api/qt/clist/get?np=1&fltt=1&invt=2&cb=jQuery37108399451080689895_1773581770814&fs=m%3A0%2Bt%3A6%2Bf%3A!2%2Cm%3A0%2Bt%3A80%2Bf%3A!2%2Cm%3A1%2Bt%3A2%2Bf%3A!2%2Cm%3A1%2Bt%3A23%2Bf%3A!2%2Cm%3A0%2Bt%3A81%2Bs%3A262144%2Bf%3A!2&fields=f12%2Cf13%2Cf14%2Cf1%2Cf2%2Cf4%2Cf3%2Cf152%2Cf5%2Cf6%2Cf7%2Cf15%2Cf18%2Cf16%2Cf17%2Cf10%2Cf8%2Cf9%2Cf23&fid=f3&pn="+ num +"&pz=100&po=1&dect=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=%7C0%7C0%7C0%7Cweb&_=1773581770820")
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();

                // 去掉 JSONP 外层包装 jinhaiyuejiang(...)
                String json = result.substring(result.indexOf("(") + 1, result.lastIndexOf(")"));

                // 解析 JSON
                JSONObject jsonObject = JSONObject.parseObject(json);

                // 取出 data 里的 diff 列表
                JSONArray diffList = jsonObject.getJSONObject("data").getJSONArray("diff");

                // 遍历转成对象
                for (int i = 0; i < diffList.size(); i++) {
                    JSONObject item = diffList.getJSONObject(i);
                    StockBk stockBk = new StockBk();
                    stockBk.setCode(item.getString("f12"));
                    stockBk.setName(item.getString("f14"));
                    stockBk.setHotNum(item.getBigDecimal("f62"));
                    stockBk.setType(4);  // 固定值，板块类型
                    list.add(stockBk);
                }

                System.out.println(list);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        list.forEach(stockBkMapper::insert);

//        stockBkMapper.selectList(null);
        return "hello";
    }

    @Override
    public void freshStockData() throws InterruptedException {
        List<StockBk> stockBkList = stockBkMapper.selectList(new QueryWrapper<StockBk>().eq("type", 4));
        stockBkList.forEach(stockBk -> {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("\n" +
                            "\n" +
                            "https://push2.eastmoney.com/api/qt/stock/get?invt=2&fltt=1&cb=jQuery351012612748479588431_1773587925063&fields=f135%2Cf136%2Cf137%2Cf138%2Cf139%2Cf140%2Cf141%2Cf142%2Cf143%2Cf144%2Cf145%2Cf146%2Cf147%2Cf148%2Cf149&secid=0." + stockBk.getCode() + "&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=%7C0%7C0%7C0%7Cweb&dect=1&_=1773587925068")
                    .get()
                    .build();
            try {
                Thread.sleep(1000);
                Response response = client.newCall(request).execute();
                String result = response.body().string();
                System.out.println(result);

                // 去掉 JSONP 外层包装 jinhaiyuejiang(...)
                String json = result.substring(result.indexOf("(") + 1, result.lastIndexOf(")"));

                // 解析 JSON
                JSONObject jsonObject = JSONObject.parseObject(json);
                JSONObject item = jsonObject.getJSONObject("data");
                if (item == null || item.isEmpty()) {
                    errorStockNo.add(stockBk.getCode());
                    return;
                }

                JSONArray klinesArray = item.getJSONArray("klines");
                String timeString = "2026-03-15 15:30";
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime tradeDate = null;
                try {
                    tradeDate = LocalDateTime.parse(timeString, formatter);
                } catch (Exception e) {
                    errorStockNo.add(stockBk.getCode());
                    System.out.println("COMES TO LocalDateTime PARSE");
                    return;
                }
                StockMoneyFlow stockMoneyFlow = new StockMoneyFlow();
                stockMoneyFlow.setStockCode(stockBk.getCode());
                stockMoneyFlow.setStockName(stockBk.getName());
                stockMoneyFlow.setTradeDate(tradeDate);
                stockMoneyFlow.setCreateTime(LocalDateTime.now());
                stockMoneyFlow.setMainNet(item.getBigDecimal("f137"));
                stockMoneyFlow.setSmallNet(item.getBigDecimal("f149"));
                stockMoneyFlow.setMiddleNet(item.getBigDecimal("f146"));
                stockMoneyFlow.setLargeNet(item.getBigDecimal("f143"));
                stockMoneyFlow.setSuperNet(item.getBigDecimal("f140"));
                System.out.println("====================================================");
                System.out.println("stockMoneyFlow is " + stockMoneyFlow.toString());
                System.out.println("====================================================");
                try {
                    stockMoneyFlowMapper.insert(stockMoneyFlow);
                } catch (Exception e) {
                    log.error(e.toString());
                    System.out.println("==========数据存入异常==========");
                }
            } catch (IOException e) {
                errorStockNo.add(stockBk.getCode());
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(errorStockNo);

    }

    public void freshStockMoneyFlowDataAll() throws InterruptedException {
        List<StockMoneyFlow> list = new ArrayList<>();
        for (int num = 1; num <= 52; num++) {
            Thread.sleep(1000);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://push2delay.eastmoney.com/api/qt/clist/get?cb=jQuery1123009439079321877197_1773589707123&fid=f62&po=1&pz=100&pn="+ num +"&np=1&fltt=2&invt=2&ut=8dec03ba335b81bf4ebdf7b29ec27d15&fs=m%3A0%2Bt%3A6%2Bf%3A!2%2Cm%3A0%2Bt%3A13%2Bf%3A!2%2Cm%3A0%2Bt%3A80%2Bf%3A!2%2Cm%3A1%2Bt%3A2%2Bf%3A!2%2Cm%3A1%2Bt%3A23%2Bf%3A!2&fields=f12%2Cf14%2Cf2%2Cf3%2Cf62%2Cf184%2Cf66%2Cf69%2Cf72%2Cf75%2Cf78%2Cf81%2Cf84%2Cf87%2Cf204%2Cf205%2Cf124%2Cf1%2Cf13")
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();

                // 去掉 JSONP 外层包装 jinhaiyuejiang(...)
                String json = result.substring(result.indexOf("(") + 1, result.lastIndexOf(")"));

                // 解析 JSON
                JSONObject jsonObject = JSONObject.parseObject(json);

                // 取出 data 里的 diff 列表
                JSONArray diffList = jsonObject.getJSONObject("data").getJSONArray("diff");


                String timeString = getCurrentTradeTime();

                // 遍历转成对象
                for (int i = 0; i < diffList.size(); i++) {
                    JSONObject item = diffList.getJSONObject(i);
                    StockMoneyFlow stockMoneyFlow = new StockMoneyFlow();
                    stockMoneyFlow.setStockCode(item.getString("f12"));
                    stockMoneyFlow.setStockName(item.getString("f14"));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime tradeDate = LocalDateTime.parse(timeString, formatter);
                    stockMoneyFlow.setTradeDate(tradeDate);
                    stockMoneyFlow.setStockPrice(item.getBigDecimal("f2"));
                    stockMoneyFlow.setStockPriceRate(item.getBigDecimal("f3"));
                    stockMoneyFlow.setCreateTime(LocalDateTime.now());
                    stockMoneyFlow.setMainNet(item.getBigDecimal("f62"));
                    stockMoneyFlow.setSmallNet(item.getBigDecimal("f84"));
                    stockMoneyFlow.setMiddleNet(item.getBigDecimal("f78"));
                    stockMoneyFlow.setLargeNet(item.getBigDecimal("f72"));
                    stockMoneyFlow.setSuperNet(item.getBigDecimal("f66"));
                    list.add(stockMoneyFlow);
                }
                stockMoneyFlowMapper.insertOrUpdate(list);
                System.out.println(list);
                list = new ArrayList<>();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void freshStockPriceDataAll() throws InterruptedException {
        List<StockMoneyFlow> list = new ArrayList<>();
        for (int num = 1; num <= 55; num++) {
            Thread.sleep(1000);
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("https://push2.eastmoney.com/api/qt/clist/get?np=1&fltt=1&invt=2&cb=jQuery37105078362270830823_1773668416609&fs=m%3A0%2Bt%3A6%2Bf%3A!2%2Cm%3A0%2Bt%3A80%2Bf%3A!2%2Cm%3A1%2Bt%3A2%2Bf%3A!2%2Cm%3A1%2Bt%3A23%2Bf%3A!2%2Cm%3A0%2Bt%3A81%2Bs%3A262144%2Bf%3A!2&fields=f12%2Cf13%2Cf14%2Cf1%2Cf2%2Cf4%2Cf3%2Cf152%2Cf5%2Cf6%2Cf7%2Cf15%2Cf18%2Cf16%2Cf17%2Cf10%2Cf8%2Cf9%2Cf23&fid=f3&pn="+ num +"&pz=100&po=1&dect=1&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=%7C0%7C0%7C0%7Cweb&_=1773668416613")
                    .get()
                    .build();
            try {
                Response response = client.newCall(request).execute();
                String result = response.body().string();

                // 去掉 JSONP 外层包装 jinhaiyuejiang(...)
                String json = result.substring(result.indexOf("(") + 1, result.lastIndexOf(")"));

                // 解析 JSON
                JSONObject jsonObject = JSONObject.parseObject(json);

                // 取出 data 里的 diff 列表
                JSONArray diffList = jsonObject.getJSONObject("data").getJSONArray("diff");


                String timeString = getCurrentTradeTime();

                // 遍历转成对象
                for (int i = 0; i < diffList.size(); i++) {
                    JSONObject item = diffList.getJSONObject(i);
                    StockMoneyFlow stockMoneyFlow = new StockMoneyFlow();
                    stockMoneyFlow.setStockCode(item.getString("f12"));
                    stockMoneyFlow.setStockName(item.getString("f14"));
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime tradeDate = LocalDateTime.parse(timeString, formatter);
                    stockMoneyFlow.setTradeDate(tradeDate);
                    stockMoneyFlow.setCreateTime(LocalDateTime.now());
                    BigDecimal divisor = BigDecimal.TEN.pow(UnitEnum.HUNDRED.unitDigit);
                    BigDecimal stockPriceMax = item.getBigDecimal("f15").divide(divisor, 2, RoundingMode.HALF_UP);
                    BigDecimal stockPriceMin = item.getBigDecimal("f16").divide(divisor, 2, RoundingMode.HALF_UP);
                    BigDecimal turnoverRate = item.getBigDecimal("f8").divide(divisor, 2, RoundingMode.HALF_UP);
                    BigDecimal volumeRatio = item.getBigDecimal("f10").divide(divisor, 2, RoundingMode.HALF_UP);
                    BigDecimal perRoll = item.getBigDecimal("f9").divide(divisor, 2, RoundingMode.HALF_UP);
                    BigDecimal volume = item.getBigDecimal("f5");
                    BigDecimal amount = item.getBigDecimal("f6");
                    stockMoneyFlow.setStockPriceMax(stockPriceMax);
                    stockMoneyFlow.setStockPriceMin(stockPriceMin);
                    stockMoneyFlow.setTurnoverRate(turnoverRate);
                    stockMoneyFlow.setVolumeRatio(volumeRatio);
                    stockMoneyFlow.setPerRoll(perRoll);
                    stockMoneyFlow.setVolume(volume);
                    stockMoneyFlow.setAmount(amount);
                    list.add(stockMoneyFlow);
                }
                stockMoneyFlowMapper.insertOrUpdate(list);
                System.out.println(list);
                list = new ArrayList<>();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void getWxAnalyzeMessage() {
        log.info("current TradeTime is {}", getCurrentTradeTime());
        LocalDateTime currentTime = stockMoneyFlowMapper.getLatestTradeDate();
        LocalDateTime fifteenMinutesAgo = currentTime.minusMinutes(15);

        String summary = "";
        String content = "";

        List<StockMoneyFlow> stockMoneyFlowList = stockMoneyFlowMapper.getTradeTimeBetween(fifteenMinutesAgo, currentTime);

        // 按 mainNet 排序 取前10
        List<StockMoneyFlow> top10ByMainNet = stockMoneyFlowList.stream()
                .sorted(Comparator.comparingDouble(s -> -s.getMainNet().doubleValue()))
                .limit(10)
                .collect(Collectors.toList());
        log.info("===== 主力净流入 前10 =====");
        content = content + "===== 主力净流入 前10 =====\n";
        top10ByMainNet.forEach(s -> log.info("{}", StockMoneyFlow.getWxDataInfo(s)));
        for (int i = 0; i < top10ByMainNet.size(); i++) {
            content = content + StockMoneyFlow.getWxDataInfo(top10ByMainNet.get(i));
        }
        content = content + "\n\n\n";

        // 按 stockPriceRate 排序 取前10
        List<StockMoneyFlow> top10ByPriceRate = stockMoneyFlowList.stream()
                .sorted(Comparator.comparingDouble(s -> -s.getStockPriceRate().doubleValue()))
                .limit(10)
                .collect(Collectors.toList());
        log.info("===== 股票涨幅 前10 =====");
        content = content + "===== 股票涨幅 前10 =====\n";
        top10ByPriceRate.forEach(s -> log.info("{}", StockMoneyFlow.getWxDataInfo(s)));
        for (int i = 0; i < top10ByPriceRate.size(); i++) {
            content = content + StockMoneyFlow.getWxDataInfo(top10ByPriceRate.get(i));
        }
        content = content + "\n\n\n";

        // 按 superNet 排序 取前10
        List<StockMoneyFlow> top10BySuperNet = stockMoneyFlowList.stream()
                .sorted(Comparator.comparingDouble(s -> -s.getSuperNet().doubleValue()))
                .limit(10)
                .collect(Collectors.toList());
        log.info("===== 超大单净流入 前10 =====");
        content = content + "===== 超大单净流入 前10 =====\n";
        top10BySuperNet.forEach(s -> log.info("{}", StockMoneyFlow.getWxDataInfo(s)));
        for (int i = 0; i < top10BySuperNet.size(); i++) {
            content = content + StockMoneyFlow.getWxDataInfo(top10BySuperNet.get(i));
        }
        content = content + "\n\n\n";

        // 按 largeNet 排序 取前10
        List<StockMoneyFlow> top10ByLargeNet = stockMoneyFlowList.stream()
                .sorted(Comparator.comparingDouble(s -> -s.getLargeNet().doubleValue()))
                .limit(10)
                .collect(Collectors.toList());
        log.info("===== 大单净流入 前10 =====");
        content = content + "===== 大单净流入 前10 =====\n";
        top10ByLargeNet.forEach(s -> log.info("{}", StockMoneyFlow.getWxDataInfo(s)));
        for (int i = 0; i < top10ByLargeNet.size(); i++) {
            content = content + StockMoneyFlow.getWxDataInfo(top10ByLargeNet.get(i));
        }
        content = content + "\n\n\n";

        summary = currentTime + "盘中定时主力天量";
        log.info("summary is {}", summary);
        log.info("content is {}", content);
        WxPostUtils wxPostUtils = new WxPostUtils();
        wxPostUtils.postMessage(summary, content);
    }

    public void getWxSelectedAnalyzeMessage() {
        // 1. 查出所有自选股
        List<SelectedStock> selectedStockList = selectedStockMapper.selectList(null);
        if (selectedStockList.isEmpty()) {
            return;
        }
        // 2. 取出所有自选股的 code
        List<String> selectedCodeList = selectedStockList.stream()
                .map(SelectedStock::getCode)
                .collect(Collectors.toList());

        log.info("current TradeTime is {}", getCurrentTradeTime());
        LocalDateTime currentTime = stockMoneyFlowMapper.getLatestTradeDate();
        LocalDateTime fifteenMinutesAgo = currentTime.minusMinutes(15);

        String summary = "";
        String content = "";

        List<StockMoneyFlow> stockMoneyFlowList = stockMoneyFlowMapper.getTradeTimeBetweenAndCodeList(fifteenMinutesAgo, currentTime, selectedCodeList);

        // 按 mainNet 排序 取前10
        List<StockMoneyFlow> top10ByMainNet = stockMoneyFlowList.stream()
                .sorted(Comparator.comparingDouble(s -> -s.getMainNet().doubleValue()))
                .collect(Collectors.toList());
        log.info("===== 主力净流入=====");
        content = content + "===== 主力净流入=====\n";
        top10ByMainNet.forEach(s -> log.info("{}", StockMoneyFlow.getWxDataInfo(s)));
        for (int i = 0; i < top10ByMainNet.size(); i++) {
            content = content + StockMoneyFlow.getWxDataInfo(top10ByMainNet.get(i));
        }
        content = content + "\n\n\n";

        // 按 stockPriceRate 排序 取前10
        List<StockMoneyFlow> top10ByPriceRate = stockMoneyFlowList.stream()
                .sorted(Comparator.comparingDouble(s -> -s.getStockPriceRate().doubleValue()))
                .collect(Collectors.toList());
        log.info("===== 股票涨幅 =====");
        content = content + "===== 股票涨幅=====\n";
        top10ByPriceRate.forEach(s -> log.info("{}", StockMoneyFlow.getWxDataInfo(s)));
        for (int i = 0; i < top10ByPriceRate.size(); i++) {
            content = content + StockMoneyFlow.getWxDataInfo(top10ByPriceRate.get(i));
        }
        content = content + "\n\n\n";

        // 按 superNet 排序 取前10
        List<StockMoneyFlow> top10BySuperNet = stockMoneyFlowList.stream()
                .sorted(Comparator.comparingDouble(s -> -s.getSuperNet().doubleValue()))
                .collect(Collectors.toList());
        log.info("===== 超大单净流入 =====");
        content = content + "===== 超大单净流入=====\n";
        top10BySuperNet.forEach(s -> log.info("{}", StockMoneyFlow.getWxDataInfo(s)));
        for (int i = 0; i < top10BySuperNet.size(); i++) {
            content = content + StockMoneyFlow.getWxDataInfo(top10BySuperNet.get(i));
        }
        content = content + "\n\n\n";

        // 按 largeNet 排序 取前10
        List<StockMoneyFlow> top10ByLargeNet = stockMoneyFlowList.stream()
                .sorted(Comparator.comparingDouble(s -> -s.getLargeNet().doubleValue()))
                .collect(Collectors.toList());
        log.info("===== 大单净流入 =====");
        content = content + "===== 大单净流入 =====\n";
        top10ByLargeNet.forEach(s -> log.info("{}", StockMoneyFlow.getWxDataInfo(s)));
        for (int i = 0; i < top10ByLargeNet.size(); i++) {
            content = content + StockMoneyFlow.getWxDataInfo(top10ByLargeNet.get(i));
        }
        content = content + "\n\n\n";

        summary = currentTime + "盘中自选天量";
        log.info("summary is {}", summary);
        log.info("content is {}", content);
        WxPostUtils wxPostUtils = new WxPostUtils();
        wxPostUtils.postMessage(summary, content);
    }

    @Override
    public void changeSelectedStock() {
        // 1. 查出所有自选股
        List<SelectedStock> selectedStockList = selectedStockMapper.selectList(null);
        if (selectedStockList.isEmpty()) return;

        // 2. 获取最新 trade_date
        LocalDateTime latestTradeDate = stockMoneyFlowMapper.getLatestTradeDate();
        if (latestTradeDate == null) {
            log.warn("未找到最新交易时间");
            return;
        }
        log.info("最新交易时间: {}", latestTradeDate);

        // 3. 取出所有自选股的 code
        List<String> codes = selectedStockList.stream()
                .map(SelectedStock::getCode)
                .collect(Collectors.toList());

        // 4. 根据 code 和最新 trade_date 查询 StockMoneyFlow
        List<StockMoneyFlow> flowList = stockMoneyFlowMapper.selectList(
                new LambdaQueryWrapper<StockMoneyFlow>()
                        .in(StockMoneyFlow::getStockCode, codes)
                        .eq(StockMoneyFlow::getTradeDate, latestTradeDate)
        );

        // 5. 转成 Map 方便查找
        Map<String, StockMoneyFlow> flowMap = flowList.stream()
                .collect(Collectors.toMap(StockMoneyFlow::getStockCode, f -> f));

        // 6. 更新 SelectedStock
        for (SelectedStock stock : selectedStockList) {
            StockMoneyFlow flow = flowMap.get(stock.getCode());
            if (flow == null) {
                log.warn("未找到股票 {} 的资金流向数据", stock.getCode());
                continue;
            }
            stock.setName(flow.getStockName());
            stock.setCurrentPrice(flow.getStockPrice());
            stock.setUpgradeTime(latestTradeDate);
            selectedStockMapper.updateById(stock);
        }

        log.info("自选股价格更新完成，共更新 {} 条", flowMap.size());
    }


    public String getCurrentTradeTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        LocalDate currentDate = now.toLocalDate();

        LocalTime marketOpen  = LocalTime.of(9, 30);
        LocalTime marketClose = LocalTime.of(15, 0);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime result;

        if (currentTime.isBefore(marketOpen)) {
            // 早于9:30 取上一个工作日15:00
            LocalDate lastWorkDay = getLastWorkDay(currentDate);
            result = LocalDateTime.of(lastWorkDay, marketClose);
        } else if (currentTime.isAfter(marketClose)) {
            // 晚于15:00 取当天15:00
            result = LocalDateTime.of(currentDate, marketClose);
        } else {
            // 在交易时间内 取当前时间
            result = now;
        }

        return result.format(formatter);
    }

    // 获取上一个工作日（跳过周六周日）
    private LocalDate getLastWorkDay(LocalDate date) {
        LocalDate lastDay = date.minusDays(1);
        // 如果是周六往前推2天 如果是周日往前推1天
        while (lastDay.getDayOfWeek() == DayOfWeek.SATURDAY ||
                lastDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
            lastDay = lastDay.minusDays(1);
        }
        return lastDay;
    }


}


