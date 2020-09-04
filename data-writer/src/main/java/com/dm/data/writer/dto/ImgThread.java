package com.dm.data.writer.dto;


import com.dm.data.writer.util.ImageBase64Utils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * 保存图片的线程
 * @author wendongshan
 */
@Slf4j
public class ImgThread implements Runnable {

    /**
     * 存储地址
     */
    private String url;
    /**
     * 图片的base64 字符串
     */
    private String img;

    public ImgThread(String url, String img) {
        this.url = url;
        this.img = img;
    }

    @Override
    public void run() {
        try {
            log.info(Thread.currentThread().getName()+"==>"+url);
            ImageBase64Utils.base64ToImageFile(img.replace(ImageBase64Utils.GIF, "").replace(ImageBase64Utils.JPG, "").replace(ImageBase64Utils.PNG, ""),url);
        } catch (IOException e) {
            log.error("{}",e);
        }

    }
}
