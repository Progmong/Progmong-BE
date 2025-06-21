package com.progmong.api.explore.service;

import com.progmong.api.user.entity.User;
import com.progmong.api.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SolvedProblemSyncServiceTest {
    @Autowired
    private SolvedProblemSyncService solvedProblemSyncService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("solved.ac 유저가 푼 문제 가져오는 테스트")
    void solvedProblemSyncTest() {

        // 테스트용 사용자 생성 및 저장
        User testUser = User.builder()
                .bojId("xoals85")          //백준ID
                .email("test@gamil.com")
                .password("1234")
                .build();
        userRepository.save(testUser);
        solvedProblemSyncService.syncSolvedProblems(testUser.getId());

    }

}