package com.console.board.service;

import com.console.board.dtos.ReportDto;
import com.console.board.dtos.SuspendedUserDto;
import com.console.board.mapper.ReportMapper;
import com.console.board.mapper.SuspendedUserMapper;
import com.console.board.status.StatusStatus;

import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportMapper reportMapper;
    private final SuspendedUserMapper suspendedUserMapper;

    @Transactional
    public void saveReport(ReportDto reportDto) {
        reportMapper.insertReport(reportDto);
    }
    
    public List<ReportDto> getReports(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<ReportDto> reports = reportMapper.findReportsWithPostIds(pageSize, offset);

        return reports;   
    }

    public int getTotalReportCount() {
        return reportMapper.countReports();
    }
    
    @Transactional
    public void resolveReports(List<ReportDto> updateRequests) {
        for (ReportDto request : updateRequests) {
            // Report 테이블에 신고 상태와 정지 일수 업데이트
            reportMapper.updateReportStatusAndSuspendedDays(request.getReportId(), StatusStatus.RESOLVED.name(), request.getSuspendedDays());

            // 정지 정보가 필요하면 SuspendedUser에 추가
            if (request.getSuspendedDays() > 0) {
                String targetType = request.getTargetType().toString();
                int userId = reportMapper.findReportedUserId(targetType, request.getTargetId());

                SuspendedUserDto suspendedUser = new SuspendedUserDto();
                suspendedUser.setUserId(userId);
                suspendedUser.setReason(request.getReason());
                suspendedUser.setStartDate(Timestamp.valueOf(LocalDateTime.now()));
                suspendedUser.setEndDate(Timestamp.valueOf(LocalDateTime.now().plusDays(request.getSuspendedDays())));
                suspendedUserMapper.insertSuspendedUser(suspendedUser);
            }
        }
    }

}
