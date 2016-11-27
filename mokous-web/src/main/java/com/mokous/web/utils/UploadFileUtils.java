// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.mokous.web.exception.ServiceException;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class UploadFileUtils {
    public static String getSuffix(CommonsMultipartFile file) {
        String name = file.getOriginalFilename();
        int index = name.lastIndexOf(".");
        if (index == -1) {
            return "png";
        }
        return name.substring(index + 1);
    }

    public static String getSuffix(String file) {
        int index = file.lastIndexOf(".");
        if (index == -1) {
            return "png";
        }
        return file.substring(index + 1);
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static void writeFile(CommonsMultipartFile srcFile, File outFile) {
        FileOutputStream fos = null;
        InputStream ins = null;
        try {
            fos = new FileOutputStream(outFile);
            ins = srcFile.getInputStream();
            byte[] buffer = new byte[8192];
            int len;
            while ((len = ins.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e1) {
            throw ServiceException.getInternalException("未能正常获取文件数据.");
        } finally {
            IOUtils.close(fos);
            IOUtils.close(ins);
        }
    }

    public static byte[] getFileBytes(CommonsMultipartFile file) {
        try {
            InputStream is = file.getInputStream();
            byte[] ret = new byte[is.available()];
            try {
                is.read(ret);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (Exception e) {
                }
            }
            return ret;
        } catch (IOException e1) {
            throw ServiceException.getInternalException("未能正常获取文件数据.");
        }
    }

    public static byte[] getFileBytes(File file) {
        try {
            InputStream is = new FileInputStream(file);
            byte[] ret = new byte[is.available()];
            try {
                is.read(ret);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (Exception e) {
                }
            }
            return ret;
        } catch (IOException e1) {
            throw ServiceException.getInternalException("未能正常获取文件数据.");
        }
    }

    public static byte[] getFileBytes(String file) {
        return getFileBytes(new File(file));
    }

    public static Boolean writeFileToDisk(ByteArrayOutputStream is, String savePath) {
        try {
            OutputStream outputStream = new FileOutputStream(savePath);
            is.writeTo(outputStream);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getMd5(String filePath) throws Exception {
        return IOUtils.getMd5(new File(filePath));
    }

    public static String getMd5(File f) throws Exception {
        return IOUtils.getMd5(f);
    }

    public static String getMd5(MultipartFile file) throws NoSuchAlgorithmException, IOException {
        return DigestUtils.md5Hex(file.getBytes());
    }

    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }
}
