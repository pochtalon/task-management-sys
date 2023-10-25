package mate.intro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@SQLDelete(sql = "UPDATE projects SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
@Table(name = "projects")
@Accessors(chain = true)
public class Project {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private List<Task> tasks;
    private boolean isDeleted;

    public enum Status {
        INITIATED,
        IN_PROGRESS,
        COMPLETED
    }
}
