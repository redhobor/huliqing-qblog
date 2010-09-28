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

import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.daocache.PhotoCache;
import name.huliqing.qblog.entity.FolderEn;
import name.huliqing.qblog.entity.PhotoEn;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;

/**
 *
 * @author huliqing
 */
public class PhotoSe {

    public final static boolean save(PhotoEn photo) {
        FolderEn folder = FolderSe.find(photo.getFolder());
        if (folder == null) {
            Messenger.sendError("指定的相册不存在,Folder Id=" + photo.getFolder());
            return false;
        }
        if (PhotoCache.getInstance().save(photo)) {
            // 检查图片文件夹是否缺少封面，如果缺少封面则将当前图片设为封面。
            if (folder.getCover() == null || "".equals(folder.getCover())) {
                folder.setCover("/photo/photoId=" + photo.getPhotoId() + ",min=true");
                FolderSe.update(folder);
            }
            return true;
        }
        return false;
    }

    public final static boolean update(PhotoEn photo) {
        // 这里必须重新merge数据，因为photo取出时可能没有fetch bytes数据
        // 如果直接update,则可能导致bytes,bytesMin被置为null页丢失数据
        // 并且部分字段是不能够update的
        PhotoEn pe = PhotoCache.getInstance().find(photo.getPhotoId());
        pe.setDes(photo.getDes());
        pe.setFolder(photo.getFolder());
        pe.setName(photo.getName());
        return PhotoCache.getInstance().update(pe);
    }

    /**
     * 删除某张图片
     * @param photoId
     * @return
     */
    public final static boolean delete(Long photoId) {
        return PhotoCache.getInstance().delete(photoId);
    }

    /**
     * 删除某个相册下的所有图片
     * @param folderId
     * @return
     */
    public final static boolean deleteByFolderId(Long folderId) {
        return PhotoCache.getInstance().deleteByFolderId(folderId); 
    }

    public final static PhotoEn find(Long photoId) {
        return PhotoCache.getInstance().find(photoId);
    }

    /**
     * 通过PhotoId查找图片，注：该方法不会取出photo的data数据字段
     * @param photoId
     * @return
     */
    public final static PhotoEn findSimple(Long photoId) {
        return PhotoCache.getInstance().findSimple(photoId);
    }

    /**
     * 查询某相册夹下的所有图片
     * @param pp
     * @param folderId
     * @return
     */
    public final static PageModel<PhotoEn> findByFolderId(PageParam pp, Long folderId) {
        PageModel<PhotoEn> pm = new PageModel<PhotoEn>();
        pm.setPageData(PhotoCache.getInstance().findByFolder(
                folderId, pp.getSortField(), pp.getAsc(), pp.getStart(), pp.getPageSize()));
        pm.setTotal(PhotoCache.getInstance().countByFolder(folderId));
        return pm;
    }
}
