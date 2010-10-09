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
import java.util.LinkedHashMap;
import java.util.List;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.dao.FolderDa;
import name.huliqing.qblog.entity.FolderEn;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qblog.enums.Security;

/**
 *
 * @author huliqing
 */
public class FolderCache extends FolderDa{
    private final static FolderCache ins = new FolderCache();
    private FolderCache(){}
    public final static FolderCache getInstance() {
        return ins;
    }

    // 缓存最近创建的相册夹,这个Map是有顺序的，根据createDate倒序。最新发表的放在0位置
    private LinkedHashMap<Long, FolderEn> folderMap;
    private boolean needToRecache = true;

    @Override
    public boolean save(FolderEn t) {
        needToRecache = true;
        return super.save(t);
    }

    @Override
    public boolean update(FolderEn t) {
        if (t.getFolderId() == null)
            throw new NullPointerException("FolderId not found!");
        needToRecache = true;
        return super.update(t);
    }

    @Override
    public boolean delete(Long id) {
        needToRecache = true;
        return super.delete(id);
    }

    @Override
    public FolderEn find(Long id) {
        cacheFolders();
        if (folderMap.containsKey(id)) {
            return folderMap.get(id);
        }
        return super.find(id);
    }

    /**
     * 通过名称查询folder
     * @param name
     * @return
     */
    public List<FolderEn> findByName(String name) {
        FolderEn fe = new FolderEn();
        fe.setName(name);
        List<FolderEn> folders = findByObject(fe, null, null, null, null);
        return folders;
    }

    /**
     * 查找相册文件夹，从start位置开始，查找size数量的记录，该方法使用createDate倒序
     * 排列
     * @param start 如果start为null,则从0取起
     * @param size 如果size为null则获取全部
     * @return
     */
    public List<FolderEn> findAll(Integer start, Integer size) {
        cacheFolders();
        if (start == null)
            start = 0;
        List<FolderEn> result = new ArrayList<FolderEn>();
        if (!folderMap.isEmpty() && start < folderMap.size()) {
            Collection<FolderEn> fes = folderMap.values();
            int i = 0;
            for (FolderEn fe : fes) {
                if (i >= start) {
                    result.add(fe);
                }
                if (size != null && result.size() >= size) {
                    break;
                }
                i++;
            }
        }
        if (size == null || result.size() < size) {
            Integer nextStart = start + result.size();
            Integer needToGet = null;
            if (size != null) {
                needToGet = size - result.size();
            }
            FolderEn search = new FolderEn();
            List<FolderEn> other = super.findByObject(search, "createDate", Boolean.FALSE, nextStart, needToGet);
            if (other != null && !other.isEmpty()) {
                result.addAll(other);
            }
        }
        return result;
    }

    @Override
    public List<FolderEn> findAll(String sortField, Boolean asc, Integer start, Integer size) {
        return super.findAll(sortField, asc, start, size);
    }

    public Integer countAll() {
        return countByObject(new FolderEn());
    }

    @Override
    public List<FolderEn> findAllExceptPrivate(String sortField, Boolean asc, Integer start, Integer size) {
        return super.findAllExceptPrivate(sortField, asc, start, size);
    }

    @Override
    public Integer countAllExceptPrivate() {
        return super.countAllExceptPrivate();
    }

    /**
     * 查找最近创建的“公开”的相册,该方法按createDate倒序
     * @param start 起始位置，如果为null则从0开始
     * @param size 允许返回的最高数量
     * @return
     */
    public List<FolderEn> findAllPublic(Integer start, Integer size) {
        cacheFolders();
        if (start == null)
            start = 0;
        List<FolderEn> result = new ArrayList<FolderEn>();
        if (!folderMap.isEmpty() && start < folderMap.size()) {
            Collection<FolderEn> fes = folderMap.values();
            int i = 0;
            for (FolderEn fe : fes) {
                if (i >= start && fe.getSecurity() == Security.PUBLIC) {
                    result.add(fe);
                }
                if (size != null && result.size() >= size) {
                    break;
                }
                i++;
            }
        }
        if (size == null || result.size() < size) {
            Integer nextStart = start + result.size();
            Integer needToGet = null;
            if (size != null) {
                needToGet = size - result.size();
            }
            FolderEn search = new FolderEn();
            search.setSecurity(Security.PUBLIC);
            List<FolderEn> other = super.findByObject(search, "createDate", Boolean.FALSE, nextStart, needToGet);
            if (other != null && !other.isEmpty()) {
                result.addAll(other);
            }
        }
        return result;
    }

    private void cacheFolders() {
        if (needToRecache) {
            needToRecache = false;
            Integer limit = ConfigManager.getInstance().getAsInteger(Config.CON_PHOTO_CACHE_FOLDER);
            if (limit <= 0) {
               folderMap = new LinkedHashMap<Long, FolderEn>(0);
               return;
            }
            folderMap = new LinkedHashMap<Long, FolderEn>(limit);
            List<FolderEn> fes = super.findByObject(new FolderEn(), "createDate", Boolean.FALSE, 0, limit);
            if (fes != null && !fes.isEmpty()) {
                for (FolderEn fe : fes) {
                    folderMap.put(fe.getFolderId(), fe);
                }
            }
        }
    }
}
