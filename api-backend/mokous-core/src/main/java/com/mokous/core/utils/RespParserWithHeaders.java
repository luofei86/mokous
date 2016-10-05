// Copyright 2016-2016 www.mokous.com Inc. All Rights Reserved.

package com.mokous.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;

import com.mokous.core.utils.RespParser.FullResp;

/**
 * @author luofei (Your Name Here)
 * @date 2016年10月5日
 * 
 */
public class RespParserWithHeaders implements RespParser<FullResp> {

    @Override
    public FullResp handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        FullResp resp = new FullResp();
        // status
        resp.status = response.getStatusLine().getStatusCode();
        // headers
        Header[] respHeaders = response.getAllHeaders();
        Map<String, List<String>> headers = new HashMap<>();
        for (Header header : respHeaders) {
            String key = header.getName();
            String value = header.getValue();
            key = key.toLowerCase();
            List<String> values = headers.get(key);
            if (null == values) {
                values = new ArrayList<String>();
                headers.put(key, values);
            }
            values.add(value);
        }
        resp.headers = headers;
        // content
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
        resp.content = sb.toString();

        return resp;
    }

}
