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

import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlForm;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.processor.XmlProcessor2;
import name.huliqing.qblog.processor.attr.AttrInputText;
import name.huliqing.qblog.processor.attr.AttrSelectBooleanCheckbox;
import name.huliqing.qblog.processor.attr.AttrSelectOneRadio;
import name.huliqing.qblog.processor.attr.Attribute2;
import name.huliqing.qfaces.component.Scroller;

/**
 *
 * @author huliqing
 */
public class AlbumProcessor extends XmlProcessor2{

    public List<Attribute2> getRequiredAttributes() {
        AttrInputText columns = new AttrInputText("Columns", "1", "每行显示多少列");

        AttrInputText pageSize = new AttrInputText("Page Size", "4", "每页显示多少个相册");

        AttrInputText displayPage = new AttrInputText("Display Page", "3", "最多的可见页码数,默认:3");

        AttrSelectBooleanCheckbox displayJump = new AttrSelectBooleanCheckbox("Display Jump", "false", "是否显示一个输入框用于输入页面进行翻页跳转.");

        AttrSelectOneRadio order = new AttrSelectOneRadio("Order", "createDate", "排序,按相册的创建时间还是按相册名称进行排列,默认：按时间");
        order.addItem("createDate", "按时间");
        order.addItem("name", "按名称");

        AttrSelectOneRadio sort = new AttrSelectOneRadio("Sort", "false", "正序还是倒序");
        sort.addItem("true", "正序");
        sort.addItem("false", "倒序");

        AttrSelectOneRadio face = new AttrSelectOneRadio("Face", "1", "翻页条的样式,可选择0/1,默认:2");
        face.addItem("0", "样式1");
        face.addItem("1", "样式2");

        AttrSelectOneRadio display = new AttrSelectOneRadio("Display", "bottom", "将翻页栏显示在哪个位置,默认：下面");
        display.addItem("top", "上面");
        display.addItem("bottom", "下面");
        display.addItem("both", "上下");

        AttrSelectOneRadio autoScroller = new AttrSelectOneRadio("Scroller", "true",
                "选择“自动”，则只有在必要时显示翻页栏，如相册数多于一页时。 选择“总是”，则不管相册数是否多于一页总是显示翻页。");
        autoScroller.addItem("true", "自动");
        autoScroller.addItem("false", "总是显示");

        AttrSelectBooleanCheckbox showInfo = new AttrSelectBooleanCheckbox("Show Info", "false", "是否显示相册数及页数");

        AttrSelectOneRadio position = new AttrSelectOneRadio("Position", "center", "翻页栏位置");
        position.addItem("left", "左对齐");
        position.addItem("center", "居中");
        position.addItem("right", "右对齐");

        AttrSelectBooleanCheckbox showPrivate = new AttrSelectBooleanCheckbox("Show Private", "true",
                "显示隐私相册，当为登录状态时可以显示出隐私的相册,其它任何用户都看不到。");
        
        List<Attribute2> as = new ArrayList<Attribute2>(12);
        as.add(columns);
        as.add(order);
        as.add(sort);
        as.add(pageSize);
        as.add(displayPage);
        as.add(displayJump);
        as.add(showInfo);
        as.add(display);
        as.add(face);
        as.add(autoScroller);
        as.add(position);
        as.add(showPrivate);
        return as;
    }

    public UIComponent render(ModuleEn module) {
        AttrMap attr = getAttributes(module);

        // 必须为每个组件指定一个ID，否则可能在DataTable组件之内ID一直变动
        // 而被认为是一直需要重新初始化。并且同一个页面可能存在多个同样的组件，
        // 所以必须确保每次渲染时ID都能够唯一。（这里使用了currentTimeMillis)
        String suffix = String.valueOf(System.currentTimeMillis());
        String albumId = "album" + suffix;

        // Create Scroller
        Scroller s = new Scroller();
        s.setFor(albumId);
        s.setListenerAsExpression("#{albumFetch.loadData}");
        s.setPageSize(attr.getAsInteger("Page Size", 4));
        s.setDisplayPage(attr.getAsInteger("Display Page", 3));
        s.setDisplay(attr.getAsString("Display", "bottom"));
        s.setDisplayJump(attr.getAsBoolean("Display Jump", Boolean.FALSE));
        s.setFace(attr.getAsString("Face", "1"));
        s.setDisplayCount(attr.getAsBoolean("Show Info", Boolean.FALSE));
        s.setStrRecordTotal("相册:");
        s.setStrPageTotal("页数:");
        String position = attr.getAsString("Position", "center");
        if (position.equals("center")) {
            s.setStyle("margin:auto;");
        } else {
            s.setStyle("margin-left:12px;float:" + position);
        }
//        s.setSrcPageFirst("");
//        s.setSrcPageLast("");
//        s.setSrcPagePrevious("");
//        s.setSrcPageNext("");

        AlbumDataTable adt = new AlbumDataTable();
        adt.setId(albumId);
        adt.setColumns(attr.getAsInteger("Columns", 1));
        adt.setAutoScroller(attr.getAsBoolean("Scroller", Boolean.TRUE));
        adt.setSortField(attr.getAsString("Order", "createDate"));
        adt.setAsc(attr.getAsBoolean("Sort", Boolean.FALSE));
        adt.setShowPrivate(attr.getAsBoolean("Show Private", Boolean.TRUE));

        HtmlForm form = new HtmlForm();
        form.setStyle("margin:0;padding:0");
        form.getChildren().add(s);
            s.getChildren().add(adt);
        return form;
    }

    public String getName() {
        return "相册集";
    }

    public String getDescription() {
        return "这个渲染器生成一个相册列表";
    }

}
