package com.sparta.mbti.entity;

import com.sparta.mbti.dto.UserInterestRequestDto;
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
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long interestId;

    @Column
    private String interest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private User user;

    public void updateProfile(UserInterestRequestDto interestDto) {
        this.interest = interestDto.getInterest();
    }
}
