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

import java.util.List;
import name.huliqing.qblog.daocache.HelpCache;
import name.huliqing.qblog.entity.HelpEn;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;

/**
 *
 * @author huliqing
 */
public class HelpSe {

    public final static boolean save(HelpEn helpEn) {
        return HelpCache.getInstance().save(helpEn);
    }

    public final static boolean update(HelpEn helpEn) {
        return HelpCache.getInstance().update(helpEn);
    }

    public final static boolean delete(String helpId) {
        return HelpCache.getInstance().delete(helpId);
    }

    public final static HelpEn find(String helpId) {
        return HelpCache.getInstance().find(helpId);
    }

    public final static List<HelpEn> findAll() {
        return HelpCache.getInstance().findAll(); 
    }
    
    public final static PageModel<HelpEn> findAll(PageParam pp) {
        PageModel<HelpEn> pm = new PageModel<HelpEn>();
        pm.setPageData(HelpCache.getInstance().findAll(pp.getSortField(), pp.getAsc(), pp.getStart(), pp.getPageSize()));
        pm.setTotal(HelpCache.getInstance().countAll());
        return pm;
    }
}
