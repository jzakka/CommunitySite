package CommunitySIte.demo.web.controller.problemcheck;

import CommunitySIte.demo.domain.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AccessibilityChecker {
    public static boolean canUserUpdateOrDelete(Users user, Crudible object) {
        if(isUserAdmin(user)) return true;
        else if(isAnonymousCrudObject(object)) return true;
        else if(isUserWriter(user, object)) return true;
        else return false;
    }

    public static boolean isUserWriter(Users user, Crudible object) {
        return user != null && object.getPostType() == PostType.NORMAL && object.getUser().equals(user);
    }

    public static boolean isAnonymousCrudObject(Crudible object) {
        return object.getPostType().equals(PostType.ANONYMOUS);
    }

    public static boolean isUserManagerOrAdmin(Users user, Forum forum) {
        if(isUserAdmin(user)) return true;
        List<ForumManager> forumManagers = forum.getForumManagers();
        for (ForumManager forumManager : forumManagers) {
            if (forumManager.getUser().equals(user)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isUserAdmin(Users user) {
        return user != null && user.getUserType() == UserType.ADMIN;
    }
}
