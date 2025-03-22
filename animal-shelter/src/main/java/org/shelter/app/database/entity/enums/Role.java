package org.shelter.app.database.entity.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,

    ADMIN,

    VERIFIED_USER,

    VET;

    @Override
    public String getAuthority() {
        return name();
    }
}
