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
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UISelectItem;
import javax.faces.component.html.HtmlSelectManyCheckbox;
import javax.faces.context.FacesContext;

/**
 *
 * @author huliqing
 */
public class AttrSelectManyCheckbox extends Attribute2{

    public AttrSelectManyCheckbox(){}

    public AttrSelectManyCheckbox(String name, String value, String des) {
        super(name, value, des);
    }

    @Override
    public String getAttrType() {
        return "AttrSelectManyCheckbox";
    }
    private List<String[]> items = new ArrayList<String[]>();

    private Object[] _values;
    @Override
    public void restoreState(FacesContext fc, Object state) {
        _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.items = (List<String[]>) _values[1];
    }

    @Override
    public Object saveState(FacesContext fc) {
        if (_values == null) {
            _values = new Object[2];
        }
        _values[0] = super.saveState(fc);
        _values[1] = this.items;
        return _values;
    }

    public void addItem(String value, String label) {
        if (value == null || label == null)
            return;
        items.add(new String[]{value, label});
    }

    @Override
    public void encodeBegin(FacesContext fc) throws IOException {
        super.encodeBegin(fc);
        HtmlSelectManyCheckbox mm = new HtmlSelectManyCheckbox();
        mm.setLayout("pageDirection");
        mm.setBorder(0);
        if (getStyle() != null) {
            mm.setStyle(getStyle());
        } else {
            mm.setStyle("margin:0;padding:0;margin-left:-7px;");
        }
        String attrValue = getAttrValue();
        if (attrValue != null) {
            mm.setValue(attrValue.split(","));
        }
        for (String[] item : items) {
            UISelectItem it = new UISelectItem();
            it.setItemValue(item[0]);
            it.setItemLabel(item[1]);
            mm.getChildren().add(it);
        }
        this.getChildren().clear();
        this.getChildren().add(mm);
    }

    @Override
    public void decode(FacesContext fc) {
        HtmlSelectManyCheckbox checkbox = (HtmlSelectManyCheckbox) this.getChildren().get(0);
        Object value = checkbox.getSubmittedValue();
        if (value != null) {
            String[] valueArr = (String[]) value;
            if (valueArr.length <= 0) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (String val : valueArr) {
                sb.append(val).append(",");
            }
            setAttrValue(sb.substring(0, sb.length() - 1));
        }
    }

}
