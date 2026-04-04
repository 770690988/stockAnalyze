package com.biubiu.stock.stockanalyze.service;

import com.biubiu.stock.stockanalyze.utils.WxPostUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Author biubiu
 * @Description WxNotify Service
 * @Date 2026/4/4 13:02
 */
@Service
@Slf4j
public class WxNotifyService {
    private final WxPostUtils wxPostUtils = new WxPostUtils();

    public void sendInformation(String title, String message) {
        wxPostUtils.postMessage(title, message);
    }
}
