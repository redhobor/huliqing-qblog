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

package name.huliqing.qblog.web;

import com.google.appengine.api.datastore.Text;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.Constant;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.StringUtils;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.entity.ReplyEn;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qblog.service.ArticleSe;
import name.huliqing.qblog.service.MailSe;
import name.huliqing.qblog.service.ReplySe;
import name.huliqing.qfaces.QFaces;
import name.huliqing.qfaces.model.MiniPageModel;
import name.huliqing.qfaces.model.MiniPageParam;
import name.huliqing.qfaces.model.PageModel;

@ManagedBean
@RequestScoped
public class ArticleWe extends BaseWe {

    private ArticleEn article;
    private ArticleEn previous;
    private ArticleEn next;
    private Long pageId;

    public ArticleWe() {
        super();
        Long tempPageId = QBlog.getPageId();
        if (tempPageId != null) {
            pageId = tempPageId;
        }
        Long articleId = QBlog.getArticleId();
        if (articleId != null) {
            this.article = ArticleSe.find(articleId);
            if (this.article != null) {
                ArticleSe.increaseTotalView(articleId, 1);
                this.previous = ArticleSe.findNextPublic(article, false);
                this.next = ArticleSe.findNextPublic(article, true);
            }
        }
    }

    // ---- Getter and Setter

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public ArticleEn getArticle() {
        return article;
    }

    public void setArticle(ArticleEn article) {
        this.article = article;
    }

    public ArticleEn getNext() {
        if (next == null) {
            next = new ArticleEn();
        }
        return next;
    }

    public void setNext(ArticleEn next) {
        this.next = next;
    }

    public ArticleEn getPrevious() {
        if (previous == null) {
            previous = new ArticleEn();
        }
        return previous;
    }

    public void setPrevious(ArticleEn previous) {
        this.previous = previous;
    }

    // 在admin有登录的情况下，帮助填充回复栏部分的内容
    public String getDefaultName() {
        if (QBlog.getCurrentVisitor().isLogin()) {
            return QBlog.getCurrentVisitor().getAccount().getNickname();
        }
        return "";
    }

    public void setDefaultName(String defaultName) {
        // ignore
    }

    public String getDefaultEmail() {
        if (QBlog.getCurrentVisitor().isLogin()) {
            return ConfigManager.getInstance().findConfig(Config.CON_EMAIL_ADDR_SERVER).getValue();
        }
        return "";
    }

    public void setDefaultEmail(String defaultEmail) {
        // ignore
    }

    public Boolean getReplyable() {
        return ArticleSe.isReplyable(article);
    }

    public Integer getReplyPageSize() {
        Integer pageSize = QFaces.convertToInteger(ConfigManager.getInstance().findConfig(Config.CON_REPLY_PAGE_SIZE).getValue());
        if (pageSize == null) {
            pageSize = QFaces.convertToInteger(Config.CON_REPLY_PAGE_SIZE.getValue());
        }
        return pageSize;
    }

    // ---- Action

