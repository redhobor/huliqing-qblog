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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpServletRequest;
import name.huliqing.qblog.entity.AccountEn;

/**
 * session bean
 * @author huliqing
 */
@ManagedBean
@SessionScoped
public class Visitor implements java.io.Serializable {
    private static final long serialVersionUID = -1239344055119479556L;

    private AccountEn account;
    private String remoteAddr;

    /**
     * 判断用户是否已经登录系统
     * @return
     */
    public Boolean isLogin() {
        return getLogin();
    }

    /**
     * 这个方法用于xhtml页面调用，不能使用isLogin,否则页面会
     * 认为没有这个login属性而导致property not found
     * @return
     */
    public Boolean getLogin() {
        return (this.account != null);
    }

    /**
     * 退出系统
     */
    public String logout() {
        this.setAccount(null);
        return Constant.OUT_SUCCESS;
    }

    // ---- Setter and Getter
    public AccountEn getAccount() {
        return account;
    }

    public void setAccount(AccountEn account) {
        this.account = account;
    }

    /**
     * 获取当前访问者的IP信息
     * @return
     */
    public String getRemoteAddr() {
        if (this.remoteAddr == null) {
            HttpServletRequest hsr = (HttpServletRequest)  QBlog.getFacesContext().getExternalContext().getRequest();
            remoteAddr = hsr.getRemoteAddr();
        }
        return remoteAddr;
    }
}
