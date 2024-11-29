package com.console.board.mapper;

import com.console.board.dtos.UserDto;
import org.apache.ibatis.annotations.Param;

import java.awt.print.Pageable;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    // 회원가입 
    int registerUser(UserDto userDto);  // 성공 시 영향을 받은 행의 수 반환 (1이면 성공)

    // 아이디 중복 확인
    int countByUsername(String username);

    // 이메일 중복 확인 
    int countByEmail(String email);

    // 사용자 조회
    UserDto findByUsername(String username);
    
    // id-> 닉네임
    UserDto getUserById(int userId);
    
 // UserMapper 인터페이스
    int countUsers();

    // 모든 사용자 조회
    List<UserDto> findAll(@Param("limit") int limit, @Param("offset") int offset);

 // Role 업데이트
    int updateUserRole(UserDto userDto);
}
