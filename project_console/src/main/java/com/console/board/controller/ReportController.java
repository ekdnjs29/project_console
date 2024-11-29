package com.console.board.controller;

import com.console.board.dtos.ReportDto;
import com.console.board.dtos.UserDto;
import com.console.board.service.ReportService;
import com.console.board.status.StatusStatus;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    
    @PostMapping
    public ResponseEntity<?> reportItem(@RequestBody ReportDto reportDto, HttpSession session) {
        UserDto loggedInUser = (UserDto) session.getAttribute("userDto");
        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        reportDto.setReporterId(loggedInUser.getUserId());
        reportDto.setStatus(StatusStatus.PENDING); // 기본 상태는 'PENDING'으로 설정
        reportService.saveReport(reportDto);

        return ResponseEntity.ok("신고가 접수되었습니다.");
    }
    
    @GetMapping("/list")
    public String showReportList(Model model,
                                 @RequestParam(value = "page", required = false) Integer page,
                                 @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        int currentPage = (page != null) ? page : 1;         // 기본값 1
        int size = (pageSize != null) ? pageSize : 10;       // 기본 페이지 크기 10

        List<ReportDto> reports = reportService.getReports(currentPage, size);
        int totalReports = reportService.getTotalReportCount();
        int totalPages = (int) Math.ceil((double) totalReports / size);

        model.addAttribute("reports", reports);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        return "admin/report";
    }

    @PostMapping("/resolve")
    public ResponseEntity<?> resolveReport(@RequestBody List<ReportDto> updateRequests) {
        try {
            reportService.resolveReports(updateRequests);
            return ResponseEntity.ok("신고가 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("처리 중 오류가 발생했습니다.");
        }
    }
}
