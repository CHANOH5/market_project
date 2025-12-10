package com.market.market.auth.service;

import com.market.market.auth.dto.LoginRequest;
import com.market.market.auth.dto.SignupRequest;
import com.market.market.auth.dto.TokenResponse;
import com.market.market.global.jwt.JwtTokenProvider;
import com.market.market.user.entity.User;
import com.market.market.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    // 일단 14일로 고정 (초 단위)
    private static final int REFRESH_TOKEN_COOKIE_MAX_AGE = 14 * 24 * 60 * 60;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입
     */
    @Transactional
    public void signup(SignupRequest dto) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.password());

        // User 엔티티 생성 (필드 이름은 User 엔티티에 맞게 수정 필요할 수 있음)
        User user = User.of(
                dto.loginId(),
                dto.username(),        // userName
                encodedPassword,       // password
                dto.email(),            // email
                dto.phone()
        );
        userRepository.save(user);
    }

    /**
     * 로그인: accessToken + refreshToken 발급
     */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest dto, HttpServletResponse response) {
        // 이메일로 유저 조회
        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + dto.email()));

        // 비밀번호 검증
        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new BadCredentialsException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // refreshToken 쿠키에 넣기 (HttpOnly)
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);      // HTTPS 환경에서만 전송 (개발 중 http면 false로 바꿔도 됨)
        cookie.setPath("/");
        cookie.setMaxAge(REFRESH_TOKEN_COOKIE_MAX_AGE);
        response.addCookie(cookie);

        return new TokenResponse(accessToken);
    }

    /**
     * refreshToken으로 새 accessToken 발급
     */
    @Transactional(readOnly = true)
    public TokenResponse refresh(HttpServletRequest request) {
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new IllegalArgumentException("Refresh token 이 없습니다.");
        }

        // 토큰 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 refresh token 입니다.");
        }

        Long userId = jwtTokenProvider.getUserId(refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));

        String newAccessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getRole());

        return new TokenResponse(newAccessToken);
    }

    /**
     * 로그아웃: refreshToken 쿠키 삭제
     * (지금은 서버쪽 저장소 없이 쿠키만 삭제하는 방식)
     */
    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 클라이언트쪽 쿠키 삭제
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 만료
        response.addCookie(cookie);

        // ※ 만약 나중에 DB/Redis에 refreshToken을 저장한다면,
        //   여기서 해당 토큰을 찾아 삭제하거나 블랙리스트 처리하면 됨.
    }

    /**
     * 쿠키에서 refreshToken 꺼내기
     */
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;

        for (Cookie cookie : cookies) {
            if (REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

}
