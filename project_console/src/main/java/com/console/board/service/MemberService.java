package com.console.board.service;

import com.console.board.dtos.UserDto;
import com.console.board.mapper.UserMapper;
import com.console.board.mapper.SuspendedUserMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final UserMapper userMapper;
    private final SuspendedUserMapper suspendedUserMapper;

    public List<UserDto> getMembers(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<UserDto> members = userMapper.findAll(pageSize, offset);
        return members;
    }

    public int getTotalUsersCount() {
        return userMapper.countUsers();
    }

    @Transactional
    public void updateMemberRoleAndSuspended(UserDto userDto) {
        // Role 업데이트
        userMapper.updateUserRole(userDto);

        // Suspended가 0으로 설정된 경우, SuspendedUser 테이블에서 삭제
        if (userDto.getSuspended() == 0) {
            suspendedUserMapper.deleteSuspendedUserByUserId(userDto.getUserId());
        }
    }
}
