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
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.entity.TagArticleEn;
import name.huliqing.qblog.entity.TagEn;
import name.huliqing.qblog.processor.XmlProcessor2;
import name.huliqing.qblog.processor.attr.AttrInputText;
import name.huliqing.qblog.processor.attr.AttrSelectBooleanCheckbox;
import name.huliqing.qblog.processor.attr.AttrSelectOneRadio;
import name.huliqing.qblog.processor.attr.Attribute2;
import name.huliqing.qblog.service.TagArticleSe;
import name.huliqing.qblog.service.TagSe;

/**
 *
 * @author huliqing
 */
public class TagArticlesProcessor extends XmlProcessor2{

    public List<Attribute2> getRequiredAttributes() {
        List<Attribute2> as = new ArrayList<Attribute2>(10);

        // 标签选项
        List<TagEn> tags = TagSe.findAll();
        AttrSelectOneRadio tag = new AttrSelectOneRadio("TAG", "", "选择要显示那一个标签的文章");
        tag.setLayout("pageDirection");
        if (tags != null && !tags.isEmpty()) {
            for (TagEn te : tags)
                tag.addItem(te.getName(), te.getName());
        }

        AttrInputText size = new AttrInputText("Size", "6", "显示多少条记录,填整数");

        AttrSelectOneRadio asc = new AttrSelectOneRadio("Asc", "false", "使用发布时间的倒序或是正序。提示：使用倒序来显示最新发表的文章，使用正序来始终显示最早发表的文章。默认：倒序");
        asc.addItem("true", "正序");
        asc.addItem("false", "倒序");

        AttrSelectOneRadio target = new AttrSelectOneRadio("Target", "_self", "打开文章的目标窗口，默认：原窗口");
        target.addItem("_self", "原窗口");
        target.addItem("_blank", "新窗口");

        AttrSelectBooleanCheckbox showIndex = new AttrSelectBooleanCheckbox("Show Index", "true", "是否显示序号.");
        AttrSelectBooleanCheckbox showDate = new AttrSelectBooleanCheckbox("Show Date", "false", "是否显示发表日期");
        AttrSelectBooleanCheckbox showEdit = new AttrSelectBooleanCheckbox("Show Edit", "false", "是否显示快速编辑按钮,当您为登录状态时可以看到编辑、删除按钮，方便操作.");
        AttrSelectBooleanCheckbox showMore = new AttrSelectBooleanCheckbox("Show More", "false", "是否显示按钮“More(更多)”");
        AttrInputText pattern = new AttrInputText("Pattern", "yyyy-MM-dd", "\"发表日期\"的时间格式，默认：yyyy-MM-dd");
        AttrInputText timeZone = new AttrInputText("Time Zone", "GMT+8", "\"发表日期\"的时区设置,默认为GMT+8");

        as.add(tag);
        as.add(size);
        as.add(showIndex);
        as.add(showDate);
        as.add(showEdit);
        as.add(showMore);
        as.add(asc);
        as.add(target);
        as.add(pattern);
        as.add(timeZone);
        return as;
    }

    public UIComponent render(ModuleEn module) {
        AttrMap attr = getAttributes(module);
        String tag = attr.getAsString("TAG", "");
        Integer size = attr.getAsInteger("Size", 6);
        Boolean asc = attr.getAsBoolean("Asc", Boolean.FALSE);

        String target = attr.getAsString("Target", "_self");
        Boolean showIndex = attr.getAsBoolean("Show Index", Boolean.TRUE);
        Boolean showDate = attr.getAsBoolean("Show Date", Boolean.FALSE);
        Boolean showEdit = attr.getAsBoolean("Show Edit", Boolean.FALSE);
        Boolean showMore = attr.getAsBoolean("Show More", Boolean.FALSE);
        String pattern = attr.getAsString("Pattern", "yyyy-MM-dd");
        String timeZone = attr.getAsString("Time Zone", "GMT+8");

        List<TagArticleEn> taes = TagArticleSe.findPublicByTag(tag, size, asc);
        TagArticlesDataTable table = new TagArticlesDataTable();
        table.setTag(tag);
        table.setValue(taes);
        table.setTarget(target);
        table.setShowDate(showDate);
        table.setPattern(pattern);
        table.setTimeZone(timeZone);
        table.setShowEdit(showEdit);
        table.setShowIndex(showIndex);
        table.setShowMore(showMore);
        return table;
    }

    public String getName() {
        return "文章分类列表";
    }

    public String getDescription() {
        return "该渲染器可以帮助您列出最近刚刚发表的某类(Tag)文章，注：该渲染器将根据" +
                "您所指定的Tag来列出该类最近刚发表的文章。";
    }

}
