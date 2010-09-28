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

import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.web.BaseWe;
import name.huliqing.qblog.processor.Processor;
import name.huliqing.qblog.processor.ProcessorFactory;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;

/**
 *
 * @author huliqing
 */
@ManagedBean
@RequestScoped
public class ProcessorListWe extends BaseWe {
 
    private UIData uiData;

    public ProcessorListWe() {
        super();
    }

    public PageModel<Processor> loadData(PageParam pp) {
        List<Processor> all = ProcessorFactory.findAllProcessor();
        if (pp.getStart() >= all.size())
            return null;
        int end = pp.getStart().intValue() + pp.getPageSize().intValue();
        int start = pp.getStart().intValue();
        if (end >= all.size())
            end = all.size() - 1;
        List<Processor> data = new ArrayList<Processor>(end - start);
        for (int i = start; i <= end; i++) {
            data.add(all.get(i));
        }
        PageModel<Processor> pm = new PageModel<Processor>();
        pm.setTotal(all.size());
        pm.setPageData(data);
        return pm;
    }

    public UIData getUiData() {
        return uiData;
    }

    public void setUiData(UIData uiData) {
        this.uiData = uiData;
    }

    // ---- Action

    public void selectProcessor() {
        Processor p = (Processor) this.uiData.getRowData();
        ModuleEditWe mew = (ModuleEditWe) QBlog.getBean("moduleEditWe");
        mew.setProcessorName(p.getType());
        FacesContext fc = QBlog.getFacesContext();
        UIViewRoot view = fc.getApplication().getViewHandler().createView(fc, "/admin/system/moduleAdd.xhtml");
        fc.setViewRoot(view);
    }
}
