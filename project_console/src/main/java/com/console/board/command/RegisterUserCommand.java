package com.console.board.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserCommand {

    @NotBlank(message = "아이디는 필수 입력 사항입니다.")
    @Size(min = 2, max = 10, message = "아이디는 2~10자로 입력해야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 알파벳과 숫자로만 구성할 수 있습니다.")
    private String id;

    @NotBlank(message = "닉네임은 필수 입력 사항입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자로 입력해야 합니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 알파벳, 숫자로만 구성할 수 있습니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수 입력 사항입니다.")
    @Email(message = "올바른 이메일 형식을 입력해야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 사항입니다.")
    @Size(min = 8, max = 16, message = "비밀번호는 8~16자로 입력해야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]+$", message = "비밀번호는 알파벳, 숫자, 특수문자를 포함해야 합니다.")
    private String password;
}
