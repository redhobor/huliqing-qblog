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

import java.io.File;
import name.huliqing.qblog.upload.FileUploadRequestWrapper.FileItem;

/**
 *
 * @author huliqing
 */
public class QFile {
    private FileItem fileItem;
    
    public QFile(FileItem fileItem) {
        if (fileItem == null)
           throw new NullPointerException("FileItem couldn't be null!");
        this.fileItem = fileItem;
    }
    
    /**
     * 保存当前文件
     * @param file
     * @throws java.lang.Exception
     */
    public void save(File file) throws Exception {
        throw new UnsupportedOperationException("未支持...");
    }

    /**
     * 获取文件大小(字节)
     * @return size
     */
    public long getSize() {
        return this.fileItem.getBytes().length;
    }

    /**
     * 获取contentType
     * @return contentType
     */
    public String getContentType() {
        String contentType = fileItem.getContentType();
        if (contentType != null && contentType.indexOf("\r\n") != -1) {
            contentType = contentType.substring(0, contentType.indexOf("\r\n"));
        }
        return contentType;
    }

    /**
     * 获取原始文件名(不包含路径及后缀名)
     * @return filename
     */
    public String getFilename() {
        String name = fileItem.getFilename();
        if (name != null) {
            if (name.indexOf("\\") != -1) {
                name = name.substring(name.lastIndexOf("\\") + 1);
            }
            if (name.indexOf(".") != -1) {
                name = name.substring(0, name.lastIndexOf("."));
            }
        }
        return name;
    }

    /**
     * 获取文件后缀名,如:gif,jpg,zip,rar...等等.
     * @return suffix or null
     */
    public String getSuffix() {
        String name = fileItem.getFilename();
        if (name.indexOf(".") != -1) {
            return name.substring(name.lastIndexOf(".") + 1);
        } else {
            return null;
        }
    }

    public byte[] getBytes() {
        return this.fileItem.getBytes();
    }
    
    /**
     * 是否为图片文件
     * @return true or false
     */
    public boolean isImage() {
        String contentType = this.getContentType();
        if (contentType != null) {
            String type = contentType.substring(0, contentType.indexOf("/")).trim();
            return ("image".equals(type));
        } else {
            return false;
        }
    }
    
}
