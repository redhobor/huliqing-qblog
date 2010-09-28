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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import name.huliqing.qblog.dao.PageModuleDa;
import name.huliqing.qblog.entity.PageModuleEn;

/**
 *
 * @author huliqing
 */
public class PageModuleCache extends PageModuleDa{
    private final static PageModuleCache ins = new PageModuleCache();
    private PageModuleCache() {}
    public final static PageModuleCache getInstance() {
        return ins;
    }

    // PageModuleAll,  key -> pageId, value -> PageModuleEn
    // notice: key is PageId not PageModuleId
    private Map<Long, List<PageModuleEn>> pmMap;

    @Override
    public boolean save(PageModuleEn t) {
        throw new UnsupportedOperationException("Don't use this method.");
    }

    @Override
    public boolean update(PageModuleEn t) {
        throw new UnsupportedOperationException("Don't use this method.");
    }

    @Override
    public boolean delete(Long pageModuleId) {
        throw new UnsupportedOperationException("Don't use this method.");
    }

    /**
     * 保存一个配置信息
     * @param pageId
     * @param group
     * @param moduleId
     * @param sort
     */
    public boolean saveToPage(Long pageId, List<PageModuleEn> pmes) {
        if (pmes != null) {
            // check data
            for (PageModuleEn pme : pmes) {
                if (pme.getModuleId() == null || pme.getModuleGroup() == null) {
                    throw new NullPointerException("moduleId=" + pme.getModuleId()
                            + ", group=" + pme.getModuleGroup());
                }
            }
            // 保存配置
            for (PageModuleEn pme : pmes) {
                pme.setPageId(pageId);
                super.save(pme);
            }
            // 重新缓存页面配置信息
            cachePageModules(pageId);
            return true;
        }
        return false;
    }

    /**
     * 删除某个页面下的所有配置信息
     * @param pageId
     * @return
     */
    public boolean deleteByPage(Long pageId) {
        PageModuleEn s = new PageModuleEn();
        s.setPageId(pageId);
        List<PageModuleEn> pmes = findByObject(s, null, null, null, null);
        for (PageModuleEn pme : pmes) {
            super.delete(pme.getPageModuleId());
        }
        if (pmMap != null) {
            pmMap.remove(pageId);
        }
        return true;
    }

    @Override
    public boolean deleteAll() {
        super.deleteAll();
        pmMap = null;
        return true;
    }

    /**
     * 导入
     * @param pmes
     * @return
     */
    public boolean importAll(List<PageModuleEn> pmes) {
        if (pmes != null && !pmes.isEmpty()) {
            for (PageModuleEn pme : pmes) {
                if (pme.getPageId() != null && pme.getModuleGroup() != null && pme.getModuleId() != null) {
                    super.save(pme);
                }
            }
            // called to recache all
            pmMap = null;
            return true;
        }
        return false;
    }

    /**
     * 导出
     * @return
     */
    public List<PageModuleEn> exportAll() {
        return super.findAll(null, null, null, null);
    }
    
    /**
     * 醒找某一页面的配置信息
     * @param pageId
     * @return
     */
    public List<PageModuleEn> findByPage(Long pageId) {
        if (pmMap == null || !pmMap.containsKey(pageId)) {
            cachePageModules(pageId);
        }
        return pmMap.get(pageId);
    }

    /**
     * 重新缓存某个页面的配置
     * @param pageId
     */
    private void cachePageModules(Long pageId) {
        if (pmMap == null) {
            pmMap = new HashMap<Long, List<PageModuleEn>>();
        }
        pmMap.remove(pageId);
        PageModuleEn s = new PageModuleEn();
        s.setPageId(pageId);
        List<PageModuleEn> pmes = findByObject(s, null, null, null, null);
        if (pmes == null) {
            pmes = new ArrayList<PageModuleEn>();
        }
        Collections.sort(pmes, new OrderByGroupAndSort());
        pmMap.put(pageId, pmes);
    }

    private class OrderByGroupAndSort implements Comparator<PageModuleEn> {

        // 先按group排序，再按sort排序
        public int compare(PageModuleEn pm1, PageModuleEn pm2) {
            int result = pm1.getModuleGroup().compareTo(pm2.getModuleGroup());
            if (result == 0) {
                result = pm1.getSort().compareTo(pm2.getSort());
            }
            return result;
        }

    }
}
