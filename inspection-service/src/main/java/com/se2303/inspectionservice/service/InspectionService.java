package com.se2303.inspectionservice.service;

import com.se2303.inspectionservice.dto.ApiResponse;
import com.se2303.inspectionservice.dto.InspectionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InspectionService {

    private final LLMService llmService;
    private final AlertService alertService;

    @Autowired
    public InspectionService(LLMService llmService, AlertService alertService) {
        this.llmService = llmService;
        this.alertService = alertService;
    }

    public ApiResponse inspect(String content) {
        boolean isSafe = llmService.isContentSafe(content);
        if (!isSafe) {
            alertService.sendAlert("Unsafe content detected.");
            InspectionResponse InspectionResult = new InspectionResponse(false, "Content is not safe.");
            return new ApiResponse(true, InspectionResult, "Content is not safe.");
        }
        InspectionResponse InspectionResult = new InspectionResponse(true, "Content is safe.");
        return new ApiResponse(true, InspectionResult, "Content is safe.");
    }
} 