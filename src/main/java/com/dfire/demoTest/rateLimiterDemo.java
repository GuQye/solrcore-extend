package com.dfire.demoTest;

import com.google.common.util.concurrent.RateLimiter;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by . on 2018/3/28.
 */
public class rateLimiterDemo {
    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");

        RateLimiter rateLimiter = RateLimiter.create(0.5);

        while(true) {

            rateLimiter.acquire();

            System.out.println(simpleDateFormat.format(new Date()));

        }
    }
}
