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
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.processor.XmlProcessor2;
import name.huliqing.qblog.processor.attr.AttrInputText;
import name.huliqing.qblog.processor.attr.AttrSelectBooleanCheckbox;
import name.huliqing.qblog.processor.attr.Attribute2;
import name.huliqing.qblog.service.ArticleSe;

/**
 * 最近发表的文章列表,该处理器用于列出最近发表的文章列表。
 * @author huliqing
 */
public class RecentPostArticleProcessor extends XmlProcessor2{

    public List<Attribute2> getRequiredAttributes() {
        List<Attribute2> as = new ArrayList<Attribute2>(5);
        as.add(new AttrInputText("Size", "6", "显示的文章数,默认:6"));
        as.add(new AttrSelectBooleanCheckbox("Show Index", "true", "是否显示序号，请填写 true/false,默认:true"));
        as.add(new AttrSelectBooleanCheckbox("Show Date", "false", "是否显示发表日期，请填写true/false, 默认:false"));
        as.add(new AttrInputText("Date Format", "yyyy-MM-dd HH:mm", "文章发表日期的格式，只有Show Date为true时才有意义,默认:yyyy-MM-dd HH:mm"));
        as.add(new AttrInputText("Time Zone", "GMT+8", "时区，默认:GMT+8"));
        return as;
    }

    public UIComponent render(ModuleEn module) {
        AttrMap attr = getAttributes(module);

        Integer size = attr.getAsInteger("Size", 6);
        Boolean showIndex = attr.getAsBoolean("Show Index", Boolean.TRUE);
        Boolean showDate = attr.getAsBoolean("Show Date", Boolean.FALSE);
        String dateFormat = attr.getAsString("Date Format", "yyyy-MM-dd");
        String timeZone = attr.getAsString("Time Zone", "GMT+8");

        List<ArticleEn> aes = ArticleSe.findAllPublic(size); 

        // Create DataTable
        RecentPostArticleDataTable table = new RecentPostArticleDataTable();
        table.setValue(aes);
        table.setShowIndex(showIndex);
        table.setShowDate(showDate);
        table.setDateFormat(dateFormat);
        table.setTimeZone(timeZone);
        return table;
    }

    public String getName() {
        return "最近发表的文章";
    }

    public String getDescription() {
        return "显示我最近发表的文章";
    }

}
