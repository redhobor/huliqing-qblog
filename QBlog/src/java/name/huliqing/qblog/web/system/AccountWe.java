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

package name.huliqing.qblog.web.system;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIData;
import name.huliqing.common.StringUtils;
import name.huliqing.qblog.Constant;
import name.huliqing.qblog.web.BaseWe;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.AccountEn;
import name.huliqing.qblog.enums.AccountType;
import name.huliqing.qblog.service.AccountSe;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;

@ManagedBean
@RequestScoped
public class AccountWe extends BaseWe {

    private UIData uiAccounts;
    private AccountEn newAccount;

    public AccountWe() {
        super();
    }

    public PageModel<AccountEn> loadData(PageParam pp) {
        return AccountSe.findAll(pp);
    }

    public AccountEn getNewAccount() {
        if (this.newAccount == null) {
            this.newAccount = new AccountEn();
        }
        return newAccount;
    }

    public void setNewAccount(AccountEn newAccount) {
        this.newAccount = newAccount;
    }

    public UIData getUiAccounts() {
        return uiAccounts;
    }

    public void setUiAccounts(UIData uiAccounts) {
        this.uiAccounts = uiAccounts;
    }

    public boolean isAdminActived() {
        AccountEn admin = AccountSe.find(Constant.ADMIN_ACCOUNT);
        return (admin.getActive() != null && admin.getActive());
    }

    // ---- Action
    public void createAccount() {
        try {
            AccountEn accountNew = new AccountEn();
            accountNew.setActive(true);
            accountNew.setAccountType(AccountType.ADMIN);
            accountNew.setAccount(this.newAccount.getAccount());
            accountNew.setNickname(this.newAccount.getNickname());
            accountNew.setPassword(StringUtils.getInstance().encodeByMd5(this.newAccount.getPassword()));
            if (AccountSe.save(accountNew)) {
                Messenger.sendInfo("创建帐号成功！");
                this.newAccount = null;
            }
        } catch (NoSuchAlgorithmException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        } catch (UnsupportedEncodingException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public void updateAll() {
        List<AccountEn> _accounts = (List<AccountEn>) this.uiAccounts.getValue();
        for (AccountEn acc : _accounts) {
            if (AccountSe.update(acc)) {
                Messenger.sendInfo(QBlog.formatDate(new Date()) + ", " + acc.getAccount() + ", 更新成功");
            }
        }
        // 因为用户可能把所有的帐号都停用了，如果万一出现这种情况，
        // 这时必须重新激活默认的admin帐号
        if (!AccountSe.isExistActiveAdminAccount()) {
            AccountSe.activeDefaultAdminAccount();
            Messenger.sendWarn("admin帐号已经重新激活,"
                    + "这种情况可能是由于你把所有的帐号停用了!"
                    + "系统必须保证至少有一个帐号可以登录！");
        }
    }

    public void deleteAccount() {
        AccountEn acc = (AccountEn) this.uiAccounts.getRowData();
        AccountSe.delete(acc);
    }
}
