package com.sparta.mbti.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Group extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private int totalMem;

    @Column
    private boolean roomFull;

    @Column
    private Long userId;

    @ManyToOne
    @JoinColumn
    private Interest interest;
}
