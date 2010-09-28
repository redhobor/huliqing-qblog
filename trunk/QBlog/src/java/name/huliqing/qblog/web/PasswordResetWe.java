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
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import name.huliqing.common.StringUtils;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.AccountEn;
import name.huliqing.qblog.service.AccountSe;

/**
 *
 * @author huliqing
 */
@ManagedBean
@RequestScoped
public class PasswordResetWe extends BaseWe {

    private String account;
    private String uuid;
    private String newPassword;

    public PasswordResetWe() {
        super();
        String tempAccount = QBlog.getParam("account");
        String tempUUID = QBlog.getParam("uuid");
        if (tempAccount != null && tempUUID != null) {
            account = tempAccount;
            uuid = tempUUID;
        }
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    // ---- Action

    public void reset() {
        if (account == null || newPassword == null || uuid == null) {
            return;
        }
        AccountEn ae = AccountSe.find(account);
        if (ae == null) {
            Messenger.sendError("帐号不存在!");
            return;
        }
        if (uuid == null || !uuid.equals(ae.getUuid())) {
            Messenger.sendError("标识码错误！");
            return;
        }
        if (ae.getUuid() == null) {
            Messenger.sendError("找不到标识码，您需要先使用取回密码功能！");
            return;
        }
        if (ae.getUuidValid() == null) {
            Messenger.sendError("标识码有效期错误！");
            return;
        }
        Calendar cal = Calendar.getInstance();
        if (cal.getTime().after(ae.getUuidValid())) {
            Messenger.sendError("标识码过期！");
            return;
        }
        try {
            ae.setPassword(StringUtils.getInstance().encodeByMd5(newPassword));
            ae.setUuid(null);
            ae.setUuidValid(null);
            if (AccountSe.update(ae)) {
                Messenger.sendInfo("重设密码成功，您现在可以使用新密码登录。");
                this.account = null;
                this.newPassword = null;
                this.uuid = null;
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PasswordResetWe.class.getName()).log(Level.SEVERE, null, ex);
            Messenger.sendError("重设密码过程发生错误。");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PasswordResetWe.class.getName()).log(Level.SEVERE, null, ex);
            Messenger.sendError("重设密码过程发生错误。");
        }
    }
}
