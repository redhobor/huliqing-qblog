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

package name.huliqing.qblog.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.model.SelectItem;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.daocache.TagCache;
import name.huliqing.qblog.entity.TagEn;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;

/**
 *
 * @author huliqing
 */
public class TagSe {

    public final static Boolean save(TagEn tagEn) {
        TagEn te = TagCache.getInstance().find(tagEn.getName());
        if (te != null) {
            Messenger.sendError("该Tag已经存在, Tag Name=" + te.getName());
            return Boolean.FALSE;
        }
        return TagCache.getInstance().save(tagEn);
    }

    public final static Boolean update(TagEn tagEn) {
        tagEn.setModifyDate(new Date());
        return TagCache.getInstance().update(tagEn);
    }

    public final static Boolean delete(String tagName) {
        return TagCache.getInstance().delete(tagName);
    }

    public final static TagEn find(String tagName) {
        TagEn te = TagCache.getInstance().find(tagName);
        return te;
    }

    public final static List<TagEn> findAll() {
        return TagCache.getInstance().findAll();
    }

    public final static PageModel<TagEn> findAll(PageParam pp) {
        PageModel<TagEn> pm = new PageModel<TagEn>();
        pm.setPageData(TagCache.getInstance().findAll(
                pp.getSortField(), pp.getAsc(), pp.getStart(), pp.getPageSize()));
        pm.setTotal(TagCache.getInstance().countAll());
        return pm;
    }
    
    public final static List<SelectItem> findAllAsSelectItems() {
        List<TagEn> tags = TagCache.getInstance().findAll();
        if (tags == null) {
            return new ArrayList<SelectItem>(0);
        }
        List<SelectItem> items = new ArrayList<SelectItem>(tags.size());
        for (TagEn te : tags) {
            items.add(new SelectItem(te.getName(), te.getName()));
        }
        return items;
    }
}
