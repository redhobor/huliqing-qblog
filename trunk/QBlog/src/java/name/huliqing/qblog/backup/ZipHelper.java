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

package name.huliqing.qblog.backup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import name.huliqing.common.XmlUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author huliqing
 */
public class ZipHelper {

    public final static void export(String filename, byte[] bytes, String charset) throws
            IOException {
        HttpServletResponse response = findResponse();
        if (charset != null) {
            response.setCharacterEncoding(charset);
        }
        response.setContentType("application/octet-stream");
        response.setHeader("content-disposition", "attachment;filename=" + filename + ".zip");
        OutputStream os = response.getOutputStream();
        ZipOutputStream zos = new ZipOutputStream(os);
        ZipEntry zipEntry = new ZipEntry(filename + ".xml");
        zos.putNextEntry(zipEntry);
        zos.write(bytes);
        zos.close();
    }

    public final static void export(String filename, Document doc)
            throws TransformerException, UnsupportedEncodingException, IOException {
        export(filename, doc, "UTF-8");
    }

    public final static void export(String filename, Document doc, String charset)
            throws TransformerException, UnsupportedEncodingException, IOException {
        byte[] bytes = XmlUtils.toXmlString(doc).getBytes(charset);
        export(filename, bytes, charset);
    }

    public final static byte[] importAsByte(byte[] zipBytes) throws IOException {
        ZipInputStream zis = new ZipInputStream(new InputStreamHelper(zipBytes));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipEntry ze = null;
        int len;
        byte[] buff = new byte[2048];
        while ((ze = zis.getNextEntry()) != null) {
            while ((len = zis.read(buff, 0, buff.length)) != -1) {
                baos.write(buff, 0, len);
            }
        }
        return baos.toByteArray();
    }

    public final static Document importAsDoc(byte[] zipBytes) throws
            IOException,
            SAXException,
            ParserConfigurationException {
        return importAsDoc(zipBytes, "UTF-8");
    }

    public final static Document importAsDoc(byte[] zipBytes, String charset) throws
            IOException,
            SAXException,
            ParserConfigurationException {
        byte[] bytes = importAsByte(zipBytes);
        String strValue = new String(bytes, 0, bytes.length, charset);
        return XmlUtils.newDocument(strValue);
    }

    private final static HttpServletResponse findResponse() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
        return response;
    }

    private final static class InputStreamHelper extends InputStream{
        private byte[] bytes;
        private int pos;

        public InputStreamHelper(byte[] bytes) {
            this.bytes = bytes;
        }

        @Override
        public int read() throws IOException {
            if (bytes.length <= 0 || pos >= bytes.length) {
                return -1;
            }
            return bytes[pos++] & 0xff;
        }
    }
}
