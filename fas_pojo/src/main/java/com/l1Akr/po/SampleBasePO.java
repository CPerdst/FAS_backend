package com.l1Akr.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SampleBasePO {
    Integer id; // 主键
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
    int permission; // 访问权限 默认0

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
