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

package name.huliqing.qblog.web;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import name.huliqing.common.StringUtils;
import name.huliqing.qblog.Constant;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.AccountEn;
import name.huliqing.qblog.service.AccountSe;

@ManagedBean
@RequestScoped
public class LoginWe extends BaseWe {

    private AccountEn account;
    private boolean keepLogin;

    public LoginWe() {
        super();
        String action = QBlog.getParam("action");
        if (action != null && action.equals("logout")) {
            if (QBlog.getCurrentVisitor().isLogin()) {
                AccountSe.logout();
                QBlog.redirect(returnURL != null ? returnURL : "/");
            }
        }
    }

    // ---- Getter and Setter

    public AccountEn getAccount() {
        if (account == null) {
            account = new AccountEn();
        }
        return account;
    }

    public void setAccount(AccountEn account) {
        this.account = account;
    }

    public boolean isKeepLogin() {
        return keepLogin;
    }

    public void setKeepLogin(boolean keepLogin) {
        this.keepLogin = keepLogin;
    }

    // ---- Action

    public void login() {
        try {
            this.account.setPassword(StringUtils.getInstance().encodeByMd5(account.getPassword()));
            if (AccountSe.loginWithPasswordEncoded(this.account)) {
                if (this.keepLogin) {
                    Cookie aCookie = new Cookie(Constant.COOKIE_ACCOUNT, this.account.getAccount());
                    Cookie pCookie = new Cookie(Constant.COOKIE_PASSWORD, this.account.getPassword());
                    aCookie.setMaxAge(60 * 60 * 24 * 30);   // 约30天
                    pCookie.setMaxAge(60 * 60 * 24 * 30);
                    HttpServletResponse response = (HttpServletResponse) QBlog.getFacesContext().
                        getExternalContext().getResponse();
                    response.addCookie(aCookie);
                    response.addCookie(pCookie);
                }
                QBlog.redirect(returnURL != null ? returnURL : "/admin/blog/articleAdd.faces");
            }
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}
