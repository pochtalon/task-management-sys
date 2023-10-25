package mate.intro.model;

import java.time.LocalDate;

public class Task {
    private Long id;
    private String name;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDate dueDate;
    private Project project;
    private User assignee;
    private boolean isDeleted;

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }

    public enum Status {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }
}
