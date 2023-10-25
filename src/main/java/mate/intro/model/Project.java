package mate.intro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    private Status status;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "project")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Task> tasks;
    @Column(nullable = false, name = "is_deleted")
    private boolean isDeleted = false;

    public enum Status {
        INITIATED,
        IN_PROGRESS,
        COMPLETED
    }
}
