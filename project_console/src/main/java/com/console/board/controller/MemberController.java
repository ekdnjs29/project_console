package com.console.board.controller;

import com.console.board.dtos.UserDto;
import com.console.board.service.MemberService;
   
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/list")
    public String getMemberList(Model model,
		    		@RequestParam(value = "page", required = false) Integer page,
		            @RequestParam(value = "pageSize", required = false) Integer pageSize) {
		int currentPage = (page != null) ? page : 1;         // 기본값 1
		int size = (pageSize != null) ? pageSize : 10;       // 기본 페이지 크기 10
       
        List<UserDto> members = memberService.getMembers(currentPage, size);
        int totalUsers = memberService.getTotalUsersCount();
        int totalPages = (int) Math.ceil((double) totalUsers / size);

		model.addAttribute("members", members); // 현재 페이지의 회원 목록
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);

        return "admin/memberList";
    }
    
    @PostMapping("/update")
    public String updateMembers(@RequestBody List<UserDto> users) {
        for (UserDto userDto : users) {
            memberService.updateMemberRoleAndSuspended(userDto);
        }
        return "redirect:/members/list";
    }
}
