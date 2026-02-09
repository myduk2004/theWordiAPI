package theWordI.backend.domain.user.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import theWordI.backend.domain.user.dto.UserCreateRequestDTO;
import theWordI.backend.domain.user.dto.UserUpdateRequestDTO;
import theWordI.backend.domain.user.exception.AccountLockedException;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", unique = true, nullable = false, updatable = false)
    private String username;

    @Column(name="password", nullable = false)
    private String password;

    @Column(name="email", nullable = false)
    private String email;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="phone")
    private String phone;


    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
    private UserRoleType role;

    @Column(name="is_social", nullable = false)
    private boolean isSocial;


    @Enumerated(EnumType.STRING)
    @Column(name="social_provider_type")
    private SocialProviderType socialProviderType;


    @Column(name="is_lock", nullable=false)
    private boolean isLock;


    @CreatedDate
    @Column(name="created_dt", updatable = false)
    private LocalDateTime createdDt;


    @LastModifiedDate
    @Column(name="updated_dt")
    private LocalDateTime updatedDt;


    @Column(name="last_login_dt")
    private LocalDateTime lastLoginDt;


    public void updateLastLogin()
    {
        this.lastLoginDt = LocalDateTime.now();
    }


    public void validateNotLocked()  {
        if (this.isLock){
            throw new AccountLockedException("계정이 잠겼습니다. 관리자에게 문의해주세요.");
        }
    }


    public void updateUser(UserUpdateRequestDTO dto)
    {
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.phone = dto.getPhone();
        updateLastLogin();
    }
    
    //회원 엔티티 생성(일반 유저)
    public static UserEntity createBasic(UserCreateRequestDTO dto, PasswordEncoder encoder)
    {
        return UserEntity.builder()
                .username(dto.getUsername())
                .password(encoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .name(dto.getName())
                .phone(dto.getPhone())
                .role(UserRoleType.USER)
                .isSocial(false)
                .isLock(false)
                .build();
    }

    //회원 엔티티 생성(OAuth 유저)
    public static UserEntity createSocial(String username, String email, String name, String registrationId)
    {
       return UserEntity.builder()
                .username(username)
                .password("")
                .email(email)
                .name(name)
                .phone("")
                .role(UserRoleType.USER)
                .isSocial(true)
                .socialProviderType(SocialProviderType.valueOf(registrationId))
                .isLock(false)
                .build();
    }


}
