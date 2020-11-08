package com.erudev.contentcenter.service.content;

import com.alibaba.fastjson.JSON;
import com.erudev.contentcenter.dao.content.ShareMapper;
import com.erudev.contentcenter.dao.message.RocketmqTransactionLogMapper;
import com.erudev.contentcenter.domain.dto.content.ShareAuditDTO;
import com.erudev.contentcenter.domain.dto.content.ShareDTO;
import com.erudev.contentcenter.domain.dto.message.UserAddBonusMsgDTO;
import com.erudev.contentcenter.domain.dto.user.UserDTO;
import com.erudev.contentcenter.domain.entity.content.Share;
import com.erudev.contentcenter.domain.entity.message.RocketmqTransactionLog;
import com.erudev.contentcenter.domain.enums.AuditStatusEnum;
import com.erudev.contentcenter.feignclient.UserCenterFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

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
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired(required = false)
    private RocketmqTransactionLogMapper rocketmqTransactionLogMapper;
    @Autowired
    private Source source;

    public ShareDTO findById(Integer id) {
        Share share = shareMapper.selectByPrimaryKey(id);
        Integer userId = share.getUserId();

        UserDTO userDTO = userCenterFeignClient.findById(userId);

        final ShareDTO shareDTO = new ShareDTO();
        BeanUtils.copyProperties(share, shareDTO);
        shareDTO.setUserId(userDTO.getId());

        return shareDTO;
    }

    public Share auditById(Integer id, ShareAuditDTO auditDTO) {
        // 1. 查询 share 是否存在，传入的share 状态是否为 NOT_YET
        Share share = shareMapper.selectByPrimaryKey(id);
        if (share == null) {
            throw new RuntimeException("参数非法！该分享不存在！");
        }
        if (!Objects.equals("NOT_YET", share.getAuditStatus())) {
            throw new RuntimeException("参数非法！该分享已审核通过或审核不通过！");
        }
        // 2. 审核资源，将状态设为PASS/REJECT
        share.setAuditStatus(auditDTO.getAuditStatusEnum().toString());
        // 3. 如果为PASS,为发布人加积分
        if (AuditStatusEnum.PASS.equals(auditDTO.getAuditStatusEnum())) {
            // 发送半消息
            String transactionId = UUID.randomUUID().toString();

            source.output().send(
                    MessageBuilder
                            .withPayload(
                                    UserAddBonusMsgDTO.builder()
                                            .userId(share.getUserId())
                                            .bonus(50)
                                            .build()
                            )
                            // header也有妙用...
                            .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                            .setHeader("share_id", id)
                            .setHeader("dto", JSON.toJSONString(auditDTO))
                            .build()
            );
        } else {
            auditByIdInDB(id, auditDTO);
        }
        rocketMQTemplate.convertAndSend("add-bonus",
                UserAddBonusMsgDTO.builder().userId(share.getUserId()).bonus(50).build()
        );
        return share;
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdInDB(Integer id, ShareAuditDTO auditDTO) {
        Share share = Share.builder()
                .id(id)
                .auditStatus(auditDTO.getAuditStatusEnum().toString())
                .reason(auditDTO.getReason())
                .build();

        shareMapper.updateByPrimaryKeySelective(share);
        // TODO 把share写到缓存
    }

    @Transactional(rollbackFor = Exception.class)
    public void auditByIdWithRocketMqLog(Integer id, ShareAuditDTO auditDTO, String transactionId) {
        this.auditById(id, auditDTO);

        rocketmqTransactionLogMapper.insertSelective(
                RocketmqTransactionLog.builder()
                        .id(id)
                        .transactionId(transactionId)
                        .build());
    }
}
