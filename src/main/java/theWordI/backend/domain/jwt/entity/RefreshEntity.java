package theWordI.backend.domain.jwt.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name="refresh_tokens")
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="refresh_token_id")
    private Long id;

    @Column(name="user_id", nullable = false)
    private Long userId;


    @Column(name="refresh_token", nullable = false, length = 512, unique = true)
    private String refreshToken;

    @CreatedDate
    @Column(name="created_dt", updatable = false, nullable = false)
    private LocalDateTime createdDt;

}
