package mate.intro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Data
@SQLDelete(sql = "UPDATE labels SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
@Table(name = "labels")
@Accessors(chain = true)
public class Label {
    private Long id;
    private String name;
    private String color;
    private boolean isDeleted;
}
