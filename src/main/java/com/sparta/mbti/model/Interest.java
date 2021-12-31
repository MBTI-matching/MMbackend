package com.sparta.mbti.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INTEREST_ID")
    private Long id;                            // 테이블 기본키

    @Column
    private String interest;                   // 관심사

//    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL)    // 관심사 삭제 => 해당 사용자관심사리스트 모두 삭제
//    List<UserInterest> userInterestList = new ArrayList<>();        // 관심사 리스트
}
