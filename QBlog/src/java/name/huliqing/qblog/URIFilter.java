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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.huliqing.qblog.entity.PageEn;
import name.huliqing.qblog.service.PageSe;
import name.huliqing.qblog.task.TaskManager;
import name.huliqing.qfaces.QFaces;

/**
 * @deprecated use URIFilter2 instead
 * @author huliqing
 */
public class URIFilter implements Filter{
    private FilterConfig filterConfig = null;

    private enum URI {
        HOME("/"),

        PAGE("/page/"),

        ARTICLE("/article/"),

        ARTICLES("/articles/"),

        PHOTO("/photo/"),

        TASK("/task/"),

        RSS("/rss/"),

        UNKNOW("");

        private String v;
        private URI(String value) {
            this.v = value;
        }
        public String value() {
            return this.v;
        }
    }

    public URIFilter() {}

    public void doFilter(ServletRequest request, 
            ServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest hsr = (HttpServletRequest) request;
        String requestURI = hsr.getRequestURI();
        
        URIFilter.URI uri = parseURI(requestURI);
        if (uri == URI.HOME) {
            // 如果直接进入首页，则应该转到某一个Page页面
            List<PageEn> pe = PageSe.findAllEnabled();
            if (pe != null && !pe.isEmpty()) {
                Long pageId = pe.get(0).getPageId();
                findQBlogAttributeMap(request).put("pageId", pageId);
                request.getRequestDispatcher("/page.faces").forward(request, response);
            } else {
                // 如果系统当前还未有任何可用页面,则转入后台
                request.getRequestDispatcher("/admin/system/start.faces").forward(request, response);
            }

        } else if (uri == URI.PAGE) {

            Long pageId = findPageId(requestURI);
            if (pageId != null) {
                findQBlogAttributeMap(request).put("pageId", pageId);
            }
            request.getRequestDispatcher("/page.faces").forward(request, response);

        } else if (uri == URI.ARTICLE) {

            Long articleId = findArticleId(requestURI);
            findQBlogAttributeMap(request).put("articleId", articleId);
            request.getRequestDispatcher("/article.faces").forward(request, response);

        } else if (uri == URI.ARTICLES) {

            request.getRequestDispatcher("/articles.faces").forward(request, response);
            
        } else if (uri == URI.PHOTO) {

            PhotoManager.outputPhoto(hsr, (HttpServletResponse) response);

        } else if(uri == URI.TASK) {

            TaskManager.executeTasks();

        } else if(uri == URI.RSS) {

            RSSManager.process(hsr, (HttpServletResponse) response); 

        } else if(uri == URI.UNKNOW) {
            
            try {
                chain.doFilter(request, response);
            } catch(Throwable t) {
                t.printStackTrace();
            }

        }
    }
    
    private URIFilter.URI parseURI(String uri) {
        // Don't use uri.startsWith("/")
        if (uri.equals("/") || uri.startsWith("/index.")) {

            return URIFilter.URI.HOME;

        } else if (uri.startsWith(URI.PAGE.value())) {

            return URIFilter.URI.PAGE;

        } else if (uri.startsWith(URI.ARTICLE.value())) {

            return URIFilter.URI.ARTICLE;

        } else if (uri.startsWith(URI.ARTICLES.value())) {

            return URIFilter.URI.ARTICLES;

        } else if (uri.startsWith(URI.PHOTO.value())) {
            
            return URIFilter.URI.PHOTO;
            
        } else if (uri.startsWith(URI.TASK.value())) {

            return URIFilter.URI.TASK;

        } else if (uri.startsWith(URI.RSS.value())) {
            return URIFilter.URI.RSS;
        }
        return URIFilter.URI.UNKNOW;
    }

    private final static Map<String, Object> findQBlogAttributeMap(ServletRequest request) {
        Map<String, Object> attrMap = (Map<String, Object>)
                request.getAttribute(Constant.QBLOG_ATTRIBUTE_MAP_ID);
        if (attrMap == null) {
            attrMap = new HashMap<String, Object>(1);
            request.setAttribute(Constant.QBLOG_ATTRIBUTE_MAP_ID, attrMap);
        }
        return attrMap;
    }

    private final static Long findPageId(String uri) {
        // Format e.g. http://www.huliqing.name/page/X
        // The "X" means page id.
        if (!uri.contains(URI.PAGE.value()))
            return null;
        if (uri.indexOf(";jsessionid") != -1)
            uri = uri.substring(0, uri.indexOf(";jsessionid"));
        if (uri.endsWith("#"))
            uri = uri.substring(0, uri.length() - 1);
        String tempId = uri.substring(uri.lastIndexOf(URI.PAGE.value()) + URI.PAGE.value().length());
        return QFaces.convertToLong(tempId);
    }

    private final static Long findArticleId(String uri) {
        // Format e.g. http://www.huliqing.name/article/X
        // The "X" means article id.
        if (!uri.contains(URI.ARTICLE.value()))
            return null;
        if (uri.indexOf(";jsessionid") != -1)
            uri = uri.substring(0, uri.indexOf(";jsessionid"));
        if (uri.endsWith("#"))
            uri = uri.substring(0, uri.length() - 1);
        String tempId = uri.substring(uri.lastIndexOf(URI.ARTICLE.value()) + URI.ARTICLE.value().length());
        return QFaces.convertToLong(tempId);
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
