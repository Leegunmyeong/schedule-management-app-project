package com.schedulemanagementappproject.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String detail;
    @Column(length = 100, nullable = false)
    private String name;
    @Column(nullable = false)
    private String pw;

    public Schedule(String title, String detail, String name, String pw) {
        this.title = title;
        this.detail = detail;
        this.name = name;
        this.pw = pw;
    }


    // 일정제목이랑 작성자명만 수정하는 메서드
    // 객체의 필드들이 private로 캡슐화 되어 있기 때문에 Service에서 직접 수정 X
    // 그래서 내부에 메서드 생성
    // Setter를 사용할 수도 있지만 update를 사용하면 일정 수정하는 기능을 하나로 묶을 수 있고
    // 수정 가능한 필드를 제한 가능
    public void update(String title, String name) {
        this.title = title;
        this.name = name;
    }

}
