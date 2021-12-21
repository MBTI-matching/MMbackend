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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String nickname;

    @Column
    private String profileImage;

    @Column
    private String intro;

    @Column
    private String location;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private String gender;

    @Column
    private String ageRange;

    @Column
    private String address;
}
