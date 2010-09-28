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
import name.huliqing.qblog.backup.BakFlag;
import name.huliqing.qblog.enums.ArticleSecurity;
import org.datanucleus.jpa.annotations.Extension;

/**
 * 该Entity保存了Tag与Article之间的关系(多对多),主要作为Tag快速查询时使用。
 * 数据不一定实时更新，所以不能指望在这里获得实时的Tag或Article的完整信息.
 * 存在一些与ArticleEn对应的冗余数据,详细查看ArticleEn。
 * @author huliqing
 */
@Entity
public class TagArticleEn implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    // 没有具体意义的ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchField
    @BakFlag
    private Long tagArticleId;

    @SearchField
    @BakFlag
    private String tagName;

    @SearchField
    @BakFlag
    private Long articleId;

    @SearchField
    @BakFlag
    private String title;

    @SearchField
    @BakFlag
    private String summary;

    @Basic(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @SearchField
    @BakFlag
    private ArticleSecurity security;

    @Extension(vendorName = "datanucleus", key = "is-second-class", value = "false")
    @SearchField
    @Temporal(javax.persistence.TemporalType.DATE)
    @BakFlag
    private Date createDate;

    // 阅读数
    @SearchField
    @BakFlag
    private Long totalView;

    // 评论数
    @SearchField
    @BakFlag
    private Long totalReply;

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public ArticleSecurity getSecurity() {
        return security;
    }

    public void setSecurity(ArticleSecurity security) {
        this.security = security;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getTagArticleId() {
        return tagArticleId;
    }

    public void setTagArticleId(Long tagArticleId) {
        this.tagArticleId = tagArticleId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTotalReply() {
        return totalReply;
    }

    public void setTotalReply(Long totalReply) {
        this.totalReply = totalReply;
    }

    public Long getTotalView() {
        return totalView;
    }

    public void setTotalView(Long totalView) {
        this.totalView = totalView;
    }
    
}
