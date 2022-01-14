package com.sparta.mbti.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Matching {

    @Id
<<<<<<< HEAD
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
=======
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MATCHING_ID")
>>>>>>> develop
    private Long id;

    @Column
    private Long hostId;

    @Column
    private Long guestId;

}
