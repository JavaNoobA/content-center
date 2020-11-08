package com.erudev.contentcenter.controller;

import com.erudev.contentcenter.auth.CheckLogin;
import com.erudev.contentcenter.domain.dto.content.ShareDTO;
import com.erudev.contentcenter.service.content.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pengfei.zhao
 * @date 2020/11/7 13:16
 */
@RestController
@RequestMapping("/shares")
public class ShareController {
    @Autowired
    private ShareService shareService;

    @GetMapping("/{id}")
    @CheckLogin
    public ShareDTO findById(@PathVariable Integer id) {
        return shareService.findById(id);
    }
}
