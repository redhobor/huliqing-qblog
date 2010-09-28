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

package name.huliqing.qblog.daocache;

import java.util.ArrayList;
import java.util.List;
import name.huliqing.qblog.dao.PageDa;
import name.huliqing.qblog.entity.PageEn;

/**
 *
 * @author huliqing
 */
public class PageCache extends PageDa{
    private final static PageCache ins = new PageCache();
    private PageCache() {} 
    public final static PageCache getInstance() {
        return ins;
    }

    // 缓存所有page
    private List<PageEn> pageAll;
    // 是否需要重新载入页面信息
    private boolean needToRecache = true;

    @Override
    public boolean save(PageEn t) {
        needToRecache = true;
        return super.save(t);
    }

    @Override
    public boolean update(PageEn t) {
        if (t.getPageId() == null)
            throw new NullPointerException("PageId not found!");
        needToRecache = true;
        return super.update(t);
    }

    @Override
    public boolean delete(Long id) {
        needToRecache = true;
        return super.delete(id);
    }

    /**
     * 直接删除所有页面信息
     * @return
     */
    @Override
    public boolean deleteAll() {
        needToRecache = true;
        return super.deleteAll();
    }

    @Override
    public PageEn find(Long pageId) {
        List<PageEn> all = findAll();
        for (PageEn pe : all) {
            if (pe.getPageId().longValue() == pageId.longValue()) {
                return pe;
            }
        }
        return null;
    }

    /**
     * 查找所有page,按sort字段正序
     * @return
     */
    public List<PageEn> findAll() {
        if (needToRecache) {
            cacheAllPages();
        }
        return pageAll;
    }

    public Integer countAll() {
        return findAll().size();
    }

    /**
     * 查找所有开启的Page,该方法按sort字段正序排序
     * @return
     */
    public List<PageEn> findAllEnabled() {
        List<PageEn> all = findAll();
        List<PageEn> result = new ArrayList<PageEn>(all.size());
        for (PageEn pe : all) {
            if (pe.getEnabled() != null && pe.getEnabled()) {
                result.add(pe);
            }
        }
        return result;
    }

    private void cacheAllPages() {
        logger.info("载入页面开始.");
        needToRecache = false;
        pageAll = super.findAll("sort", Boolean.TRUE, null, null);
        if (pageAll == null) {
            pageAll = new ArrayList<PageEn>(0);
        }
        logger.info("载入页面OK, size=" + pageAll.size());
    }
}
