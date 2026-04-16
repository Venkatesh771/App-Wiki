package com.example.dictionary;

import com.example.dictionary.model.User;
import com.example.dictionary.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

@Controller
public class PageController {

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/")
    public String index(HttpSession session) {
        // Redirect to login if not authenticated
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "home";
    }

    @GetMapping("/mainfile")
    public String mainPage(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);
        model.addAttribute("userRole", user.getRole());
        return "mainfile";
    }

    @GetMapping("/applicationserverdetails")
    public String appServerPage(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);
        model.addAttribute("userRole", user.getRole());
        return "applicationserverdetails";
    }

    @GetMapping("/clouddetails")
    public String cloudPage(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);
        model.addAttribute("userRole", user.getRole());
        return "clouddetails";
    }

    @GetMapping("/databaseserverdetails")
    public String databasePage(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);
        model.addAttribute("userRole", user.getRole());
        return "databaseserverdetails";
    }

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);
        return "home";
    }

    @GetMapping("/admindashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        
        // Only allow Super Admin and Admin roles
        if (!user.getRole().equals("Super Admin") && !user.getRole().equals("Admin")) {
            return "redirect:/home";
        }
        
        model.addAttribute("currentUser", user);
        model.addAttribute("users", authenticationService.getAllUsers());
        return "admindashboard";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Redirect to home if already authenticated
        if (session.getAttribute("user") != null) {
            return "redirect:/home";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(
            @RequestParam String cwid,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        User user = authenticationService.authenticate(cwid, password);
        
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:/home";
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid CWID or password");
            return "redirect:/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
