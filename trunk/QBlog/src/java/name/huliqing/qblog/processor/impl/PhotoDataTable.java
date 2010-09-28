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
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.PhotoEn;
import name.huliqing.qfaces.component.Scroller;

/**
 *
 * @author huliqing
 */
public class PhotoDataTable extends HtmlDataTable implements java.io.Serializable{

    // 默认的相册id，当指定了always为true,或者找不到folderId时使用这个defaultAlbum
    private Long defaultAlbum;

    // 是否固定显示相册,如果为固定，则只显示defaultAlbum所指定的相册
    private Boolean always;
    private String sortField;
    private Boolean asc;

    // ---- 以上4个参数在 PhotoFetch中会用到,把状态保存在这里是因为:如果页面中存在
    // ---- 多个相集，在翻页及链接上可能出现混乱，所以必须区分每个相集的各自状态

    // 每行显示多少列
    private Integer columns;

    // 是否自动显示scroller
    private Boolean autoScroller;

    private Boolean showName;
    private Boolean showSize;
    private Boolean showEdit;
    private Boolean showLine;

    // 文件夹获取码
    private String fetchCode;
    
    // 每张图片的最大宽度限制,这会添加到 style的 max-width属性中
    private Integer picMaxWidth;

    private Object[] _values;
    @Override
    public void restoreState(FacesContext fc, Object state) {
        _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.columns = (Integer) _values[1];
        this.autoScroller = (Boolean) _values[2];
        this.showSize = (Boolean) _values[3];
        this.showEdit = (Boolean) _values[4];
        this.showLine = (Boolean) _values[5];
        this.showName = (Boolean) _values[6];
        this.fetchCode = (String) _values[7];
        this.picMaxWidth = (Integer) _values[8];
        // ----
        this.defaultAlbum = (Long) _values[9];
        this.always = (Boolean) _values[10];
        this.sortField = (String) _values[11];
        this.asc = (Boolean) _values[12];
    }

