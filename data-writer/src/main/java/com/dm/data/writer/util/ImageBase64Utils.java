package com.dm.data.writer.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.*;

/**
 * 把base64和图片 相互转
 *
 * @author wendongshan
 */
@Slf4j
public class ImageBase64Utils {


    public static final String PNG = "data:image/png;base64,";
    public static final String GIF = "data:image/gif;base64,";
    public static final String JPG = "data:image/jpg;base64,";

    public static String bytesToBase64(byte[] bytes) {
        return Base64.encodeBase64String(bytes);
    }

    /**
     * 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     *
     * @param path 图片路径
     * @return base64字符串 返回Base64编码过的字节数组字符串
     */
    public static String imageToBase64(String path) throws IOException {
        byte[] data = null;
        // 读取图片字节数组
        InputStream in = null;
        try {
            in = new FileInputStream(path);
            data = new byte[in.available()];
            in.read(data);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("ImageBase64Utils->imageToBase64:{}", e);
                }
            }
        }
        return Base64.encodeBase64String(data);
    }

    /**
     * 处理Base64解码并写图片到指定位置
     * 对字节数组字符串进行Base64解码并生成图片
     *
     * @param base64 图片Base64数据
     * @param path   图片保存路径
     * @return
     */
    public static boolean base64ToImageFile(String base64, String path) throws IOException {
        // 生成jpeg图片
        try {
            OutputStream out = new FileOutputStream(path);
            return base64ToImageOutput(base64, out);
        } catch (FileNotFoundException e) {
            log.error("ImageBase64Utils->base64ToImageFile:{}", e);
        }
        return false;
    }

    /**
     * 处理Base64解码并输出流
     *
     * @param base64
     * @param out
     * @return
     */
    public static boolean base64ToImageOutput(String base64, OutputStream out) throws IOException {
        // 图像数据为空
        if (base64 == null) {
            return false;
        }
        try {
            // Base64解码
            byte[] bytes = Base64.decodeBase64(base64);
            for (int i = 0; i < bytes.length; ++i) {
                // 调整异常数据
                if (bytes[i] < 0) {
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            out.write(bytes);
            out.flush();
            return true;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                log.error("ImageBase64Utils->base64ToImageOutput:{}", e);
            }
        }
    }


    public static String getImgSuffix(String base64ImgData) {
        if (base64ImgData.contains(PNG)) {
            return ".png";
        }
        if (base64ImgData.contains(JPG)) {
            return ".jpg";
        }
        if (base64ImgData.contains(GIF)) {
            return ".gif";
        }
        return null;
    }
}
