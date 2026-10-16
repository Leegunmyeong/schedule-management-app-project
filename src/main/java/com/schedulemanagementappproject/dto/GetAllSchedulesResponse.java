package com.schedulemanagementappproject.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class GetAllSchedulesResponse {
    private final List<GetOneScheduleResponse> scheduleList;

    public GetAllSchedulesResponse(List<GetOneScheduleResponse> scheduleList) {
        this.scheduleList = scheduleList;
    }
}
