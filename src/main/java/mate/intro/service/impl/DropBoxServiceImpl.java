package mate.intro.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import mate.intro.dto.dropbox.DropBoxResponseDto;
import mate.intro.model.Attachment;
import mate.intro.service.DropBoxService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DropBoxServiceImpl implements DropBoxService {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String TYPE = "application/octet-stream";
    private static final String DROPBOX_ARG = "Dropbox-API-Arg";
    @Value("${dropbox.token}")
    private String token;
    @Value("${upload.url}")
    private String uploadUrl;
    @Value("${download.url}")
    private String downloadUrl;
    @Value("${download.path}")
    private String downloadPath;
    private final ObjectMapper objectMapper;

    @Override
    public DropBoxResponseDto upload(String filePath, String fileName) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofFile(Path.of(filePath + fileName)))
                    .uri(URI.create(uploadUrl))
                    .header(AUTHORIZATION, BEARER + token)
                    .header(CONTENT_TYPE, TYPE)
                    .header(DROPBOX_ARG, buildUploadParams(fileName))
                    .build();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File is not found. Check path and name", e);
        }
        try {
            HttpResponse<String> response = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 409) {
                throw new RuntimeException("File with name " + fileName + " is already exist. "
                        + "Change filename");
            }
            return objectMapper.readValue(response.body(), DropBoxResponseDto.class);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void download(Attachment attachment) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(downloadUrl))
                .header(AUTHORIZATION, BEARER + token)
                .GET()
                .header(DROPBOX_ARG, buildDownloadParams(attachment.getDropboxFile()))
                .build();
        try (FileOutputStream fos = new FileOutputStream(downloadPath + attachment.getFileName())) {
            HttpResponse<InputStream> response = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofInputStream());
            fos.write(response.body().readAllBytes());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildDownloadParams(String fileId) {
        return "{" + "\"path\": \"" + fileId + "\"" + "}";
    }

    private String buildUploadParams(String filename) {
        return "{"
                + "\"autorename\": false,"
                + "\"mode\": \"add\","
                + "\"mute\": false,"
                + "\"path\": \"/"
                + filename + "\","
                + "\"strict_conflict\": false"
                + "}";
    }
}
