package com.console.board.mapper;

import com.console.board.dtos.SuspendedUserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SuspendedUserMapper {

    // SuspendedUser 테이블에 새로운 정지 사용자 추가
    int insertSuspendedUser(SuspendedUserDto suspendedUserDto);

    // 특정 userId에 대한 endDate 업데이트 
    int updateSuspendedUserEndDate(@Param("userId") int userId, @Param("endDate") java.sql.Timestamp endDate);

    // 특정 userId로 정지된 사용자 조회 (정지 여부 확인용)
    SuspendedUserDto findSuspendedUserByUserId(@Param("userId") int userId);
    
 // 특정 사용자 SuspendedUser 레코드 삭제
    int deleteSuspendedUserByUserId(@Param("userId") int userId);
}
