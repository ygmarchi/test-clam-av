package it.bmw.clamav;

import io.sensesecure.clamav4j.ClamAV;
import io.sensesecure.clamav4j.ClamAVException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test di base per il ClamAV Client
 */
@SpringBootTest(classes = ClamAVClientTest.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ClamAVClientTest {

    public static final int TIMEOUT = 60 * 1000;
    private static final Duration CLAM_READY_TIMEOUT = Duration.ofMinutes(3);
    private ClamAV client;

    @SpringBootConfiguration
    static class TestConfig {
        // Configurazione minima richiesta da @SpringBootTest.
    }

    @BeforeAll
    static void ensureClamAvIsReady() throws InterruptedException {
        long deadline = System.currentTimeMillis() + CLAM_READY_TIMEOUT.toMillis();
        while (System.currentTimeMillis() < deadline) {
            ClamAV probeClient = new ClamAV(new InetSocketAddress("localhost", 3310), TIMEOUT);
            if (probeClient.ping()) {
                return;
            }
            Thread.sleep(3000);
        }
        throw new IllegalStateException("ClamAV non pronto entro il timeout: " + CLAM_READY_TIMEOUT);
    }

    @BeforeEach
    public void setUp() {
        InetSocketAddress localhost = new InetSocketAddress("localhost", 3310);
        client = new ClamAV(localhost, TIMEOUT);
    }

    @Test
    public void testEicarFiles() throws IOException, ClamAVException {
        assertTrue(client.ping(), "ClamAV deve essere raggiungibile prima dei test manuali");

        Map<String, String> readmeFiles = new LinkedHashMap<>();
        readmeFiles.put("eicar.com", "https://secure.eicar.org/eicar.com");
        readmeFiles.put("eicar.com.txt", "https://secure.eicar.org/eicar.com.txt");
        readmeFiles.put("eicar_com.zip", "https://secure.eicar.org/eicar_com.zip");
        readmeFiles.put("eicar_com2.zip", "https://secure.eicar.org/eicar_com2.zip");

        int infectedFiles = 0;

        for (Map.Entry<String, String> entry : readmeFiles.entrySet()) {
            String scanResult = scanRemoteFile(entry.getValue());
            assertEquals("Eicar-Test-Signature", scanResult, "Il file " + entry.getKey() + " deve risultare infetto, risultato: " + scanResult);
            infectedFiles++;
        }

        assertEquals(4, infectedFiles, "Devono risultare 4 file infetti come nel README");
    }

    private String scanRemoteFile(String url) throws IOException, ClamAVException {
        try (java.io.InputStream inputStream = URI.create(url).toURL().openStream()) {
            return client.scan(inputStream);
        }
    }

    @Test
    public void testNotViralFile() throws IOException, ClamAVException {
        assertTrue(client.ping(), "ClamAV deve essere raggiungibile prima dei test manuali");

        try (InputStream inputStram = new ByteArrayInputStream("Hello, this is a clean file.".getBytes())) {
            String cleanScanResult = client.scan(inputStram);
            assertEquals("OK", cleanScanResult, "Il file pulito deve risultare OK, risultato: " + cleanScanResult);
        }
    }


}

