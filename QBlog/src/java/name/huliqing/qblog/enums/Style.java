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

package name.huliqing.qblog.enums;

/**
 * 定义样式名称
 * @author huliqing
 */
public enum Style {

    // ---- Body 区域样式

    /** 页面Body样式 */
    css_body,

    // ---- 导航栏样式

    /** 整个导航栏的样式 */
    css_nav_class,

    /** 导航按钮的一般样式，或mouse离开按钮时的样式 */
    css_nav_onmouseout,

    /** 导航按钮，当mouse放上去时的样式 */
    css_nav_onmouseover,

    /** 导航按钮，当前选择的栏目的样式 */
    css_nav_onmousedown,


    // ---- Module 样式

    /** Module 整体样式 */
    css_module_full,

    /** Module标题(Name)的样式名称,title由outer与inner及title,三个div嵌套 */
    css_module_titleOuter,
    css_module_titleInner,
    css_module_title,

    /** Module内容的样式名称 */
    css_module_content,

    // ---- Article 样式

    /** 文章标题样式 */
    css_article_titleOuter,
    css_article_titleInner,
    css_article_title,

    /** 文章摘要的样式 */
    css_article_summary,

    /** 文章内容样式 */
    css_article_content,

    /** 文章页脚样式，页脚包含：发表日期，评论数，阅读数等信息,这个样式在文章列表及
     * 文章内容显示页都会用到 */
    css_article_footerOuter,
    css_article_footerInner,
    css_article_footer,
}
