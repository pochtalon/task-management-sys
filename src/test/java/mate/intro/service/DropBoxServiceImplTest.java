package mate.intro.service;

import mate.intro.service.impl.DropBoxServiceImpl;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DropBoxServiceImplTest {
    @InjectMocks
    DropBoxServiceImpl dropBoxService;
}