package com.core.market.trade.app;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadService {

    private final Storage storage;

    @Value("${spring.cloud.gcp.storage.bucket}")
    private String bucketName;

    @Value("${spring.cloud.gcp.storage.url}")
    private String storagePath;

    public List<String> uploadImagesInStorage(List<MultipartFile> files) {
        List<String> imageUrlList = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            String ext = file.getContentType();
            String uuid = UUID.randomUUID().toString();

            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, uuid)
                    .setContentType(ext)
                    .build();

            try {
                Blob blob = storage.create(blobInfo, file.getInputStream());
                imageUrlList.add(storagePath + uuid);
            } catch (IOException e) {
                log.warn("image Upload fail ");
            }

        }
        return imageUrlList;
    }

}
