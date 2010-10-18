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
public class ArticlesProcessor extends XmlProcessor2{

    @Override
    public List<Attribute2> getRequiredAttributes() {
        List<Attribute2> as = new ArrayList<Attribute2>(10);
        AttrInputText pageSize = new AttrInputText("PageSize", "10", "每页显示多少篇文章，默认10");
        AttrInputText displayPage = new AttrInputText("DisplayPage", "9", "最多的可见页数,默认:9");
        AttrSelectBooleanCheckbox displayJump = new AttrSelectBooleanCheckbox("DisplayJump", "false", "是否显示一个输入框用于输入页面进行翻页跳转.");
        AttrSelectOneRadio target = new AttrSelectOneRadio("Target", "_self", "打开文章连接的目标窗口。");
        target.addItem("_self", "当前窗口");
        target.addItem("_blank", "新窗口");
        AttrSelectBooleanCheckbox showSummary = new AttrSelectBooleanCheckbox("Show Summary", "true", "是否显示摘要信息.");
        AttrSelectBooleanCheckbox showFooter = new AttrSelectBooleanCheckbox("Show Footer", "true", "是否显示文章Footer,该区域包含发表日期，评论数，阅读数及编辑按钮.");
        AttrSelectOneRadio face = new AttrSelectOneRadio("Face", "1", "翻页条的样式,可选择1/2,默认:2");
        face.addItem("0", "样式1");
        face.addItem("1", "样式2");
        AttrInputText timeZone = new AttrInputText("TimeZone", "GMT+8", "文章发表时间的时区设置,默认:GMT+8");
        AttrInputText pattern = new AttrInputText("Pattern", "yyyy-MM-dd HH:mm", "文章发表时间的日期时间格式，默认：yyyy-MM-dd HH:mm");
        AttrSelectOneRadio display = new AttrSelectOneRadio("Display", "bottom", "将翻页栏显示在哪个位置,默认：下面");
        display.addItem("top", "上面");
        display.addItem("bottom", "下面");
        display.addItem("both", "上下");

        as.add(pageSize);
        as.add(displayPage);
        as.add(displayJump);
        as.add(showSummary);
        as.add(showFooter);
        as.add(target);
        as.add(display);
        as.add(face);
        as.add(timeZone);
        as.add(pattern);

        return as;
    }
        
    @Override
    public UIComponent render(ModuleEn module) {
        AttrMap attr = getAttributes(module);

        // 必须为每个组件指定一个ID，否则可能在DataTable组件之内ID一直变动
        // 而被认为是一直需要重新初始化。并且同一个页面可能存在多个同样的组件，
        // 所以必须确保每次渲染时ID都能够唯一。（这里使用了currentTimeMillis)
        String suffix = String.valueOf(System.currentTimeMillis());
        String dId = "DID" + suffix;

        // Create Scroller
        Scroller s = new Scroller();
        s.setFor(dId);
        s.setVar("article"); // 这个var与DataTable的var意义相同
        s.setListenerAsExpression("#{articlesFetch.loadData}");
//        s.setMethod("get");

        // Load attribute
        s.setPageSize(attr.getAsInteger("PageSize", 20));
        s.setDisplayPage(attr.getAsInteger("DisplayPage", 9));
        s.setFace(attr.getAsString("Face", "1"));
        s.setDisplayJump(attr.getAsBoolean("DisplayJump", Boolean.FALSE));
        s.setDisplay(attr.getAsString("Display", "bottom"));

        ArticlesDataTable table = new ArticlesDataTable();
        s.getChildren().add(table);
        table.setId(dId);
        table.setShowSummary(attr.getAsBoolean("Show Summary", Boolean.TRUE));
        table.setShowFooter(attr.getAsBoolean("Show Footer", Boolean.TRUE));
        table.setTarget(attr.getAsString("Target", "_self"));
        table.setPattern(attr.getAsString("Pattern", "yyyy-MM-dd HH:mm"));
        table.setTimeZone(attr.getAsString("TimeZone", "GMT+8"));

        HtmlForm form = new HtmlForm();
        form.setStyle("margin:0;padding:0");
        form.getChildren().add(s);
        return form;
    }
    
    @Override
    public String getDescription() {
        return "该渲染器能够帮助您产生“文章列表”模块，这个模块可以列出系统中所有文章信息。" +
                "在创建模块之后，您可以将它添加到其它任何页面中。";
    }

    @Override
    public String getName() {
        return "文章列表渲染器";
    }

    @Override
    public Boolean defaultShowName() {
        return Boolean.FALSE;
    }


}
