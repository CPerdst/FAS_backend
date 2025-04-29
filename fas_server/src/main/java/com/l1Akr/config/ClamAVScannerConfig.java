package com.l1Akr.config;

import com.l1Akr.utils.ClamAVScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ClamAVScannerConfig {

    @Value("${clamav.host}")
    private String clamavHost;

    @Value("${clamav.port}")
    private String clamavPort;

    @Bean
    public ClamAVScanner clamAVScanner() {
        ClamAVScanner clamAVScanner =new ClamAVScanner.Builder()
                .host(clamavHost)
                .port(Integer.parseInt(clamavPort))
                .build();
        log.info("ClamAVScanner initialized with host: {}, port: {}", clamavHost, clamavPort);
        return clamAVScanner;
    }

}
