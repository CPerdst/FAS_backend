package com.l1Akr.manager;


import com.l1Akr.common.util.PDFReportGenerator;
import com.l1Akr.common.util.OssUtils;
import com.l1Akr.common.util.ShaUtils;
import com.l1Akr.pojo.dao.mapper.FileMapper;
import com.l1Akr.pojo.dao.mapper.UserSampleMappingMapper;
import com.l1Akr.pojo.dto.TaskWithUserSampleDTO;
import com.l1Akr.pojo.po.SampleBasePO;
import com.l1Akr.pojo.po.UserSampleMappingPO;
import com.l1Akr.utils.ClamAVClient;
import com.l1Akr.utils.ClamAVScanner;
import com.l1Akr.websocket.handler.SampleStatusWebSocketHandler;
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
    private final BlockingQueue<TaskWithUserSampleDTO>sampleQueue = new LinkedBlockingQueue<>(1000);

    // 线程池
    private final ExecutorService executorService = Executors.newFixedThreadPool(EXECUTOR_THREAD_POOL_SIZE);

    private final OssUtils ossUtils;

    private final FileMapper fileMapper;

    private final ClamAVScanner clamAVScanner;

    private final UserSampleMappingMapper userSampleMappingMapper;

    private final ShaUtils shaUtils = new ShaUtils();

    private final SampleStatusWebSocketHandler sampleStatusWebSocketHandler;

    public SampleCheckManager(OssUtils ossUtils, 
        FileMapper fileMapper, 
        ClamAVScanner clamAVScanner,
        UserSampleMappingMapper userSampleMappingMapper,
        SampleStatusWebSocketHandler sampleStatusWebSocketHandler) {
        this.ossUtils = ossUtils;
        this.fileMapper = fileMapper;
        this.clamAVScanner = clamAVScanner;
        this.userSampleMappingMapper = userSampleMappingMapper;
        this.sampleStatusWebSocketHandler = sampleStatusWebSocketHandler;
    }

    @PostConstruct
    public void init() {
        startAsyncProcess();
    }

    public void addSampleToQueue(TaskWithUserSampleDTO taskWithUserSampleDTO) {
        try {
            if(!sampleQueue.offer(taskWithUserSampleDTO)) {
                log.info("SampleQueue was full, sample {} waiting to check", taskWithUserSampleDTO.getSampleBasePO().getFilename());
                return;
            }
            // 更新到处理中的状态
            SampleBasePO.getToUpdateDisposeStatus(taskWithUserSampleDTO.getSampleBasePO().getId(), 2);
            fileMapper.updateSampleBySampleBasePo(taskWithUserSampleDTO.getSampleBasePO());
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
                       TaskWithUserSampleDTO taskWithUserSampleDTO = sampleQueue.take();
                       SampleBasePO sampleBasePO = taskWithUserSampleDTO.getSampleBasePO();
                       Integer userId = taskWithUserSampleDTO.getUserId();
                       // 首先通知用户当前文件的状态为 检测中-2
                       sampleStatusWebSocketHandler.notifyUserWithNewStatus(userId, sampleBasePO.getId(), 2);
                       log.info("Sample {} is checking", sampleBasePO.getFilename());
                       processSample(userId, sampleBasePO);
                   } catch (InterruptedException e) {
                       log.info("Thread was interrupt");
                       Thread.currentThread().interrupt();
                   } catch (Exception e) {
                       log.error("Error occurred while processing sample: {}", e.getMessage());
                   }
               }
            });
        }
    }

    public void processSample(Integer userId, SampleBasePO sampleBasePO) {
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
                // 首先通知用户当前文件的状态为 安全-3 发现病毒-4
                sampleStatusWebSocketHandler.notifyUserWithNewStatus(userId, sampleBasePO.getId(), scan.isInfected() ? 4 : 3);
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
