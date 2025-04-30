package com.l1Akr.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class ShaUtilsTest {

    private final ShaUtils shaUtils = new ShaUtils();
    @Test
    public void testStringMD5() {
        String STR_123456_MD5 = "e10adc3949ba59abbe56e057f20f883e";
        Assertions.assertEquals(STR_123456_MD5, shaUtils.MD5("123456"));
    }

    @Test
    public void testFileMD5() throws Exception {
        // 创建一个临时文件用来测试
        File file = File.createTempFile("test", ".txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] data = new byte[1024]; // 全部是\x00
            // 像文件写入5MB数据
            for(int i = 0; i < 5120; i++) {
                fos.write(data);
            }
            fos.flush();
        }

        // 然后测试文件哈希
        try (FileInputStream fis = new FileInputStream(file)) {
            String STR_FILE_MD5 = "15d5f7681abb9dc405e4c85a73482a8d";
            Assertions.assertEquals(STR_FILE_MD5, shaUtils.MD5(file));
        }
        boolean delete = file.delete();
    }
}
