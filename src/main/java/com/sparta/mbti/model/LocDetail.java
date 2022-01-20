package com.sparta.mbti.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class LocDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOC_DETAIL_ID")
    private Long id;                            // 테이블 기본키

    @Column
    private String locDetail;                   // 위치

    @ManyToOne
    @JoinColumn(name = "LOCATION_ID")
    private Location location;                  // 위치 (특별시, 도)
}
