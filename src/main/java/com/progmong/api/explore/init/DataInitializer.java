package com.progmong.api.explore.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SolvedAcImportService solvedAcImportService;

    @Override
    public void run(String... args) {
        // 문제 테이블이 비어있을 때만 실행
        if (solvedAcImportService.isProblemTableEmpty()) {
            solvedAcImportService.importProblemsFromSolvedAc();
        }
    }
}