package com.l1Akr.manager;


import com.l1Akr.common.util.PDFReportGenerator;
import com.l1Akr.common.util.OssUtils;
import com.l1Akr.common.util.ShaUtils;
import com.l1Akr.mapper.FileMapper;
import com.l1Akr.mapper.UserSampleMappingMapper;
import com.l1Akr.pojo.po.SampleBasePO;
import com.l1Akr.pojo.po.UserSampleMappingPO;
import com.l1Akr.utils.ClamAVClient;
import com.l1Akr.utils.ClamAVScanner;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
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

    private final UserSampleMappingMapper userSampleMappingMapper;

    private final ShaUtils shaUtils = new ShaUtils();

    public SampleCheckManager(OssUtils ossUtils, 
        FileMapper fileMapper, 
        ClamAVScanner clamAVScanner,
        UserSampleMappingMapper userSampleMappingMapper) {
        this.ossUtils = ossUtils;
        this.fileMapper = fileMapper;
        this.clamAVScanner = clamAVScanner;
        this.userSampleMappingMapper = userSampleMappingMapper;
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
            ClamAVClient.ScanResult scan = null;
            try {
                scan = clamAVScanner.scan(sample);
            } catch (Exception e) {
                log.info("sample {}:{}:{} scan failed as {}",
                        sampleBasePO.getFilename(),
                        sampleBasePO.getFileSize(),
                        sampleBasePO.getFileMd5(),
                        e.getMessage());
                // 更新样本处理失败信息
                SampleBasePO toUpdateSample = SampleBasePO.getToUpdateDisposeStatus(sampleBasePO.getId(), 5);
                fileMapper.updateSampleBySampleBasePo(toUpdateSample);
                return;
            }
            
            log.info("sample {}:{}:{} dispose success: {}",
                    sampleBasePO.getFilename(),
                    sampleBasePO.getFileSize(),
                    sampleBasePO.getFileMd5(),
                    scan);

            byte[] reportByte = PDFReportGenerator.generateReport(sampleBasePO, scan);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(reportByte);
            // 上传pdf到oss
            try {
                // 首先获取样本的上传用户
                UserSampleMappingPO userSampleMappingByUserIdAndSampleId = userSampleMappingMapper.getUserSampleMappingByUserIdAndSampleId(null, sampleBasePO.getId(), 1);;
                String uploadPDFReportPath = ossUtils.uploadPDFReport(
                                    byteArrayInputStream, 
                                    ossUtils.generateUniqueFileNameForPDFReport(
                                        sampleBasePO, 
                                        userSampleMappingByUserIdAndSampleId.getUserId().toString()
                                        )
                                    );
                // 更新样本的pdf路径，并同时更新样本查杀结果
                SampleBasePO toUpdateSample = new SampleBasePO();
                toUpdateSample.setId(sampleBasePO.getId());
                toUpdateSample.setPdfPath(uploadPDFReportPath);
                toUpdateSample.setPdfSize(Long.valueOf(reportByte.length));
                toUpdateSample.setPdfMd5(shaUtils.MD5(byteArrayInputStream));
                toUpdateSample.setPdfCreateTime(LocalDateTime.now());
                toUpdateSample.setPdfDownloadTimes(0);
                toUpdateSample.setDisposeStatus(scan.isInfected() ? 4 : 3);
                fileMapper.updateSampleBySampleBasePo(toUpdateSample);
                log.info("sample {}:{}:{} pdf upload success",
                        sampleBasePO.getFilename(),
                        sampleBasePO.getFileSize(),
                        sampleBasePO.getFileMd5());
            } catch (Exception e) {
                log.info("sample {}:{}:{} pdf upload failed as {}",
                        sampleBasePO.getFilename(),
                        sampleBasePO.getFileSize(),
                        sampleBasePO.getFileMd5(),
                        e.getMessage());
                // 更新样本处理失败信息
                SampleBasePO toUpdateSample = SampleBasePO.getToUpdateDisposeStatus(sampleBasePO.getId(), 5);
                fileMapper.updateSampleBySampleBasePo(toUpdateSample);
                return;
            }
        } catch (Exception e) {
            log.info("sample {}:{}:{} dispose failed as {}",
                    sampleBasePO.getFilename(),
                    sampleBasePO.getFileSize(),
                    sampleBasePO.getFileMd5(),
                    e.getMessage());
            // 更新样本处理失败信息
            SampleBasePO toUpdateSample = SampleBasePO.getToUpdateDisposeStatus(sampleBasePO.getId(), 5);
            fileMapper.updateSampleBySampleBasePo(toUpdateSample);
        }
    }

}
