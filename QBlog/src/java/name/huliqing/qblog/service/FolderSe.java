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

import java.util.Date;
import java.util.List;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.daocache.FolderCache;
import name.huliqing.qblog.entity.FolderEn;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;

/**
 *
 * @author huliqing
 */
public class FolderSe {

    public final static boolean save(FolderEn folder) {
        folder.setCreateDate(new Date());
        return FolderCache.getInstance().save(folder);
    }

    public final static boolean update(FolderEn folder) {
        return FolderCache.getInstance().update(folder);
    }

    /**
     * 删除一个相册，该方法同时删除相册下的所有图片
     * @param folderId
     * @return
     */
    public final static boolean delete(Long folderId) {
        if (QBlog.getApp().isDemo() && folderId.longValue() == 1L) {
            Messenger.sendError("Notice:Demo版本不允许删除默认相册,但是您可以修改或编辑它.");
            return false;
        }
        PhotoSe.deleteByFolderId(folderId); // 删除图片，相册内不一定存在着图片
        return FolderCache.getInstance().delete(folderId);
    }

    public final static FolderEn find(Long folderId) {
        return FolderCache.getInstance().find(folderId);
    }

    public final static List<FolderEn> findByName(String name) {
        return FolderCache.getInstance().findByName(name);
    }

    public final static List<FolderEn> findAll() {
        return FolderCache.getInstance().findAll(null, null);
    }

    /**
     * 查询所有相册
     * @param pp
     * @return
     */
    public final static PageModel<FolderEn> findAll(PageParam pp) {
        PageModel<FolderEn> pm = new PageModel<FolderEn>();
        if ("createDate".equals(pp.getSortField()) && Boolean.FALSE.equals(pp.getAsc())) {
            pm.setPageData(FolderCache.getInstance().findAll(pp.getStart(), pp.getPageSize()));
        } else {
            pm.setPageData(FolderCache.getInstance().findAll(
                    pp.getSortField(), pp.getAsc(), pp.getStart(), pp.getPageSize()));
        }
        pm.setTotal(FolderCache.getInstance().countAll());
        return pm;
    }

    /**
     * 查询除隐私之外的所有相册
     * @param pp
     * @return
     */
    public final static PageModel<FolderEn> findAllExceptPrivate(PageParam pp) {
        PageModel<FolderEn> pm = new PageModel<FolderEn>();
        pm.setPageData(FolderCache.getInstance().findAllExceptPrivate(
                pp.getSortField(), pp.getAsc(), pp.getStart(), pp.getPageSize()));
        pm.setTotal(FolderCache.getInstance().countAll());
        return pm;
    }

    /**
     * 获取最近刚刚创建的一个"公开的"相册,如果系统中不存在public的，则返回null.
     * @return
     */
    public final static FolderEn findLastPublic() {
        List<FolderEn> fes = FolderCache.getInstance().findAllPublic(0, 1);
        if (fes != null && !fes.isEmpty()) {
            return fes.get(0);
        } else {
            return null;
        }
    }
}
