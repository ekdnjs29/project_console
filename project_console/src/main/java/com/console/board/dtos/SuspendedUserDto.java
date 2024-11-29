package com.console.board.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("SuspendedUserDto")
public class SuspendedUserDto {
    private int suspendedUserId;
    private int userId;
    private String reason;
    private Timestamp startDate;
    private Timestamp endDate;

}

