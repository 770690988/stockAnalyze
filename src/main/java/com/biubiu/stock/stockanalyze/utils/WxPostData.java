package com.biubiu.stock.stockanalyze.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WxPostData {
    private String appToken = "AT_ePujzxMRXkPmvJSUxHCpobMwsKjCmw3Q";
    private String content;
    private String summary;
    private Integer contentType = 1;
    private List<Integer> topicIds = getOrigintopicIds();
    private List<String> uids = getOriginUids();
    private String url = "https://wxpusher.zjiecode.com";
    private Boolean verifyPay = false;

    private List<Integer> getOrigintopicIds() {
        List<Integer> topicIds = new ArrayList<>();
        topicIds.add(49460);
        return topicIds;
    }

    private List<String> getOriginUids() {
        List<String> uids = new ArrayList<>();
        uids.add("UID_5p6of9SQt3znNxMdqCIh6qxeLoTx");
        return uids;
    }

    public WxPostData(String summary, String content) {
        this.content = content;
        this.summary = summary;
    }
}
