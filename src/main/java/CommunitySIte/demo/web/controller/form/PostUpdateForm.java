package CommunitySIte.demo.web.controller.form;

import CommunitySIte.demo.domain.PostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateForm {
    @NotNull(message = "임의 변경 불가")
    private Long id;
    @NotBlank(message = "제목을 입력하세요.")
    private String title;
    private PostType postType;
    private String password;
    private MultipartFile imageFile;
    private String uploadFileName;
    private String storeFileName;
    @NotBlank(message = "내용을 입력하세요")
    private String content;

    public PostUpdateForm(String title, String content, PostType postType, String uploadFileName, String storeFileName) {
        setTitle(title);
        setContent(content);
        setPostType(postType);
        setUploadFileName(uploadFileName);
        setStoreFileName(storeFileName);
    }
}
