package CommunitySIte.demo.web.controller.problemcheck;

import CommunitySIte.demo.domain.Crudible;
import CommunitySIte.demo.domain.Post;
import CommunitySIte.demo.domain.PostType;
import CommunitySIte.demo.domain.Users;
import CommunitySIte.demo.web.controller.form.PostUpdateForm;
import CommunitySIte.demo.web.controller.form.UsernamePasswordRequiredForm;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorChecker {

    public static boolean updateErrorCheck(Users user, PostUpdateForm postForm, BindingResult bindingResult, Post post) {
        //유동 글 비밀번호 검증
        if (post.getPostType() == PostType.ANONYMOUS) {
            passwordErrorBind(postForm.getPassword(), bindingResult, post);
        }
        //일반 글 사용자 검증
        else {
            if (!(post.getUser() == null || post.getUser().equals(user))) {
                //리다이렉트 말고 오류메시지나 오류창으로 넘어가게 만들기
                return false;
            }
        }
        return true;
    }

    public static void passwordErrorBind(String password, BindingResult bindingResult, Crudible crudObject) {
        if (!StringUtils.hasText(password) || (crudObject.getPassword() != null && !crudObject.getPassword().equals(password))) {
            bindingResult.rejectValue("password","password" ,"비밀번호를 올바르게 입력해주세요");
        }
    }

    public static String getNextUrlByBindingError(Users user, PostUpdateForm postForm, BindingResult bindingResult, Post post) {
        boolean updatable = updateErrorCheck(user, postForm, bindingResult, post);
        if (!updatable) {
            return  "redirect:/forum/" + post.getForum().getId() + "/post/{postId}";
        }
        if (bindingResult.hasErrors()) {
            return  "posts/updateForm";
        }
        return null;
    }

    public static void anonymousErrorBinding(UsernamePasswordRequiredForm form, BindingResult bindingResult, Users loginUser) {
        //일반 사용자냐 유동이냐에 따라 투고 작성자 이름이 달라야함
        //일반 사용자
        //유동 사용자
        if (loginUser == null) {
            if (!StringUtils.hasText(form.getUsername())) {
                bindingResult.rejectValue("username", "username", "비회원은 이름기입이 필수입니다.");
            }
            if (!StringUtils.hasText(form.getPassword())) {
                bindingResult.rejectValue("password", "password", "비회원은 비밀번호기입이 필수입니다.");
            }
        }
    }

    public static String getNextUrlByBindingError(BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            /**
             *찾아봤는데 컨트롤러는 호출 될 때마다 bindingResult를 덮어씌우는 듯
             * 그래서 ForumController로 가도 bindingResult의 결과는 다시 덮어씌워지나봄'
             * 실제 구글 검색해봐도 같은 컨트롤러 내에서의 예시만 있고 다른 컨트롤러로 가는 건 없다
             * 결국 해시맵에 에러 다시 담아서 리다이렉트...
             */
            //------개선 필요---------
            Map<String, String> errors = new HashMap<>();

            if (bindingResult.hasFieldErrors("title")) {
                errors.put("title", bindingResult.getFieldError("title").getDefaultMessage());
            }
            if (bindingResult.hasFieldErrors("username")) {
                errors.put("username", bindingResult.getFieldError("username").getDefaultMessage());
            }
            if (bindingResult.hasFieldErrors("password")) {
                errors.put("password", bindingResult.getFieldError("password").getDefaultMessage());
            }
            if (bindingResult.hasFieldErrors("content")) {
                errors.put("content", bindingResult.getFieldError("content").getDefaultMessage());
            }

            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:/forum/{forumId}";
        }
        return null;
    }
}
