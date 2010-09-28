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

package name.huliqing.qblog.processor.impl;

import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import name.huliqing.qblog.component.UIModule;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.processor.XmlProcessor2;
import name.huliqing.qblog.processor.attr.AttrInputText;
import name.huliqing.qblog.processor.attr.AttrInputTextarea;
import name.huliqing.qblog.processor.attr.AttrSelectOneRadio;
import name.huliqing.qblog.processor.attr.Attribute2;
import name.huliqing.qblog.service.ModuleSe;
import name.huliqing.qfaces.QFaces;

/**
 *
 * @author huliqing
 */
public class ModulePanelProcessor extends XmlProcessor2{

    public List<Attribute2> getRequiredAttributes() {
        AttrInputText modules = new AttrInputText("Modules", "", 
                "模块ID列表,使用半角逗号分隔各个moduleId(注意：不能指定到当前模块的自身ID,指定到自身ID时将被忽略),格式像这样：moduleId1,moduleId2,moduleId3");

        AttrInputText columns = new AttrInputText("Columns", "2", "列数,即每一行渲染多少个模块,这些模块是由“Modules”参数指定的.");
        
        AttrInputText border = new AttrInputText("Border", "0", "边框，只能填整数.");

        AttrInputText width = new AttrInputText("Width", "100%", "宽度,只能填整数或百分比, 如：“100”或“100%”");

        AttrSelectOneRadio valign = new AttrSelectOneRadio("Valign", "top", "模块的垂直对齐方式。");
        valign.addItem("top", "上对齐");
        valign.addItem("middle", "居中对齐");
        valign.addItem("bottom", "下对齐");

        AttrInputText styleFull = new AttrInputText("Style Full", "", "table的css样式");
        styleFull.setStyle("width:99%");

        AttrInputTextarea styleColumns = new AttrInputTextarea("Style Columns", "", "各列的css样式,使用换行来分格各列的样式.");
        styleColumns.setStyle("width:99%");

        List<Attribute2> as = new ArrayList<Attribute2>(7);
        as.add(modules);
        as.add(columns);
        as.add(border);
        as.add(width);
        as.add(valign);
        as.add(styleFull);
        as.add(styleColumns);
        return as;
    }

    public UIComponent render(ModuleEn module) {
        AttrMap attr = getAttributes(module);
        ModulePanelGrid mpdt = new ModulePanelGrid();

        String[] temp = attr.getAsString("Modules", "").split(",");
        if (temp != null && temp.length > 0) {
            for (String t : temp) {
                Long moduleId = QFaces.convertToLong(t);
                if (moduleId != null) {
                    if (moduleId.longValue() == module.getModuleId().longValue()) {
                        String error = "“模块容器(ModulePanel)”不允许指定到自身的ModuleId" +
                                "，这会造成死循环，而使页面无法显示。您当前ModulePanel的ModuleId为:" + module.getModuleId() +
                                ", 参数“Modules”偿试指定的模块列表为：" + attr.getAsString("Modules", "") +
                                ", 其中包含了自身ModuleId:" + moduleId +
                                ", 程序将自动丢弃您所设定的这个moduleId,其它moduleId保留.";
                        logger.severe(error);
                        continue;
                    }
                    ModuleEn me = ModuleSe.find(moduleId);
                    if (me != null) {
                        UIModule uim = new UIModule();
                        uim.getAttributes().put("module", me);
                        uim.setReload(Boolean.TRUE);
                        mpdt.getChildren().add(uim);
                    } else {
                        logger.warning("找不到指定的ModuleEn, moduleId=" + moduleId);
                    }
                }
            }
        }
        String tempSC = attr.getAsString("Style Columns", null);
        String[] styleColumns = null;
        if (tempSC != null) {
            styleColumns = tempSC.trim().split("\n");
        }
        mpdt.setColumns(attr.getAsInteger("Columns", 2));
        mpdt.setBorder(attr.getAsString("Border", "0"));
        mpdt.setWidth(attr.getAsString("Width", null));
        mpdt.setValign(attr.getAsString("Valign", "top"));
        mpdt.setStyleFull(attr.getAsString("Style Full", null));
        mpdt.setStyleColumns(styleColumns);
        return mpdt;
    }

    public String getName() {
        return "模块容器";
    }

    public String getDescription() {
        return "模块容器，可以帮助你将其它模块以并排或网格布局的方式来显示，" +
                "当您遇到需要将一些模块并排显示时，可以使用它。";
    }

}
