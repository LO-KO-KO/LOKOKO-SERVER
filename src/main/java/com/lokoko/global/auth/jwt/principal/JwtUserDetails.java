package com.lokoko.global.auth.jwt.principal;

import java.util.Collections;
import java.util.List;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class JwtUserDetails extends User {

    private final Long id;

    public JwtUserDetails(Long id, String lineId, List<GrantedAuthority> authorities) {
        super(lineId, "", authorities);
        this.id = id;
    }

    public static JwtUserDetails of(Long id, String LindId, String role) {
        return new JwtUserDetails(id, LindId, Collections.singletonList(new SimpleGrantedAuthority(role)));
    }
}
