package mate.intro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Data
@SQLDelete(sql = "UPDATE comments SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
@Table(name = "comments")
@Accessors(chain = true)
public class Comment {
    private Long id;
    private Task task;
    private User user;
    private String text;
    private LocalDateTime timestamp;
    private boolean isDeleted;
}
