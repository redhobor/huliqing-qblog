/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 huliqing, huliqing.cn@gmail.com
 *
 * This file is part of QBlog.
 * QBlog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QBlog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with QBlog.  If not, see <http://www.gnu.org/licenses/>.
 *
 * 这个文件是QBlog的一部分。
 * 您可以单独使用或分发这个文件，但请不要移除这个头部声明信息.
 * QBlog是一个自由软件，您可以自由分发、修改其中的源代码或者重新发布它，
 * 新的任何修改后的重新发布版必须同样在遵守LGPL3或更后续的版本协议下发布.
 * 关于LGPL协议的细则请参考COPYING、COPYING.LESSER文件，
 * 您可以在QBlog的相关目录中获得LGPL协议的副本，
 * 如果没有找到，请连接到 http://www.gnu.org/licenses/ 查看。
 *
 * - Author: Huliqing
 * - Contact: huliqing.cn@gmail.com
 * - License: GNU Lesser General Public License (LGPL)
 * - Blog and source code availability: http://www.huliqing.name/
 */

package name.huliqing.qblog.upload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 简单的文件上传类，请注意：上传的附件全部存放在内存中，所以不适合大文件
 * 的传输,GAE对于表单提交的大小可能也存在限制,这个方法只是为应对GAE小文件上传的措施，
 * 可能不使合于其它项目的使用。
 * @author Huliqing
 */
public class FileUploadRequestWrapper extends HttpServletRequestWrapper {

    public static final String WWW_FORM_URLENCODED_TYPE = "application/x-www-form-urlencoded";
    private HttpServletRequest request = null;
    private Map<String, Object> parameterMap;
    private Map<String, FileItem> fileMap;

