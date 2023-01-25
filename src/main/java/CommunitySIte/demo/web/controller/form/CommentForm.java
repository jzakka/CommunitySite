package CommunitySIte.demo.web.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentForm implements UsernamePasswordRequiredForm {

    private String username;
    private String password;
    @NotBlank(message = "내용을 입력하세요.")
    private String content;
}
