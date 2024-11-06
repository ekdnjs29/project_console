package com.console.board.controller;

import com.console.board.command.RegisterUserCommand;
import com.console.board.command.LoginCommand;
import com.console.board.service.UserService;
import com.console.board.dtos.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입 폼 이동
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerUserCommand", new RegisterUserCommand());
        return "user/registerForm";  // 회원가입 폼 뷰 페이지
    }

    // 회원가입 처리
    @PostMapping("/register")
    public String registerUser(@Validated RegisterUserCommand registerUserCommand,
                               BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "user/registerForm";  // 유효성 검사 실패 시 폼으로 이동
        }

        try {
            userService.registerUser(registerUserCommand);
            return "redirect:/user/login";  // 성공 시 홈으로 리다이렉트
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());  // 중복 오류 메시지 전달
            return "user/registerForm";
        }
    }

    // 아이디 중복 체크 (AJAX 요청)
    @ResponseBody
    @GetMapping("/idChk")
    public Map<String, String> idChk(String id) {
        boolean isDuplicate = userService.isUsernameDuplicate(id);  // 아이디 중복 확인
        Map<String, String> response = new HashMap<>();
        response.put("isDuplicate", String.valueOf(isDuplicate));  // JSON 응답
        return response;
    }

    // 이메일 중복 체크 (AJAX 요청)
    @ResponseBody
    @GetMapping("/emailChk")
    public Map<String, String> emailChk(String email) {
        boolean isDuplicate = userService.isEmailDuplicate(email);  // 이메일 중복 확인
        Map<String, String> response = new HashMap<>();
        response.put("isDuplicate", String.valueOf(isDuplicate));  // JSON 응답
        return response;
    }


    // 로그인 폼 이동
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginCommand", new LoginCommand());
        return "user/loginForm";  // 로그인 폼 뷰 페이지
    }

    // 로그인 처리
    @PostMapping("/login")
    public String loginUser(@Validated LoginCommand loginCommand, BindingResult result,
                            HttpServletRequest request, Model model) {
        if (result.hasErrors()) {
            return "user/loginForm";  // 유효성 검사 실패 시 폼으로 이동
        }

        String path = userService.loginUser(loginCommand, request, model);
        
     // 로그인 성공 시 홈 경로로 리다이렉트
        if ("home".equals(path)) {
            return "redirect:/"; // 홈 경로로 리다이렉트
        }
        
        return path;  // 로그인 성공 시 홈으로, 실패 시 로그인 페이지로 이동
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();  // 세션 무효화
        return "redirect:/";  // 로그아웃 후 홈 페이지로 리다이렉트
    }
}
