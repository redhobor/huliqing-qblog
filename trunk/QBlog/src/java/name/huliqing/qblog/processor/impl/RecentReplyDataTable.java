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
import name.huliqing.qblog.entity.ReplyEn;
import name.huliqing.qblog.enums.Config;

/**
 *
 * @author huliqing
 */
public class RecentReplyDataTable extends HtmlDataTable implements java.io.Serializable{

    private Object[] _values;
    private Boolean showIndex;
    private Boolean showDate;
    private String dateFormat;
    private String timeZone;

    @Override
    public void restoreState(FacesContext fc, Object state) {
        _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.showIndex = (Boolean) _values[1];
        this.showDate = (Boolean) _values[2];
        this.dateFormat = (String) _values[3];
        this.timeZone = (String) _values[4];
    }

    @Override
    public Object saveState(FacesContext fc) {
        if (_values == null) {
            _values = new Object[5];
        }
        _values[0] = super.saveState(fc);
        _values[1] = this.showIndex;
        _values[2] = this.showDate;
        _values[3] = this.dateFormat;
        _values[4] = this.timeZone;
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

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
    }

    @Override
    public void encodeChildren(FacesContext fc) throws IOException {
        List<ReplyEn> res = (List<ReplyEn>) this.getValue();
        if (res != null && !res.isEmpty()) {
            ResponseWriter rw = fc.getResponseWriter();
            SimpleDateFormat sdf = null;
            if (getShowDate()) {
                try {
                    sdf = new SimpleDateFormat((dateFormat != null && !"".equals(dateFormat)) ?
                        dateFormat : ConfigManager.getInstance().getAsString(Config.CON_SYSTEM_DATE_FORMAT));
                } catch (Exception e) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                }
                sdf.setTimeZone(TimeZone.getTimeZone((timeZone != null && !"".equals(timeZone)) ?
                    timeZone : ConfigManager.getInstance().getAsString(Config.CON_SYSTEM_TIME_ZONE)));
            }
            Long pageId = QBlog.getPageId();
            int i = 0;
            for (ReplyEn re : res) {
                encodeArticle(rw, re, ++i, sdf, pageId);
            }
        }
    }

    public void encodeArticle(ResponseWriter rw, ReplyEn re, Integer index,
            SimpleDateFormat sdf, Long pageId) throws IOException {
        rw.startElement("div", this);
        rw.writeAttribute("style", "padding:3px;", null);
        if (getShowIndex()) {
            rw.startElement("span", this);
            rw.writeAttribute("style", "color:gray;", null);
            rw.writeText(index + ". ", null);
            rw.endElement("span");
        }
        String href = "/article/articleId=" + re.getArticle();
        if (pageId != null) {
            href += ",pageId=" + pageId;
        }
        String content = re.getContent() != null ? re.getContent().getValue() : "";
        if (content != null && content.length() > 30) {
            content = content.substring(0, 30);
        }
        rw.startElement("a", this);
        rw.writeAttribute("href", href, null);
        rw.writeText(content, null);
        rw.endElement("a");
        if (sdf != null) {
            rw.startElement("div", this);
            rw.writeAttribute("style", "font-size:0.8em;text-align:right;color:#C0C0C0;", null);
            rw.writeText(" - " + sdf.format(re.getCreateDate()), null);
            rw.endElement("div");
        }
        rw.endElement("div");
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
