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

package name.huliqing.qblog.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import name.huliqing.common.StringUtils;
import name.huliqing.qblog.Constant;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.daocache.AccountCache;
import name.huliqing.qblog.entity.AccountEn;
import name.huliqing.qblog.enums.AccountType;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;

public class AccountSe {

    /**
     * 创建一个帐号
     * @param account
     */
    public final static Boolean save(AccountEn account) {
        if (QBlog.getApp().isDemo()) {
            Messenger.sendError("Demo 版本不允许创建帐号.");
            return false;
        }
        if (AccountCache.getInstance().isExistAccount(account.getAccount())) {
            Messenger.sendError("出错：该帐号已经存在!");
            return false;
        }
        account.setCreateDate(new Date());
        return AccountCache.getInstance().save(account);
    }

    /**
     * 更新帐号信息
     * @param account
     * @return
     */
    public final static Boolean update(AccountEn account) {
        if (QBlog.getApp().isDemo()) {
            Messenger.sendError("Demo 版本不允许更新帐号信息.");
            return false;
        }
        // 如果准备停用某一个帐号，而且该帐号为当前使用的帐号.则不能停用
        if (!account.getActive()) {
            if (QBlog.getCurrentVisitor().getAccount().getAccount().equals(account.getAccount())) {
                Messenger.sendError(QBlog.formatDate(new Date()) + ", "
                        + account.getAccount() + ", 出错，当前正在使用的帐号不能够被停用！"
                        + "否则有可能造成无法登录系统。您可以使用其它帐号登录后再停用当前帐号。");
                return false;
            }
        }
        account.setModifyDate(new Date());
        return AccountCache.getInstance().update(account);
    }

    /**
     * 删除一个帐号
     * @param account
     * @return
     */
    public final static Boolean delete(AccountEn account) {
        if (QBlog.getApp().isDemo()) {
            Messenger.sendError("Demo 版本不允许删除帐号.");
            return false;
        }
        if (Constant.ADMIN_ACCOUNT.equals(account.getAccount())) {
            Messenger.sendError("出错：系统的默认Admin帐号不能被删除!"
                    + "但是您可以停用该帐户.");
            return false;
        }
        if (QBlog.getCurrentVisitor().getAccount().
                getAccount().equals(account.getAccount())) {
            Messenger.sendError("出错：你不能删除你当前正在使用的帐号");
            return false;
        }
        return AccountCache.getInstance().delete(account.getAccount());
    }

    public final static AccountEn find(String accountId) {
        return AccountCache.getInstance().find(accountId);
    }

    public final static List<AccountEn> findAll() {
        return AccountCache.getInstance().findAll();
    }

    public final static PageModel<AccountEn> findAll(PageParam pp) {
        PageModel<AccountEn> pm = new PageModel<AccountEn>();
        pm.setPageData(AccountCache.getInstance().findAll());
        pm.setTotal(AccountCache.getInstance().countAll());
        return pm;
    }

    /**
     * 登录系统，该方法要求登录Account的Password必须是已经加过密的
     * @param account
     * @return
     */
    public final static Boolean loginWithPasswordEncoded(AccountEn account) {
        // 如果系统不存在admin帐号，则创建一个默认的admin帐户
        if (!AccountCache.getInstance().isExistAdminAccount()) {
            createDefaultAdminAccount();
        }
        // 如果不存在激活的admin帐号，则激活默认的admin帐号
        if (!AccountCache.getInstance().isExistActiveAdminAccount()) {
            activeDefaultAdminAccount();
        }
        AccountEn result = AccountCache.getInstance().find(account.getAccount());
        if (result == null) {
            Messenger.sendError("账号不存在.");
            return false;
        }
        if (!result.getActive()) {
            Messenger.sendError("该帐号已经停用");
            return false;
        }
        if (!result.getPassword().equals(account.getPassword())) {
            Messenger.sendError("密码错误！");
        } else {
            QBlog.getCurrentVisitor().setAccount(result);
            return true;
        }
        return false;
    }

    /**
     * 用于非JSF生命周期内的登录验证。
     * 如果登录成功，则返回null<BR />
     * 如果登录失败，则返回错误信息。
     * @param account
     * @return
     */
    public final static String loginWithPasswordEncodedRPC(AccountEn account) {
        AccountEn result = AccountCache.getInstance().find(account.getAccount());
        if (result == null) {
            return "不存在的用户帐号：" + account.getAccount();
        }
        if (!result.getActive()) {
            return "帐号没有激活";
        }
        if (!result.getPassword().equals(account.getPassword())) {
            return "密码错误";
        } else {
            return null;
        }
    }

    /**
     * 退出用户登录,同时清除Cookie
     */
    public final static void logout() {
        QBlog.getCurrentVisitor().logout();
        Cookie aCookie = QBlog.getCookie(Constant.COOKIE_ACCOUNT);
        Cookie pCookie = QBlog.getCookie(Constant.COOKIE_PASSWORD);
        QBlog.clearCookie(aCookie);
        QBlog.clearCookie(pCookie);
    }

    public final static boolean isExistActiveAdminAccount() {
        return AccountCache.getInstance().isExistActiveAdminAccount();
    }

    /**
     * 重新激活默认的admin帐号
     * @return
     */
    public final static boolean activeDefaultAdminAccount() {
        AccountEn admin = AccountCache.getInstance().find(Constant.ADMIN_ACCOUNT);
        admin.setActive(true);
        AccountCache.getInstance().update(admin);
        return true;
    }

    private final static void createDefaultAdminAccount() {
        Logger.getLogger(AccountSe.class.getName()).log(Level.WARNING, "Create default admin account...");
        AccountEn admin = new AccountEn();
        admin.setAccount(Constant.ADMIN_ACCOUNT);
        try {
            admin.setPassword(StringUtils.getInstance().encodeByMd5(Constant.ADMIN_PASSWORD));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(AccountSe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(AccountSe.class.getName()).log(Level.SEVERE, null, ex);
        }
        admin.setAccountType(AccountType.ADMIN);
        admin.setActive(true);
        AccountCache.getInstance().save(admin);
    }
}
