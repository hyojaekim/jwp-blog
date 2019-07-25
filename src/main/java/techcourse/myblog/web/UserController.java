package techcourse.myblog.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import techcourse.myblog.domain.User;
import techcourse.myblog.dto.UserDto;
import techcourse.myblog.dto.UserUpdateDto;
import techcourse.myblog.service.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static techcourse.myblog.web.ControllerUtil.putLoginUser;

@Controller
public class UserController {
    private static final String LOGIN_SESSION_KEY = "loginUser";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public String createUser(@Valid UserDto userDto) {
        userService.save(userDto);
        return "redirect:/login";
    }

    @GetMapping("/signup")
    public String signUpView() {
        return "signup";
    }

    @GetMapping("/users")
    public String userListView(HttpSession session, Model model) {
        putLoginUser(session, model);
        model.addAttribute("userList", userService.getUserList());
        return "user-list";
    }

    @GetMapping("/mypage")
    public String myPageView(HttpSession session, Model model) {
        model.addAttribute(LOGIN_SESSION_KEY, session.getAttribute(LOGIN_SESSION_KEY));
        return "mypage";
    }

    @GetMapping("/mypage-edit")
    public String editMyPageView(HttpSession session, Model model) {
        model.addAttribute(LOGIN_SESSION_KEY, session.getAttribute(LOGIN_SESSION_KEY));
        return "mypage-edit";
    }

    @PutMapping("/mypage-edit")
    public String updateUser(@Valid UserUpdateDto userUpdateDto, HttpSession session) {
        User user = (User) session.getAttribute(LOGIN_SESSION_KEY);
        userService.update(user, userUpdateDto);
        return "redirect:/mypage";
    }

    @DeleteMapping("/withdrawal")
    public String withdrawalUser(HttpSession session) {
        userService.delete(session);
        return "redirect:/";
    }
}