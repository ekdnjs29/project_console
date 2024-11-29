package com.console.board.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import com.console.board.status.StatusStatus;
import com.console.board.status.TargetTypeStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("ReportDto")
public class ReportDto {
    private int reportId;
    private int targetId;
    private int reporterId;
    private TargetTypeStatus targetType; // POST or COMMENT
    private String reason;
    private StatusStatus status; // PENDING or RESOLVED
    private Integer postId; // 추가된 필드
    private int suspendedDays; // 정지 일수 필드 추가
}