    @Override
    public Object saveState(FacesContext fc) {
        if (_values == null) {
            _values = new Object[13];
        }
        _values[0] = super.saveState(fc);
        _values[1] = this.columns;
        _values[2] = this.autoScroller;
        _values[3] = this.showSize;
        _values[4] = this.showEdit;
        _values[5] = this.showLine;
        _values[6] = this.showName;
        _values[7] = this.fetchCode;
        _values[8] = this.picMaxWidth;
        // ----
        _values[9] = this.defaultAlbum;
        _values[10] = this.always;
        _values[11] = this.sortField;
        _values[12] = this.asc;
        return _values;
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public Boolean getAutoScroller() {
        return autoScroller;
    }

    public void setAutoScroller(Boolean autoScroller) {
        this.autoScroller = autoScroller;
    }

    public Boolean getShowEdit() {
        return showEdit;
    }

    public void setShowEdit(Boolean showEdit) {
        this.showEdit = showEdit;
    }

    public Boolean getShowLine() {
        return showLine;
    }

    public void setShowLine(Boolean showLine) {
        this.showLine = showLine;
    }

    public Boolean getShowSize() {
        return showSize;
    }

    public void setShowSize(Boolean showSize) {
        this.showSize = showSize;
    }

    public Boolean getShowName() {
        return showName;
    }

    public void setShowName(Boolean showName) {
        this.showName = showName;
    }

    public String getFetchCode() {
        return fetchCode;
    }

    public void setFetchCode(String fetchCode) {
        this.fetchCode = fetchCode;
    }

    public Integer getPicMaxWidth() {
        return picMaxWidth;
    }

    public void setPicMaxWidth(Integer picMaxWidth) {
        this.picMaxWidth = picMaxWidth;
    }

    public Boolean getAlways() {
        return always;
    }

    public void setAlways(Boolean always) {
        this.always = always;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }

    public Long getDefaultAlbum() {
        return defaultAlbum;
    }

    public void setDefaultAlbum(Long defaultAlbum) {
        this.defaultAlbum = defaultAlbum;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    // ----

    @Override
    public void encodeBegin(FacesContext context) throws IOException {}

    @Override
    public void encodeEnd(FacesContext context) throws IOException {}

    @Override
    public void setValue(Object value) {
        super.setValue(value);
        // Auto Scroller
        if (getAutoScroller()) {
            Scroller s = (Scroller) this.getParent();
            s.setVisible(s.getPageTotal() > 1);
        }
    }

    @Override
    public void encodeChildren(FacesContext fc) throws IOException {
        List<PhotoEn> fes = (List<PhotoEn>) this.getValue();

        Integer _columns = columns;
        if (_columns == null || _columns <= 0)
            _columns = 1;

        if (fes != null && !fes.isEmpty()) {
            int totalRow = fes.size() / _columns;
            if (fes.size() % _columns != 0) {
                totalRow++;
            }
            boolean showEditButton = (showEdit != null && showEdit && QBlog.getCurrentVisitor().isLogin());
            String returnURL = QBlog.getOriginalURI(true, true);

            ResponseWriter rw = fc.getResponseWriter();
            rw.startElement("table", this);
            rw.writeAttribute("border", "0", null);
            rw.writeAttribute("cellspacing", "0", null);
            rw.writeAttribute("cellpadding", "2", null);
            rw.writeAttribute("style", "text-align:center;", null);
            if (getWidth() != null) {
                rw.writeAttribute("width", getWidth(), null);
            }
            List<PhotoEn> oneRow = new ArrayList<PhotoEn>(_columns);
            int currentRow = -1;
            for (PhotoEn pe : fes) {
                oneRow.add(pe);
                // 渲染一行，然后清除，重启另一行。这里没有使用单个PhotoEn进行渲染
                // 主要是为了使用table进行布局，这可以让每一行的图片名称，大小，编辑按钮
                // 等信息并排对齐，而不用管每张图片的高度
                if (oneRow.size() >= _columns.intValue()) {
                    currentRow++;
                    encodeOneRow(rw, showEditButton, (showLine != null && showLine && currentRow < (totalRow - 1)), oneRow, currentRow, returnURL);
                    oneRow.clear();
                }
            }
            // 最后几个photo可能不足满行（如果满行，则这里为empty）
            if (!oneRow.isEmpty()) {
                encodeOneRow(rw, showEditButton, false, oneRow, ++currentRow, returnURL);
//                oneRow.clear();
            }
            rw.endElement("table");
        }
    }

    // 一行一行渲染
    private void encodeOneRow(ResponseWriter rw,
            boolean showEditButton,
            boolean renderLine,
            List<PhotoEn> photos,
            int rowIndex,
            String returnURL) throws IOException {
        
        String prefix = getClientId() + ":" + rowIndex + ":";
        String rowPhoto = prefix + "p";
        String rowName  = prefix + "n";
        String rowSize  = prefix + "s";
        String rowEdit  = prefix + "e";
        String eMouseOver = "document.getElementById('" + rowEdit + "').style.display = ''";
        String eMouseOut  = "document.getElementById('" + rowEdit + "').style.display = 'none'";

        // row: photo
        rw.startElement("tr", this);
        rw.writeAttribute("id", rowPhoto, null);
        rw.writeAttribute("onmouseover", eMouseOver, null);
        rw.writeAttribute("onmouseout",  eMouseOut, null);
        for (PhotoEn p : photos) {
            String url = "/photo/photoId=" + p.getPhotoId();
            if (fetchCode != null)
                url += ",fetchCode=" + fetchCode;
            rw.startElement("td", this);
                rw.startElement("a", this);
                rw.writeAttribute("href", url, null);
                rw.writeAttribute("target", "_blank", null);
                rw.writeAttribute("title", "看大图", null);
                rw.startElement("img", this);
                rw.writeAttribute("style", "max-width:" + (picMaxWidth != null ? picMaxWidth : "100") + "px;", null);
                rw.writeAttribute("src", (url + ",min=true"), null);
                rw.writeAttribute("alt", p.getName(), null);
                rw.endElement("img");
                rw.endElement("a");
            rw.endElement("td");
        }
        rw.endElement("tr");

        // row: name
        if (showName != null && showName) {
            rw.startElement("tr", this);
            rw.writeAttribute("id", rowName, null);
            rw.writeAttribute("onmouseover", eMouseOver, null);
            rw.writeAttribute("onmouseout",  eMouseOut, null);
            for (PhotoEn p : photos) {
                rw.startElement("td", this);
                rw.writeAttribute("style", "color:gray;", null);
                rw.writeText(p.getName() + "." + p.getSuffix(), null);
                rw.endElement("td");
            }
            rw.endElement("tr");
        }

        // row: size
        if (showSize != null && showSize) {
            rw.startElement("tr", this);
            rw.writeAttribute("id", rowSize, null);
            rw.writeAttribute("onmouseover", eMouseOver, null);
            rw.writeAttribute("onmouseout",  eMouseOut, null);
            for (PhotoEn p : photos) {
                rw.startElement("td", this);
                rw.writeAttribute("style", "color:gray;font-size:0.8em", null);
                rw.writeText((p.getFileSize() / 1024) + " K", null);
                rw.endElement("td");
            }
            rw.endElement("tr");
        }

        // row: edit
        if (showEditButton) {
            rw.startElement("tr", this);
            rw.writeAttribute("id", rowEdit, null);
            rw.writeAttribute("onmouseover", eMouseOver, null);
            rw.writeAttribute("onmouseout",  eMouseOut, null);
            rw.writeAttribute("style", "display:none;", null);
            for (PhotoEn p : photos) {
                rw.startElement("td", this);
                    String editURL = "/admin/photo/photoEdit.faces?photoId=" + p.getPhotoId() + "&returnURL=" + returnURL;
                    String deleteURL = "/admin/photo/photoDelete.faces?photoId=" + p.getPhotoId() + "&returnURL=" + returnURL;

                    rw.startElement("a", this);
                    rw.writeAttribute("style", "margin:0 5px;", null);
                    rw.writeAttribute("href", editURL, null);
                    rw.writeAttribute("target", "_self", null);
                    rw.startElement("img", this);
                    rw.writeAttribute("src", "/_res/image/button-edit.gif", null);
                    rw.writeAttribute("alt", "", null);
                    rw.endElement("img");
                    rw.endElement("a");

                    rw.startElement("a", this);
                    rw.writeAttribute("style", "margin:0 5px;", null);
                    rw.writeAttribute("href", deleteURL, null);
                    rw.writeAttribute("target", "_self", null);
                    rw.writeAttribute("onclick", "return confirm('您真的要删除该图片吗？删除后不能恢复。(图片：" + p.getName() + "." + p.getSuffix() + ")')", null);
                    rw.startElement("img", this);
                    rw.writeAttribute("src", "/_res/image/button-delete.gif", null);
                    rw.writeAttribute("alt", "", null);
                    rw.endElement("img");
                    rw.endElement("a");
                rw.endElement("td");
            }
            rw.endElement("tr");
        }

        rw.startElement("tr", this);
        rw.startElement("td", this);
        rw.writeAttribute("colspan", photos.size(), null);
        rw.writeAttribute("height", getCellspacing(), null);
        if (renderLine) {
            rw.writeAttribute("style", "border-top:1px dotted #C0C0C0;", null);
        }
        rw.endElement("td");
        rw.endElement("tr");
    }
    
    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
