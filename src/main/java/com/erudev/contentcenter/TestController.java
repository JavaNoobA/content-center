package com.erudev.contentcenter;

import com.erudev.contentcenter.dao.content.MidUserShareMapper;
import com.erudev.contentcenter.domain.entity.content.MidUserShare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author pengfei.zhao
 * @date 2020/11/7 9:44
 */
@RestController
@RequestMapping("/content")
public class TestController {

    @Autowired
    private MidUserShareMapper midUserShareMapper;
    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping
    public MidUserShare test() {
        final MidUserShare share = MidUserShare.builder().shareId(1).userId(1).build();
        midUserShareMapper.insertSelective(share);
        return share;
    }

    @GetMapping("/list")
    public List<ServiceInstance> list() {
        // 查询指定服务名所有的实例信息
        return discoveryClient.getInstances("user-center");
    }
}
