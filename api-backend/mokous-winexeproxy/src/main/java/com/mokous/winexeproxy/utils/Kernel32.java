// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.winexeproxy.utils;

import java.util.HashMap;
import java.util.Map;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIFunctionMapper;
import com.sun.jna.win32.W32APITypeMapper;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public interface Kernel32 extends StdCallLibrary {

    /* http://msdn.microsoft.com/en-us/library/ms683179(VS.85).aspx */
    WinNT.HANDLE GetCurrentProcess();

    /* http://msdn.microsoft.com/en-us/library/ms683215.aspx */
    int GetProcessId(WinNT.HANDLE Process);

    final static Map<String, Object> WIN32API_OPTIONS = new HashMap<String, Object>() {

        private static final long serialVersionUID = 1L;

        {
            put(Library.OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
            put(Library.OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
        }
    };

    public Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("Kernel32", Kernel32.class, WIN32API_OPTIONS);

    /*
     * BOOL WINAPI GetVolumeInformation( __in_opt LPCTSTR lpRootPathName, __out
     * LPTSTR lpVolumeNameBuffer, __in DWORD nVolumeNameSize, __out_opt LPDWORD
     * lpVolumeSerialNumber, __out_opt LPDWORD lpMaximumComponentLength,
     * __out_opt LPDWORD lpFileSystemFlags, __out LPTSTR lpFileSystemNameBuffer,
     * __in DWORD nFileSystemNameSize );
     */
    public boolean GetVolumeInformation(String lpRootPathName, char[] lpVolumeNameBuffer, DWORD nVolumeNameSize,
            IntByReference lpVolumeSerialNumber, IntByReference lpMaximumComponentLength,
            IntByReference lpFileSystemFlags, char[] lpFileSystemNameBuffer, DWORD nFileSystemNameSize);

    public int GetLastError();
}
