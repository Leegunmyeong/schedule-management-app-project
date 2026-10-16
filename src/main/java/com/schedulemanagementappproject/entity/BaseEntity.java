package com.schedulemanagementappproject.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
// 자식 Entity가 부모의 필드를 물려받도록 함
@MappedSuperclass
// 일정 저장, 수정 시 Auditing 기능이 작동하도록 연결
@EntityListeners(AuditingEntityListener.class)
// 다른 Entity가 상속받아 공통 날짜를 사용하기 위한 부모 클래스
public abstract class BaseEntity {

    // 처음 생성된 시간 자동 기록
    // updatable = false -> Hibernate가 UPDATE SQL 쿼리를 생성할 때
    // 이 컬럼을 제외 시킴
    // 값을 변경하고 저장해도 createdAt은 변경 되지않음
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    // 마지막 수정 시간 자동 기록
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
