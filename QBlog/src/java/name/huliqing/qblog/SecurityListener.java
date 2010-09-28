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

import java.util.logging.Logger;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.Cookie;
import name.huliqing.qblog.entity.AccountEn;
import name.huliqing.qblog.service.AccountSe;

/**
 *
 * @author huliqing
 */
public class SecurityListener implements PhaseListener {
    private final static Logger logger = Logger.getLogger(SecurityListener.class.getName());

    public void beforePhase(PhaseEvent pe) {}

    public void afterPhase(PhaseEvent pe) {
        Visitor visitor = QBlog.getCurrentVisitor();
        // 偿试登录
        if (!visitor.isLogin()) {
            Cookie account = QBlog.getCookie(Constant.COOKIE_ACCOUNT);
            Cookie password = QBlog.getCookie(Constant.COOKIE_PASSWORD);
            if (account != null && password != null) {
                if (account.getValue() != null && password.getValue() != null) {
                    logger.info("偿试使用Cookie自动登录...");
                    AccountEn ae = new AccountEn();
                    ae.setAccount(account.getValue());
                    ae.setPassword(password.getValue());
                    if (!AccountSe.loginWithPasswordEncoded(ae)) {
                        // clear cookie
                        QBlog.clearCookie(account);
                        QBlog.clearCookie(password); 
                    } else {
                        logger.info("Auto login (use cookie) successfull.");
                    }
                }
            }
        }

        // 检查权限
        String viewId = pe.getFacesContext().getViewRoot().getViewId();
        if (isSystemFile(viewId) || isLayoutFile(viewId)) {
            visitor = QBlog.getCurrentVisitor();
            if (!visitor.isLogin()) {
                logger.warning("正在试图访问未经授权的页面：viewId=" + viewId + ", remote address =" + visitor.getRemoteAddr());
                String returnURL = QBlog.getOriginalURI(true, true);
                QBlog.redirect(Constant.DEFAULT_URI_LOGIN + "?returnURL=" + returnURL);
            }
        }

    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }

    private boolean isSystemFile(String viewId) {
        return (viewId.indexOf("/admin/") != -1);
    }

    private boolean isLayoutFile(String viewId) {
        return (viewId.indexOf("/_res/layout/") != -1);
    }
}
