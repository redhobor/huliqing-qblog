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

import java.util.List;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.entity.PageEn;
import name.huliqing.qblog.enums.Style;
import name.huliqing.qblog.processor.HtmlProcessor;
import name.huliqing.qblog.processor.attr.Attribute2;
import name.huliqing.qblog.service.PageSe;

/**
 * 渲染页面导航信息
 * @author huliqing
 */
public class NavigationProcessor extends HtmlProcessor {

    // 生成一个导航列
    private final String genColumn(Long pageId, String pageName, Long currentPageId) {
        boolean curr = (currentPageId != null && currentPageId.longValue() == pageId.longValue());
        String out = Style.css_nav_onmouseout.name();
        String over = Style.css_nav_onmouseover.name();
        String down = Style.css_nav_onmousedown.name();
        if (curr)
            out = Style.css_nav_onmousedown.name();
        return "<div class='"+ out + "' "
                + " onmouseover=\"this.className='" + over + "'\" "
                + "  onmouseout=\"this.className='" + out  + "'\" "
                + " onmousedown=\"this.className='" + down + "'\" "
                + " onclick=\"window.location.href='/page/pageId=" + pageId + "'\" >"
                + pageName
                + "</div>";
    }

    @Override
    public String makeHTML(ModuleEn module) {
//        AttrMap attr = getAttributes(module);

        // 当前页PageId
        Long currentPageId = QBlog.getPageId();
        List<PageEn> pes = PageSe.findAllEnabled();
        // 拼接导航信息
        StringBuilder sb = new StringBuilder().append("<div class=\"" + Style.css_nav_class + "\" >");
        sb.append("<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\" ><tr>");
        if (pes != null) {
            for (PageEn pe : pes) {
                sb.append("<td>");
                sb.append(genColumn(pe.getPageId(), pe.getName(), currentPageId));
                sb.append("</td>");
            }
        }
        sb.append("</tr></table>");
        sb.append("</div>");
        return sb.toString();
    }

    public List<Attribute2> getRequiredAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return "导航栏渲染器";
    }

    @Override
    public String getDescription() {
        return "该渲染器可以帮助您产生一个用于当前系统的导航栏模块.当您使用这个" +
                "渲染器生成导航栏后，您可以把导航模块配置到系统的任何一个页面中" +
                "用于页面导航。";
    }

    @Override
    public Boolean defaultAutoStyle() {
        return Boolean.FALSE;
    }

    @Override
    public Boolean defaultShowName() {
        return Boolean.FALSE;
    }

}
