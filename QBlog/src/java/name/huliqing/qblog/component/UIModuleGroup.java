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

package name.huliqing.qblog.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.entity.PageModuleEn;
import name.huliqing.qblog.enums.Group;
import name.huliqing.qblog.service.ModuleSe;
import name.huliqing.qblog.service.PageModuleSe;
import name.huliqing.qfaces.QFaces;

/**
 *
 * @author huliqing
 */
public class UIModuleGroup extends UIComponentBase{
    private final static Logger logger = Logger.getLogger(UIModuleGroup.class.getName());

    // 用于确定是否已经装载了当前Group的Module
    private Boolean hasInitModules;

    private Long pageId;
    private String groupId;

    @Override
    public String getFamily() {
        return null;
    }

    @Override
    public void restoreState(FacesContext fc, Object state) {
        Object[] _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.hasInitModules = (Boolean) _values[1];
        this.pageId = (Long) _values[2];
        this.groupId = (String) _values[3];
    }

    @Override
    public Object saveState(FacesContext fc) {
        Object[] _values = new Object[4];
        _values[0] = super.saveState(fc);
        _values[1] = this.hasInitModules;
        _values[2] = this.pageId;
        _values[3] = this.groupId;
        return _values;
    }

    @Override
    public void encodeBegin(FacesContext fc) throws IOException {
        if (hasInitModules == null)
            hasInitModules = false;
 
        if (pageId == null)
            pageId = QFaces.convertToLong(this.getAttributes().get("pageId"));
        
        if (groupId == null)
            groupId = getId();

        // 在回传的时候hasInitModules应该为true,所以不应该再初始化
        if (!hasInitModules) {
            hasInitModules = true;
            getChildren().clear();
            // 获取所有模组件
            List<ModuleEn> modules = loadModules(pageId, groupId);
            if (modules != null && !modules.isEmpty()) {
                for (ModuleEn m : modules) {
                    UIModule uim = new UIModule();
                    uim.getAttributes().put("module", m);
                    // 必要的,告诉UIModule必须保存组件状态，否则在回传时会丢失数据， 
                    // 这会造成UIModule每次都重渲染并初始化Processor生成的组件(不论存在回传与否)
                    uim.setReload(Boolean.TRUE);
                    getChildren().add(uim);
                }
            }
        }
        ResponseWriter rw = fc.getResponseWriter();
        rw.startElement("div", this);
        rw.writeAttribute("id", groupId, null);
        rw.writeAttribute("style", "margin:0;padding:0;border:0;", null);
        // Group Title
        rw.startElement("div", this);
        rw.writeAttribute("id", groupId + ":title", null);
        rw.writeAttribute("style", "display:none;padding:3px;", null);
        rw.writeText(groupId, null);
        rw.endElement("div");
    }

    @Override
    public void encodeEnd(FacesContext fc) throws IOException {
        ResponseWriter rw = fc.getResponseWriter();
        rw.endElement("div");
    }

    public List<ModuleEn> loadModules(Long pageId, String groupName) {
        if (pageId == null) {
            FacesContext fc = FacesContext.getCurrentInstance();
            throw new NullPointerException("Page Id not found, viewId=" + fc.getViewRoot().getViewId());
        }

        Group group = null;
        try {
            group = Group.valueOf(groupName);
        } catch (Exception e) {
            logger.severe("Unknow group name, group=" + groupName);
            return null;
        }

        // Fetch Module
        List<PageModuleEn> pmes = PageModuleSe.find(pageId, group);
        if (pmes == null || pmes.isEmpty())
            return null;

        List<ModuleEn> mes = new ArrayList<ModuleEn>(pmes.size());
        for (PageModuleEn pme : pmes) {
            ModuleEn me = ModuleSe.find(pme.getModuleId());
            // 只有module为enable的才需要显示
            if (me != null && me.getEnabled()) {
                mes.add(me);
            }
        }
        return mes;
    }

    /**
     * 设置为false后，当前ModuleGroup将重新载入所有Modules
     * @param b
     */
    public void setHasInitModules(Boolean b) {
        this.hasInitModules = b;
    }
}