    public FileUploadRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    private void parseRequest() {
        try {
            ServletInputStream sis = request.getInputStream();
            String contentType = request.getContentType();
            String charset = request.getCharacterEncoding();
            
            String boundaryS = "--" + contentType.substring(contentType.indexOf("boundary=") + 9);
            String boundaryE = boundaryS + "--";

            List<Item> items = new ArrayList<Item>();

            byte[] buff = new byte[2048];
            int len = 0;
            String lineStr = null;
            Item item = null;
            while((len = sis.readLine(buff, 0, buff.length)) != -1) {
                lineStr = new String(buff, 0, len);
//                System.out.print(lineStr);
                if (lineStr.startsWith(boundaryE)) {
                    if (item != null) {
                        items.add(item);
                    }
                    break;
                }
                if (lineStr.startsWith(boundaryS)) {
                    // -- process previous item
                    if (item != null) {
                        items.add(item);
                    }

                    // -- process next item
                    item = new Item();
                    String fieldName = null;
                    String filename = null;

                    // Content-Disposition
                    len = sis.readLine(buff, 0, buff.length);
                    String cd = convertByCharset(buff, 0, len, charset);
                    if (cd.indexOf("Content-Disposition: form-data;") != -1) {
                        // Process field name
                        int nameStart = cd.indexOf("name=\"") + 6;
                        int nameEnd = cd.indexOf("\"", nameStart);
                        fieldName = cd.substring(nameStart, nameEnd);
                        item.setName(fieldName);
                        item.setType(Type.TEXT);

                        // Process file name is exists 
                        int filenameStart = cd.indexOf("filename=\"");
                        if (filenameStart != -1) {
                            int filenameEnd = cd.indexOf("\"", filenameStart + 10);
                            filename = cd.substring(filenameStart + 10, filenameEnd);
                            item.setFilename(filename);
                            item.setType(Type.FILE);
                        }
                    }

                    // Content-Type
                    len = sis.readLine(buff, 0, buff.length);
                    String ct = convertByCharset(buff, 0, len, charset);
                    if (ct != null && !"\r\n".equals(ct)) {
                        if (ct.indexOf("Content-Type: ") != -1) {
                            item.setContentType(ct.substring(ct.indexOf("Content-Type: ") + 14));
                            sis.readLine(buff, 0, buff.length); // 读取空行
                        }
                    }
                } else {
                    // 读取value
                    if (item == null) {
                        continue;
                    } else {
                        item.append(buff, 0, len);
                    }
                }
            }

            parameterMap = new HashMap<String, Object>();
            for (Item i : items) {
                if (i.type == Type.TEXT) {
                    String name = i.getName();
                    String value = convertByCharset(i.getBytes(), charset);
                    this.addTextParameter(name, value);
                } else if (i.type == Type.FILE) {
                    if (fileMap == null)
                        fileMap = new HashMap<String, FileItem>();

                    FileItem fileItem = new FileItem();
                    fileItem.setContentType(i.getContentType());
                    fileItem.setFilename(i.getFilename());
                    fileItem.setBytes(i.getBytes());
                    fileMap.put(i.getName(), fileItem);
                }
            }

            //Add the query string paramters
            for (Iterator<?> it = request.getParameterMap().entrySet().iterator(); it.hasNext();) {
                Map.Entry<?,?> entry = (Map.Entry<?, ?>) it.next();
                Object value = entry.getValue();
                if (value instanceof String[]) {
                    String[] valueArr = (String[]) value;
                    for (int i = 0; i < valueArr.length; i++) {
                        addTextParameter((String) entry.getKey(), valueArr[i]);
                    }
                } else if (value instanceof String) {
                    String strValue = (String) value;
                    addTextParameter((String) entry.getKey(), strValue);
                } else if (value != null) {
                    throw new IllegalStateException(
                            "Value cannot be handled, key=" + entry.getKey() 
                            + ", value type=" + value.getClass());
                }
            }

//            System.out.print("");

        } catch (IOException ex) {
            Logger.getLogger(FileUploadRequestWrapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String convertByCharset(byte[] bytes, String charset) {
        String value = null;
        if (charset != null) {
            try {
                value = new String(bytes, charset);
            } catch (UnsupportedEncodingException uee) {
                value = new String(bytes);
            }
        } else {
            value = new String(bytes);
        }
        return value;
    }

    private String convertByCharset(byte[] bytes, int offset, int len, String charset) {
        String value = null;
        if (charset != null) {
            try {
                value = new String(bytes, offset, len, charset);
            } catch (UnsupportedEncodingException uee) {
                value = new String(bytes, offset, len);
            }
        } else {
            value = new String(bytes, offset, len);
        }
        return value;
    }

    private void addTextParameter(String name, String value) {
        if (!parameterMap.containsKey(name)) {
            String[] valuesArray = {value};
            parameterMap.put(name, valuesArray);
        } else {
            String[] storedValues = (String[]) parameterMap.get(name);
            int lengthSrc = storedValues.length;
            String[] valuesArray = new String[lengthSrc + 1];
            System.arraycopy(storedValues, 0, valuesArray, 0, lengthSrc);
            valuesArray[lengthSrc] = value;
            parameterMap.put(name, valuesArray);
        }
    }

    @Override
    public Enumeration<?> getParameterNames() {
        if (parameterMap == null) {
            parseRequest();
        }
        return Collections.enumeration(parameterMap.keySet());
    }

    @Override
    public String getParameter(String name) {
        if (parameterMap == null) {
            parseRequest();
        }
        String[] values = (String[]) parameterMap.get(name);
        if (values == null) {
            return null;
        }
        return values[0];
    }

    @Override
    public String[] getParameterValues(String name) {
        if (parameterMap == null) {
            parseRequest();
        }
        return (String[]) parameterMap.get(name);
    }

    @Override
    public Map<?, ?> getParameterMap() {
        if (parameterMap == null) {
            parseRequest();
        }
        return parameterMap;
    }

    @Override
    public Object getAttribute(String string) {
        return super.getAttribute(string);
    }

    @Override
    public String getContentType() {
        return WWW_FORM_URLENCODED_TYPE;
    }

    public FileItem getFileItem(String name) {
        if (fileMap == null) {
            return null;
        }
        return fileMap.get(name);
    }

    public enum Type {
        TEXT,
        FILE;
    }

    public class FileItem {
        private String filename;
        private String contentType;
        private byte[] bytes;

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

    }

    private class Item {
        private Type type;
        // text
        private String name;

        // file
        private String filename;
        private String contentType;
        private ByteArrayOutputStream baos = new ByteArrayOutputStream();

        public void append(byte[] buff, int offset, int len) {
            baos.write(buff, offset, len);
        }

        public byte[] getBytes() {
            // 最后两个byte为 \r\n
            byte[] bytes = baos.toByteArray();
            byte newbuf[] = new byte[bytes.length - 2];
            System.arraycopy(bytes, 0, newbuf, 0, bytes.length - 2);
            return newbuf;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }

        public String getFilename() {
            return filename;
        }

        public void setFilename(String filename) {
            this.filename = filename;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }
    }

}
