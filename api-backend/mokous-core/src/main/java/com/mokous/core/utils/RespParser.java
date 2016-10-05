// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;


/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public interface RespParser<T> extends ResponseHandler<T> {

    public static RespParser<String> STRING_PARSER = new RespParser<String>() {

        @Override
        public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            try {
                InputStream is = response.getEntity().getContent();
                Header[] contentEncoding = response.getHeaders("content-encoding");
                for (Header header : contentEncoding) {
                    if (header.getValue().contains("gzip")) {
                        is = new GZIPInputStream(is);
                        break;
                    }
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();
                StringBuilder sb = new StringBuilder();
                while (null != line) {
                    sb.append(line + "\n");
                    line = br.readLine();
                }
                return sb.toString();
            } catch (Exception e) {
                return null;
            }
        }
    };

    public static RespParser<Void> VOID_PARSER = new RespParser<Void>() {

        @Override
        public Void handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            return null;
        }
    };

    public static class FullResp {
        public int status;
        public Map<String, List<String>> headers;
        public String content;

        @Override
        public String toString() {
            return "FullResp [status=" + status + ", headers=" + headers + ", content=" + content + "]";
        }
    }
}
