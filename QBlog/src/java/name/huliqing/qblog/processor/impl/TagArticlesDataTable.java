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
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.TagArticleEn;
import name.huliqing.qblog.enums.Config;

/**
 *
 * @author huliqing
 */
public class TagArticlesDataTable extends HtmlDataTable implements java.io.Serializable {

    // 绑定的tag
    private String tag;

    // 打开文章的目标窗口
    private String target;

    // 是否显示发布日期
    private Boolean showDate;

    // 文章发表日期格式，如：yyyy-MM-dd
    private String pattern;

    // 文章发表日期的时区
    private String timeZone;

    // 是否显示编辑、删除按钮
    private Boolean showEdit;

    // 是否显示序号
    private Boolean showIndex;

    // 显示"更多..."
    private Boolean showMore;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Boolean getShowDate() {
        return showDate;
    }

    public void setShowDate(Boolean showDate) {
        this.showDate = showDate;
    }

    public Boolean getShowEdit() {
        return showEdit;
    }

    public void setShowEdit(Boolean showEdit) {
        this.showEdit = showEdit;
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

    public Boolean getShowIndex() {
        return showIndex;
    }

    public void setShowIndex(Boolean showIndex) {
        this.showIndex = showIndex;
    }

    public Boolean getShowMore() {
        return showMore;
    }

    public void setShowMore(Boolean showMore) {
        this.showMore = showMore;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    
    private Object[] _values;
    @Override
    public void restoreState(FacesContext fc, Object state) {
        _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.target = (String) _values[1];
        this.showDate = (Boolean) _values[2];
        this.pattern = (String) _values[3];
        this.timeZone = (String) _values[4];
        this.showEdit = (Boolean) _values[5];
        this.showIndex = (Boolean) _values[6];
        this.showMore = (Boolean) _values[7];
        this.tag = (String) _values[8];
    }

    @Override
    public Object saveState(FacesContext fc) {
        if (_values == null) {
            _values = new Object[9];
        }
        _values[0] = super.saveState(fc);
        _values[1] = this.target;
        _values[2] = this.showDate;
        _values[3] = this.pattern;
        _values[4] = this.timeZone;
        _values[5] = this.showEdit;
        _values[6] = this.showIndex;
        _values[7] = this.showMore;
        _values[8] = this.tag;
        return _values;
    }

    @Override
    public void encodeBegin(FacesContext fc) throws IOException {}

    @Override
    public void encodeEnd(FacesContext fc) throws IOException {}

    @Override
    public void encodeChildren(FacesContext fc) throws IOException {
        List<TagArticleEn> aes = (List<TagArticleEn>) this.getValue();
        if (aes != null && !aes.isEmpty()) {
            ResponseWriter rw = fc.getResponseWriter();
            String returnURL = QBlog.getOriginalURI(true, true);
            SimpleDateFormat sdf = null;
            if (showDate != null && showDate) {
                try {
                    sdf = new SimpleDateFormat(pattern != null ? 
                        pattern : ConfigManager.getInstance().getAsString(Config.CON_SYSTEM_DATE_FORMAT));
                } catch (Exception e) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                }
                sdf.setTimeZone(TimeZone.getTimeZone(timeZone != null ? 
                    timeZone : ConfigManager.getInstance().getAsString(Config.CON_SYSTEM_TIME_ZONE)));
            }
            Long pageId = QBlog.getPageId();
            boolean _showEdit = (QBlog.getCurrentVisitor().isLogin() && showEdit != null && showEdit);
            int i = 0;
            for (TagArticleEn ae : aes) {
                encodeArticle(++i, rw, pageId, ae, sdf, _showEdit, returnURL);
            }
            if (showMore != null && showMore) {
                String href = "/articles/pageId=" + pageId + ",tag=" + tag;
                rw.startElement("div", this);
                rw.writeAttribute("style", "text-align:right;", null);
                    rw.startElement("a", this);
                    rw.writeAttribute("href", href, null);
                    rw.writeAttribute("title", "Show More", null);
                    rw.writeText("More", null);
                    rw.endElement("a");
                rw.endElement("div");
            }
        }
    }

    private void encodeArticle(int index, ResponseWriter rw, Long pageId, TagArticleEn tae, SimpleDateFormat sdf, boolean _showEdit, String returnURL) throws IOException {
        String href = "/article/articleId=" + tae.getArticleId();
        if (pageId != null) {
            href += ",pageId=" + pageId;
        }

        rw.startElement("div", this);
        rw.writeAttribute("style", "padding:3px;", null);
            if (getShowIndex()) {
                rw.startElement("span", this);
                rw.writeAttribute("style", "color:gray;", null);
                rw.writeText(index + ". ", null);
                rw.endElement("span");
            }
        
            rw.startElement("a", this);
            rw.writeAttribute("href", href, null);
            rw.writeAttribute("onfocus", "this.blur()", null); // 让超链接不产生虚线框
            rw.writeAttribute("target", (target != null ? target : "_self"), null);
            rw.writeAttribute("title", tae.getTitle(), null);
            rw.writeText(tae.getTitle(), null);
            rw.endElement("a");

            if (sdf != null) {
                rw.startElement("span", this);
                rw.writeAttribute("style", "margin-left:5px;color:gray;", null);
                rw.writeText(sdf.format(tae.getCreateDate()), null);
                rw.endElement("span");
            }

            if (_showEdit) {
                String editEvent = "window.location.href ='/admin/blog/articleEdit.faces?articleId="
                        + tae.getArticleId() + "&returnURL=" + returnURL + "'";
                String deleteEvent = "if (confirm('您真的要删除这篇文章吗? 删除后不能恢复')) {window.location.href ='/admin/blog/articleDelete.faces?articleId="
                        + tae.getArticleId() + "&returnURL=" + returnURL + "'}";
                rw.startElement("span", this);
                rw.writeAttribute("style", "margin-left:5px;color:gray;cursor:pointer", null);
                rw.writeAttribute("onclick", editEvent, null);
                rw.writeText(" 编辑", null);
                rw.endElement("span");

                rw.startElement("span", this);
                rw.writeAttribute("style", "margin-left:5px;color:gray;cursor:pointer", null);
                rw.writeAttribute("onclick", deleteEvent, null);
                rw.writeText(" 删除", null);
                rw.endElement("span");
            }
        rw.endElement("div");
    }
}
