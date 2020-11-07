package com.erudev.contentcenter.service.content;

import com.erudev.contentcenter.dao.content.ShareMapper;
import com.erudev.contentcenter.domain.dto.content.ShareDTO;
import com.erudev.contentcenter.domain.dto.user.UserDTO;
import com.erudev.contentcenter.domain.entity.content.Share;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author pengfei.zhao
 * @date 2020/11/7 13:06
 */
@Slf4j
@Service
public class ShareService {
    @Resource
    private ShareMapper shareMapper;
    @Resource
    private DiscoveryClient discoveryClient;
    @Resource
    private RestTemplate restTemplate;

    public ShareDTO findById(Integer id) {
        Share share = shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();
        List<ServiceInstance> userInstances = discoveryClient.getInstances("user-center");
        String userInstanceUrl = userInstances.stream()
                .map(instance -> instance.getUri().toString() + "/users/{id}")
                .findFirst()
                .orElseThrow(() -> new RuntimeException("用户服务没有可以的实例"));

        log.info("请求的目标地址: {}", userInstanceUrl);

        UserDTO userDTO = restTemplate.getForObject(userInstanceUrl, UserDTO.class, userId);

        final ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setUserId(userDTO.getId());

        return shareDTO;
    }
}
