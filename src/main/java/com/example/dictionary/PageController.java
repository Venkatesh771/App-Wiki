package com.example.dictionary;

import com.example.dictionary.entity.BasicIdentity;
import com.example.dictionary.model.User;
import com.example.dictionary.entity.ActivityLog;
import com.example.dictionary.service.ActivityLogService;
import com.example.dictionary.service.AuthenticationService;
import com.example.dictionary.service.BasicIdentityService;
import com.example.dictionary.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PageController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private BasicIdentityService basicIdentityService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping("/")
    public String index(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "redirect:/home";
    }

    @GetMapping("/mainfile")
    public String mainPage(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        model.addAttribute("currentUser", user);
        model.addAttribute("userRole", user.getRole());
        model.addAttribute("assignmentGroups", groupService.getDistinctL2Names());
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

    @GetMapping("/application/{id}")
    public String applicationDetail(@PathVariable Long id,
                                    @RequestParam(defaultValue = "false") boolean edit,
                                    HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        Optional<BasicIdentity> appOpt = basicIdentityService.getByIdWithDetails(id);
        if (appOpt.isEmpty()) {
            return "redirect:/home";
        }
        String role = user.getRole();
        boolean editMode = edit && (role.equals("Admin") || role.equals("Super Admin"));
        BasicIdentity app = appOpt.get();

        List<String> assignmentGroups = new ArrayList<>(groupService.getDistinctL2Names());
        if (app.getAssignmentGroup() != null && !app.getAssignmentGroup().trim().isEmpty()) {
            if (assignmentGroups.stream().noneMatch(ag -> ag.equalsIgnoreCase(app.getAssignmentGroup()))) {
                assignmentGroups.add(app.getAssignmentGroup());
            }
        }
        Collections.sort(assignmentGroups);

        model.addAttribute("app", app);
        model.addAttribute("currentUser", user);
        model.addAttribute("userRole", role);
        model.addAttribute("editMode", editMode);
        model.addAttribute("assignmentGroups", assignmentGroups);
        return "appdetail";
    }

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");

        User freshUser = authenticationService.getUserByCwid(user.getCwid());
        if (freshUser != null) {
            user = freshUser;
            session.setAttribute("user", user);
        }
        model.addAttribute("currentUser", user);
        model.addAttribute("userFilterGroups", user.getFilterGroups() != null ? user.getFilterGroups() : "[]");
        model.addAttribute("assignmentGroups", groupService.getDistinctL2Names());
        List<BasicIdentity> applications = basicIdentityService.getAll();
        model.addAttribute("applications", applications);
        return "home";
    }

    @PostMapping("/api/user/filter-groups")
    @ResponseBody
    public Map<String, Object> saveFilterGroups(@RequestBody(required = false) String body, HttpSession session) {
        Map<String, Object> resp = new HashMap<>();
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            resp.put("success", false);
            resp.put("error", "unauthorized");
            return resp;
        }
        String json = (body == null || body.trim().isEmpty()) ? "[]" : body.trim();
        boolean saved = authenticationService.updateFilterGroups(sessionUser.getCwid(), json);
        if (saved) {
            sessionUser.setFilterGroups(json);
            session.setAttribute("user", sessionUser);
        }
        resp.put("success", saved);
        return resp;
    }

    @GetMapping("/admin-apps")
    public String adminApps(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        if (!user.getRole().equals("Super Admin") && !user.getRole().equals("Admin")) {
            return "redirect:/home";
        }
        model.addAttribute("currentUser", user);
        model.addAttribute("applications", basicIdentityService.getAllIncludingInactive());
        return "adminapps";
    }

    @GetMapping("/admindashboard")
    public String adminDashboard(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");

        if (!user.getRole().equals("Super Admin") && !user.getRole().equals("Admin")) {
            return "redirect:/home";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("users", authenticationService.getAllUsers());
        return "admindashboard";
    }

    @GetMapping("/admin-groups")
    public String adminGroups(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        if (!user.getRole().equals("Super Admin") && !user.getRole().equals("Admin")) {
            return "redirect:/home";
        }
        model.addAttribute("currentUser", user);
        model.addAttribute("groups", groupService.listWithCounts());
        return "admingroups";
    }

    @GetMapping("/admin-logs")
    public String adminLogs(HttpSession session, Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("user");
        if (!user.getRole().equals("Super Admin") && !user.getRole().equals("Admin")) {
            return "redirect:/home";
        }
        model.addAttribute("currentUser", user);
        model.addAttribute("appAddLogs", activityLogService.listByType(ActivityLog.TYPE_APP_ADD));
        model.addAttribute("editLogs", activityLogService.listAllEditsAndDeletes());
        model.addAttribute("userAddLogs", activityLogService.listByType(ActivityLog.TYPE_USER_ADD));
        model.addAttribute("groupAddLogs", activityLogService.listByType(ActivityLog.TYPE_GROUP_ADD));
        model.addAttribute("userLoginLogs", activityLogService.listByType(ActivityLog.TYPE_USER_LOGIN));
        return "adminlogs";
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {

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
            activityLogService.recordUserLogin(user.getCwid(), user.getUsername(), user.getRole());
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

    @GetMapping("/api/search")
    @ResponseBody
    public List<Map<String, Object>> searchApplications(@RequestParam String q, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return List.of();
        }
        return basicIdentityService.search(q).stream()
            .map(app -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id", app.getId());
                m.put("beatId", app.getBeatId() != null ? app.getBeatId() : "");
                m.put("applicationName", app.getApplicationName() != null ? app.getApplicationName() : "");
                return m;
            })
            .collect(Collectors.toList());
    }
}
