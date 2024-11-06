package com.console.board.config;

import com.console.board.service.ImageService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class ScheduleConfig {

    private final ImageService imageService;

    public ScheduleConfig(ImageService imageService) {
        this.imageService = imageService;
    }

    // 매 시간 정각마다 임시 이미지를 정리하는 스케줄링 작업
    @Scheduled(cron = "0 0 * * * *") // 매시간 실행
    public void cleanTemporaryImages() {
        imageService.deleteTemporaryImages(); // 임시 이미지 삭제 메서드 호출
        System.out.println("임시 이미지 정리 작업이 실행되었습니다."); // 로그 출력
    }
}
