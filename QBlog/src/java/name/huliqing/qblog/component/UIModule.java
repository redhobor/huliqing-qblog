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
import java.util.List;
import java.util.logging.Logger;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.enums.Style;
import name.huliqing.qblog.processor.Processor;
import name.huliqing.qblog.processor.ProcessorFactory;
import name.huliqing.qfaces.ResLoader2.LoadType;

/**
 *
 * @author huliqing
 */
public class UIModule extends UIComponentBase {
    private final static Logger logger = Logger.getLogger(UIModule.class.getName());

    public enum Type {
        /**
         * 懒惰的，即装载一次数据之后，将保存数据状态，
         * 在回传时将不再重新装载并初始化Module数据
         */
        LAZY,

        /**
         * 每次都重新装载Module数据并初始化，不论是否存在回传。
         */
        ENERGY;
    }

    // 参考above
    private UIModule.Type type;

    // 告诉组件是否需要重新装载Module数据并初始化。如果为true,则忽略Type属性
    private Boolean reload;


    // 整个Module的页面容器ID
    private String modulePanelId;

    // Module Name
    private String name;

    // 是否在页面显示Module Name
    private Boolean displayName;

    // 是否自动应用来自模版文件的样式
    private Boolean autoStyle;

    @Override
    public void restoreState(FacesContext fc, Object state) {
        Object[] _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.reload = (Boolean) _values[1];
        this.modulePanelId = (String) _values[2];
        this.type = (UIModule.Type) _values[3];
        this.name = (String) _values[4];
        this.displayName = (Boolean) _values[5];
        this.autoStyle = (Boolean) _values[6];
    }

    @Override
    public Object saveState(FacesContext fc) {
        Object[] _values = new Object[7];
        _values[0] = super.saveState(fc);
        _values[1] = this.reload;
        _values[2] = this.modulePanelId;
        _values[3] = this.type;
        _values[4] = this.name;
        _values[5] = this.displayName;
        _values[6] = this.autoStyle;
        return _values;
    }

    @Override
    public String getFamily() {
        return null;
    }

    private Object getFromExp(String name) {
        ValueExpression _ve = getValueExpression(name);
        if (_ve != null) {
            return _ve.getValue(getFacesContext().getELContext());
        } else {
            return null;
        }
    }

    public Boolean getReload() {
        if (reload != null)
            return reload;
        return (Boolean) this.getFromExp("reload");
    }

    public void setReload(Boolean reload) {
        this.reload = reload;
    }

