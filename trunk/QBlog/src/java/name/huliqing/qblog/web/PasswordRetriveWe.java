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

import java.util.Calendar;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.AccountEn;
import name.huliqing.qblog.service.AccountSe;
import name.huliqing.qblog.service.MailSe;

/**
 *
 * @author huliqing
 */
@ManagedBean
@RequestScoped
public class PasswordRetriveWe extends BaseWe {

    private String account;

    public PasswordRetriveWe() {
        super();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    // ---- Action

    public void retrive() {
        if (QBlog.getApp().isDemo()) {
            Messenger.sendError("Notice:Demo版本不允许使用密码取回功能。");
            return;
        }

        AccountEn ae = AccountSe.find(account);
        if (ae == null) {
            Messenger.sendError("帐号不存在!");
            return;
        }
        
        // 标识码的有效期限制:24小时
        int limit = 1000 * 60 * 60 * 24;

        // 重复取标识码的时间间隔限制:1小时
        int retriveLimit = 1000 * 60 * 60;

        // 检查限制：1个小时内不允许再次使用取回密码
        if (ae.getUuidValid() != null) {
            long diff = new Date().getTime() + limit - ae.getUuidValid().getTime();
            if (diff <= retriveLimit) {
                Messenger.sendError("1个小时内不能重复使用取回密码的功能！");
                return;
            }
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(cal.getTimeInMillis() + limit);
        String uuid = java.util.UUID.randomUUID().toString();
        ae.setUuid(uuid);
        ae.setUuidValid(cal.getTime());
        if (AccountSe.update(ae)) {
            String url = QBlog.getHostRequest() + "/passwordReset.faces?account=" + account + "&uuid=" + uuid;
            String title = "QBlog密码取回";
            String content = "<a href=\"" + url + "\" > 点击连接重新设置我的QBlog密码 </a>";
            if (MailSe.sendToSelf(title, content)) {
                Messenger.sendInfo("重设密码的链接已经发送到您的Email中，该链接在24内保持有效，" +
                        "您应该尽快重设您的密码。");
            }
        }
    }
}
