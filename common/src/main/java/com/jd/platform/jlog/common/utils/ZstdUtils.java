package com.jd.platform.jlog.common.utils;

import com.github.luben.zstd.Zstd;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * zstd压缩工具类
 *
 * @author wuweifeng
 * @version 1.0
 * @date 2021-08-16
 */
public class ZstdUtils {

    /**
     * 压缩
     */
    public static byte[] compress(byte[] bytes) {
        return Zstd.compress(bytes);
    }

    /**
     * 解压
     */
    public static String decompress(byte[] bytes) {
        int size = (int) Zstd.decompressedSize(bytes);
        byte[] ob = new byte[size];
        Zstd.decompress(ob, bytes);

        return new String(ob);
    }

    /**
     * 解压
     */
    public static byte[] decompressBytes(byte[] bytes) {
        int size = (int) Zstd.decompressedSize(bytes);
        byte[] ob = new byte[size];
        Zstd.decompress(ob, bytes);

        return ob;
    }
}