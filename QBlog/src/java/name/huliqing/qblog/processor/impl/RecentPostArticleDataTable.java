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
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.enums.Config;

/**
 *
 * @author huliqing
 */
public class RecentPostArticleDataTable extends HtmlDataTable implements java.io.Serializable{

    private Boolean showIndex;
    private Boolean showDate;
    private String dateFormat;
    private String timeZone;
    // 是否显示编辑、删除按钮
    private Boolean showEdit;
    // 打开文章的目标窗口
    private String target;

    private Object[] _values;
    @Override
    public void restoreState(FacesContext fc, Object state) {
        _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.showIndex = (Boolean) _values[1];
        this.showDate = (Boolean) _values[2];
        this.dateFormat = (String) _values[3];
        this.timeZone = (String) _values[4];
        this.showEdit = (Boolean) _values[5];
        this.target = (String) _values[6];
    }

    @Override
    public Object saveState(FacesContext fc) {
        if (_values == null) {
            _values = new Object[7];
        }
        _values[0] = super.saveState(fc);
        _values[1] = this.showIndex;
        _values[2] = this.showDate;
        _values[3] = this.dateFormat;
        _values[4] = this.timeZone;
        _values[5] = this.showEdit;
        _values[6] = this.target;
        return _values;
    }

    public Boolean getShowIndex() {
        return showIndex;
    }

    public void setShowIndex(Boolean showIndex) {
        this.showIndex = showIndex;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public Boolean getShowDate() {
        return showDate;
    }

    public void setShowDate(Boolean showDate) {
        this.showDate = showDate;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
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
    
    @Override
    public void encodeBegin(FacesContext context) throws IOException {}

    @Override
    public void encodeEnd(FacesContext context) throws IOException {}

    @Override
    public void encodeChildren(FacesContext fc) throws IOException {
        List<ArticleEn> aes = (List<ArticleEn>) this.getValue();
        if (aes != null && !aes.isEmpty()) {
            ResponseWriter rw = fc.getResponseWriter();
            SimpleDateFormat sdf = null;
            if (getShowDate()) {
                try {
                    sdf = new SimpleDateFormat(dateFormat != null ?
                        dateFormat : ConfigManager.getInstance().getAsString(Config.CON_SYSTEM_DATE_FORMAT));
                } catch (Exception e) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    e.printStackTrace();
                }
                sdf.setTimeZone(TimeZone.getTimeZone(timeZone != null ? timeZone : ConfigManager.getInstance().getAsString(Config.CON_SYSTEM_TIME_ZONE)));
            }
            Long pageId = QBlog.getPageId();
            int i = 0;
            boolean _showEdit = (QBlog.getCurrentVisitor().isLogin() && showEdit != null && showEdit);
            String returnURL = QBlog.getOriginalURI(true, true);
            for (ArticleEn ae : aes) {
                encodeArticle(rw, ae, ++i, sdf, pageId, _showEdit, returnURL);
            }
        }
    }

    public void encodeArticle(ResponseWriter rw, ArticleEn ae, Integer index,
            SimpleDateFormat sdf, Long pageId, boolean _showEdit, String returnURL) throws IOException {
        String href = "/article/articleId=" + ae.getArticleId();
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
            rw.writeAttribute("title", ae.getTitle(), null);
            rw.writeText(ae.getTitle(), null);
            rw.endElement("a");
            if (sdf != null) {
                rw.startElement("span", this);
                rw.writeAttribute("style", "margin-left:5px;color:gray;", null);
                rw.writeText(sdf.format(ae.getCreateDate()), null);
                rw.endElement("span");
            }
            if (_showEdit) {
                String editEvent = "window.location.href ='/admin/blog/articleEdit.faces?articleId="
                        + ae.getArticleId() + "&returnURL=" + returnURL + "'";
                String deleteEvent = "if (confirm('您真的要删除这篇文章吗? 删除后不能恢复')) {window.location.href ='/admin/blog/articleDelete.faces?articleId="
                        + ae.getArticleId() + "&returnURL=" + returnURL + "'}";
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

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
