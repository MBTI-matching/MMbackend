package com.sparta.mbti.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interestId;

    @Column
    private String interest;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn
//    private User user;
}
