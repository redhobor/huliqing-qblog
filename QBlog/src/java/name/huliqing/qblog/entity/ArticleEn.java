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
import javax.persistence.Temporal;
import javax.persistence.Id;
import name.huliqing.qblog.enums.ArticleSecurity;
import org.datanucleus.jpa.annotations.Extension;
import com.google.appengine.api.datastore.Text;
import name.huliqing.qblog.backup.BakFlag;

@Entity
public class ArticleEn implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    // 文章ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchField
    @BakFlag
    private Long articleId;

    // 文章标题
    @SearchField
    @BakFlag
    private String title;

    // 文章摘要
    @SearchField
    @BakFlag
    private String summary;

    // 文章内容
    @Basic(fetch = FetchType.EAGER)
    @Enumerated
    @SearchField
    @BakFlag
    private Text content;

    // 文章作者,这个字段关联到发表文章时的登录用户的当前昵称
    @SearchField
    @BakFlag
    private String author;

    // 公开的，隐私的，或草稿
    @Basic(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @SearchField
    @BakFlag
    private ArticleSecurity security;

    // 关于createYear,createMonth,createDay使用的是当前系统时区内的时间，所以
    // 可能与createDate的各项值不一定相同。当前系统时区设置在系统参数中：
    // Config.CON_SYSTEM_TIME_ZONE
    @SearchField
    @BakFlag
    private Integer createYear;

    // 发表文章的月份,[0-11], not [1-12]
    @SearchField
    @BakFlag
    private Integer createMonth;

    // 发表文章的日期，day of month, [1-31]
    @SearchField
    @BakFlag
    private Integer createDay;

    // 发表时间
    @Extension(vendorName = "datanucleus", key = "is-second-class", value = "false")
    @SearchField
    @Temporal(javax.persistence.TemporalType.DATE)
    @BakFlag
    private Date createDate;

    // 最后修改时间
    @Extension(vendorName = "datanucleus", key = "is-second-class", value = "false")
    @SearchField
    @Temporal(javax.persistence.TemporalType.DATE)
    @BakFlag
    private Date modifyDate;

    // 是否允许评论
    @SearchField
    @BakFlag
    private Boolean replyable;

    // 是否进行邮件通知,如果开启，则在用户回复了文章时，将通过邮件通知作者
    @SearchField
    @BakFlag
    private Boolean mailNotice;

    // 阅读数
    @SearchField
    @BakFlag
    private Long totalView;

    // 评论数
    @SearchField
    @BakFlag
    private Long totalReply;

    // 文章类别
    @SearchField
    @BakFlag
    private String category;

    // Tags字段,使用半角逗号分隔，格式如： tag1,tag2,tag3
    @SearchField
    @BakFlag
    private String tags;

    // ---- Getter and Setter
    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Text getContent() {
        return content;
    }

    public void setContent(Text content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
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

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Boolean getMailNotice() {
        return mailNotice;
    }

    public void setMailNotice(Boolean mailNotice) {
        this.mailNotice = mailNotice;
    }

    public Boolean getReplyable() {
        return replyable;
    }

    public void setReplyable(Boolean replyable) {
        this.replyable = replyable;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getCreateDay() {
        return createDay;
    }

    public void setCreateDay(Integer createDay) {
        this.createDay = createDay;
    }

    public Integer getCreateMonth() {
        return createMonth;
    }

    public void setCreateMonth(Integer createMonth) {
        this.createMonth = createMonth;
    }

    public Integer getCreateYear() {
        return createYear;
    }

    public void setCreateYear(Integer createYear) {
        this.createYear = createYear;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
