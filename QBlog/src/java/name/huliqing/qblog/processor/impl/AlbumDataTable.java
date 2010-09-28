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
import java.util.List;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.FolderEn;
import name.huliqing.qfaces.component.Scroller;

/**
 *
 * @author huliqing
 */
public class AlbumDataTable extends HtmlDataTable implements java.io.Serializable{

    // 排序及是否显示private状态的相册
    private String sortField;
    private Boolean asc;
    private Boolean showPrivate;

    // 每行显示多少列
    private Integer columns;

    // 是否自动显示scroller
    private Boolean autoScroller;

    private Object[] _values;
    @Override
    public void restoreState(FacesContext fc, Object state) {
        _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.columns = (Integer) _values[1];
        this.autoScroller = (Boolean) _values[2];
        this.sortField = (String) _values[3];
        this.asc = (Boolean) _values[4];
        this.showPrivate = (Boolean) _values[5];
    }

    @Override
    public Object saveState(FacesContext fc) {
        if (_values == null) {
            _values = new Object[6];
        }
        _values[0] = super.saveState(fc);
        _values[1] = this.columns;
        _values[2] = this.autoScroller;
        _values[3] = this.sortField;
        _values[4] = this.asc;
        _values[5] = this.showPrivate;
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

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }

    public Boolean getShowPrivate() {
        return showPrivate;
    }

    public void setShowPrivate(Boolean showPrivate) {
        this.showPrivate = showPrivate;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }
    
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
        List<FolderEn> fes = (List<FolderEn>) this.getValue();
        Integer _columns = columns;
        if (_columns == null || _columns <= 0)
            _columns = 1;

        if (fes != null && !fes.isEmpty()) {
            ResponseWriter rw = fc.getResponseWriter();
            // Record start,需要记住翻页的起始位置,这样在翻到比如第二页之后，再点击相册
            // 就不会让相册列表重置到第一页了
            Scroller s = (Scroller) this.getParent();
            String urlQueryForStart = s.getClientId(fc) + ":start=" + s.getRecordStart();
            Long pageId = QBlog.getPageId();

            rw.startElement("table", this);
            rw.writeAttribute("border", "0", null);
            rw.writeAttribute("width", "100%", null);
            rw.writeAttribute("cellspacing", "3", null);
            rw.writeAttribute("cellpadding", "3", null);
            rw.writeAttribute("style", "text-align:center;", null);
            rw.startElement("tbody", this);
            for (int i = 0; i < fes.size(); i++) {
                if (i % _columns == 0) {
                    if (i != 0) {
                        rw.endElement("tr");
                    }
                    rw.startElement("tr", this);
                }
                rw.startElement("td", this);
                encodeAlbum(rw, fes.get(i), pageId, urlQueryForStart);
                rw.endElement("td");
            }
            rw.endElement("tr");
            rw.endElement("tbody");
            rw.endElement("table");
        }
    }

    public void encodeAlbum(ResponseWriter rw, FolderEn fe, Long pageId, String query) throws IOException {
        String href = "/page/pageId=" + pageId + ",folderId=" + fe.getFolderId();
        // query保证相册列表在翻页后，点击某个相册时不会导致重置翻页
        if (query != null) {
            href += "?" + query;
        }
        
        // 相框
        rw.startElement("div", this);
        rw.writeAttribute("style",
                "border:0px solid gray;width:185px;height:155px;" +
                "background:url(/_res/image/album.png) no-repeat;", null);

            // 封面,为了使这个相框布局在各个浏览器下的表现一致，花了不少时间调整，不要较易
            // 修改这里的代码。
            rw.startElement("div", this); 
            rw.writeAttribute("style", "border:0;height:21px;", null);
            rw.endElement("div");
            rw.startElement("div", this);
            rw.writeAttribute("style", "border:0;height:95px;margin:0 30px 0 39px;text-align:center;overflow:hidden;", null);
            if (fe.getCover() != null && !"".equals(fe.getCover())) {
                rw.startElement("a", this);
                rw.writeAttribute("href", href, null);
                rw.writeAttribute("target", "_self", null);
                    rw.startElement("img", this);
                    rw.writeAttribute("style", "max-width:114px;max-height:95px;", null);
                    rw.writeAttribute("src", fe.getCover(), null);
                    rw.writeAttribute("alt", "", null);
                    rw.writeAttribute("title", "浏览相册", null);
                    rw.endElement("img");
                rw.endElement("a");
            } else {
                rw.writeText("无封面", null);
            }
            rw.endElement("div");

            // 名称
            rw.startElement("div", this);
            rw.writeAttribute("style", "height:16px;line-height:16px;border:0px solid red;margin:10px 10px 0 12px;text-align:center;overflow:hidden;", null);
                if (QBlog.getCurrentVisitor().isLogin()) {
                    String uploadURL = "/admin/photo/photoUpload.faces?folderId=" + fe.getFolderId();
                    String returnURL = QBlog.getOriginalURI(true, true);
                    if (returnURL != null) {
                        uploadURL += "&returnURL=" + returnURL;
                    }
                    rw.startElement("a", this);
                    rw.writeAttribute("href", uploadURL, null);
                    rw.writeAttribute("target", "_self", null);
                    rw.startElement("img", this);
                    rw.writeAttribute("src", "/_res/image/button-add.gif", null);
                    rw.writeAttribute("alt", "Upload Photo", null);
                    rw.writeAttribute("title", "上传图片", null);
                    rw.writeAttribute("style", "float:right;", null);
                    rw.endElement("img");
                    rw.endElement("a");
                }
                rw.startElement("a", this);
                rw.writeAttribute("href", href, null);
                rw.writeAttribute("target", "_self", null);
                rw.writeText(fe.getName(), null);
                rw.endElement("a");
            rw.endElement("div");
        rw.endElement("div");
    }
    
    @Override
    public boolean getRendersChildren() {
        return true;
    }
}
