package com.biubiu.stock.stockanalyze.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.biubiu.stock.stockanalyze.utils.NumChangeUtis;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("stock_money_flow")
public class StockMoneyFlow {

    @TableId(type = IdType.AUTO)
    private Integer id;

    //股票代码
    @TableField("stock_code")
    private String stockCode;

    //股票名称
    @TableField("stock_name")
    private String stockName;

    //股票价格
    @TableField("stock_price")
    private BigDecimal stockPrice;

    //股票最高价格
    @TableField("stock_price_max")
    private BigDecimal stockPriceMax;

    //股票最低价格
    @TableField("stock_price_min")
    private BigDecimal stockPriceMin;

    //换手率
    @TableField("turnover_rate")
    private BigDecimal turnoverRate;

    //量比
    @TableField("volume_ratio")
    private BigDecimal volumeRatio;

    //市盈率
    @TableField("per_roll")
    private BigDecimal perRoll;

    // 成交量
    @TableField("volume")
    private BigDecimal volume;

    // 成交额
    @TableField("amount")
    private BigDecimal amount;

    //股票涨跌幅
    @TableField("stock_price_rate")
    private BigDecimal stockPriceRate;

    //股票主力流入
    @TableField("main_net")
    private BigDecimal mainNet;

    //股票净小单流入
    @TableField("small_net")
    private BigDecimal smallNet;

    //股票净中单流入
    @TableField("middle_net")
    private BigDecimal middleNet;

    //股票净大单流入
    @TableField("large_net")
    private BigDecimal largeNet;

    //股票净超大单流入
    @TableField("super_net")
    private BigDecimal superNet;

    //统计时间
    @TableField("trade_date")
    private LocalDateTime tradeDate;

    //创建时间
    @TableField("create_time")
    private LocalDateTime createTime;

    public static String getWxDataInfo(StockMoneyFlow data) {
        String out = "";
        out += "股票代码:" + data.getStockCode() + " \n";
        out += "股票名称:" + data.getStockName() + " \n";
        out += "股票价格:" + data.getStockPrice() + " \n";
        out += "股票涨幅:" + data.getStockPriceRate() + " \n";
        out += "股票主力流入〇〇:" + NumChangeUtis.getNumChangeStr(data.getMainNet()) + " \n";
        out += "股票净超大单流入:" + NumChangeUtis.getNumChangeStr(data.getSuperNet()) + " \n";
        out += "股票大单流入〇〇:" + NumChangeUtis.getNumChangeStr(data.getLargeNet()) + " \n";
        out += "股票中单流入〇〇:" + NumChangeUtis.getNumChangeStr(data.getMiddleNet()) + " \n";
        out += "股票净小单流入〇:" + NumChangeUtis.getNumChangeStr(data.getSmallNet()) + " \n";

        out = out + "\n";
        return out;
    }
}
