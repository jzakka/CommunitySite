package CommunitySIte.demo.web.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryForm {

    @NotBlank(message = "카테고리명을 입력하세요")
    private String categoryName;
}
