package CommunitySIte.demo.web.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginForm {
    @NotBlank(message = "아이디를 입력하세요")
    private String loginId;
    @NotBlank(message = "비밀번호를 입력하세요")
    private String password;
}
