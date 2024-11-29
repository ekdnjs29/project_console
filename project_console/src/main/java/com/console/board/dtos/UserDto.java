package com.console.board.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

import com.console.board.status.RoleStatus;

@Data // @Data: getter, setter, toString, equals, hashCode 자동 생성
@NoArgsConstructor // @NoArgsConstructor: 기본 생성자 생성
@AllArgsConstructor // @AllArgsConstructor: 모든 필드를 매개변수로 받는 생성자 생성
@Alias("UserDto")
public class UserDto {
    private int userId;
    private String username;
    private String nickname;
    private String email;
    private String password;
    private RoleStatus role;
    private Integer suspended; // suspended 필드 추가

}
