//Copyright © 2016 - 2020 luofei86@gmail.com All Rights Reserved.罗飞 版本所有
package com.mokous.dragon.boat.base;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author luofei
 * Generate 2020/1/12
 */
public class Charsets {
    public Charsets() {
    }

    public static Charset toCharset(Charset charset) {
        return charset == null ? Charset.defaultCharset() : charset;
    }

    public static Charset toCharset(String charset) {
        return charset == null ? Charset.defaultCharset() : Charset.forName(charset);
    }
}
