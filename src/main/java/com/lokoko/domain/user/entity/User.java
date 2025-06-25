package com.lokoko.domain.user.entity;

import com.lokoko.domain.user.entity.enums.PersonalColor;
import com.lokoko.domain.user.entity.enums.Role;
import com.lokoko.domain.user.entity.enums.SkinTone;
import com.lokoko.domain.user.entity.enums.SkinType;
import com.lokoko.domain.user.entity.enums.UserStatus;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String lineId;

    @Column(unique = true)
    private String email;

    @Column
    private LocalDateTime lastLoginAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column
    private PersonalColor personalColor;

    @Enumerated(EnumType.STRING)
    @Column
    private SkinTone skinTone;

    @Enumerated(EnumType.STRING)
    @Column
    private SkinType skinType;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.ACTIVE;

    /*
     * TODO: 추후 scope 확장 시, 필드추가
     */

    public static User createLineUser(String lineUserId) {
        return User.builder()
                .lineId(lineUserId)
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .lastLoginAt(LocalDateTime.now())
                .build();
    }

    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
