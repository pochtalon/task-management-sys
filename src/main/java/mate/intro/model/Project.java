package mate.intro.model;

import java.time.LocalDate;

public class Project {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private boolean isDeleted;

    public enum Status {
        INITIATED,
        IN_PROGRESS,
        COMPLETED
    }
}
