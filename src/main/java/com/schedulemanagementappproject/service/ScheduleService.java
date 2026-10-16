package com.schedulemanagementappproject.service;

import com.schedulemanagementappproject.dto.*;
import com.schedulemanagementappproject.entity.Schedule;
import com.schedulemanagementappproject.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;

    // 일정 생성
    // 1. 클라이언트가 보낸 요청 DTO의 값을 꺼내 Schedule Entity 객체 생성
    // request에는 title, detail, name, pw가 들어있음
    // Entity는 DB에 저장할 일정 데이터를 표현하는 객체
    // 지금은 객체만 생성했을 뿐, 아직 DB에 저장하지않음(비영속)
    @Transactional
    public CreateScheduleResponse save(CreateScheduleRequest request) {
        Schedule schedule = new Schedule(
                request.getTitle(),
                request.getDetail(),
                request.getName(),
                request.getPw()
        );
        // 2. 생성한 Schedule 객체를 Repository를 통해 DB에 저장
        // savedSchedule은 저장된 일정 Entity 객체를 참조
        //저장 과정에서 ID가 생성되고 JPA Auditing으로 날짜가 설정됨
        Schedule savedSchedule = scheduleRepository.save(schedule);
        // 3. DB에 저장된 Schedule Entity의 값을 읍답 DTO로 변환하여 반환
        // Entity에는 비밀번호(pw)가 포함되어 있으므로 필요한 정보만 Response DTO에 담음
        // new CreateScheduleResponse()로 응답 객체를 생성하고 return으로 Controller에 전달
        return new CreateScheduleResponse(
                savedSchedule.getId(),
                savedSchedule.getTitle(),
                savedSchedule.getDetail(),
                savedSchedule.getName(),
                savedSchedule.getCreatedAt(),
                savedSchedule.getUpdatedAt()
        );
    }

    // 선택 일정 조회
    @Transactional(readOnly = true)
    public GetOneScheduleResponse findOne(Long scheduleId) {
        // 1. 일정 ID로 조회
        // 일정이 존재하면 Schedule 객체를 꺼내고
        // 존재하지 않으면 orElseThrow()로 예외 발생
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new IllegalStateException("없는 일정 입니다")
        );
        // 2. 조회한 Entity의 정보를 DTO에 담아서 반환
        return new GetOneScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getDetail(),
                schedule.getName(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    // 전체 일정 조회
    @Transactional(readOnly = true)
    public GetAllSchedulesResponse findAllSchedules(String name) {
        // 1. DB에 저장된 모든 일정을 수정일(updatedAt) 기준 내림차순으로 조회
        // Sort.by(): 정렬 기준을 지정
        // Sort.Direction.DESC -> 내림차순(최근 수정한 일정부터)
        // "updatedAt" : 정렬 기준이 되는 Entity의 필드 이름
        // scheduleList에는 이미 수정일 기준 내림차순으로 정렬된 Schedule 객체들이 저장됨
        List<Schedule> scheduleList = scheduleRepository.findAll(
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );
        // 2. 클라이언트에게 반환할 응담 DTO 목록 생성
        // 처음에는 비어 있는 리스트, 조건에 맞는 일정들을 순서대로 추가할 예정
        List<GetOneScheduleResponse> responseList = new ArrayList<>();

        // 3. 조회할 일정들을 하나씩 확인
        // scheduleList의 0번 인덱스부터 순서대로 꺼내기 때문에 기존 내림차순 정렬이 유지됨
        for (Schedule schedule : scheduleList) {
            // 작성자명 전달 X(null) (false), AND 연산자기 때문에 이미 틀려서 뒤에 있는 조건 확인 안하고 리스트에 추가
            // 그러면은 전체 일정이 리스트에 담김
            // 작성자명이 있으면 null이 아니기 때문에 (true) 뒤에 조건 전달받은 이름과 DB에 저장된 이름이 다르면 (true)
            // 둘다 true이기 때문에 continue 발생 그러면은 전달받은 이름은 있는데 이름과 다른 일정이 리스트에서 제외됨
            // 최종적으로 이름과 동일한 일정만 남는다
            if (name != null && !name.equals(schedule.getName())) {
                continue;
            }
            // 4. continue로 제외되지 않은 Schedule Entity를 응답 DTO로 변환
            // Entity의 필요한 정보만 가져오므로 비밀번호(pw)는 응답에 포함되지않음
            // GetOneScheduleResponse 생성자 조건을 지켜야함
            GetOneScheduleResponse response = new GetOneScheduleResponse(
                    schedule.getId(),
                    schedule.getTitle(),
                    schedule.getDetail(),
                    schedule.getName(),
                    schedule.getCreatedAt(),
                    schedule.getUpdatedAt()
            );
            // 5. 변환한 DTO를 responseList의 맨 뒤에 추가
            // 기존 scheduleList의 순서대로 추가하기 때문에 수정일 내림차순 정렬이 유지됨
            responseList.add(response);
        }
        // 6. 최종 DTO 목록을 GetAllSchedulesResponse 객체로 묶어 Controller에 반환
        // 반환되는 JSON은 scheduleList 배열을 포함하는 형태
        return new GetAllSchedulesResponse(responseList);
    }


    // 일정 수정
    @Transactional
    public UpdateScheduleResponse updatedSchedule(Long scheduleId, UpdateScheduleRequest request) {
        // 1. findById(scheduleId)로 전달받은 ID로 DB에서 일정조회
        // 일정이 존재하면 Schedule 객체를 schedule 변수에 저장
        // 존재하지 않으면 orElseThrow()로 예외 발생
        // 조회한 Schedule은 JPA가 관리하는 영속 상태의 Entity
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new IllegalStateException("없는 일정 입니다.")
        );
        // 2. 요청받은 비밀번호와 DB에 저장되어있는 pw의 일치여부 검사
        // equals()로 두 비밀번호 비교하고 !로 결과를 반대로 변경
        // 비밀번호가 다르면 예외를 발생시켜 수정 작업 중단
        // 비밀번호가 같으면 if문 건너뛰고 다음 수정 작압 진행
        if (!schedule.getPw().equals(request.getPw())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다");
        }
        // 3. Schedule Entity의 update() 메서드를 호출하여 수정
        // request에서 새로운 제목, 작성자명 꺼내서 전달
        // Entity 내부에서 기존 title과 name 필드를 전달받은 새로운 값으로 변경
        // title과 name 필드빼고 나머지는 수정 X
        // @Transcational 안에서 영속 Entity를 변경했기 때문에
        // Transactional 종료 시 JPA가 변경 감지(Dirty Checking)을 통해 DB에 반영
        schedule.update(request.getTitle(), request.getName());

        // 4. 수정된 Schedule Entity의 정보를 응답 DTO로 변환하여 반환
        // Entity 전체를 반환 X
        // 필요한 정보만 DTO에 담는다, 비밀번호는 응답에 미포함
        // return으로 Controller에 UpdateScheduleResponse 객체 전달
        //
        return new UpdateScheduleResponse(
                schedule.getId(),
                schedule.getTitle(),
                schedule.getDetail(),
                schedule.getName(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    // 일정 삭제
    @Transactional
    public DeleteScheduleResponse deleteSchedule(Long scheduleId, DeleteScheduleRequest request) {
        // 1. DB에서 Schedule 꺼내오기
        // 전달받은 ID로 DB에서 가져온 기존 일정 조회
        // 일정이 존재하면 조회한 Schedule 객체를 schedule 변수에 저장
        // 일정이 존재하지 않으면 orElseThrow()로 예외 발생
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new IllegalStateException("일정이 존재하지 않습니다")
        );
        // 2. 비밀번호 일치 여부 검사
        if (schedule.getPw().equals(request.getPw())) {
            // 비밀번호가 일치하면 DB에서 일정 삭제
            scheduleRepository.deleteById(scheduleId);
            // 삭제 성공 메세지를 담은 DeleteScheduleResponse DTO 반환
            return new DeleteScheduleResponse("일정이 삭제 되었습니다!");
        } else {
            // 비밀번호가 일치하지 않으면 예외 발생
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
}
