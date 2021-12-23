package com.sparta.mbti.validator;

import com.sparta.mbti.model.User;
import com.sparta.mbti.security.UserDetailsImpl;

public class UserInfoValidator {

    public static User userDetailsIsNull(UserDetailsImpl userDetails) {
        if(userDetails != null){
            return userDetails.getUser();
        }else{
            throw new NullPointerException("유효하지 않은 사용자입니다.");
            // 이 부분은 어떻게 검사해야 할지 잘 몰라서 보류.
        }
    }

}
