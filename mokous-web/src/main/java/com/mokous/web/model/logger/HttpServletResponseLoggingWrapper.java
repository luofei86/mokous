// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.web.model.logger;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月4日
 * 
 */
public class HttpServletResponseLoggingWrapper extends HttpServletResponseWrapper implements HttpServletResponse {

    private CopyPrintWriter copiedWriter = null;
    private ServletOutputStream outputStream = null;
    final private HttpServletResponse _response;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response
     * @throws IllegalArgumentException if the response is null
     */
    public HttpServletResponseLoggingWrapper(HttpServletResponse response) {
        super(response);
        this._response = response;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (copiedWriter == null) {
            try {
                PrintWriter writer = _response.getWriter();
                copiedWriter = new CopyPrintWriter(writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return copiedWriter;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            try {
                ServletOutputStream stream = _response.getOutputStream();
                outputStream = new CopyServletOutputStream(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream;
    }

}

//
// class CopyPrintWriter extends PrintWriter implements
// CopyResponseStreamWrapper {
//
// private StringBuilder copy = new StringBuilder();
//
// public CopyPrintWriter(Writer writer) {
// super(writer);
// }
//
// @Override
// public void write(int c) {
// copy.append((char) c); // It is actually a char, not an int.
// super.write(c);
// }
//
// @Override
// public void write(char[] chars, int offset, int length) {
// copy.append(chars, offset, length);
// super.write(chars, offset, length);
// }
//
// @Override
// public void write(String string, int offset, int length) {
// copy.append(string, offset, length);
// super.write(string, offset, length);
// }
//
// @Override
// public void write(char[] buf) {
// copy.append(buf);
// super.write(buf);
// }
//
// @Override
// public void write(String s) {
// copy.append(s);
// super.write(s);
// }
//
// @Override
// public String getCopy() {
// return copy.toString();
// }
//
// }
//
// class CopyServletOutputStream extends ServletOutputStream implements
// CopyResponseStreamWrapper {
//
// private StringBuilder copy = new StringBuilder();
// private ServletOutputStream servletOutputStream;
//
// public CopyServletOutputStream(ServletOutputStream outputStream) {
// super();
// servletOutputStream = outputStream;
// }
//
// @Override
// public void print(boolean b) throws IOException {
// copy.append(b);
// servletOutputStream.print(b);
// }
//
// @Override
// public void print(double d) throws IOException {
// copy.append(d);
// servletOutputStream.print(d);
// }
//
// @Override
// public void print(String s) throws IOException {
// copy.append(s);
// servletOutputStream.print(s);
// }
//
// @Override
// public void print(char c) throws IOException {
// copy.append(c);
// servletOutputStream.print(c);
// }
//
// @Override
// public void print(int i) throws IOException {
// copy.append(i);
// servletOutputStream.print(i);
// }
//
// @Override
// public void print(long l) throws IOException {
// copy.append(l);
// servletOutputStream.print(l);
// }
//
// @Override
// public void print(float f) throws IOException {
// copy.append(f);
// servletOutputStream.print(f);
// }
//
// @Override
// public void println() throws IOException {
// copy.append("\n");
// servletOutputStream.println();
// }
//
// @Override
// public void println(String s) throws IOException {
// copy.append(s);
// copy.append("\n");
// servletOutputStream.println(s);
// }
//
// @Override
// public void println(boolean b) throws IOException {
// copy.append(b);
// copy.append("\n");
// servletOutputStream.println(b);
// }
//
// @Override
// public void println(char c) throws IOException {
// copy.append(c);
// copy.append("\n");
// servletOutputStream.println(c);
// }
//
// @Override
// public void println(int i) throws IOException {
// copy.append(i);
// copy.append("\n");
// servletOutputStream.println(i);
// }
//
// @Override
// public void println(long l) throws IOException {
// copy.append(l);
// copy.append("\n");
// servletOutputStream.println(l);
// }
//
// @Override
// public void println(float f) throws IOException {
// copy.append(f);
// copy.append("\n");
// servletOutputStream.println(f);
// }
//
// @Override
// public void println(double d) throws IOException {
// copy.append(d);
// copy.append("\n");
// servletOutputStream.println(d);
// }
//
// @Override
// public void write(int b) throws IOException {
// copy.append(b);
// servletOutputStream.write(b);
// }
//
// @Override
// public void write(byte[] b) throws IOException {
// copy.append(b);
// servletOutputStream.write(b);
// }
//
// @Override
// public void write(byte[] b, int off, int len) throws IOException {
// copy.append(new String(b, off, len));
// servletOutputStream.write(b, off, len);
// }
//
// @Override
// public String getCopy() {
// return copy.toString();
// }
// }
