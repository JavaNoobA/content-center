package com.erudev.contentcenter.domain.dto.content;

import com.erudev.contentcenter.domain.enums.AuditStatusEnum;
import lombok.Data;

/**
 * @author pengfei.zhao
 * @date 2020/11/8 10:02
 */
@Data
public class ShareAuditDTO {
    private AuditStatusEnum auditStatusEnum;
    private String reason;
}
