package mate.intro.service;

import mate.intro.mapper.LabelMapper;
import mate.intro.repository.LabelRepository;
import mate.intro.repository.TaskRepository;
import mate.intro.service.impl.LabelServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LabelServiceImplTest {
    @InjectMocks
    LabelServiceImpl labelService;
    @Mock
    private LabelRepository labelRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private LabelMapper labelMapper;
}