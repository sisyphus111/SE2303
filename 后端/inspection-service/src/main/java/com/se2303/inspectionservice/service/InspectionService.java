package com.se2303.inspectionservice.service;

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

    public InspectionResponse inspect(String content) {
        boolean isSafe = llmService.isContentSafe(content);
        if (!isSafe) {
            alertService.sendAlert("Unsafe content detected.");
            return new InspectionResponse(false, "Content is not safe.");
        }
        return new InspectionResponse(true, "Content is safe.");
    }
} 