    public Type getType() {
        if (type != null)
            return type;
        Object temp = this.getFromExp("type");
        if (temp != null) {
            try {
                return UIModule.Type.valueOf(temp.toString());
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setType(UIModule.Type type) {
        this.type = type;
    }


    // ---- Encode

    @Override
    public void encodeBegin(FacesContext fc) throws IOException {
        if (this.getType() == null) {
            this.setType(UIModule.Type.LAZY);
        }

        if (this.getReload() != null && this.getReload()) {
            // 下次将不再重新载入
            this.setReload(Boolean.FALSE);
            loadData();
        } else {
            if (UIModule.Type.ENERGY == this.type) {
                loadData();
            }
        }

        // ----Render modulePanel
        ResponseWriter rw = fc.getResponseWriter();
        String nameId = modulePanelId + ":name";

        // Render All
        rw.startElement("div", this);
        rw.writeAttribute("id", modulePanelId, null);
        rw.writeAttribute("class", Style.css_module_full, null);

        // Render module name
        rw.startElement("div", this);
        rw.writeAttribute("class", (autoStyle && displayName ? Style.css_module_title : ""), null);
        encodeModuleName(rw, nameId);
        rw.endElement("div");

        // Render module content
        rw.startElement("div", this);

        // 如果用户有登录，但是没有显示模块名称，那么应该在内容区域触发事件，使它
        // 能够触发模块名称的显示，从而可以通过名称直接点击编辑按钮
        if (QBlog.getCurrentVisitor().isLogin() && !displayName) {
            String eventOver = "document.getElementById('" + nameId + "').style.display = \"\"";
            String eventOut = "document.getElementById('" + nameId + "').style.display = \"none\"";
            rw.writeAttribute("onmouseover", eventOver, null);
            rw.writeAttribute("onmouseout", eventOut, null);
        }
        
        rw.writeAttribute("class", (autoStyle ? Style.css_module_content : ""), null);
    }

    private void encodeModuleName(ResponseWriter rw, String nameId) throws IOException {
        // Edit button
        ModuleEn module = (ModuleEn) this.getAttributes().get("module");
        if (QBlog.getCurrentVisitor().isLogin()) {
            String url = "/admin/system/moduleEdit.faces?moduleId=" + module.getModuleId();
            String returnURL = QBlog.getOriginalURI(true, true);
            if (returnURL != null) {
                url += "&returnURL=" + returnURL;
            }

            String eventOver = "document.getElementById('" + nameId + "').style.display = ''";
            rw.startElement("a", this);
            rw.writeAttribute("href", url, null);
            rw.writeAttribute("target", "_self", null);
            rw.startElement("div", this);
            if (!displayName) {
                rw.writeAttribute("style", "cursor:pointer;display:none;position:absolute;padding:2px;margin-top:-15px;border:1px dotted #CCC;", null);
            }
            rw.writeAttribute("title", "编辑模块：" + module.getName(), null);
            rw.writeAttribute("id", nameId, null);
            rw.writeAttribute("onmouseover", eventOver, null);
            rw.writeText(module.getName(), null);
            rw.endElement("div");
            rw.endElement("a");
        } else {
            rw.startElement("div", this);
            rw.writeAttribute("style", displayName ? "" : "display:none;", null);
            rw.writeText(module.getName(), null);
            rw.endElement("div");
        }
    }

    @Override
    public void encodeEnd(FacesContext fc) throws IOException {
        ResponseWriter rw = fc.getResponseWriter();
        rw.endElement("div"); // End content
        rw.endElement("div"); // End All
    }

    private void loadData() {
        // Load module data
        ModuleEn module = (ModuleEn) this.getAttributes().get("module");
        if (module == null)
            throw new NullPointerException("\"module\" not found!");

        // LoadType
        // 在进行Ajax生成组件时必须确保loadType为energy,这保证组件的所有资源能够及时
        // 的装载。这不是必须的，但是使用energy时可以在生成组件时实时的看到组件效果。
        // 在配置页面时使用energy可获得较好效果。
        // 而对于正常显示页面则不要使用energy,这个参数会使资源不能够被缓存。
        // 注：这个参数只对QFaces组件有效.
        Boolean energy = LoadType.ENERGY.name().equals(this.getAttributes().get("loadType"));
        
        name = module.getName();
        displayName = module.getDisplayName();
        if (displayName == null)
            displayName = Boolean.TRUE;
        autoStyle = module.getAutoStyle();
        if (autoStyle == null)
            autoStyle = Boolean.TRUE;
        modulePanelId = "module:" + module.getModuleId() + ":" + System.currentTimeMillis();

        // 从Module中找出渲染处理器
        Processor p = ProcessorFactory.find(module.getProcessor());
        UIComponent uiFromRenderer = p.render(module);

        // 关于energy,参考上面说明
        if (energy && uiFromRenderer != null) {
            long start = System.currentTimeMillis();
            processAsEnergy(uiFromRenderer);
            logger.info("Process ui as energy use time="
                    + (System.currentTimeMillis() - start));
        }
        // 清除其它组件,必要的
        getChildren().clear();
        // 添加到当前组件中
        getChildren().add(uiFromRenderer);
    }

    private void processAsEnergy(UIComponent ui) {
        ui.getAttributes().put("loadType", LoadType.ENERGY.name());
        List<UIComponent> children = ui.getChildren();
        for (UIComponent child : children) {
            processAsEnergy(child);
        }
    }
}
