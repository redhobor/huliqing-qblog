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

public enum Config {

    // ---- Email

    /** 系统的Email地址，用于发送电子邮件,如发送回复信息的通知,建议使用与当前ApplicationID相关的Email地址（Gmail） default:"" */
    CON_EMAIL_ADDR_SERVER(""),

    // ---- System

    /** 当前系统所使用的默认模版,默认值:default，在修改这个值之前建议先查看当前系统都有什么模版可用，否则可能会使页面无法显示。 */
    CON_SYSTEM_LAYOUT("default"),

    /** 系统默认的日期时间格式如："yyyy-MM-dd hh:mm:ss" */
    CON_SYSTEM_DATE_FORMAT("yyyy-MM-dd HH:mm:ss"),

    /** 系统使用的默认时区:默认 "GMT+8" */
    CON_SYSTEM_TIME_ZONE("GMT+8"),

    /** 系统ICO图标地址  */
    CON_SYSTEM_ICO_ADDRESS("/qblog.ico"),

    // ---- Article

    /** 是否允许文章被订阅，可选值true/false, 如果关闭了文章RSS功能，则系统将不允许文章被订阅，即使添加了RSS模块,默认值：true */
    CON_ARTICLE_RSS_ENABLE("true"),

    /** 文章缓存数设定，对最近刚刚发表的文章进行缓存，提高性能。注：这个缓存只对公开发表的文章起作用，对隐私，草稿类型的文章不作缓存。默认值:80 (数量不要太大,一般3-4页数据足够) */
    CON_ARTICLE_CACHE_LIMIT("80"),

    /** 文章自动生成摘要时，允许的字数最大长度,最大不能超过500个字符,指定为0时将不生成摘要。 */
    CON_ARTICLE_SUMMARY_LIMIT("300"),

    // ---- Reply

    /** [true/false]开启文章的回复功能,即允许用户回复文章,这个定义是从全局上的，如果关闭了这个功能，则所有文章都不能够被回复，即使文章自身设置了可回复的， 默认值:true */
    CON_REPLY_ENABLE("true"),

    /** [true/false]是否开启文章的回复通知,当有回复时发送邮件通知作者, 默认:true */
    CON_REPLY_NOTICE("true"),

    /** [Int] 回复信息的可编辑时间限制，除了admin之外，访客可编辑自己的回复信息，但是必须有一个时间限制，在回复信息发表后的这个时间内访客可编辑自己的回复（IP必须相同） default:3600(单位秒) */
    CON_REPLY_EDIT_LIMIT("3600"),

    /** [int] 回复信息每页显示的记录数,超过该数则换页显示 */
    CON_REPLY_PAGE_SIZE("10"),

    /** [true/false]是否以正序的排列方式显示访客的评论（按回复的发表时间） */
    CON_REPLY_SORT_ASC("false"),

    /** [Int]对最近的回复信息进行缓存的数量,当存在相关模块时，这能够提升系统性能，允许值0-30， 默认：10 */
    CON_REPLY_CACHE_LAST_REPLY("10"),

    /** [true/false] 是否对文章的回复信息进行HTML转义,如果打开这个选项，则以后的文章回复信息将对可能存在的Html标签进行转义，这可以防止一些恶意的Html代码攻击(在此之前的文章回复不会被转义),建议开启.默认：true */
    CON_REPLY_HTML_ESCAPE("true"),

    // ---- Module

    // ---- Photo

    /** [Int]相册图片缓存的天数，指定为0时，表示不存缓存，一般应该指定尽可能长的缓存时间，默认:360 */
    CON_PHOTO_CACHE_DAYS("360"),

    /** [Int]相册缓存数，对最近创建的相册进行缓存，注：这里缓存的是相册信息，这不会缓存相册中的图片文件。默认:50 */
    CON_PHOTO_CACHE_FOLDER("50"),

    // ---- MetaWeblog Setting

    /** 
     * 指定你的Blog名称, 如果留空，则这个参数在必要时会自动设置为“Your Blog Name”
     */
    CON_METAWEBLOG_BLOG_NAME("Your Blog Name"),

    /** 
     * 你的Blog地址(URL)， 这个地址会由系统自动判断，不需要手动设置. 
     */
    CON_METAWEBLOG_SITE(""),

    /**
     * 默认的存放附件的相册ID，当来自客户端的文章包含图片等附件时，将使用这
     * 个文件夹来存放附件,这个参数在必要时会自动设置, 你也可以手动指定。
     */
    CON_METAWEBLOG_ALBUM("");

    /**
     * 默认值
     */
    private String value;

    private Config(String value) {
        this.value = value;
    }
 
    /**
     * 获取默认值
     * @return
     */
    public String getValue() {
        return this.value;
    }
}
