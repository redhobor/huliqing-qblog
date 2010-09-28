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

package name.huliqing.qblog;

public class Constant {

    /** Map id,相关Map存放于request中 */
    public final static String QBLOG_ATTRIBUTE_MAP_ID 
            = "name.huliqing.qblog.attributeMap";

    /**
     * 默认的admin用户名，该用户名会在系统首次初始化时添加到DB中
     */
    public final static String ADMIN_ACCOUNT = "admin";
    /**
     * 默认的admin密码
     */
    public final static String ADMIN_PASSWORD = "admin";
    /**
     * Visitor的bean name,
     * 注：不要修改！
     */
    public final static String VISITOR_KEY = "visitor";
    /**
     * 默认的登录页地址，相对于根目录，当用户无权限访问相应的目录时
     * 会自动跳转到这个地址.
     * 注：不要指定到admin目录下的文件
     */
    public final static String DEFAULT_URI_LOGIN = "/login.faces";
    /**
     * 默认的主页地址
     */
    public final static String DEFAULT_URI_HOME = "/index.faces";
    /**
     * 资源束名称,指定到默认的资源束位置
     */
    public final static String RESOURCE = "resource";
    /**
     * return success.
     * 注：不要修改它
     */
    public final static String OUT_SUCCESS = "success";
    /**
     * return "failure"
     * 注：不要修改它
     */
    public final static String OUT_FAILURE = "failure";

    /** Cookie中保存用户帐号名的cookie名称 */
    public final static String COOKIE_ACCOUNT = "account";

    /** Cookie中保存用户密码的cookie名称 */
    public final static String COOKIE_PASSWORD = "password";
}
