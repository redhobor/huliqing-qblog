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

import com.google.appengine.api.datastore.Text;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import name.huliqing.qblog.service.ReplySe;
import org.datanucleus.jpa.annotations.Extension;
import name.huliqing.qblog.backup.BakFlag;

@Entity
public class ReplyEn implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    // 评论信息ID 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchField
    @BakFlag
    private Long replyId;

    // 评论标题(未用20100716)
    @SearchField
    @BakFlag
    private String title;

    // 所评论的目标文章ID
    @SearchField
    @BakFlag
    private Long article;

    // 评论内容
    @Basic(fetch = FetchType.EAGER)
    @Enumerated
    @SearchField
    @BakFlag
    private Text content;

    // 评论者的Email地址
    @SearchField
    @BakFlag
    private String email;

    // 评论者名称
    @SearchField
    @BakFlag
    private String replyBy;

    // 评论者IP地址
    @SearchField
    @BakFlag
    private String replyIp;

    // 评论时间
    @Extension(vendorName = "datanucleus", key = "is-second-class", value = "false")
    @SearchField
    @Temporal(javax.persistence.TemporalType.DATE)
    @BakFlag
    private Date createDate;

    // 被编辑时间
    @Extension(vendorName = "datanucleus", key = "is-second-class", value = "false")
    @SearchField
    @Temporal(javax.persistence.TemporalType.DATE)
    @BakFlag
    private Date modifyDate;

    /**
     * 获取回复信息的ID
     * @return
     */
    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    /**
     * 获取该回复信息所对应的文章的ID，通过该ID可获取主文章信息。
     * @return
     */
    public Long getArticle() {
        return article;
    }

    public void setArticle(Long article) {
        this.article = article;
    }

    /**
     * 获取回复信息的内容
     * @return
     */
    public Text getContent() {
        return content;
    }

    public void setContent(Text content) {
        this.content = content;
    }

    /**
     * 获取回复者的Email信息。
     * @return
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取回复者名称
     * @return
     */
    public String getReplyBy() {
        return replyBy;
    }

    public void setReplyBy(String replyBy) {
        this.replyBy = replyBy;
    }

    /**
     * 获取回复者IP信息
     * @return
     */
    public String getReplyIp() {
        return replyIp;
    }

    public void setReplyIp(String replyIp) {
        this.replyIp = replyIp;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // ----
    /**
     * 获取IP，经过处理的IP，格式如：127.0.0.*
     * 不显示最后一个数值，主要为隐私考虑
     */
    public String getReplyIpRemake() {
        String ipRemake = null;
        if (this.replyIp != null) {
            ipRemake = this.replyIp.substring(0, this.replyIp.lastIndexOf(".") + 1) + "*";
        }
        return ipRemake;
    }

    public boolean isEditable() {
        return ReplySe.isEditable(this);
    }
}
