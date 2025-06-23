package com.lokoko.domain.user.entity;

import com.lokoko.domain.user.entity.enums.PersonalColor;
import com.lokoko.domain.user.entity.enums.Role;
import com.lokoko.domain.user.entity.enums.SkinTone;
import com.lokoko.domain.user.entity.enums.SkinType;
import com.lokoko.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String lineId;

    @Column(nullable = false, unique = true)
    private String email;

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

    /*
     * TODO: 추후 scope 확장 시, 필드추가
     */
}
