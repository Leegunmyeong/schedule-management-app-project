package com.schedulemanagementappproject.dto;

import lombok.Getter;

@Getter
public class DeleteScheduleResponse {
    private final String message;

    public DeleteScheduleResponse(String message) {
        this.message = message;
    }
}
