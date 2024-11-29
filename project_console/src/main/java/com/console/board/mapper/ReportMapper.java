package com.console.board.mapper;

import com.console.board.dtos.ReportDto;
import org.apache.ibatis.annotations.Param;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportMapper {

    // 신고 정보 삽입
    int insertReport(ReportDto reportDto);

    // 특정 유저가 특정 대상에 대해 신고했는지 확인 (중복 신고 방지 용도)
    int countByReporterAndTarget(@Param("reporterId") int reporterId, @Param("targetId") int targetId, @Param("targetType") String targetType);

    // 특정 상태의 모든 신고 조회 (예: PENDING 상태만 조회)
    List<ReportDto> getReportsByStatus(String status);

 // 신고 상태와 정지 일수 업데이트 (예: PENDING -> RESOLVED, 정지 일수 업데이트)
    int updateReportStatusAndSuspendedDays(@Param("reportId") int reportId, @Param("status") String status, @Param("suspendedDays") int suspendedDays);

    // 페이징 처리된 리포트 리스트를 가져오기
    List<ReportDto> findReportsWithPostIds(@Param("limit") int limit, @Param("offset") int offset);

    // 전체 리포트 수를 가져오기
    int countReports();

 // 신고된 유저의 ID를 찾기 위한 메서드
    int findReportedUserId(@Param("targetType") String targetType, @Param("targetId") int targetId);

}
