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

package name.huliqing.qblog.processor.attr;

import java.io.IOException;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 *
 * @author huliqing
 */
public abstract class Attribute2 extends UIComponentBase implements java.io.Serializable{

    private String attrName;
    private String attrValue;
    private String attrDes;
    // 输入组件的样式
    private String style;

    public Attribute2(){}

    public Attribute2(String name, String value, String des) {
        setAttrName(name);
        setAttrValue(value);
        setAttrDes(des);
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public String getAttrDes() {
        return attrDes;
    }

    public void setAttrDes(String attrDes) {
        this.attrDes = attrDes;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * 返回Attr RendererType
     * @return
     */
    public abstract String getAttrType();

    private Object[] _values;
    @Override
    public void restoreState(FacesContext fc, Object state) {
        _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.attrName = (String) _values[1];
        this.attrValue = (String) _values[2];
        this.attrDes = (String) _values[3];
        this.style = (String) _values[4];
    }

    @Override
    public Object saveState(FacesContext fc) {
        if (_values == null) {
            _values = new Object[5];
        }
        _values[0] = super.saveState(fc);
        _values[1] = this.attrName;
        _values[2] = this.attrValue;
        _values[3] = this.attrDes;
        _values[4] = this.style;
        return _values;
    }

    @Override
    public String getFamily() {
        return null;
    }

    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeBegin(FacesContext fc) throws IOException {
        ResponseWriter rw = fc.getResponseWriter();
        rw.startElement("span", this);
        rw.writeText(attrName != null ? attrName : "", null);
        rw.endElement("span");
    }

    @Override
    public void encodeEnd(FacesContext fc) throws IOException {
        ResponseWriter rw = fc.getResponseWriter();
        rw.startElement("span", this);
        rw.writeText(attrDes != null ? attrDes : "", null);
        rw.endElement("span");
    }
}
