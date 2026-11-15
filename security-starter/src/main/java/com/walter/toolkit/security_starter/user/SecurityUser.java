package com.walter.toolkit.security_starter.user;

import java.util.Collection;

public interface SecurityUser {
    String getEmailAddress();
    String getPassword();
    Collection<String> getRoles();
    default boolean isEnabled(){
        return true;
    }
}
