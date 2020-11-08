package com.erudev.contentcenter.controller;

import com.erudev.contentcenter.domain.dto.content.ShareAuditDTO;
import com.erudev.contentcenter.domain.entity.content.Share;
import com.erudev.contentcenter.service.content.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author pengfei.zhao
 * @date 2020/11/8 9:48
 */
@RestController
@RequestMapping("/admin/shares")
public class ShareAdminController {

    @Autowired
    private ShareService shareService;

    @PutMapping("/audit/{id}")
    public Share auditById(@PathVariable Integer id, @RequestBody ShareAuditDTO auditDTO) {
        // TODO 鉴权 登录状态
        return shareService.auditById(id, auditDTO);
    }
}
