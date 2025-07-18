package com.se2303.inspectionservice.dto;

public class InspectionResponse {
    private boolean isSafe;
    private String reason;

    public InspectionResponse(boolean isSafe, String reason) {
        this.isSafe = isSafe;
        this.reason = reason;
    }

    // Getters and Setters
    public boolean isSafe() {
        return isSafe;
    }

    public void setSafe(boolean safe) {
        isSafe = safe;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
} 