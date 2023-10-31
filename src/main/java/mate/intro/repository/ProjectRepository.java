package mate.intro.repository;

import mate.intro.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query(value = "SELECT p FROM project p LEFT JOIN FETCH task.p LEFT JOIN FETCH task.user WHERE user.id = :id")
    List<Project> findByUserId(@Param("id") Long id);
}
