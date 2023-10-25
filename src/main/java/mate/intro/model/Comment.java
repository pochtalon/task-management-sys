package mate.intro.model;

import java.time.LocalDateTime;

public class Comment {
    private Long id;
    private Task task;
    private User user;
    private String text;
    private LocalDateTime timestamp;
    private boolean isDeleted;
}
