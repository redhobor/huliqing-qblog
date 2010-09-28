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

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import name.huliqing.qblog.enums.Security;
import org.datanucleus.jpa.annotations.Extension;

/**
 * 这个Entity表示了一个文件夹，但是目前只当相册文件夹使用，毕竟使用AppEngine的免费空
 * 间来存储大文件确实受到很多限制。在有其它更好的解决办法之前暂时只当相册文件夹使用。
 * @author huliqing
 */
@Entity
public class FolderEn implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    // ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchField
    private Long folderId;

    // 文件夹名称,不带后缀。
    @SearchField
    private String name;

    // 公开的，受保护的，完全隐私的,
    // 公开的：表示任何人都可以访问该文件夹，
    // 受保护的: 在用户提供提取码之后可以访问，一个简单的保护码，非密码.
    // 隐私的：只有管理员自己能够访问，其它任何人都访问不了。
    @Basic(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @SearchField
    private Security security;

    // 提取码，只有当security为protected时，这个字段才生效.
    // 当security为protected时，提供这个“提取码”可访问此文件夹中内容
    @SearchField
    private String fetchCode;

    // 文件夹下的所有文件大小之和,在一定时间间隔统计一下文件夹内的所有文件size
    // 单位：byte, 暂时未提供该功能
    @SearchField
    private Long totalSize;

    // 像册封面，该字段是一个图片的URL地址，如：http://xxxx...,也可以是站内地址
    // 如: /_res/image/xxxx.gif
    // 如果这个字段为null或空，则使用文件夹内第一张图片作为封面。
    @SearchField
    private String cover;

    // 创建时间
    @Extension(vendorName = "datanucleus", key = "is-second-class", value = "false")
    @SearchField
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date createDate;

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFetchCode() {
        return fetchCode;
    }

    public void setFetchCode(String fetchCode) {
        this.fetchCode = fetchCode;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(Long totalSize) {
        this.totalSize = totalSize;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
}
