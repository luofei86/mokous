// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.model.logger;

import java.io.PrintWriter;
import java.io.Writer;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月6日
 */
public class CopyPrintWriter extends PrintWriter implements CopyResponseStreamWrapper {

    private StringBuilder copy = new StringBuilder();

    public CopyPrintWriter(Writer writer) {
        super(writer);
    }

    @Override
    public void write(int c) {
        copy.append((char) c); // It is actually a char, not an int.
        super.write(c);
    }

    @Override
    public void write(char[] chars, int offset, int length) {
        copy.append(chars, offset, length);
        super.write(chars, offset, length);
    }

    @Override
    public void write(String string, int offset, int length) {
        copy.append(string, offset, length);
        super.write(string, offset, length);
    }

    @Override
    public void write(char[] buf) {
        copy.append(buf);
        super.write(buf);
    }

    @Override
    public void write(String s) {
        copy.append(s);
        super.write(s);
    }

    @Override
    public String getCopy() {
        return copy.toString();
    }

}
