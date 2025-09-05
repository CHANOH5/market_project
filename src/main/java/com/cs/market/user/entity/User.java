package com.cs.market.user.entity;

import com.cs.market.user.dto.UserRequestDTO;
import com.cs.market.user.dto.UserResponseDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id", nullable = false)
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
    private User(String userName, String password, String email, Integer status) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.status = status;
    }

    public static User of(String userName, String password, String email, Integer stauts) {
        return new User(userName, password, email, stauts);
    }

    private User(String userName, String password, String email) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.status = 1;
    }

    // ======================= 정적 팩토리 메서드 (비즈니스 로직) - 생성과 검증 =====================

    /**
     * DTO 객체를 받아 User 엔티티를 생성합니다.
     * 생성 로직에 의미를 부여하고 편의성을 제공합니다.
     */
    public static User from(UserRequestDTO dto) {
        return new User(dto.getUserName(), dto.getPassword(), dto.getEmail());
    } // from

    public void update(UserRequestDTO dto) {
        this.userName = dto.getUserName();
        this.password = dto.getPassword();
        this.email = dto.getEmail();
    }

    public void withdraw() {
        if(this.status == 0) {
            throw new IllegalStateException("이미 탈퇴 처리된 회원입니다.");
        }
        this.status = 0;
    } // updateStatus

} // end class
