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

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.LayoutManager;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.PageEn;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qblog.service.PageSe;
import name.huliqing.qfaces.QFaces;

/**
 *
 * @author huliqing
 */
@ManagedBean
@RequestScoped
public class PageWe extends BaseWe {
    // 是否处于可编辑状态
    private boolean editable;

    private Long pageId;
    private String layout;
    private String title;

    public PageWe() {
        super();

        // 偿试获取pageId
        Long tempPageId = QFaces.convertToLong(QBlog.getParam("pageId"));
        if (tempPageId != null) {
            this.pageId = tempPageId;
        }

        // 判断是否为可编辑的
        Boolean tempEditable = QFaces.convertToBoolean(QBlog.getParam("editable"));
        if (tempEditable != null && tempEditable && QBlog.getCurrentVisitor().isLogin()) {
            HttpServletRequest hsr = (HttpServletRequest) QBlog.getFacesContext().getExternalContext().getRequest();
            // 只有“page”页面能够被配置
            if (hsr.getRequestURI().startsWith("/page.")) {
                editable = tempEditable;
            }
        }
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public String getLayout() {
        if (layout == null) {
            layout = findLayout(pageId);
        }
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public String getTitle() {
        if (title == null && pageId != null) {
            PageEn pe = PageSe.find(pageId);
            if (pe != null) {
                return pe.getName();
            }
        } else {
            title = "QBlog - Welcome to my blog";
        }
        return title;
    }

    /**
     * 查找模版当前访问页应该使用的是哪一个模版，<BR />
     * 查找：每一个页面(PageEn)都可能有自己的特定模版，如果页面指定了特定模版，则使用
     *     这个模版。 可能当前页并非Page页面，如article页，但是只要URL参数中能够
     *     找到pageId,则使用这个PageId所对应的Page的模版。 如果PageId找不到，
     *     或者当前页面没有定义特定模版，则继续查找<BR />
     * 查找: 如果系统所设定的默认模版名称存在问题，则直接使用default模版。<BR />
     * <b>总之，这个方法必须返回一个可用的模版，否则页面将会无法正常显示</b>
     * @return layout
     */
    private String findLayout(Long pid) {
        return QBlog.findCurrentLayout(pageId).getName();
        
        // remove
//
//        // 优先从parameter中获取
//        String name = QBlog.getParam("layout");
//
//        if (name != null && LayoutManager.getInstance().exists(name)) {
//            return name;
//        }
//
//        // 从PageEn中获取
//        if (pid == null) {
//            pid = QFaces.convertToLong(QBlog.getParam("pageId"));
//        }
//        if (pid != null) {
//            List<PageEn> pes = PageSe.findAllEnabled();
//            if (pes != null) {
//                for (PageEn pe : pes) {
//                    if (pe.getPageId().longValue() == pid.longValue()) {
//                        name = pe.getLayout();
//                        break;
//                    }
//                }
//            }
//        }
//        if (name != null && LayoutManager.getInstance().exists(name)) {
//            return name;
//        }
//
//        // 从默认系统设置中获取
//        name = ConfigManager.getInstance().findConfig(Config.CON_SYSTEM_LAYOUT).getValue();
//        if (name != null && LayoutManager.getInstance().exists(name)) {
//            return name;
//        }
//
//        // 如果系统默认layout设定不存在
//        Logger.getLogger(PageWe.class.getName()).log(Level.SEVERE, "找不到任何可用的模版，现在将使用默认模版: default");
//        return "default";
    }
}
