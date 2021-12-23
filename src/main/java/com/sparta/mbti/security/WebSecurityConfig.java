package com.sparta.mbti.security;

import com.sparta.mbti.security.filter.JwtAuthFilter;
import com.sparta.mbti.security.jwt.HeaderTokenExtractor;
import com.sparta.mbti.security.provider.JWTAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JWTAuthProvider jwtAuthProvider;
    private final HeaderTokenExtractor headerTokenExtractor;

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        // CustomAuthenticationProvider()를 호출하기 위해서 Overriding
        auth
                .authenticationProvider(jwtAuthProvider);
    }

    @Override
    public void configure(WebSecurity web) {
        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        // 서버에서 인증은 JWT로 인증하기 때문에 Session의 생성을 막습니다.
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        /*
         * 1.
         * UsernamePasswordAuthenticationFilter 이전에 JwtFilter 를 등록합니다.
         * JwtFilter       : 서버에 접근시 JWT 확인 후 인증을 실시합니다.
         */
        http
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests()
                // 어떤 요청이든 허용
                .anyRequest().permitAll()
//                .and()
//                // [로그인 기능]
//                .formLogin()
//                // 로그인 View 제공 (GET /user/login)
//                .loginPage("/user/login")
//                // 로그인 처리 (POST /user/login)
//                .loginProcessingUrl("/user/login")
//                // 로그인 처리 후 성공 시 URL
//                .defaultSuccessUrl("/")
//                // 로그인 처리 후 실패 시 URL
//                .failureUrl("/user/login?error")
//                .permitAll()
                .and()
                // [로그아웃 기능]
                .logout()
                // 로그아웃 요청 처리 URL
                .logoutUrl("/user/logout")
                .permitAll()
                .and()
                .exceptionHandling();
    }

    private JwtAuthFilter jwtFilter() throws Exception {
        List<String> skipPathList = new ArrayList<>();

        // h2-console 허용
        skipPathList.add("GET,/h2-console/**");
        skipPathList.add("POST,/h2-console/**");

        // 회원 관리 API 허용
        skipPathList.add("GET,/user/**");

        FilterSkipMatcher matcher = new FilterSkipMatcher(
                skipPathList,
                "/**"
        );

        JwtAuthFilter filter = new JwtAuthFilter(
                matcher,
                headerTokenExtractor
        );
        filter.setAuthenticationManager(super.authenticationManagerBean());

        return filter;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}