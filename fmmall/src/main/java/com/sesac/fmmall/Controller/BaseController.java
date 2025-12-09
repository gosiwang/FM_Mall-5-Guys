package com.sesac.fmmall.Controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {

    protected int getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (int) auth.getPrincipal();
    }

}