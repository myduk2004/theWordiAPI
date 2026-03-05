package theWordI.backend.domain.file.service;


import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ObjectStorage objectStorage;

    @Value("${oci.bucket-name}")
    private String bucketName;

    @Value("${oci.namespace}")
    private String namespace;

    @Value("${oci.region}")
    private String region;

    //이미지를 OCI Object Storage에 업로드하고 URL을 반환
    public String uploadImage(MultipartFile file) throws IOException
    {
        //1. 파일 이름 생성 (중복 방지를 위해 UUID + 원본파일명)
        String fileName = UUID.randomUUID().toString()
                + "_" + file.getOriginalFilename();

        //2. 업로드 요청 객체 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucketName)
                .objectName(fileName)
                .contentType(file.getContentType())
                .putObjectBody(file.getInputStream())
                .build();

        //3. OCI 서버 전송
        objectStorage.putObject(putObjectRequest);

        //4. 저장된 객체의 접근 URL 반환
        // 형식: https://objectstorage.{region}.oraclecloud.com/n/{namespace}/b/{bucket}/o/{objectName}

        return String.format("https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s",
                region, namespace, bucketName, fileName);
    }
}
