package com.erudev.contentcenter.sentineltest;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义 sentinel 错误页返回信息. 例如流控、簇点链路等
 *
 * @author pengfei.zhao
 * @date 2020/11/7 23:52
 */
@Component
public class MyUrlBlockHandler implements UrlBlockHandler {
    @Override
    public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws IOException {
        ErrorMsg msg = null;
        if (ex instanceof FlowException) {
            msg = ErrorMsg.builder()
                    .status(100)
                    .msg("限流了")
                    .build();
        } else if (ex instanceof DegradeException) {
            msg = ErrorMsg.builder()
                    .status(101)
                    .msg("降级了")
                    .build();
        } else if (ex instanceof ParamFlowException) {
            msg = ErrorMsg.builder()
                    .status(102)
                    .msg("热点参数限流")
                    .build();
        } else if (ex instanceof SystemBlockException) {
            msg = ErrorMsg.builder()
                    .status(103)
                    .msg("系统规则（负载/...不满足要求）")
                    .build();
        } else if (ex instanceof AuthorityException) {
            msg = ErrorMsg.builder()
                    .status(104)
                    .msg("授权规则不通过")
                    .build();
        }
        response.setStatus(500);
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.setContentType("application/json;charset=utf-8");
        request.setCharacterEncoding("utf-8");
        // spring mvc自带的json操作工具 jackson
        new ObjectMapper().writeValue(response.getWriter(), msg);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class ErrorMsg{
    private Integer status;
    private String msg;
}
