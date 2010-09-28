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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;
import name.huliqing.qblog.LayoutManager;
import name.huliqing.qblog.LayoutManager.Layout;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.entity.PageEn;
import name.huliqing.qblog.entity.PageModuleEn;
import name.huliqing.qblog.enums.Group;
import name.huliqing.qblog.service.ModuleSe;
import name.huliqing.qblog.service.PageModuleSe;
import name.huliqing.qblog.service.PageSe;
import name.huliqing.qfaces.QFaces;
import name.huliqing.qfaces.model.MiniPageModel;
import name.huliqing.qfaces.model.MiniPageParam;
import name.huliqing.qfaces.model.PageModel;

/**
 *
 * @author huliqing
 */
@ManagedBean
@RequestScoped
public class PmcWe extends BaseWe {

    private Long pageId;

    // 如果存在值，则表明指定了特定的layout
    private String layout;

    // 页面配置后的临时有格式字符串
    private String configValue;
    
    public PmcWe() {
        super();
        Long tempPageId = QFaces.convertToLong(QBlog.getParam("pageId"));
        if (tempPageId != null) {
            pageId = tempPageId;
            PageEn pe = PageSe.find(tempPageId);
            if (pe != null) {
                layout = pe.getLayout();
            }
        }
        String tempLayout = QBlog.getParam("layout");
        if (tempLayout != null) {
            this.layout = tempLayout;
        }
    }

    // ---- Getter and Setter

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public String getLayout() {
        return layout;
    }

    public void setLayout(String layout) {
        this.layout = layout;
    }

    public List<SelectItem> getLayouts() {
        List<SelectItem> layouts = new ArrayList<SelectItem>();
        Map<String, Layout> asMap = LayoutManager.getInstance().getAllLayout();
        if (asMap != null && !asMap.isEmpty()) {
            Set<String> keys = asMap.keySet();
            for (String key : keys) {
                Layout l = asMap.get(key);
                layouts.add(new SelectItem(l.getName(), l.getName()));
            }
        }
        return layouts;
    }

    // ---- load modules

    public MiniPageModel loadModules(MiniPageParam mpp) {
        MiniPageModel mpm = new MiniPageModel();
        PageModel<ModuleEn> pm = ModuleSe.findAll(mpp);
        List<ModuleEn> mes = pm.getPageData();
        for (ModuleEn me : mes) {
            List<String> r = new ArrayList<String>(1);
            StringBuilder sb = new StringBuilder()
            .append("<div").append(" class=\"bold\" ").append(">")
            .append(me.getName())
            .append(createActionButton(me.getModuleId()))
            .append("</div>")

            .append("<div").append(" class=\"gray\" ").append(">")
            .append(me.getDes())
            .append("</div>")
            .append(""); // End.
            r.add(sb.toString()); 
            mpm.addRecord(r);
        }
        mpm.setTotal(pm.getTotal());
        return mpm;
    }

    private String createActionButton(Long moduleId) {
        StringBuilder sb = new StringBuilder()
                .append("<a href=\"javascript:void(0)\" style=\"float:right;\" ")
                .append(" onclick=\"AjaxModule.createModule('" + moduleId + "')\"")
                .append(">")
                .append("生成模块->")
                .append("</a>");
        return sb.toString();
    }

    // ---- Action

    // 保存配置
    public void saveConfig() {
        if (configValue == null || "".equals(configValue))
            return;

        // 更新页面Layout
        PageEn pe = PageSe.find(pageId);
        pe.setLayout(layout);
        PageSe.update(pe);

        // 删除页面旧的配置
        PageModuleSe.deleteByPage(pageId);

        // Format -> groupA,moduleId,moduleId;groupB,moduleId,moduleId;groupC,moduleId,moduleId,moduleId
        String[] groups = configValue.split(";");
        for (int i = 0; i < groups.length; i++) {
            String[] groupStr = groups[i].split(",");
            // 大于1才有意义，因temp[0] = groupName,temp[1]开始才为module id
            if (groupStr.length <= 1)
                continue;
            Group group = null;
            try {
                 group = Group.valueOf(groupStr[0]);
            } catch (Exception e) {
                // Group Name not correct.
                logger.severe("Group name unknow, group name=" + groupStr[0]);
                continue;
            }
            // Add new module to current group;
            List<PageModuleEn> pmes = new ArrayList<PageModuleEn>(groupStr.length - 1);
            for (int j = 1; j < groupStr.length; j++) {
                Long moduleId = QFaces.convertToLong(groupStr[j]);
                PageModuleEn pme = new PageModuleEn();
                pme.setModuleGroup(group);
                pme.setModuleId(moduleId);
                pme.setSort(j);
                pmes.add(pme);
            }
            PageModuleSe.saveToPage(pageId, pmes);
        }
        // 重新载入所有Module信息
        QBlog.redirect("/page/pageId=" + this.pageId);
    }
}
