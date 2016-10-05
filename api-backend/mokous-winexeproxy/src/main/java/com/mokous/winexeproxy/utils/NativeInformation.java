// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.utils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

import org.apache.commons.codec.digest.DigestUtils;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class NativeInformation {
    public static int getVolumeSerialNumber(String diskRoot) {
        char[] lpVolumeNameBuffer = new char[256];
        DWORD nVolumeNameSize = new DWORD(256);
        IntByReference lpVolumeSerialNumber = new IntByReference();
        IntByReference lpMaximumComponentLength = new IntByReference();
        IntByReference lpFileSystemFlags = new IntByReference();

        char[] lpFileSystemNameBuffer = new char[256];
        DWORD nFileSystemNameSize = new DWORD(256);

        lpVolumeSerialNumber.setValue(0);
        lpMaximumComponentLength.setValue(256);
        lpFileSystemFlags.setValue(0);

        Kernel32.INSTANCE.GetVolumeInformation(diskRoot, lpVolumeNameBuffer, nVolumeNameSize, lpVolumeSerialNumber,
                lpMaximumComponentLength, lpFileSystemFlags, lpFileSystemNameBuffer, nFileSystemNameSize);

        // System.out.println("Last error: "+Kernel32.INSTANCE.GetLastError()+"\n\n");
        // String fs = new String(lpFileSystemNameBuffer);
        // System.out.println(fs.trim());

        if (Kernel32.INSTANCE.GetLastError() == 0) {
            return lpVolumeSerialNumber.getValue();
        }
        return 0;
    }

    public static int getPid(Process process) {
        try {
            Class<?> ProcessImpl = process.getClass();
            Field field = ProcessImpl.getDeclaredField("handle");
            field.setAccessible(true);
            Long handl = field.getLong(process);
            Kernel32 kernel = Kernel32.INSTANCE;
            WinNT.HANDLE handle = new WinNT.HANDLE();
            handle.setPointer(Pointer.createConstant(handl));
            return kernel.GetProcessId(handle);
        } catch (Throwable e) {
            return -1;
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            System.out.println(DigestUtils.md5Hex("{846ee340-7039-11de-9d20-806e6f6e6963}".getBytes("UTF_16LE")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
