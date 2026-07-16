package theWordI.backend.domain.header.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name="header_title")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class HeaderTitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "header_title_id")
    private Long headerTitleId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "menu_id", nullable = false)
    private Integer menuId;

    @Column(name = "text", nullable = false, length = 1000)
    private String text;


    @Column(name = "sub_text", length = 500)
    private String subText;


    @Column(name = "source", length = 200)
    private String source;


    @Column(name = "active_yn", length = 1, nullable = false, columnDefinition = "CHAR(1)")
    private String activeYn = "N";

    @Column(name="start_dt", nullable = false)
    private LocalDateTime startDt;


    @Column(name = "end_dt", nullable = false)
    private LocalDateTime endDt;


    @CreatedDate
    @Column(name="reg_dt", updatable = false)
    private LocalDateTime regDt;
}
