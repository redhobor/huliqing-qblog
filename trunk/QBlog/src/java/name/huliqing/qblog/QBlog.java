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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qfaces.QFaces;

public class QBlog {
    private final static Logger logger = Logger.getLogger(QBlog.class.getName());

    public final static App getApp() {
        return (App) getBean("app");
    }


    /**
     * 该方法不能在非JSF生命周期内调用。
     * 获取当前访问用户
     * @return
     */
    public final static Visitor getCurrentVisitor() {
        Application app = FacesContext.getCurrentInstance().getApplication();
        ELContext elc = FacesContext.getCurrentInstance().getELContext();
        ExpressionFactory ef = app.getExpressionFactory();
        ValueExpression ve = ef.createValueExpression(elc,
                "#{" + Constant.VISITOR_KEY + "}",
                Visitor.class);
        Visitor visitor = (Visitor) ve.getValue(elc);
        if (visitor == null) {
            visitor = new Visitor();
            ve.setValue(elc, visitor);
        }
        return visitor;
    }

    /**
     * 通过bean 名称获取bean
     * @param beanName
     * @return
     */
    public final static Object getBean(String beanName) {
        Application app = FacesContext.getCurrentInstance().getApplication();
        ELContext elc = FacesContext.getCurrentInstance().getELContext();
        ExpressionFactory ef = app.getExpressionFactory();
        ValueExpression ve = ef.createValueExpression(elc,
                "#{" + beanName + "}",
                Object.class);
        return ve.getValue(elc);
    }

