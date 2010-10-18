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
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.processor.XmlProcessor2;
import name.huliqing.qblog.processor.attr.AttrSelectBooleanCheckbox;
import name.huliqing.qblog.processor.attr.Attribute2;
import name.huliqing.qfaces.component.Calendar;
import name.huliqing.qfaces.component.Frame;

/**
 *
 * @author huliqing
 */
public class CalendarProcessor extends XmlProcessor2{

    @Override
    public List<Attribute2> getRequiredAttributes() {
        List<Attribute2> as = new ArrayList<Attribute2>(2);
        as.add(new AttrSelectBooleanCheckbox("Pop Panel", "false",
                "是否显示一个快捷弹出窗口，当鼠标移动到某一个有高亮显示的日期时会" +
                "弹出相应的快捷窗口，该窗口显示当天所发表过的所有文章。默认值：false"));
        as.add(new AttrSelectBooleanCheckbox("Today", "true", "是否在日历页脚显示今天的日期,默认选中"));
        return as;
    }

    @Override
    public UIComponent render(ModuleEn module) {
        AttrMap attr = getAttributes(module);

        Boolean showToday = attr.getAsBoolean("Today", Boolean.FALSE);
        Calendar cal = new Calendar();
        if (cal.getFrame() == null) {
            cal.setFrame(new Frame());
        }
        cal.getFrame().setRenderBorder(Boolean.FALSE);
        cal.setHref("/articles/pageId=" + QBlog.getPageId());
        cal.setListenerAsExpression("#{calendarActionDateFetch.fetch}");
        cal.setShowActionPanel(attr.getAsBoolean("Pop Panel", Boolean.FALSE));
        // 隐藏或显示calender的footer
        cal.setStyleClassCalendarFooter(showToday ? "gray" : "none");
        cal.setStyleClassActionPanel("actionPanel");
        cal.setStyleClassActionDate("actionDate");
        return cal;
    }

    @Override
    public String getName() {
        return "日历渲染器";
    }

    @Override
    public String getDescription() {
        return "添加该事件日历可以帮助显示当前月发表过文章的日子，点击相应日期会快速查询到当天发表过的所有文章。";
    }
}
