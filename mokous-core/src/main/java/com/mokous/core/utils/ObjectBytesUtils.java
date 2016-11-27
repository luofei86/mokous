// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.mokous.web.utils.CollectionUtils;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class ObjectBytesUtils {
    private static final Logger log = Logger.getLogger(ObjectBytesUtils.class);

    public static <T extends Serializable> byte[][] objectToBytes(List<T> datas) throws IOException {
        byte[][] dataBytes = new byte[datas.size()][];
        for (int i = 0; i < datas.size(); i++) {
            byte[] value = ObjectBytesUtils.objectToBytes(datas.get(i));
            dataBytes[i] = value;
        }
        return dataBytes;
    }

    public static byte[] objectToBytes(Serializable o) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject((Object) o);
            return bos.toByteArray();
        } catch (IOException e) {
            log.error("Object to bytes failed.Errmsg:" + e + e.getMessage(), e);
            throw e;
        } finally {
            safeClose(bos);
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T bytesToObject(byte[] bytes) throws IOException, ClassNotFoundException {
        if (bytes == null) {
            return null;
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (T) in.readObject();
        } catch (IOException e) {
            log.error("Bytes to Bbject failed.Errmsg:" + e + e.getMessage(), e);
            throw e;
        } catch (ClassNotFoundException e) {
            log.error("Bytes to Bbject failed.Errmsg:" + e + e.getMessage(), e);
            throw e;
        } finally {
            safeClose(bis);
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> bytesToObject(Collection<byte[]> bytes) throws IOException,
            ClassNotFoundException {
        if (CollectionUtils.emptyOrNull(bytes)) {
            return null;
        }
        List<T> ret = new ArrayList<T>(bytes.size());
        for (byte[] bs : bytes) {
            ret.add((T) bytesToObject(bs));
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Serializable> List<T> bytesToObject(byte[][] bytes) throws IOException,
            ClassNotFoundException {
        if (CollectionUtils.emptyOrNull(bytes)) {
            return null;
        }
        List<T> ret = new ArrayList<T>(bytes.length);
        for (byte[] bs : bytes) {
            ret.add((T) bytesToObject(bs));
        }
        return ret;
    }

    private static void safeClose(Closeable ca) {
        if (ca != null) {
            try {
                ca.close();
            } catch (Exception e) {
            }
        }
    }
}
