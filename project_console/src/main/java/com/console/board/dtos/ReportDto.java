package com.console.board.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("ReportDto")
public class ReportDto {
    private int reportId;
    private int targetId;
    private int reporterId;
    private String targetType; // POST or COMMENT
    private String reason;
    private String status; // PENDING or RESOLVED
}
