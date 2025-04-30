package com.l1Akr.common.util;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class ShaUtils {

    public ShaUtils() {}

    /**
     * SHA256加密
     * @param str
     * @return
     */
    public String SHA256(final String str) {
        return SHA(str, "SHA-256");
    }

    /**
     * SHA512加密
     * @param str
     * @return
     */
    public String SHA512(final String str) {
        return SHA(str, "SHA-512");
    }

    /**
     * MD5加密
     * @param str
     * @return
     */
    public String MD5(final String str) {
        return SHA(str, "MD5");
    }

    /**
     * 文件哈希
     * @param file
     * @return
     */
    public String MD5(File file) throws IOException {
        return MD5(new FileInputStream(file.getAbsolutePath()));
    }

    /**
     * 文件哈希
     * @param stream
     * @return
     */
    public String MD5(InputStream stream) throws IOException {
        // 每1MB分块进行一次HASH
        byte[] buffer = new byte[1024 * 1024];
        int read = 0;
        List<String> digests = new ArrayList<>();
        while((read = stream.read(buffer, 0, 1024*1024)) != -1) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                return "";
            }
            md.update(buffer, 0, read);
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }
            digests.add(sb.toString());
        }

        // 然后将每块文件的哈希再次进行哈希
        StringBuilder slsb = new StringBuilder();
        digests.forEach(slsb::append);
        return MD5(slsb.toString());
    }

    /**
     * 对字符串 str 进行 type 加密
     * @param str
     * @param type
     * @return
     */
    private String SHA(final String str, final String type) {
        String resultStr = "";
        if(str != null && !str.isEmpty()) {
            try {
                // 创建加密对象
                MessageDigest md = MessageDigest.getInstance(type);
                // 传入要加密的字符
                md.update(str.getBytes());
                // 得到byte类型结果
                byte[] digest = md.digest();
                StringBuffer sb = new StringBuffer();
                for (byte b : digest) {
                    sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
                }
                resultStr = sb.toString();
            }catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return resultStr;
    }
}
