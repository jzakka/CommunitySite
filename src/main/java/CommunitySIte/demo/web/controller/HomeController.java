package CommunitySIte.demo.web.controller;

import CommunitySIte.demo.domain.Forum;
import CommunitySIte.demo.domain.Users;
import CommunitySIte.demo.service.ForumService;
import CommunitySIte.demo.web.argumentresolver.Login;
import CommunitySIte.demo.web.controller.form.LoginForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ForumService forumService;

    @ModelAttribute("forums")
    public List<Forum> forums() {
        return forumService.getForums();
    }

    @ModelAttribute("user")
    public Users user(@Login Users loginUser) {
        return loginUser;
    }

    @GetMapping("/")
    public String home() {
        return "home";
    }
}
