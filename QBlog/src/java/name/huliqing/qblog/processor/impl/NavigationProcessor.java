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
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.entity.PageEn;
import name.huliqing.qblog.enums.Style;
import name.huliqing.qblog.processor.HtmlProcessor;
import name.huliqing.qblog.processor.attr.AttrSelectOneRadio;
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
        String outer_onmouseout = Style.css_nav_itemOuter_onmouseout.name();
        String outer_onmouseover = Style.css_nav_itemOuter_onmouseover.name();
        String outer_onmousedown = Style.css_nav_itemOuter_onmousedown.name();

        String inner_onmouseout = Style.css_nav_itemInner_onmouseout.name();
        String inner_onmouseover = Style.css_nav_itemInner_onmouseover.name();
        String inner_onmousedown = Style.css_nav_itemInner_onmousedown.name();

        String item_onmouseout = Style.css_nav_item_onmouseout.name();
        String item_onmouseover = Style.css_nav_item_onmouseover.name();
        String item_onmousedown = Style.css_nav_item_onmousedown.name();

        if (curr) {
            outer_onmouseout = outer_onmousedown;
            inner_onmouseout = inner_onmousedown;
            item_onmouseout = item_onmousedown;
        }

        StringBuilder sb = new StringBuilder(); 
        sb.append("<div class='" + outer_onmouseout + "' "
                + " onmouseout=\"this.className='"  + outer_onmouseout  + "'\" "
                + " onmouseover=\"this.className='" + outer_onmouseover + "'\" "
                + " onmousedown=\"this.className='" + outer_onmousedown + "'\" "
                + " >")
          .append("<div class='" + inner_onmouseout + "' "
                + " onmouseout=\"this.className='"  + inner_onmouseout  + "'\" "
                + " onmouseover=\"this.className='" + inner_onmouseover + "'\" "
                + " onmousedown=\"this.className='" + inner_onmousedown + "'\" "
                + " >")
          .append("<a href='/page/pageId=" + pageId + "' onfocus=\"this.blur()\" >")
          .append("<div class='" + item_onmouseout + "' "
                + " onmouseout=\"this.className='"  + item_onmouseout  + "'\" "
                + " onmouseover=\"this.className='" + item_onmouseover + "'\" "
                + " onmousedown=\"this.className='" + item_onmousedown + "'\" "
                + " >")
          .append(pageName)
          .append("</div>")
          .append("</a>")
          .append("</div>")
          .append("</div>");
        return sb.toString();
    }

    @Override
    public String makeHTML(ModuleEn module) {
        AttrMap attr = getAttributes(module);
        // mode => 0 = h,1 = v
        // align => 0 = L, 1 = C, 2 = R
        // alignText => 0 = L, 1 = C, 2 = R
        Integer mode = attr.getAsInteger("Mode", 0);
        // mode为水平时给予一个左对齐的导航，垂直时给予一个中对齐的导航
        Integer align = attr.getAsInteger("Align", mode.intValue() == 0 ? 0 : 1);
        // 导航文字默认居中对齐
        Integer alignText = attr.getAsInteger("Align Text", 1);
        StringBuilder style = new StringBuilder("");
        if (mode.intValue() == 1) {
            style.append("width:100%;");
        }
        if (align.intValue() == 0) {
            style.append("margin:0 auto 0 0;");
        } else if (align.intValue() == 1) {
            style.append("margin:0 auto;");
        } else if (align.intValue() == 2) {
            style.append("margin:0 0 0 auto;");
        }
        if (alignText.intValue() == 0) {
            style.append("text-align:left;");
        } else if (alignText.intValue() == 1) {
            style.append("text-align:center;");
        } else if (alignText.intValue() == 2) {
            style.append("text-align:right;");
        }

        // 当前页PageId
        Long currentPageId = QBlog.getPageId();
        List<PageEn> pes = PageSe.findAllEnabled();

        // 拼接导航信息,垂直导航时给予一定的cellspacing
        StringBuilder sb = new StringBuilder().append("<div class=\"" + Style.css_nav_full + "\" >");
        sb.append("<table border=\"0\" cellspacing=\"" + (mode.intValue() == 1 ? 5 : 0) + "\" " +
                " cellpadding=\"0\" style=\"" + style + "\" >");
        if (pes != null && !pes.isEmpty()) {
            if (mode.intValue() == 0) { // 水平导航
                sb.append("<tr>");
                for (PageEn pe : pes) {
                    sb.append("<td>");
                    sb.append(genColumn(pe.getPageId(), pe.getName(), currentPageId));
                    sb.append("</td>");
                }
                sb.append("</tr>");
            } else {
                for (PageEn pe : pes) {
                    sb.append("<tr><td>");
                    sb.append(genColumn(pe.getPageId(), pe.getName(), currentPageId));
                    sb.append("</td></tr>");
                }
            }
        } else {
            sb.append("<tr><td></td></tr>");
        }
        sb.append("</table>");
        sb.append("</div>");
        return sb.toString();
    }

    @Override
    public List<Attribute2> getRequiredAttributes() {
        List<Attribute2> attrs = new ArrayList<Attribute2>(3);
        AttrSelectOneRadio mode = new AttrSelectOneRadio("Mode", "0", "选择导航的显示方式");
        mode.addItem("0", "水平导航条");
        mode.addItem("1", "垂直导航条");
        AttrSelectOneRadio align = new AttrSelectOneRadio("Align", "0", "导航栏的对齐方式");
        align.addItem("0", "左对齐");
        align.addItem("1", "居中对齐");
        align.addItem("2", "右对齐");
        AttrSelectOneRadio alignText = new AttrSelectOneRadio("Align Text", "1", "导航栏文字对齐方式");
        alignText.addItem("0", "左对齐");
        alignText.addItem("1", "居中对齐");
        alignText.addItem("2", "右对齐");

        attrs.add(mode);
        attrs.add(align);
        attrs.add(alignText);
        return attrs;
    }

    @Override
    public String getName() {
        return "我的导航栏";
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
