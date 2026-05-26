package com.example.skill.sharing.portal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.skill.sharing.portal.model.SkillsPeople;
import com.example.skill.sharing.portal.service.OAuthUserServiceIMPL;

import jakarta.servlet.http.HttpSession;

@Controller
public class OAuthController {

    @Autowired
    private OAuthUserServiceIMPL userService;

    @GetMapping("/oauth-success")
    public String loginSuccess(
            Authentication authentication,
            HttpSession session) {

        // CHECK LOGIN
        if (authentication == null ||
                !(authentication.getPrincipal()
                        instanceof OAuth2User)) {

            return "redirect:/home";
        }

        // GET GOOGLE USER
        OAuth2User oauthUser =
                (OAuth2User) authentication.getPrincipal();// get object of type OAuth2User from authentication principal

        // PROCESS USER
        SkillsPeople dbUser =
                userService.processOAuthUser(oauthUser);// processOAuthUser method in userService to handle user data and return a SkillsPeople object representing the user in the database

        // STORE FULL USER OBJECT
        session.setAttribute(
                "currentUser",
                dbUser);

        // STORE FULL NAME
        session.setAttribute(
                "fullName",
                dbUser.getFullName());

        // STORE EMAIL
        session.setAttribute(
                "email",
                dbUser.getEmail());

        // CHECK LOGIN TYPE
        if ("GOOGLE".equals(dbUser.getProvider())) {//true → Google OAuth login

            // GOOGLE LOGIN
            session.setAttribute(
                    "userId",
                    dbUser.getProviderId());

        } else {

            // NORMAL LOGIN
            session.setAttribute(// if false Google, store username as userId
                    "userId",
                    dbUser.getUsername());
        }

        // LOGIN TYPE
        String loginType =
                (String) session.getAttribute("loginType");

        session.removeAttribute("loginType");

        // REDIRECT
        if ("skilled".equals(loginType)) {

            return "redirect:/addSkill";
        }

        if ("seeker".equals(loginType)) {

            return "redirect:/skillspage";
        }

        return "redirect:/home";
    }
}
