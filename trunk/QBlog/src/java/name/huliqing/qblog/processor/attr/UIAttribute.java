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
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

/**
 *
 * @author huliqing
 */
public class UIAttribute extends UIComponentBase{
    
    private Boolean loaded;

    private Object[] _values;
    @Override
    public void restoreState(FacesContext fc, Object state) {
        _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.loaded = (Boolean) _values[1];
    }

    @Override
    public Object saveState(FacesContext fc) {
        if (_values == null) {
            _values = new Object[2];
        }
        _values[0] = super.saveState(fc);
        _values[1] = this.loaded;
        return _values;
    }

    @Override
    public void encodeBegin(FacesContext context) throws IOException {
        if (loaded == null || !loaded) {
            loaded = true;
            List<Attribute2> attrs = (List<Attribute2>) this.getAttributes().get("value");
            if (attrs != null && !attrs.isEmpty()) {
                getChildren().clear();
                for (Attribute2 attr : attrs) {
                    this.getChildren().add((UIComponent) attr);
                }
            }
        }
    }

    @Override
    public void encodeChildren(FacesContext fc) throws IOException {
        List<UIComponent> children = getChildren();
        ResponseWriter rw = fc.getResponseWriter();
        rw.startElement("table", this);
        rw.writeAttribute("border", "0", null);
        rw.writeAttribute("cellspacing", "3", null);
        rw.writeAttribute("cellpadding", "3", null);
        int i = 0;
        for (UIComponent child : children) {
            i++;
            rw.startElement("tr", this);
            rw.writeAttribute("style", (i % 2 == 0 ? "background:#F6F6F6" : ""), null);

            rw.startElement("td", this);
            rw.writeAttribute("width", "120", null);
            rw.writeAttribute("style", "text-align:right;", null);
            child.encodeBegin(fc);
            rw.endElement("td");

            rw.startElement("td", this);
            rw.writeAttribute("width", "350", null);
            child.encodeChildren(fc);
            rw.endElement("td");

            rw.startElement("td", this);
            rw.writeAttribute("style", "color:gray;", null);
            child.encodeEnd(fc);
            rw.endElement("td");
            
            rw.endElement("tr");
        }

        rw.endElement("table");
    }

    @Override
    public void encodeEnd(FacesContext context) throws IOException {
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