    /**
     * 获取客户端IP
     * @return
     */
    public final static String getRemoteAddress() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest hsr = (HttpServletRequest) fc.getExternalContext().getRequest();
        return hsr.getRemoteAddr();
    }

    /**
     * 获取当前系统正在运行的主机的地址,如：
     * http://www.huliqing.name
     * ,注：返回结果可能包含contextPath(如果存在)
     * @return
     */
    public final static String getHostRequest() {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        String host = ec.getRequestHeaderMap().get("host");
        String cPath = ec.getRequestContextPath();
        String hostRequest = "http://" + host + cPath;
        return hostRequest;
    }
 
    /**
     * 格式化日期
     * @param date
     * @return
     */
    public final static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        String format = ConfigManager.getInstance().findConfig(Config.CON_SYSTEM_DATE_FORMAT).getValue();
        String timeZone = ConfigManager.getInstance().findConfig(Config.CON_SYSTEM_TIME_ZONE).getValue();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        return sdf.format(date);
    }

    /**
     * 获取url参数，这个方法要求当前的请求在JSF的生命周期内,如果找不到值则返回null,
     * 该方法优先从QBlogAttributeMap中获取，找不到值再从URL中获取,最终找不到值则返回
     * null.
     * @param key
     * @return
     */
    public final static String getParam(String key) {
        Map<String, Object> qMap = findQBlogAttributeMap(null);
        if (qMap != null && qMap.containsKey(key)) {
            return qMap.get(key).toString();
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null) {
            return fc.getExternalContext().getRequestParameterMap().get(key);
        }
        return null;
    }

    /**
     * 获取URL参数,处于非JSF生命周期范围内的参数获取，这个方法必须传递ServletRequest
     * ,如果找不到值则返回null
     * @param key
     * @param request
     * @see #getParam(String)
     * @return
     */
    public final static String getParam(String key, ServletRequest request) {
        Map<String, Object> qMap = findQBlogAttributeMap(request);
        if (qMap != null && qMap.containsKey(key)) {
            return qMap.get(key).toString();
        }
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc != null) {
            return fc.getExternalContext().getRequestParameterMap().get(key);
        }
        return null;
    }

    /**
     * 获取Cookie, 从Cookies 中查找指定ID的cookie. <br />
     * 如果不存在该id的Cookie则返回null;
     * @param id
     * @return
     */
    public final static Cookie getCookie(String id) {
        if (id == null)
            return null;
        HttpServletRequest request = (HttpServletRequest) FacesContext
                .getCurrentInstance()
                .getExternalContext().getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie c : cookies) {
                if (id.equals(c.getName())) {
                    return c;
                }
            }
        }
        return null;
    }

    public final static void clearCookie(Cookie c) {
        if (c != null) {
            HttpServletResponse response = (HttpServletResponse)
                    getFacesContext().
                    getExternalContext().getResponse();
            c.setValue(null);
            c.setMaxAge(0);
            response.addCookie(c);
        }
    }

    /**
     * 1.从当前URL中查询PageId,如果找到这个PageId,则返回这个id
     * 2.如果找不到，则从QBLOG_ATTRIBUTE_MAP_ID中查找,如果从QBlog_ATTRIBUTE_MAP中
     * 查找，如果找到，则返回它。
     * 3.如果找不到，则偿试从提交值中获取。<BR />
     * 必须保证在page页面下，任何时候都不能丢失pageId，否则可能造成很难debug的问题。
     * 保证pageId不丢失的方法如下(以下按顺序优先,假如pageId=3)：<br />
     * 1.在url中附带pageId参数,如 /xxxx.faces?pageId=3
     * 2.在uri中,如 /page/3
     * 3.在form中附带隐然field, name属性(必须有name属性，id属性不会被提交)为 pageId
     * 
     * @return
     */
    public final static Long getPageId() {
        return QFaces.convertToLong(getParam("pageId"));
    }

    /**
     * 从当前URL中查询ArticleId,如果找不到，则从QBLOG_ATTRIBUTE_MAP_ID中查找,
     * 如果找不到，则返回null
     * @return
     */
    public final static Long getArticleId() {
        return QFaces.convertToLong(getParam("articleId"));
    }

    /**
     * 获取原始请求的URI,这是未经过forward之前的URI.
     * @param query
     *      是否在返回的uri中附带query参数(如果存在query).
     * @param encode 
     *      是否使用utf8编辑query参数,在手动组织URL中的query时必须编码。
     * @return
     */
    public final static String getOriginalURI(boolean query, boolean encode) {
        String oriURI = getParam("uri");
        if (oriURI != null && query) {
            FacesContext fc = getFacesContext();
            HttpServletRequest hsr = (HttpServletRequest) fc.getExternalContext().getRequest();
            String queryStr = hsr.getQueryString();
            if (queryStr != null) {
                if (encode) {
                    try {
                        // 确保在url中出现其它编码或特殊字符的情况下不出现乱码现象。
                        // 这在实现returnURL时是必不可少的(手动组织URL)
                        queryStr = URLEncoder.encode(queryStr, "utf8");
                    } catch (UnsupportedEncodingException uee) {
                        logger.log(Level.SEVERE, queryStr, uee);
                    }
                }
                oriURI += "?" + queryStr;
            }
        }
        return oriURI;
    }

    /**
     * 获取根路径
     * @return
     */
    public final static String getRealPath() {
        FacesContext fc = getFacesContext();
        return fc.getExternalContext().getRealPath("/");
    }

    /**
     * 跳转到目标URL
     * @param url e.g. "/login.faces"
     */
    public final static void redirect(String url) {
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
            response.sendRedirect(url);
        } catch (IOException ioe) {
            logger.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }

    /**
     * 转发到目标URL，
     * @param url e.g. "/login.faces";
     */
    public final static void forward(String url) {
        FacesContext fc = getFacesContext();
        UIViewRoot view = fc.getApplication().getViewHandler().createView(fc, url);
        fc.setViewRoot(view);
    }

    /**
     * 查找QBlog请求参数表
     * @param request
     * @return
     */
    public final static Map<String, Object> findQBlogAttributeMap(ServletRequest request) {
        Map<String, Object> qMap = null;
        if (request != null) {
            qMap = (HashMap<String, Object>) request.getAttribute(Constant.QBLOG_ATTRIBUTE_MAP_ID);
            if (qMap == null) {
                qMap = new HashMap<String, Object>();
                request.setAttribute(Constant.QBLOG_ATTRIBUTE_MAP_ID, qMap);
            }
        } else {
            qMap = (Map<String, Object>) getFacesContext().getExternalContext()
                    .getRequestMap().get(Constant.QBLOG_ATTRIBUTE_MAP_ID);
            if (qMap == null) {
                qMap = new HashMap<String, Object>();
                getFacesContext().getExternalContext().getRequestMap()
                        .put(Constant.QBLOG_ATTRIBUTE_MAP_ID, qMap);
            }
        }
        return qMap;
    }

    public final static FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }
}
