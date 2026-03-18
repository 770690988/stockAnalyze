package com.biubiu.stock.stockanalyze.utils;

import lombok.Data;

@Data
public class Message {
    public Integer status;
    public String message;

    public Message(Integer status, String message) {
        this.status = status;
        this.message = message;
    }
}
