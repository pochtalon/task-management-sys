package mate.intro.repository;

import java.util.List;
import mate.intro.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(value = "FROM Project p JOIN Task t on p = t.project "
            + "JOIN User u on u = t.assignee WHERE u.id = :id")
    List<Project> findByUserId(@Param("id") Long id);
}
