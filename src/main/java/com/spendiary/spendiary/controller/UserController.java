package com.spendiary.spendiary.controller;

import com.spendiary.spendiary.dto.LoginRequest;
import com.spendiary.spendiary.dto.SignupRequest;
import com.spendiary.spendiary.entity.User;
import com.spendiary.spendiary.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(SignupRequest request, Model model, RedirectAttributes redirectAttributes){
        try {
            userService.join(request);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "signup";
        }

        redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다. 로그인해주세요!");

        return "redirect:/user/login";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(LoginRequest request, HttpSession session, Model model) {
        if (request.getLoginId() == null || request.getLoginId().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            model.addAttribute("errorMessage", "아이디와 비밀번호를 모두 입력해주세요.");
            return "login";
        }
        try {
            User loginUser = userService.login(request);
            session.setAttribute("loginUser", loginUser);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/user/login";
    }

    @PostMapping("/delete")
    public String deleteAccount(HttpSession session,  RedirectAttributes redirectAttributes) {
        User loginUser = (User) session.getAttribute("loginUser");

        userService.delete(loginUser.getLoginId());

        session.invalidate();

        redirectAttributes.addFlashAttribute("successMessage", "회원 탈퇴가 완료되었습니다. 그동안 Spendiary를 이용해 주셔서 감사합니다.");

        return "redirect:/user/login";
    }
}
