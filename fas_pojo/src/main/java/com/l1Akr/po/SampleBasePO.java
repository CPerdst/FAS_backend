package com.l1Akr.po;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Data
public class SampleBasePO {
    int id; // 主键
    String filename; // 样本名
    String fileExt; // 文件后缀
    String filePath; // Oss路径
    long fileSize; // 文件大小
    String fileMd5; // 文件md5
    int fileType; // 文件类型
    String fileDesc; // 样本描述
    LocalDate createTime; // 创建时间
    LocalDate updateTime; // 更新时间
    String expiredTime; // 样本过期时间
    int downloadTimes; // 下载次数
    int permission; // 访问权限

    void parseByFile(MultipartFile file) {
        this.filename = file.getOriginalFilename();
        if (this.filename != null) {
            this.fileExt = this.filename.substring(this.filename.lastIndexOf("."));
        }
        this.fileSize = file.getSize();
        fileType = parseFileType(this.fileExt);
        createTime = LocalDate.now();
        updateTime = LocalDate.now();
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
            default -> -1;
        };
    }
}
