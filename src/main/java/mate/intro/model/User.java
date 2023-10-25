package mate.intro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;
import java.util.Set;

@Entity
@Data
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id=?")
@Where(clause = "is_deleted=false")
@Table(name = "users")
@Accessors(chain = true)
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private List<Task> tasks;
    private List<Comment> comments;
    private Set<Role> roles;
    private boolean isDeleted;
}
