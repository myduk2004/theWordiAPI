package theWordI.backend.config;


import com.oracle.bmc.Region;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.StringPrivateKeySupplier;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

//사용안함
//@Configuration
public class OciConfig {
/*
    @Value("${oci.user}") private String user;
    @Value("${oci.fingerprint}") private String fingerprint;
    @Value("${oci.tenancy}") private String tenancy;
    @Value("${oci.region}") private String region;
    @Value("${oci.private-key-path}") private String privateKeyPath;


    @Bean
    public ObjectStorage objectStorage() throws IOException {

        //1. .pem 파일의 내용을 읽어옴
        String privateKey = Files.readString(Paths.get(privateKeyPath));

        //2. 인증 공급자 구성
        SimpleAuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder()
                .userId(user)
                .fingerprint(fingerprint)
                .tenantId(tenancy)
                .region(Region.fromRegionId(region))
                .privateKeySupplier(new StringPrivateKeySupplier(privateKey))
                .build();

        return ObjectStorageClient.builder()
                .build(provider);
    }
*/
}
