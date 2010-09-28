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
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 *
 * @author huliqing
 */
public class ModulePanelGrid extends UIComponentBase implements java.io.Serializable{

    // 每一行渲染多少列
    private Integer columns;

    // 表格宽度
    private String width;

    // 各列的垂直对齐方式
    private String valign;

    // 表格的整体样式
    private String styleFull;
    
    // 列样式
    private String[] styleColumns;

    // 表格边框
    private String border;

    private Object[] _values;
    @Override
    public void restoreState(FacesContext fc, Object state) {
        _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.columns = (Integer) _values[1];
        this.width = (String) _values[2];
        this.valign = (String) _values[3];
        this.styleFull = (String) _values[4];
        this.styleColumns = (String[]) _values[5];
        this.border = (String) _values[6];
    }

    @Override
    public Object saveState(FacesContext fc) {
        if (_values == null) {
            _values = new Object[7];
        }
        _values[0] = super.saveState(fc);
        _values[1] = this.columns;
        _values[2] = this.width;
        _values[3] = this.valign;
        _values[4] = this.styleFull;
        _values[5] = this.styleColumns;
        _values[6] = this.border;
        return _values;
    }

    // ---- Getter and Setter

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public String[] getStyleColumns() {
        return styleColumns;
    }

    public void setStyleColumns(String[] styleColumns) {
        this.styleColumns = styleColumns;
    }

    public String getStyleFull() {
        return styleFull;
    }

    public void setStyleFull(String styleFull) {
        this.styleFull = styleFull;
    }

    public String getValign() {
        return valign;
    }

    public void setValign(String valign) {
        this.valign = valign;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    // ---- Encode

    @Override
    public void encodeBegin(FacesContext context) throws IOException {}

    @Override
    public void encodeEnd(FacesContext context) throws IOException {}

    @Override
    public void encodeChildren(FacesContext fc) throws IOException {
        if (this.getChildCount() <= 0) {
            return;
        }
        if (columns == null || columns <= 0) {
            columns = 1;
        }

        List<UIComponent> _children = this.getChildren();
        ResponseWriter rw = fc.getResponseWriter();

        rw.startElement("table", this);
        if (border != null) 
            rw.writeAttribute("border", border, null);

        if (styleFull != null)
            rw.writeAttribute("style", styleFull, null);

        if (width != null)
            rw.writeAttribute("width", width, null);
        
        List<UIComponent> temp = new ArrayList<UIComponent>(columns);
        for (UIComponent child : _children) {
            temp.add(child);
            if (temp.size() >= columns.intValue()) {
                renderOneRow(fc, temp);
                temp.clear();
            }
        }
        
        if (!temp.isEmpty()) {
            renderOneRow(fc, temp);
            temp.clear();
        }
        rw.endElement("table");
    }

    private void renderOneRow(FacesContext fc, List<UIComponent> uic) throws IOException {
        ResponseWriter rw = fc.getResponseWriter();
        rw.startElement("tr", this);
        int count = 0;
        for (UIComponent child : uic) {
            rw.startElement("td", this);
            if (valign != null) {
                rw.writeAttribute("valign", valign, null);
            }
            if (styleColumns != null && styleColumns.length > 0) {
                rw.writeAttribute("style", styleColumns[count % styleColumns.length], null);
            }
            child.encodeAll(fc);

            rw.endElement("td");
            count++;
        }
        rw.endElement("tr");
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public String getFamily() {
        return null;
    }
}
