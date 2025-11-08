package com.example.quickevent.controller;

import com.example.quickevent.model.User;
import com.example.quickevent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ViewController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // handles POST form submission from register.html
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username already exists!");
            return "register";
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            model.addAttribute("error", "Password cannot be empty!");
            return "register";
        }

        userService.registerUser(user);
        model.addAttribute("success", "Registration successful! Please login.");
        return "login";
    }

    // handles POST from login.html
    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            Model model) {
        var existing = userService.getUserByUsername(username);
        if (existing.isPresent()) {
            var user = existing.get();
            // password check (in real app use authenticationManager or JWT)
            if (userService.verifyPassword(password, user.getPassword())) {
                model.addAttribute("user", user);
                return "redirect:/dashboard"; // next page (we’ll make it soon)
            }
        }
        model.addAttribute("error", "Invalid username or password!");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard"; // we'll create this HTML next
    }
}
