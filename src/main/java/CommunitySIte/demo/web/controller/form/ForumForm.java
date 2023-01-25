package CommunitySIte.demo.web.controller.form;

import CommunitySIte.demo.domain.ForumType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForumForm {
    @NotBlank(message = "포럼 명을 입력해주세요.")
    String forumName;
    ForumType forumType;
}
