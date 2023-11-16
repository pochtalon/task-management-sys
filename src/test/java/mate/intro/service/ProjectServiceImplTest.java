package mate.intro.service;

import mate.intro.mapper.ProjectMapper;
import mate.intro.mapper.TaskMapper;
import mate.intro.repository.ProjectRepository;
import mate.intro.repository.TaskRepository;
import mate.intro.service.impl.ProjectServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {
    @InjectMocks
    ProjectServiceImpl projectService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectMapper projectMapper;
    @Mock
    private TaskMapper taskMapper;
}