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

package name.huliqing.qblog;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author huliqing
 */
public class ZipUtils {

    /**
     * 将byte数组压缩成zip byte数组
     * @param bytes
     * @return
     * @throws IOException
     */
    public final static byte[] pack(byte[] bytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length);
        ZipOutputStream zos = new ZipOutputStream(baos);
        zos.putNextEntry(new ZipEntry(""));
        zos.write(bytes);
        zos.close();
        return baos.toByteArray();
    }

    /**
     * 将zip bytes解压
     * @param zipBytes
     * @return
     * @throws IOException
     */
    public final static byte[] unpack(byte[] zipBytes) throws IOException {
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