    public MiniPageModel loadReplies(MiniPageParam mpp) {
        Long articleId = QFaces.convertToLong(mpp.getRefValues()[0]);
        ArticleEn _ae = ArticleSe.find(articleId);
        Boolean login = QBlog.getCurrentVisitor().isLogin();

        Boolean replyable = ArticleSe.isReplyable(_ae);
        if (replyable == null)
            replyable = Boolean.FALSE;

        Boolean asc = QFaces.convertToBoolean(ConfigManager.getInstance().findConfig(Config.CON_REPLY_SORT_ASC).getValue());
        if (asc == null)
            asc = Boolean.FALSE;
        
        mpp.setSortField("createDate");
        mpp.setAsc(asc);
        PageModel<ReplyEn> pm = ReplySe.find(mpp, articleId);
        if (pm == null) {
            return null;
        }

        List<ReplyEn> replies = pm.getPageData();
        MiniPageModel mpm = new MiniPageModel();
        mpm.setTotal(pm.getTotal());
        int count = 0; //
        for (ReplyEn r : replies) {
            count++;
            String title = r.getTitle();
            String content = r.getContent() != null ? r.getContent().getValue() : "";
            String replyBy = (r.getReplyBy() != null ? r.getReplyBy() : r.getReplyIpRemake());
            String postDate = QBlog.formatDate(r.getCreateDate());
            String actionReply = "";
            String actionEdit = "";
            String actionDelete = "";

            // 这里给定title, replyBy两个id,用于在客户端可以自定义样式
            String titleObj = "<span id=\"q_article_replies_title\">" + title + "</span>";
            String replyByObj = "<span id=\"q_article_replies_author\">" + replyBy + "</span>";

            // 只有当前文章开启了回复功能时才显示以下action
            if (replyable) 
                actionReply = "<span style='cursor:pointer;margin-left:5px;' onclick=\"replyReply('" + r.getReplyId() + "');return false;\"> Reply </span>";

            if (replyable && (login || ReplySe.isEditable(r)))
                actionEdit = "<span style='cursor:pointer;margin-left:5px;' onclick=\"editReply('" + r.getReplyId() + "');return false;\"> Edit </span>";

            if (login)
                actionDelete = "<span style='cursor:pointer;margin-left:5px;' onclick=\"deleteReply('" + r.getReplyId() + "');return false;\"> Delete </span>";
            
            // 显示回复信息
            StringBuilder sb = new StringBuilder(); // Start
            sb.append("<div>").append(titleObj).append("  ").append(replyByObj).append("</div>");
            sb.append("<div id=\"q_article_replies_content\"><pre>").append(content).append("</pre></div>");
            sb.append("<div style='color:#C0C0C0;'>").append(postDate).append(actionReply).append(r.isEditable() ? actionEdit : "").append(r.isEditable() ? actionDelete : "").append("</div>");
            if (count < replies.size()) {
                sb.append("<div style='height:5px;border-bottom:1px dotted #c0c0c0;'/>");
            }

            // 处理回复信息的编辑，这一部分主要是在页面渲染隐藏控件，用于保存各个回复信息的内容
            // 在用户点击编辑这后重新获取这些内容进行编辑
            String h_replyId = r.getReplyId() + ":replyId";
            String h_title = r.getReplyId() + ":title";
            String h_content = r.getReplyId() + ":content";
            String h_replyBy = r.getReplyId() + ":replyBy";
            String h_email = r.getReplyId() + ":email";
            sb.append("<input id='" + h_replyId + "' type=\"hidden\" value='" + r.getReplyId() + "' />");
            sb.append("<input id='" + h_title + "' type=\"hidden\" value='" + title + "' />");
            sb.append("<input id='" + h_content + "' type=\"hidden\" value='" + content + "' />");
            sb.append("<input id='" + h_replyBy + "' type=\"hidden\" value='" + replyBy + "' />");
            sb.append("<input id='" + h_email + "' type=\"hidden\" value='" + r.getEmail() + "' />");
            List<String> record = new ArrayList<String>(1);
            record.add(sb.toString());
            mpm.addRecord(record); 
        }
        return mpm;
    }

    /**
     * 回复文章，或者编辑回复信息
     * @param args
     * @return
     */
    public String saveOrUpdateReply(String[] args) {
        String tempId = args[0];
        String replyId = args[1];
        String title = args[2];
        String replyBy = args[3];
        String email = args[4];
        String content = args[5];
        Long articleId = QFaces.convertToLong(tempId);
        // 有必要对content的部分内容进行转义，相关符号：&, <, >
        // 用户可能在回复时使用相关的HTML代码.
        // 这几个符号除了有可能造成安全问题外，还可能xhtml页面不能正常显示。
        // e.g.  & -> 必须转化为 &amp; 否则无法innerHTML
        Boolean escape = ConfigManager.getInstance().getAsBoolean(Config.CON_REPLY_HTML_ESCAPE);
        if (escape) {
            content = StringUtils.convertHTML(content);
        }

        // 通过replyId是否存在以确定是save或update
        ReplyEn reply = null;
        if (replyId != null && !"".equals(replyId)) {
            reply = ReplySe.find(QFaces.convertToLong(replyId));
        } else {
            reply = new ReplyEn();
        }
        // 设置值
        reply.setTitle(title);
        reply.setEmail(email);
        reply.setContent(new Text(content));
        reply.setArticle(Long.valueOf(tempId));
        reply.setReplyIp(QBlog.getRemoteAddress());
        reply.setReplyBy(replyBy);

        // 更新或创建新的reply
        boolean isOK = false;
        if (replyId != null && !"".equals(replyId)) {
            isOK = ReplySe.update(reply);
        } else {
            isOK = ReplySe.save(reply);
        }

        // 如果系统开启了文章的Email回复通知.则分析回复内容，获取
        // 回复目标（Email地址）,然后回复email.
        if ("true".equalsIgnoreCase(ConfigManager.getInstance().findConfig(Config.CON_REPLY_NOTICE).getValue())) {
            try {
                MailSe.sendByReply(reply, articleId);
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
            }
        }

        if (isOK) {
            return Constant.OUT_SUCCESS;
        } else {
            return Constant.OUT_FAILURE;
        }
    }

    public String deleteReply(String[] args) {
        String replyId = args[0];
        if (ReplySe.delete(QFaces.convertToLong(replyId))) {
            return Constant.OUT_SUCCESS;
        } else {
            return Constant.OUT_FAILURE;
        }
    }

    public void deleteArticle() {
        ArticleSe.delete(this.getArticle().getArticleId());
        QBlog.redirect("/index.faces");
    }
}
