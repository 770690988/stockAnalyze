package com.biubiu.stock.stockanalyze.model.message;

import com.biubiu.stock.stockanalyze.model.StockMoneyFlow;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BkAnalyzeResult {
    String bkName;
    Double totalMainNet;
    Double totalSuperNet;
    Double totalLargeNet;
    Double totalMiddleNet;
    Double totalSmallNet;
    Double avgPriceRate;
    List<StockMoneyFlow> flowList;
}
