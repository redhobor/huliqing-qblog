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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import name.huliqing.qblog.dao.ModuleDa;
import name.huliqing.qblog.entity.ModuleEn;

/**
 *
 * @author huliqing
 */
public class ModuleCache extends ModuleDa{
    private final static ModuleCache ins = new ModuleCache();
    private ModuleCache() {}
    public final static ModuleCache getInstance() {
        return ins;
    }

    // 缓存所有module, key -> moduleId; value -> module
    // 有序的map, 按createDate正序asc
    private Map<Long, ModuleEn> moduleMap;

    private boolean needToRecache = true;

    @Override
    public boolean save(ModuleEn t) {
        needToRecache = true;
        return super.save(t);
    }

    @Override
    public boolean update(ModuleEn t) {
        if (t.getModuleId() == null)
            throw new NullPointerException("module id couldn't be null.");

        needToRecache = true;
        return super.update(t);
    }

    @Override
    public boolean delete(Long id) {
        needToRecache = true;
        return super.delete(id);
    }

    @Override
    public boolean deleteAll() {
        needToRecache = true;
        return super.deleteAll();
    }

    @Override
    public ModuleEn find(Long moduleId) {
        cacheAll();
        return moduleMap.get(moduleId);
    }

    public List<ModuleEn> findAll() {
        cacheAll();
        List<ModuleEn> asList = new ArrayList<ModuleEn>(moduleMap.size());
        Collection<ModuleEn> mes = moduleMap.values();
        for (ModuleEn me : mes) {
            asList.add(me);
        }
        return asList;
    }

    public Integer countAll() {
        cacheAll();
        return moduleMap.size();
    }

    public List<ModuleEn> findAll(Integer start, Integer size) {
        assert start != null && start >= 0;
        cacheAll();
        if (start >= moduleMap.size()) {
            return null;
        }
        int end = start + size - 1;
        if (end >= moduleMap.size()) {
            end = moduleMap.size() - 1;
        }
        List<ModuleEn> asList = new ArrayList<ModuleEn>(size);
        Object[] temp = moduleMap.values().toArray();
        for (int i = start; i <= end; i++) {
            asList.add((ModuleEn)temp[i]);
        }
        return asList;
    }

    private void cacheAll() {
        if (needToRecache) {
            needToRecache = false;
            logger.info("载入Module开始.");
            List<ModuleEn> mes = super.findAll("createDate", Boolean.TRUE, null, null);
            if (mes != null && !mes.isEmpty()) {
                moduleMap = new LinkedHashMap<Long, ModuleEn>(mes.size());
                for (ModuleEn me : mes) {
                    moduleMap.put(me.getModuleId(), me);
                }
            } else {
                moduleMap = new HashMap<Long, ModuleEn>(0);
            }
            logger.info("载入Module OK, size=" + moduleMap.size());
        }
    }
}
