package com.sparta.mbti.config;

import com.sparta.mbti.security.FilterSkipMatcher;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class  WebSecurityConfig extends WebSecurityConfigurerAdapter {
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
        // nginx 확인용으로 .antMatchers에 "/profile" 추가 -> /actuator/** /health /version도 필요한가?
        web
                .ignoring()
                .antMatchers("/h2-console/**", "/profile", "/actuator/**", "/health", "/version");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        // cors설정 추가
        http
                .cors()
                .configurationSource(corsConfigurationSource());

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
        // 이 부분은 JWT 토큰이 불필요한 API 만 넣어주는 곳 (JWT 필터를 안거치므로 토큰 생성안됨)

        // 카카오 로그인 페이지 허용
        skipPathList.add("GET,/user/kakao/callback");

        // 둘러보기 허용
        skipPathList.add("GET,/api/chemy/guest");

        // 채팅
        skipPathList.add("GET,/chat/room/**");
        skipPathList.add("GET,/sub/chat/room/**");
        skipPathList.add("GET,/pub/chat/room/**");
        skipPathList.add("GET,/ws-stomp/pub/chat/room/**");
        skipPathList.add("GET,/ws-stompAlarm/pub/chat/room/**");
        skipPathList.add("GET,**/pub/chat/room/**");
        skipPathList.add("GET,**/sub/chat/room/**");
        skipPathList.add("GET,/ws-stomp/**");

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // 배포 주소
//        configuration.addAllowedOrigin("http://"); // S3 주소
        configuration.setAllowCredentials(true);
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.addExposedHeader("Authorization");
        configuration.addAllowedOriginPattern("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
