package com.se2303.inspectionservice.controller;

import com.se2303.inspectionservice.dto.InspectionRequest;
import com.se2303.inspectionservice.dto.ApiResponse;
import com.se2303.inspectionservice.service.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inspect")
public class InspectionController {

    private final InspectionService inspectionService;

    @Autowired
    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @PostMapping
    public ApiResponse inspectContent(@RequestBody InspectionRequest request) {
        return inspectionService.inspect(request.getContent());
    }
} 