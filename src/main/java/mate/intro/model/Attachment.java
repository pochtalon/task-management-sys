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
@SQLDelete(sql = "UPDATE attachments SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
@Table(name = "attachtments")
@Accessors(chain = true)
public class Attachment {
    private Long id;
    private Task task;
    private String dropboxFile;
    private String fileName;
    private LocalDateTime uploadDate;
    private boolean isDeleted;
}
