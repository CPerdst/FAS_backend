package com.l1Akr.manager;


import com.l1Akr.common.utils.OssUtils;
import com.l1Akr.mapper.FileMapper;
import com.l1Akr.po.SampleBasePO;
import com.l1Akr.utils.ClamAVClient;
import com.l1Akr.utils.ClamAVScanner;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@Slf4j
public class SampleCheckManager {

    private final Integer EXECUTOR_THREAD_POOL_SIZE = 4;

    // 待查杀样本队列
    private final BlockingQueue<SampleBasePO>sampleQueue = new LinkedBlockingQueue<>(1000);

    // 线程池
    private final ExecutorService executorService = Executors.newFixedThreadPool(EXECUTOR_THREAD_POOL_SIZE);

    private final OssUtils ossUtils;

    private final FileMapper fileMapper;

    private final ClamAVScanner clamAVScanner;

    public SampleCheckManager(OssUtils ossUtils, FileMapper fileMapper, ClamAVScanner clamAVScanner) {
        this.ossUtils = ossUtils;
        this.fileMapper = fileMapper;
        this.clamAVScanner = clamAVScanner;
    }

    @PostConstruct
    public void init() {
        startAsyncProcess();
    }

    public void addSampleToQueue(SampleBasePO sampleBasePO) {
        try {
            if(!sampleQueue.offer(sampleBasePO)) {
                log.info("SampleQueue was full, sample {} waiting to check", sampleBasePO.getFilename());
                return;
            }
            // 更新到处理中的状态
            SampleBasePO.getToUpdateDisposeStatus(sampleBasePO.getId(), 2);
            fileMapper.updateSampleBySampleBasePo(sampleBasePO);
        } catch (Exception e) {
            log.error("Error occurred while adding sample to queue: {}", e.getMessage());
        }
    }

    // 异步查杀样本（多线程）
    public void startAsyncProcess() {
        for(int i = 0; i < EXECUTOR_THREAD_POOL_SIZE; i++) {
            executorService.submit(() -> {
               while(!Thread.currentThread().isInterrupted()) {
                   try {
                       SampleBasePO sampleBasePO = sampleQueue.take();
                       log.info("Sample {} is checking", sampleBasePO.getFilename());
                       processSample(sampleBasePO);
                   } catch (InterruptedException e) {
                       Thread.currentThread().interrupt();
                       log.info("Thread was interrupt");
                   } catch (Exception e) {
                       log.error("Error occurred while processing sample: {}", e.getMessage());
                   }
               }
            });
        }
    }

    public void processSample(SampleBasePO sampleBasePO) {
        if(ObjectUtils.isEmpty(sampleBasePO)) {
            log.info("sample was null");
            return;
        }
        String ossPath = sampleBasePO.getFilePath();
        if(StringUtils.isBlank(ossPath)) {
            log.info("sample's oss path was blank");
            return;
        }
        String relevantPath = ossPath.split("/", 4)[3];
        // 尝试下载样本
        try (InputStream sample = ossUtils.downloadFile(relevantPath)) {
            // 使用ClamAVScanner进行扫描
            ClamAVClient.ScanResult scan = clamAVScanner.scan(sample);
            log.info("sample {}:{}:{} dispose success: {}",
                    sampleBasePO.getFilename(),
                    sampleBasePO.getFileSize(),
                    sampleBasePO.getFileMd5(),
                    scan);
            if(ObjectUtils.isEmpty(scan)) {
                log.info("sample {}:{}:{} dispose failed",
                        sampleBasePO.getFilename(),
                        sampleBasePO.getFileSize(),
                        sampleBasePO.getFileMd5());
                return;
            }
            // 否则根据结果生成pdf


            // 更新样本查杀结果
            SampleBasePO toUpdateSample = SampleBasePO.getToUpdateDisposeStatus(sampleBasePO.getId(), scan.isInfected() ? 4 : 3);
            fileMapper.updateSampleBySampleBasePo(toUpdateSample);
        } catch (Exception e) {
            log.info("sample {}:{}:{} dispose failed as {}",
                    sampleBasePO.getFilename(),
                    sampleBasePO.getFileSize(),
                    sampleBasePO.getFileMd5(),
                    e.getMessage());
            // 更新样本处理失败信息
            SampleBasePO toUpdateSample = SampleBasePO.getToUpdateDisposeStatus(sampleBasePO.getId(), 4);
            fileMapper.updateSampleBySampleBasePo(toUpdateSample);
        }
    }

}
