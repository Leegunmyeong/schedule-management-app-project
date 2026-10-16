package com.schedulemanagementappproject.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UpdateScheduleResponse {
    private final Long id;
    private final String title;
    private final String detail;
    private final String name;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public UpdateScheduleResponse(Long id, String title, String detail, String name, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
