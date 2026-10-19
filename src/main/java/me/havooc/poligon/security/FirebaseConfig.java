package me.havooc.poligon.security;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

    private final String firebaseCredentialsBase64;

    public FirebaseConfig(
            @Value("${firebase.credentials.base64}")
            String firebaseCredentialsBase64
    ) {
        this.firebaseCredentialsBase64 = firebaseCredentialsBase64;
    }

    @PostConstruct
    public void init() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }

        byte[] credentialsBytes;

        try {
            credentialsBytes = Base64.getDecoder()
                    .decode(firebaseCredentialsBase64);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    "FIREBASE_SERVICE_ACCOUNT_BASE64 contains invalid Base64",
                    e
            );
        }

        try (InputStream serviceAccount =
                     new ByteArrayInputStream(credentialsBytes)) {

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(
                            GoogleCredentials.fromStream(serviceAccount)
                    )
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }
}