// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipFile;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.mokous.web.exception.ServiceException;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class IOUtils {
    public static String getMd5(File f) throws Exception {
        InputStream fis = new FileInputStream(f);
        try {
            return DigestUtils.md5Hex(fis);
        } finally {
            fis.close();
        }
    }


    public static String getMd5ByFile(File f) throws Exception {
        return getMd5(f);
    }

    public static String isToString(InputStream in) throws IOException {
        byte[] b = new byte[4096];
        byte[] all = new byte[4096];
        int hasUsed = 0;
        for (int n = 0; (n = in.read(b)) != -1;) {
            if (all.length - hasUsed < n) {
                byte[] tmp = new byte[hasUsed];
                System.arraycopy(all, 0, tmp, 0, hasUsed);
                all = new byte[hasUsed + n];
                System.arraycopy(tmp, 0, all, 0, hasUsed);
            }
            System.arraycopy(b, 0, all, hasUsed, n);
            hasUsed += n;
        }
        return new String(all, 0, hasUsed);
    }

    public static void close(ZipFile io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
            }
        }
    }

    public static void close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
            }
        }
    }

    public static void delete(String apkFilePath) {
        try {
            File file = new File(apkFilePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
        }
    }

    public static final String FILE_SEPARATOR = File.separator;
    public static final String URL_SEPARATOR = "/";
    public static final String HTTP_PREFIX = "http://";
    public static final String HTTPS_PREFIX = "https://";

    public static final String spliceDirPath(String prefixDir, String reativeDirPath) {
        String dirPath = "";
        if (prefixDir.endsWith(FILE_SEPARATOR)) {
            if (reativeDirPath.startsWith(FILE_SEPARATOR)) {
                dirPath = prefixDir + reativeDirPath.substring(1);
            } else {
                dirPath = prefixDir + reativeDirPath;
            }
        } else {
            if (reativeDirPath.startsWith(FILE_SEPARATOR)) {
                dirPath = prefixDir + reativeDirPath;
            } else {
                dirPath = prefixDir + FILE_SEPARATOR + reativeDirPath;
            }
        }
        if (!dirPath.endsWith(FILE_SEPARATOR)) {
            dirPath = dirPath + FILE_SEPARATOR;
        }
        return dirPath;
    }

    public static final String spliceFilePath(String prefix, String relativePath) {
        return splicePath(prefix, relativePath, FILE_SEPARATOR);
    }

    public static final void delFiles(String pathesListGson) throws ServiceException {
        List<String> pathes;
        try {
            pathes = GsonUtils.fromJsonStrToStrList(pathesListGson);
            for (String path : pathes) {
                if (StringUtils.startsWith(path, FILE_SEPARATOR)) {
                    delFile(path);
                }
            }
        } catch (Exception e) {
            throw ServiceException.getInternalException("No string list json string.Data:" + pathesListGson);
        }
    }

    public static final void delFile(String path) {
        FileUtils.deleteQuietly(new File(path));
    }

    public static final String spliceUrlPath(String prefix, String relativePath) {
        if (StringUtils.isEmpty(relativePath)) {
            return null;
        }
        if (StringUtils.isEmpty(prefix)) {
            return relativePath;
        }
        if (StringUtils.startsWithIgnoreCase(relativePath, HTTP_PREFIX)
                || StringUtils.startsWithIgnoreCase(relativePath, HTTPS_PREFIX)
                || StringUtils.startsWithIgnoreCase(relativePath, prefix)) {
            return relativePath;
        }
        return splicePath(prefix, relativePath, URL_SEPARATOR);
    }

    private static final String splicePath(String dir, String relativePath, String separator) {
        if (StringUtils.isEmpty(relativePath)) {
            return null;
        }
        if (StringUtils.isEmpty(dir)) {
            return relativePath;
        }
        if (relativePath.startsWith(dir)) {
            return relativePath;
        }
        if (StringUtils.endsWithIgnoreCase(dir, separator)) {
            if (StringUtils.startsWithIgnoreCase(relativePath, separator)) {
                return dir.substring(0, dir.length() - 1) + relativePath;
            } else {
                return dir + relativePath;
            }
        } else {
            if (relativePath.startsWith(separator)) {
                return dir + relativePath;
            } else {
                return dir + separator + relativePath;
            }
        }
    }

    public static String spliceFileName(String path, String fileName) {
        if (StringUtils.isEmpty(fileName) && StringUtils.isEmpty(path)) {
            throw ServiceException.getInternalException("无效的目录名及文件名");
        }
        if (fileName.endsWith(FILE_SEPARATOR)) {
            throw ServiceException.getInternalException("无效的文件名");
        }
        if (StringUtils.isEmpty(path)) {
            return fileName;
        }
        if (path.endsWith(FILE_SEPARATOR)) {
            if (fileName.startsWith(FILE_SEPARATOR)) {
                return path + fileName.substring(0, 1);
            }
            return path + fileName;
        } else {
            if (fileName.startsWith(FILE_SEPARATOR)) {
                return path + fileName;
            }
            return path + FILE_SEPARATOR + fileName;
        }
    }

    public static String getFileName(File file) {
        return file.getName();
    }

    public static long getFileSize(File file) {
        return file.length();
    }

    public static String getSuffix(String name) {
        if (StringUtils.isEmpty(name)) {
            return "";
        }
        int index = name.lastIndexOf(".");
        if (index < 0) {
            return "";
        }
        return name.substring(index);
    }

    public static final String getFileBytesMd5Name(byte[] bytes, String originFilename) {
        String md5 = DigestUtils.md5Hex(bytes);
        String suffix = IOUtils.getSuffix(originFilename);
        String name = md5 + suffix;
        return name;
    }

    public static final String getFileMd5Name(String md5, String fileName) {
        String suffix = IOUtils.getSuffix(fileName);
        return md5 + suffix;
    }

    public static String getFileSuffix(File file) {
        return getSuffix(file.getName());
    }
}
