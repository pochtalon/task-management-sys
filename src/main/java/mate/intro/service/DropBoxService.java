package mate.intro.service;

import mate.intro.dto.dropbox.DropBoxResponseDto;
import mate.intro.model.Attachment;

public interface DropBoxService {
    DropBoxResponseDto upload(String filePath, String fileName);

    void download(Attachment attachment);
}
