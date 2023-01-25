package CommunitySIte.demo.domain;

import java.time.LocalDateTime;

public interface Crudible {
    PostType getPostType();

    String getPassword();

    String getAnonymousUserName();

    String getContent();

    LocalDateTime getLastModifiedDate();

    Users getUser();
}
