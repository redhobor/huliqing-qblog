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

package name.huliqing.qblog.web.blog;

import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIData;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.entity.CategoryEn;
import name.huliqing.qblog.service.CategorySe;
import name.huliqing.qblog.web.BaseWe;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;

/**
 * @author huliqing
 */
@ManagedBean
@RequestScoped
public class CategoryWe extends BaseWe{

    private CategoryEn category;
    private UIData uiCategorys;

    public CategoryWe() {
        super();
    }

    public CategoryEn getCategory() {
        if (category == null) {
            category = new CategoryEn();
        }
        return category;
    }

    public void setCategory(CategoryEn category) {
        this.category = category;
    }

    public UIData getUiCategorys() {
        return uiCategorys;
    }

    public void setUiCategorys(UIData uiCategorys) {
        this.uiCategorys = uiCategorys;
    }

    // ---- Action

    public PageModel<CategoryEn> loadData(PageParam pp) {
        if (pp.getSortField() == null) {
            pp.setSortField("sort");
            pp.setAsc(Boolean.TRUE);
        }
        return CategorySe.findAll(pp);
    }

    public void save() {
        if (CategorySe.save(category)) {
            Messenger.sendInfo("新建分类成功, Category=" + category.getName());
            category = null;
        }
    }

    public void delete() {
        CategoryEn ce = (CategoryEn) uiCategorys.getRowData();
        if (CategorySe.delete(ce.getName())) {
            Messenger.sendInfo("删除分类成功， Category=" + ce.getName());
        }
    }

    public void updateAll() {
        List<CategoryEn> categorys = (List<CategoryEn>) uiCategorys.getValue();
        if (categorys != null && !categorys.isEmpty()) {
            for (CategoryEn ce : categorys) {
                CategorySe.update(ce);
            }
            Messenger.sendInfo("批量更新成功.");
        }
    }
}
