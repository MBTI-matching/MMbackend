package com.sparta.mbti.entity;

import com.sparta.mbti.dto.UserInfoRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany
    @JoinColumn
    private List<User> userList; //= new ArrayList<>();

    public void updateProfile(UserInfoRequestDto requestDto) {
        this.interest = requestDto.getInterest();
    }
}
