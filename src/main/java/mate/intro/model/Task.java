package mate.intro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Data
@SQLDelete(sql = "UPDATE tasks SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
@Table(name = "tasks")
@Accessors(chain = true)
public class Task {
    private Long id;
    private String name;
    private String description;
    private Priority priority;
    private Status status;
    private LocalDate dueDate;
    private Project project;
    private User assignee;
    private Set<Label> labels;
    private Set<Attachment> attachments;
    private List<Comment> comments;
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
