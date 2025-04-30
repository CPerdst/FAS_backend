package com.l1Akr.common.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import com.l1Akr.pojo.po.SampleBasePO;
import com.l1Akr.utils.ClamAVClient.ScanResult;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class PDFReportGenerator {
    
    // 页面边距和样式常量
    private static final float MARGIN = 60;
    private static final float LINE_SPACING = 18;
    private static final Color HEADER_COLOR = new Color(0, 76, 153);
    private static final Color WARNING_COLOR = new Color(204, 0, 0);
    private static final Color SAFE_COLOR = new Color(0, 153, 51);
    private static final String VERSION = "v1.0";
    // 添加中文字体配置
    private static PDType0Font chineseFontBold;
    private static PDType0Font chineseFontRegular;
    private static boolean fontsInitialized = false;

    public static byte[] generateReport(SampleBasePO sample, ScanResult result) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (PDDocument document = new PDDocument()) {
            // 延迟加载字体（每个文档实例只需加载一次）
            try {
                loadChineseFonts(document);
            } catch (IOException e) {
                throw new IOException("字体加载失败，请检查：\n"
                    + "1. resources/fonts/ 目录是否存在\n"
                    + "2. OTF字体文件名是否正确\n"
                    + "3. 字体文件是否被Maven过滤", e);
            }
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                // 设置初始绘制位置
                float yPosition = PDRectangle.A4.getHeight() - MARGIN;
                
                // 添加标题
                drawTitle(contentStream, "文件安全检测报告", yPosition);
                yPosition -= 50;

                // 基本信息表格
                yPosition = drawSectionTitle(contentStream, "样本基本信息", yPosition);
                yPosition = drawInfoTable(contentStream, sample, yPosition);
                yPosition -= LINE_SPACING * 2;

                // 检测结果表格
                yPosition = drawSectionTitle(contentStream, "安全检测结果", yPosition);
                yPosition = drawScanResult(contentStream, result, yPosition);
                yPosition -= LINE_SPACING * 2;

                // 添加页脚
                drawFooter(contentStream, "报告生成时间: " + 
                    result.getTimestamp().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " 版本: " + VERSION);
            }
            document.save(outputStream);
        }
        return outputStream.toByteArray();
    }

    // 添加同步字体加载方法
    @SuppressWarnings("null")
    private static synchronized void loadChineseFonts(PDDocument document) throws IOException {
        // try {
        //     ClassLoader cl = Thread.currentThread().getContextClassLoader();
            
        //     // 诊断点1：验证资源路径
        //     URL regularUrl = cl.getResource("fonts/SourceHanSansSC-Regular.otf");
        //     URL boldUrl = cl.getResource("fonts/SourceHanSansSC-Bold.otf");
        //     System.out.println("[DEBUG] Regular字体路径: " + (regularUrl != null ? regularUrl.toExternalForm() : "null"));
        //     System.out.println("[DEBUG] Bold字体路径: " + (boldUrl != null ? boldUrl.toExternalForm() : "null"));

        //     // 诊断点2：验证文件可读性
        //     if (regularUrl != null) {
        //         try (InputStream testStream = regularUrl.openStream()) {
        //             System.out.println("[DEBUG] Regular字体文件可读，大小: " + testStream.available() + " bytes");
        //         }
        //     }
        //     if (boldUrl != null) {
        //         try (InputStream testStream = boldUrl.openStream()) {
        //             System.out.println("[DEBUG] Bold字体文件可读，大小: " + testStream.available() + " bytes");
        //         }
        //     }

        //     // 实际加载（使用绝对路径保险方案）
        //     // chineseFontRegular = PDType0Font.load(document, regularUrl.openStream());
        //     chineseFontRegular = PDType0Font.load(document, 
        //         regularUrl.openStream(),
        //         true // 启用严格模式
        //     );
        //     chineseFontBold = PDType0Font.load(document, boldUrl.openStream(), true);
        // } catch (InvalidFormatException e) {
        //     throw new IOException("字体格式异常，请确认是合法的OTF文件", e);
        // }
        // if (chineseFontBold != null) return;

        ClassLoader classLoader = PDFReportGenerator.class.getClassLoader();
        // 从 resources/fonts 目录加载字体（需确保字体文件存在）
        chineseFontRegular  = PDType0Font.load(document, 
            classLoader.getResourceAsStream("fonts/ResourceHanRoundedCN-Regular.ttf"));
        chineseFontBold  = PDType0Font.load(document,
            classLoader.getResourceAsStream("fonts/ResourceHanRoundedCN-Bold.ttf"));
    }

    private static void drawTitle(PDPageContentStream contentStream, String text, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(chineseFontBold, 24);
        contentStream.setNonStrokingColor(HEADER_COLOR);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText(text);
        contentStream.endText();
    }

    private static float drawSectionTitle(PDPageContentStream contentStream, String text, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(chineseFontBold, 14);
        contentStream.setNonStrokingColor(Color.DARK_GRAY);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText(text);
        contentStream.endText();
        return y - 30;
    }

    private static float drawInfoTable(PDPageContentStream contentStream, SampleBasePO sample, float y) throws IOException {
        float startY = y;
        float col1 = MARGIN;
        float col2 = MARGIN + 250;

        addTableRow(contentStream, "文件名称:", sample.getFilename(), col1, startY);
        addTableRow(contentStream, "文件类型:", parseFileTypeName(sample.getFileType()), col2, startY);
        startY -= LINE_SPACING;

        addTableRow(contentStream, "文件大小:", formatFileSize(sample.getFileSize()), col1, startY);
        addTableRow(contentStream, "MD5 校验:", sample.getFileMd5(), col2, startY);
        startY -= LINE_SPACING;

        addTableRow(contentStream, "提交时间:", sample.getCreateTime().toString(), col1, startY);
        addTableRow(contentStream, "处理状态:", parseStatus(sample.getDisposeStatus()), col2, startY);
        
        return startY - LINE_SPACING;
    }

    private static float drawScanResult(PDPageContentStream contentStream, ScanResult result, float y) throws IOException {
        contentStream.beginText();
        contentStream.setFont(chineseFontRegular, 12);
        contentStream.setNonStrokingColor(result.isInfected() ? WARNING_COLOR : SAFE_COLOR);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText("检测状态: " + (result.isInfected() ? "发现威胁" : "安全"));
        contentStream.endText();
        y -= LINE_SPACING;

        if (result.isInfected()) {
            addTableRow(contentStream, "病毒名称:", result.getVirusName(), MARGIN, y);
            y -= LINE_SPACING;
        }

        addTableRow(contentStream, "扫描时间:", result.getTimestamp().toString(), MARGIN, y);
        return y - LINE_SPACING;
    }

    private static void drawFooter(PDPageContentStream contentStream, String text) throws IOException {
        contentStream.beginText();
        contentStream.setFont(chineseFontRegular, 10);
        contentStream.setNonStrokingColor(Color.GRAY);
        contentStream.newLineAtOffset(MARGIN, MARGIN - 10);
        contentStream.showText(text);
        contentStream.endText();
    }

    // 辅助方法
    private static void addTableRow(PDPageContentStream cs, String label, String value, float x, float y) throws IOException {
        cs.beginText();
        cs.setFont(chineseFontBold, 12);
        cs.setNonStrokingColor(Color.DARK_GRAY);
        cs.newLineAtOffset(x, y);
        cs.showText(label);
        cs.endText();

        cs.beginText();
        cs.setFont(chineseFontRegular, 12);
        cs.setNonStrokingColor(Color.BLACK);
        cs.newLineAtOffset(x + 80, y);
        cs.showText(value != null ? value : "N/A");
        cs.endText();
    }

    private static String parseFileTypeName(int typeCode) {
        return switch (typeCode) {
            case 1 -> "文本文件";
            case 2 -> "PDF 文档";
            case 3 -> "Word 文档";
            // 补充其他类型...
            default -> "未知类型";
        };
    }

    private static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), "KMGTPE".charAt(exp-1));
    }

    private static String parseStatus(int status) {
        return switch (status) {
            case 1 -> "未处理";
            case 2 -> "处理中";
            case 3 -> "安全";
            case 4 -> "发现病毒";
            case 5 -> "处理失败";
            default -> "未知状态";
        };
    }
}