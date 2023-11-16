package mate.intro.service;

import mate.intro.mapper.TaskMapper;
import mate.intro.repository.ProjectRepository;
import mate.intro.repository.TaskRepository;
import mate.intro.repository.UserRepository;
import mate.intro.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {
    @InjectMocks
    TaskServiceImpl taskService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskMapper taskMapper;
}