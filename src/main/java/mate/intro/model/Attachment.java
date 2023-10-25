package mate.intro.model;

import java.time.LocalDateTime;

public class Attachment {
    private Long id;
    private Task task;
    private String dropboxFile;
    private String fileName;
    private LocalDateTime uploadDate;
    private boolean isDeleted;
}
