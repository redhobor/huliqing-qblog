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

import java.util.List;
import name.huliqing.qblog.dao.PhotoDa;
import name.huliqing.qblog.entity.PhotoEn;

/**
 *
 * @author huliqing
 */
public class PhotoCache extends PhotoDa{
    private final static PhotoCache ins = new PhotoCache();
    private PhotoCache(){}
    public final static PhotoCache getInstance() {
        return ins;
    }

    @Override
    public boolean save(PhotoEn t) {
        return super.save(t);
    }

    @Override
    public boolean update(PhotoEn t) {
        return super.update(t);
    }

    @Override
    public boolean delete(Long photoId) {
        return super.delete(photoId);
    }

    @Override
    public boolean deleteByFolderId(Long folderId) {
        return super.deleteByFolderId(folderId);
    }

    @Override
    public PhotoEn find(Long id) {
        return super.find(id);
    }

    /**
     * 通过photoId查找photo,该方法不会取出photo的bytes字段
     * @param photoId
     * @return
     */
    public PhotoEn findSimple(Long photoId) {
        PhotoEn search = new PhotoEn();
        search.setPhotoId(photoId);
        List<PhotoEn> result = findByObject(search, null, null, 0, 1);
        if (result == null || result.isEmpty()) {
            return null;
        } else {
            return result.get(0);
        }
    }

    /**
     * 分页查询相册文件夹下的所有图片
     * @param folderId 文件夹id, 如果folderId为null，则返回null
     * @param sortField 排序字段
     * @param asc 是否正序
     * @param start 起始记录
     * @param size 获取的最高数量
     * @return
     */
    public List<PhotoEn> findByFolder(Long folderId,
            String sortField, Boolean asc, Integer start, Integer size) {
        if (folderId == null)
            throw new NullPointerException("folderId couldn't be null.");
        PhotoEn search = new PhotoEn();
        search.setFolder(folderId);
        return findByObject(search, sortField, asc, start, size);
    }

    /**
     * 查询某文件夹下所有图片的数量
     * @param folderId
     * @return
     */
    public Integer countByFolder(Long folderId) {
        if (folderId == null)
            throw new NullPointerException("folderId couldn't be null.");
        PhotoEn search = new PhotoEn();
        search.setFolder(folderId);
        return countByObject(search);
    }
}
