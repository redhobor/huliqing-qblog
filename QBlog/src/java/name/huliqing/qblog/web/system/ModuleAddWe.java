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

package name.huliqing.qblog.web.system;

import com.google.appengine.api.datastore.Text;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import name.huliqing.qblog.web.BaseWe;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.processor.Processor;
import name.huliqing.qblog.processor.ProcessorFactory;
import name.huliqing.qblog.processor.attr.Attribute2;
import name.huliqing.qblog.processor.attr.UIAttribute;
import name.huliqing.qblog.service.ModuleSe;

/**
 *
 * @author huliqing
 */
public class ModuleAddWe extends BaseWe {

    protected ModuleEn module;
    
    protected String processorName;
    protected List<Attribute2> attrs;
    protected UIAttribute uiAttrs;

    public ModuleAddWe() {
        super();
        String temp = QBlog.getParam("processorName");
        if (temp != null)
            this.processorName = temp;
    }

    public List<Attribute2> getAttrs() {
        if (attrs == null) {
            Processor p = ProcessorFactory.find(this.processorName);
            this.attrs = p.decode(getModule().getAttributes() != null ? getModule().getAttributes().getValue() : null);
        }
        return attrs;
    }

    public void setAttrs(List<Attribute2> attrs) {
        this.attrs = attrs;
    }

    public UIAttribute getUiAttrs() {
        return uiAttrs;
    }

    public void setUiAttrs(UIAttribute uiAttrs) {
        this.uiAttrs = uiAttrs;
    }

    public ModuleEn getModule() {
        if (this.module == null) {
            module = new ModuleEn();
            module.setEnabled(true);
            module.setDisplayName(true);
            if (this.processorName != null) {
                module.setProcessor(this.processorName);
                // setAutoStyle, important,默认来自于processor的关于autoStyle的建议
                // 在界面用户可以手动改变
                Processor p = ProcessorFactory.find(this.processorName);
                module.setName(p.getName());
                module.setEnabled(p.defaultEnabled());
                module.setAutoStyle(p.defaultAutoStyle());
                module.setDisplayName(p.defaultShowName());
            }
        }
        return module;
    }

    public void setModule(ModuleEn module) {
        this.module = module;
    }

    public String getProcessorName() {
        return processorName;
    }

    public void setProcessorName(String processorName) {
        this.processorName = processorName;
    }

    public String createModule() {
        List<UIComponent> children = uiAttrs.getChildren();
        if (children != null && !children.isEmpty()) {
            List<Attribute2> attributes = new ArrayList<Attribute2>(children.size());
            for (UIComponent child : children) {
                attributes.add((Attribute2) child);
            }
            Processor p = ProcessorFactory.find(processorName);
            String xmlStr = p.encode(attributes);
            module.setProcessor(processorName);
            module.setAttributes(new Text(xmlStr));
        }
        if (ModuleSe.save(module)) {
            QBlog.redirect("/admin/system/moduleList.faces");
        }
        return null;
    }
}
