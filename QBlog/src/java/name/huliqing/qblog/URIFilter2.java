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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import name.huliqing.qblog.entity.PageEn;
import name.huliqing.qblog.service.PageSe;
import name.huliqing.qblog.task.TaskManager;

/**
 *
 * @author huliqing
 */
public class URIFilter2 implements Filter{
    private final static Logger logger = Logger.getLogger(URIFilter2.class.getName());
    private FilterConfig filterConfig = null;

    public URIFilter2() {}

    private enum URI {
        HOME("/"),

        PAGE("/page/"),

        ARTICLE("/article/"),

        ARTICLES("/articles/"),

        RSS("/rss/"),

        PHOTO("/photo/"),

        TASK("/task/"),

        TEST("/test/"),

        UNKNOW(null);

        private String v;
        private URI(String value) {
            this.v = value;
        }
        public String value() {
            return this.v;
        }
    }

    public void doFilter(ServletRequest request,
            ServletResponse response, FilterChain chain) throws IOException, ServletException {

        URIFilter2.URI uri = parseURI(request);
        if (uri == URI.HOME) {
            // 如果直接进入首页，则应该转到某一个Page页面
            List<PageEn> pe = PageSe.findAllEnabled();
            if (pe != null && !pe.isEmpty()) {
                Long pageId = pe.get(0).getPageId();
                QBlog.findQBlogAttributeMap(request).put("pageId", pageId);
                request.getRequestDispatcher("/page.faces").forward(request, response);
            } else {
                // 如果系统当前还未有任何可用页面,则转入后台
                request.getRequestDispatcher("/admin/system/start.faces").forward(request, response);
            }

        } else if (uri == URI.PAGE) {

            request.getRequestDispatcher("/page.faces").forward(request, response);

        } else if (uri == URI.ARTICLE) {
            boolean error = false;
            if (QBlog.findQBlogAttributeMap(request).get("pageId") == null) {
                logger.warning("找不到可用的pageId,文章页面(article)不能没有pageId," +
                        "将偿试使用系统第一个激活的页面替代...");
                List<PageEn> pe = PageSe.findAllEnabled();
                if (pe != null && !pe.isEmpty()) {
                    Long pageId = pe.get(0).getPageId();
                    logger.warning("找到第一个激活的页面, pageId=" + pageId);
                    QBlog.findQBlogAttributeMap(request).put("pageId", pageId.toString());
                } else {
                    error = true;
                    String message = "当前系统找不到任何可用的页面，可能您未曾初始化系统。" +
                            "或者您没有开启任何一个页面。为了保证能够正常显示文章，您必须保证至少" +
                            "存在一个激活的页面。请转到 “系统设置”-》" +
                            "“<a href=\"/admin/system/pageList.faces\">页面模块</a>”来创建或激活页面。";
                    request.getRequestDispatcher("/error.faces?message=" + message).forward(request, response);
                }
            }
            if (!error) {
                request.getRequestDispatcher("/article.faces").forward(request, response);
            }

        } else if (uri == URI.ARTICLES) {

            request.getRequestDispatcher("/articles.faces").forward(request, response);

        } else if (uri == URI.PHOTO) {

            PhotoManager.outputPhoto(request, response);

        } else if(uri == URI.TASK) {

            TaskManager.executeTasks();

        } else if(uri == URI.RSS) {

            RSSManager.process(request, response); 

        } else if(uri == URI.TEST) {

            // TestManager.getInstance().process(request, response);

        } else if(uri == URI.UNKNOW) {

            try {
                chain.doFilter(request, response);
            } catch(Throwable t) {
                t.printStackTrace();
            }

        }
    }

    private URIFilter2.URI parseURI(ServletRequest request) {
        HttpServletRequest hsr = (HttpServletRequest) request;
        String uri = hsr.getRequestURI();

        Map<String, Object> qMap = QBlog.findQBlogAttributeMap(request);
        // 这个是在forward之前的原始请求的URI
        qMap.put("uri", uri);

        if (uri.indexOf(";jsessionid") != -1)
            uri = uri.substring(0, uri.indexOf(";jsessionid"));
        if (uri.endsWith("#"))
            uri = uri.substring(0, uri.length() - 1);

        String paramStr = null;
        URIFilter2.URI u = null;

        // Don't use uri.startsWith("/")
        if (uri.equals("/") || uri.equals("/index.faces")) {

            u = URI.HOME;

        } else if (uri.startsWith(URI.PAGE.value())) {

            int s = uri.indexOf(URI.PAGE.value()) + URI.PAGE.value().length();
            paramStr = uri.substring(s);
            u = URI.PAGE;

        } else if (uri.startsWith(URI.ARTICLE.value())) {

            int s = uri.indexOf(URI.ARTICLE.value()) + URI.ARTICLE.value().length();
            paramStr = uri.substring(s);
            u = URI.ARTICLE;

        } else if (uri.startsWith(URI.ARTICLES.value())) {

            int s = uri.indexOf(URI.ARTICLES.value()) + URI.ARTICLES.value().length();
            paramStr = uri.substring(s);
            u = URI.ARTICLES;

        }  else if (uri.startsWith(URI.RSS.value())) {

            int s = uri.indexOf(URI.RSS.value()) + URI.RSS.value().length();
            paramStr = uri.substring(s);
            u = URI.RSS;

        } else if (uri.startsWith(URI.PHOTO.value())) {
            
            int s = uri.indexOf(URI.PHOTO.value()) + URI.PHOTO.value().length();
            paramStr = uri.substring(s);
            u = URI.PHOTO;

        } else if (uri.startsWith(URI.TASK.value())) {

            u = URI.TASK;

        } else if (uri.startsWith(URI.TEST.value())) {

            int s = uri.indexOf(URI.TEST.value()) + URI.TEST.value().length();
            paramStr = uri.substring(s);
            u = URI.TEST;

        } else {
            u = URI.UNKNOW;
        }

        if (paramStr != null) {
            // e.g.  /page/pageId=1,articleId=2,sss=3,...
            String[] params = paramStr.split(",");
            for (String param : params) {
                if (param.indexOf("=") != -1) {
                    String[] temp = param.split("=");
                    if (temp.length >= 2) {
                        qMap.put(temp[0], temp[1]);
                    }
                }
            }
        }
        // 在配置页面的时候可能pageId无法及时获得，所以pageId的传递可能放在
        // hidden input field中.这里必须handle到这个pageId,否则会造成pageId丢失
        // 这个问题在配置页面模块时相当重要
        if (!qMap.containsKey("pageId")) {
            String pageId = hsr.getParameter("pageId");
            if (pageId != null && !"".equals(pageId)) {
                qMap.put("pageId", pageId);
            }
            String layout = hsr.getParameter("layout");
            if (layout != null && !"".equals(layout)) {
                qMap.put("layout", layout);
            }
        }

        return u;
    }

    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    public void destroy() {
    }

    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

}
