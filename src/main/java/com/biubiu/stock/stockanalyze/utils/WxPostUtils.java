package com.biubiu.stock.stockanalyze.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * WxPusher是免费的推送服务，为了能更好的服务大家，这里说明一下系统相关数据限制
 *
 * 消息发送，必须合法合规，发送违规违法欺诈等等非正常消息，可能被封号；
 * WxPusher推送的是实时消息，时效性比较强，过期以后消息也就没有价值了，目前WxPusher会为你保留7天的数据 ，7天以后不再提供可靠性保证，会不定时清理历史消息；
 * 单条消息的数据长度(字符数)限制是：content<40000;summary<20(微信的限制，大于20显示不完);url<400，;
 * 单条消息最大发送UID的数量<2000，单条消息最大发送topicIds的数量<5;
 * 单个微信用户，也就是单个UID，每天最多接收2000条消息，请合理安排发送频率；
 * 发送消息，最大QPS不能超过2，比如最多连续10秒调用20次发送接口，超过这个限制会被系统拦截。如果你需要大量发送消息，推荐使用主题，或者一次发送的时候，附带多个uid。
 */

@Slf4j
public class WxPostUtils {

    // ✅ 静态单例，整个应用只创建一次
    private static final OkHttpClient CLIENT = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .build();

    public Message postMessage(String summary, String content) {
        JSONObject postData = (JSONObject) JSONObject.toJSON(new WxPostData(summary, content));

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, postData.toJSONString());
        Request request = new Request.Builder()
                .url("https://wxpusher.zjiecode.com/api/send/message")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        try (Response response = CLIENT.newCall(request).execute()) {  // ✅ try-with-resources 自动关闭
            // 可以处理响应
        } catch (IOException e) {
            log.error("postMessage newCall failed");
            throw new RuntimeException(e);
        }
        return new Message(100, "OK");
    }
}
