package com.console.board.service;

import com.console.board.command.RegisterUserCommand;
import com.console.board.command.LoginCommand;
import com.console.board.dtos.UserDto;
import com.console.board.mapper.UserMapper;
import com.console.board.status.RoleStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // 아이디 중복 확인 메서드
    public boolean isUsernameDuplicate(String username) {
        return userMapper.countByUsername(username) > 0;
    }

    // 이메일 중복 확인 메서드
    public boolean isEmailDuplicate(String email) {
        return userMapper.countByEmail(email) > 0;
    }

    // 회원가입 메서드
    public boolean registerUser(RegisterUserCommand registerUserCommand) {
        if (isUsernameDuplicate(registerUserCommand.getId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        if (isEmailDuplicate(registerUserCommand.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 회원 데이터 설정 및 비밀번호 암호화
        UserDto userDto = new UserDto();
        userDto.setUsername(registerUserCommand.getId());
        userDto.setNickname(registerUserCommand.getNickname());
        userDto.setEmail(registerUserCommand.getEmail());
        userDto.setPassword(passwordEncoder.encode(registerUserCommand.getPassword()));
        userDto.setRole(RoleStatus.USER.toString()); // 기본 사용자 등급 설정

        return userMapper.registerUser(userDto) > 0; // 성공 여부 반환
    }

    // 로그인
    public String loginUser(LoginCommand loginCommand, HttpServletRequest request, Model model) {
        UserDto user = userMapper.findByUsername(loginCommand.getId());
        String path = "home"; 

        if (user != null) {
            System.out.println("DB에서 가져온 사용자 ID: " + user.getUserId()); // 디버깅 로그 추가

            // 비밀번호 일치 확인
            if (passwordEncoder.matches(loginCommand.getPassword(), user.getPassword())) {
                System.out.println("패스워드 일치: 로그인 성공");
                request.getSession().setAttribute("userDto", user); // 세션에 사용자 정보 저장
            } else {
                System.out.println("패스워드 불일치");
                model.addAttribute("msg", "패스워드를 확인하세요.");
                path = "user/login"; // 로그인 실패 시 로그인 페이지로 이동
            }
        } else {
            System.out.println("아이디 불일치: 회원이 아닙니다.");
            model.addAttribute("msg", "아이디를 확인하세요.");
            path = "user/login";
        }

        return path; // 최종 이동 경로 반환
    }
    
 // 사용자 ID로 사용자 정보 조회
    public UserDto getUserById(int userId) {
        return userMapper.getUserById(userId);
    }
}
