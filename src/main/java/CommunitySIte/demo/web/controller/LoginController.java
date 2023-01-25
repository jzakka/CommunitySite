package CommunitySIte.demo.web.controller;

import CommunitySIte.demo.domain.Users;
import CommunitySIte.demo.service.LoginService;
import CommunitySIte.demo.web.SessionConst;
import CommunitySIte.demo.web.controller.form.LoginForm;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final LoginService loginService;

    @GetMapping("/login")
    public String loginForm(@ModelAttribute LoginForm loginForm, HttpServletRequest request) {
        //이전 페이지로 돌아가기 위해 이전 페이지 저장
        String referer = request.getHeader("referer");
        if (referer != null && !referer.contains("/login")) {
            request.getSession().setAttribute("prevPage", referer);
        }

        return "login/loginForm";
    }

    @PostMapping("/login")
    public String login(@Validated @ModelAttribute(name = "loginForm") LoginForm loginForm,
                        BindingResult bindingResult,
                        @RequestParam(defaultValue = "/") String redirectURL,
                        HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        Users loginUser = loginService.login(loginForm.getLoginId(), loginForm.getPassword());

        if (loginUser == null) {
            bindingResult.reject("loginFail", "아이디/패스워드가 올바르지 않습니다.");
            return "login/loginForm";
        }

        HttpSession session = request.getSession();
        session.setAttribute(SessionConst.LOGIN_USER, loginUser);
//        session.setAttribute(SessionConst.USER_TYPE, loginUser.getUserType());

        String prevPage = (String) request.getSession().getAttribute("prevPage");
        if(prevPage!=null) request.getSession().removeAttribute("prevPage");

        return "redirect:" + prevPage;
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session!=null) {
            session.invalidate();
        }
        return "redirect:" + request.getHeader("referer");
    }
}
