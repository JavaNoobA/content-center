package com.erudev.contentcenter.service.content;

import com.erudev.contentcenter.dao.content.ShareMapper;
import com.erudev.contentcenter.domain.dto.content.ShareDTO;
import com.erudev.contentcenter.domain.dto.user.UserDTO;
import com.erudev.contentcenter.domain.entity.content.Share;
import com.erudev.contentcenter.feignclient.UserCenterFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;


/**
 * @author pengfei.zhao
 * @date 2020/11/7 13:06
 */
@Slf4j
@Service
public class ShareService {
    @Autowired(required = false)
    private ShareMapper shareMapper;
    @Autowired
    private UserCenterFeignClient userCenterFeignClient;

    @Resource
    private RestTemplate restTemplate;

    public ShareDTO findById(Integer id) {
        Share share = shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();

        UserDTO userDTO = userCenterFeignClient.findById(userId);

        final ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setUserId(userDTO.getId());

        return shareDTO;
    }
}
