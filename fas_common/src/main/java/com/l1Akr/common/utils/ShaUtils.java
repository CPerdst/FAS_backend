package com.l1Akr.common.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class ShaUtils {

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
