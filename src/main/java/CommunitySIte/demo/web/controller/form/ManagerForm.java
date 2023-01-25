package CommunitySIte.demo.web.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerForm {
    @NotBlank(message = "매니저를 설정할 유저 ID를 입력해주세요")
    String userId;
}