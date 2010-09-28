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

package name.huliqing.qblog.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import name.huliqing.qblog.enums.AccountType;
import org.datanucleus.jpa.annotations.Extension;

@Entity
public class AccountEn implements java.io.Serializable {
    private static final long serialVersionUID = 1L;

    // 用户帐号
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchField
    private String account;

    // 密码
    @SearchField
    private String password;

    // 用户昵称
    @SearchField
    private String nickname;

    // 安全类型,暂无用
    @Enumerated(EnumType.STRING)
    @SearchField
    private AccountType accountType;

    // 最后登录的日期
    @Extension(vendorName = "datanucleus", key = "is-second-class", value = "false")
    @SearchField
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date lastLogin;

    // 最后登录的IP
    @SearchField
    private String lastIp;

    // 是否激活的，如果非激活的，则不能登录系统
    @SearchField
    private Boolean active;

    // 该帐号的创建日期
    @Extension(vendorName = "datanucleus", key = "is-second-class", value = "false")
    @SearchField
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date createDate;

    // 最后一次更新日期
    @Extension(vendorName = "datanucleus", key = "is-second-class", value = "false")
    @SearchField
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date modifyDate;

    // UUID标识，该标识用于取回密码或其它功能可用
    @SearchField
    private String uuid;

    // 关于uuid的有效时间,超过这个时间，uuid被认为无效,需要重新生成uuid
    @Extension(vendorName = "datanucleus", key = "is-second-class", value = "false")
    @SearchField
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date uuidValid;

    /**
     * 获取用户帐号信息
     * @return
     */
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 获取用户密码
     * @return
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取用户昵称
     * @return
     */
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    /**
     * 获取最后一次登录日期时间
     * @return
     */
    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    /**
     * 获取最后一次登录IP
     * @return
     */
    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getUuidValid() {
        return uuidValid;
    }

    public void setUuidValid(Date uuidValid) {
        this.uuidValid = uuidValid;
    }
}
