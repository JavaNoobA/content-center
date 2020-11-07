package com.erudev.contentcenter.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 *  feign 配置类
 * 不需要加 @configuration, 否则必须挪到@ComponentScan能扫描的包以外
 *
 * @author pengfei.zhao
 * @date 2020/11/7 16:22
 */
public class GlobalFeignConfiguration {
    @Bean
    public Logger.Level level() {
        return Logger.Level.FULL;
    }
}
