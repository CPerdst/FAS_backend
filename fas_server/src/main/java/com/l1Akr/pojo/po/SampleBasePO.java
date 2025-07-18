package com.l1Akr.pojo.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleBasePO {
    Integer id; // 主键
    String filename; // 样本名
    String fileExt; // 文件后缀
    String filePath; // Oss路径
    Long fileSize; // 文件大小
    String fileMd5; // 文件md5
    Integer fileType; // 文件类型
    String fileDesc; // 样本描述
    LocalDateTime createTime; // 创建时间
    LocalDateTime updateTime; // 更新时间
    LocalDateTime expiredTime; // 样本过期时间
    Integer downloadTimes; // 下载次数
    Integer permission; // 访问权限 默认1-私有 2-公开
    Integer disposeStatus; // 样本处理状态 1-未处理 2-处理中 3-未发现病毒 4-发现病毒 5-处理失败
    String pdfPath; // pdf报告OSS路径
    String pdfMd5; // pdf报告md5
    Long pdfSize; // pdf报告大小
    LocalDateTime pdfCreateTime; // pdf报告创建时间
    Integer pdfDownloadTimes; // pdf报告下载次数

    public void parseByFile(MultipartFile file) {
        this.filename = file.getOriginalFilename();
        if (this.filename != null) {
            int idx = this.filename.lastIndexOf(".");
            if (idx != -1) {
                this.fileExt = this.filename.substring(this.filename.lastIndexOf(".") + 1);
            } else {
                this.fileExt = "";
            }
        }
        this.fileSize = file.getSize();
        fileType = parseFileType(this.fileExt);
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    public static SampleBasePO getToUpdateDisposeStatus(int id, int disposeStatus) {
        SampleBasePO sampleBasePO = new SampleBasePO();
        sampleBasePO.setId(id);
        sampleBasePO.setDisposeStatus(disposeStatus);
        sampleBasePO.setUpdateTime(LocalDateTime.now());
        return sampleBasePO;
    }

    private int parseFileType(String fileExt) {
        return switch (fileExt) {
            case ".txt" -> 1;
            case ".pdf" -> 2;
            case ".doc" -> 3;
            case ".docx" -> 4;
            case ".ppt" -> 5;
            case ".pptx" -> 6;
            case ".xls" -> 7;
            case ".xlsx" -> 8;
            case ".jpg" -> 9;
            case ".png" -> 10;
            case ".mp4" -> 11;
            case ".avi" -> 12;
            case ".mp3" -> 13;
            case ".zip" -> 14;
            case ".rar" -> 15;
            case ".7z" -> 16;
            case ".gz" -> 17;
            case ".tar" -> 18;
            case ".iso" -> 19;
            case ".exe" -> 20;
            case ".apk" -> 21;
            case ".jar" -> 22;
            case ".deb" -> 23;
            default -> -1;
        };
    }
}
