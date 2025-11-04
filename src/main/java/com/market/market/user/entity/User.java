package com.market.market.user.entity;

import com.market.market.user.dto.UserRequestDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 무분별하게 new로 만드는 걸 막고 JPA 프록시/리플렉션이 사용할 수 있게는 열어둠
@EntityListeners(AuditingEntityListener.class) // 스프링 데이터 JPA 감사(auditing) 기능을 켜서 @CreatedDate, @LastModifiedDate 같은 필드를 자동으로 채워줌
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, length = 255)
    private String userName;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "status", nullable = false)
    private Integer status;

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "updated_by", nullable = false, length = 255)
    private String updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 외부에서 new User로 만들지 못하게 막고, 도메인 코드에서 쓰는 유효 상태 생성은 of메서드로만 하도록 하기 위함
    private User(String userName, String password, String email) {

        if(userName == null || userName.isBlank()) throw new IllegalArgumentException("username required");
        if(password == null || password.isBlank()) throw new IllegalArgumentException("password required");
        if(email == null || email.isBlank()) throw new IllegalArgumentException("email required");

        this.userName = userName;
        this.password = password;
        this.email = email;
        this.status = 1;
    } // constructor

    /**
     * 도메인 생성, 생성 로직을 숨길 수 있어 내부 기본값/검증/정책을 한곳에 모을 수 있음
     * @param userName  사용자 이름
     * @param password  사용자 비밀번호
     * @param email     이메일
     * @return 생성된 도메인 정보 반환
     */
    // TODO: password 인코딩
    public static User of(String userName, String password, String email) {
        return new User(userName, password, email);
    } // of

    // ========= 변경 메서드 =========
    public void changeUserName(String userName) {
        setUserName(userName);
    }
    public void changeEmail(String email) {
        setEmail(email);
    }

    /**
     * 사용자를 탈퇴처리 합니다. status 0으로 변경
     */
    public void withdraw() {
        if(this.status == 0) {
            throw new IllegalStateException("이미 탈퇴 처리된 회원입니다.");
        }
        this.status = 0;
    } // withdraw

    // ======== 세터 =========

    private void setUserName(String userName) {
        if (userName == null || userName.isBlank()) throw new IllegalArgumentException("username required");
        this.userName = userName;
    }

    private void setEmail(String email) {
        if (email == null || email.isBlank()) throw new IllegalArgumentException("email required");
        this.email = email;
    }

    // TODO: 비밀번호 변경

} // end class
