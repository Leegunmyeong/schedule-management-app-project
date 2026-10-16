package com.schedulemanagementappproject.controller;

import com.schedulemanagementappproject.dto.*;
import com.schedulemanagementappproject.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    // 일정 생성
    @PostMapping
    public ResponseEntity<CreateScheduleResponse> createdSchedule(@RequestBody CreateScheduleRequest request) {
        // 1. 클라이언트가 보낸 요청 DTO를 Service에 전달하여 일정 생성 요청
        // Service는 Repository를 통해 DB에 일정을 저장하고 응답 DTO를 반환
        CreateScheduleResponse result = scheduleService.save(request);
        // 2. HTTP 상태 코드와 응답 DTO를 클라이언트에게 반환
        // ResponseEntity는 HTTP 상태 코드와 응답 데이터를 함께 전달할 수 있음
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 선택 일정 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<GetOneScheduleResponse> getOneSchedule(@PathVariable Long scheduleId) {
        // 1. URL 경로에서 일정 ID(scheduleID)를 전달받는다
        // @@PathVariable은 URL의 {scheduleId} 값을 매개변수에 저장

        // 2. 전달받은 일정 ID를 Service에 넘겨 해당 일정 조회 요청
        // Service에서 반환한 응답 DTO를 HTTP 200 OK와 함께 클라이언트에게 반환
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.findOne(scheduleId));
    }

    // 전체 일정 조회
    @GetMapping
    public ResponseEntity<GetAllSchedulesResponse> getAllSchedules(@RequestParam(required = false) String name) {
        // 1. 요청 URL에서 작성자명(name)을 전달받음
        // @RequestParam은 쿼리 파라미터 값을 매개변수에 저장
        // required = false -> 작성자명을 전달하지 않아도 요청 가능 (name = null)

        // 2.전달받은 작성자명은 Service에 넘겨 일정 목록 조회 요청
        // name이 null 이면 전체 일정 조회 , 전달된 이름이 있으면 해당 검증 로직으로 해당 작성자의 일정만 조회
        // Service에서 반환한 응답 DTO를 HTTP 200 OK와 함께 클라이언트에게 반환
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.findAllSchedules(name));
    }

    // 일정 수정
    // 1. 클라이언트가 PATCH /schedules/{scheduleId} 요청을 보내면 실행됨
    // @PathVariabe: URL에 포함된 scheduleId를 Long 타입으로 전달받음
    // @RequestBody: 요청 Body의 JSON 데이터를 UpdateScheduleRequest DTO로 변환
    // request -> UpdateScheduleRequest DTO가 클라이언트로 받은 title, name, pw가 담겨 있음
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<UpdateScheduleResponse> updatedSchedule(@PathVariable Long scheduleId, @RequestBody UpdateScheduleRequest request) {
        // 2. Service의 updateSchedule() 메서드에 일정 ID와 요청 DTO 전달
        // Service에서 일정 조회 -> 비밀번호 검증 -> 제목과 작성자명 수정 처리
        // 수정된 일정 정보를 UpdateScheduleResponse DTO로 반환 받음

        // 3. ResponseEntity를 이용해 HTTP 200 OK 상태 코드 및 응답 DTO 반환
        // Response Body에는 수정된 일정 정보가 들어감
        // 비밀번호는 미포함
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.updatedSchedule(scheduleId, request));
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<DeleteScheduleResponse> deletedSchedule(@PathVariable Long scheduleId, @RequestBody DeleteScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(scheduleService.deleteSchedule(scheduleId, request));
    }
}
