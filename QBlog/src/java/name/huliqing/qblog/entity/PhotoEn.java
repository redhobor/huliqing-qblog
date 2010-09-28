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

package name.huliqing.qblog.entity;

import java.io.IOException;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Temporal;
import name.huliqing.qblog.ZipUtils;
import org.datanucleus.jpa.annotations.Extension;

/**
 * 注：所有文件都会先压后再储存zip bytes。
 * @author huliqing
 */
@Entity
public class PhotoEn implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchField
    private Long photoId;

    // 文件名
    @SearchField
    private String name;

    // Content type
    @SearchField
    private String contentType;

    // 文件后缀名（没有包含"."号）,如“gif,jpg,png,...
    @SearchField
    private String suffix;

    // 实体数据,不要加 @SearchField,该field不能用于查询,
    // 在查找多个entity时不会fectch这个字段，只有在单独获取一个entity时才取出该字段。
    // （这些字节可能会有压缩处理）
    @Basic(fetch = FetchType.EAGER)
    @Enumerated
    @Lob
    private byte[] bytes;

    // 没有压缩处理的缩略图数据,注：并不是每张图片都一定存在缩略图，这个字段可能为null，
    // 在获取这个数据时，应该判断是否为null
    @Basic(fetch = FetchType.EAGER)
    @Enumerated
    @Lob
    private byte[] bytesMin;

    // 文件大小,如果经过压缩，这里指示的是经过压缩后的大小。
    @SearchField
    private Long fileSize;

    // 是否经过zip压缩, true压缩，false无压缩,作用较小，
    // 对大部分图片来说，压缩率都很小。对图片不压缩,效率可能更好。
    @SearchField
    private Boolean pack;

    // 文件的上传时间
    @Extension(vendorName = "datanucleus", key = "is-second-class", value = "false")
    @SearchField
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date createDate;

    // 属于哪一个文件夹
    @SearchField
    private Long folder;

    // 图片备注
    @SearchField
    private String des;

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

    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getFolder() {
        return folder;
    }

    public void setFolder(Long folder) {
        this.folder = folder;
    }

    public Boolean getPack() {
        return pack;
    }

    public void setPack(Boolean pack) {
        this.pack = pack;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public byte[] getBytesMin() {
        return bytesMin;
    }

    public void setBytesMin(byte[] bytesMin) {
        this.bytesMin = bytesMin;
    }
    
    // ---- action

    /**
     * 获取解压缩后的byte
     * @return
     * @throws IOException
     */
    public byte[] getUnpackData() throws IOException {
        if (pack) {
            return ZipUtils.unpack(bytes);
        } else {
            return bytes;
        }
    }
}
