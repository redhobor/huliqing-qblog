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

package name.huliqing.qblog.processor.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.enums.Style;

/**
 *
 * @author huliqing
 */
public class ArticlesDataTable extends HtmlDataTable implements java.io.Serializable{
    private final static Logger logger = Logger.getLogger(ArticlesDataTable.class.getName());

    // 是否显示文章的摘要信息
    private Boolean showSummary;

    // 是否显示文章页脚信息
    private Boolean showFooter;

    // 打开文章连接的目标窗口，如_self,_blank...
    private String target;

    // 文章发表日期格式，如：yyyy-MM-dd
    private String pattern;

    // 文章发表日期的时区
    private String timeZone;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Boolean getShowFooter() {
        return showFooter;
    }

    public void setShowFooter(Boolean showFooter) {
        this.showFooter = showFooter;
    }

    public Boolean getShowSummary() {
        return showSummary;
    }

    public void setShowSummary(Boolean showSummary) {
        this.showSummary = showSummary;
    }

    private Object[] _values;
    @Override
    public void restoreState(FacesContext fc, Object state) {
        _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.target = (String) _values[1];
        this.pattern = (String) _values[2];
        this.timeZone = (String) _values[3];
        this.showSummary = (Boolean) _values[4];
        this.showFooter = (Boolean) _values[5];
    }

    @Override
    public Object saveState(FacesContext fc) {
        if (_values == null) {
            _values = new Object[6];
        }
        _values[0] = super.saveState(fc);
        _values[1] = this.target;
        _values[2] = this.pattern;
        _values[3] = this.timeZone;
        _values[4] = this.showSummary;
        _values[5] = this.showFooter;
        return _values;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
    }

    @Override
    public void encodeChildren(FacesContext fc) throws IOException {
        List<ArticleEn> aes = (List<ArticleEn>) this.getValue();
        if (aes != null && !aes.isEmpty()) {
            ResponseWriter rw = fc.getResponseWriter();
            String returnURL = QBlog.getOriginalURI(true, true);
            SimpleDateFormat sdf = null;
            try {
                sdf = new SimpleDateFormat(pattern != null ? pattern : "yyyy-MM-dd HH:mm");
            } catch (Exception e) {
                logger.warning("发现不符合规则的日期格式，目标pattern=" + pattern + "," +
                        "偿试改为默认格式：yyyy-MM-dd HH:mm");
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            }
            sdf.setTimeZone(TimeZone.getTimeZone(timeZone != null ? timeZone : "GMT+8"));
            Long pageId = QBlog.getPageId();
            for (ArticleEn ae : aes) {
                encodeArticle(rw, ae, pageId, sdf, returnURL);
            }
        }
    }

    public void encodeArticle(ResponseWriter rw, ArticleEn ae, 
            Long pageId, SimpleDateFormat sdf, String returnURL) throws IOException {
        // title
        String href = "/article/articleId=" + ae.getArticleId();
        if (pageId != null)
            href += ",pageId=" + pageId;

        rw.startElement("div", this);
        rw.writeAttribute("class", Style.css_article_titleOuter, null);
            rw.startElement("div", this);
            rw.writeAttribute("class", Style.css_article_titleInner, null);
                rw.startElement("div", this);
                rw.writeAttribute("class", Style.css_article_title, null);
                    rw.startElement("a", this);
                    rw.writeAttribute("href", href, null);
                    rw.writeAttribute("target", (target != null ? target : "_self"), null);
                    rw.writeText(ae.getTitle(), null);
                    rw.endElement("a");
                rw.endElement("div");
            rw.endElement("div");
        rw.endElement("div");

        // summary
        if (showSummary != null && showSummary) {
            rw.startElement("div", this);
            rw.writeAttribute("class", Style.css_article_summary, null);
            rw.writeText("摘要：" + ae.getSummary(), null);
                rw.startElement("a", this);
                rw.writeAttribute("href", href, null);
                rw.writeAttribute("target", (target != null ? target : "_self"), null);
                rw.writeText("[阅读全文]", null);
                rw.endElement("a");
            rw.endElement("div");
        }

        // des
        if (showFooter != null && showFooter) {
            rw.startElement("div", this);
            rw.writeAttribute("class", Style.css_article_footerOuter, null);
            rw.startElement("div", this);
            rw.writeAttribute("class", Style.css_article_footerInner, null);
            rw.startElement("div", this);
            rw.writeAttribute("class", Style.css_article_footer, null);

            // post date
            rw.writeText(sdf.format(ae.getCreateDate()), null);
            // views
            rw.writeText(" 阅读: " + ae.getTotalView(), null);
            // comments
            rw.writeText(" 评论: " + ae.getTotalReply(), null);

            // edit and delete
            if (QBlog.getCurrentVisitor().isLogin()) {
                String editEvent = "window.location.href ='/admin/blog/articleEdit.faces?articleId=" 
                        + ae.getArticleId() + "&returnURL=" + returnURL + "'";
                String deleteEvent = "if (confirm('您真的要删除这篇文章吗? 删除后不能恢复')) {window.location.href ='/admin/blog/articleDelete.faces?articleId="
                        + ae.getArticleId() + "&returnURL=" + returnURL + "'}";
                rw.startElement("span", this);
                rw.writeAttribute("style", "cursor:pointer", null);
                rw.writeAttribute("onclick", editEvent, null);
                rw.writeText(" 编辑", null);
                rw.endElement("span");

                rw.startElement("span", this);
                rw.writeAttribute("style", "cursor:pointer", null);
                rw.writeAttribute("onclick", deleteEvent, null);
                rw.writeText(" 删除", null);
                rw.endElement("span");
            }
            // tags
            if (ae.getTags() != null && !"".equals(ae.getTags())) {
                String[] tags = ae.getTags().split(",");
                String link = "/articles/pageId=" + pageId + ",tag=";
                rw.startElement("span", this);
                rw.writeAttribute("style", "margin:0 3px;", null);
                rw.writeText("[TAG:", null);
                for (String tag : tags) {
                    rw.startElement("a", this);
                    rw.writeAttribute("style", "margin:0 3px;", null);
                    rw.writeAttribute("href", link + tag, null);
                    rw.writeText(tag, null);
                    rw.endElement("a");
                }
                rw.writeText("]", null);
                rw.endElement("span");
            }
            rw.endElement("div");
            rw.endElement("div");
            rw.endElement("div");
        }
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
