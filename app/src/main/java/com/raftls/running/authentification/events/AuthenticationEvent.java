package com.raftls.running.authentification.events;

import com.raftls.running.authentification.models.User;

public class AuthenticationEvent {
    private final User user;

    public AuthenticationEvent(User user) {
        this.user = user;
    }

    public boolean isAuthenticated() {
        return user != null;
    }

    public User getUser() {
        return user;
    }
}
