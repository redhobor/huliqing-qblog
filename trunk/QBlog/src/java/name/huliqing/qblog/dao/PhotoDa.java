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

package name.huliqing.qblog.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import name.huliqing.qblog.entity.PhotoEn;

/**
 *
 * @author huliqing
 */
public abstract class PhotoDa extends BaseDao<PhotoEn, Long>{
    public PhotoDa() {
        super(PhotoEn.class);
    }

    /**
     * 直接删除
     * @param photoId
     * @return
     */
    @Override
    protected boolean delete(Long photoId) {
        String q = "delete from PhotoEn obj where obj.photoId=:photoId";
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try {
            Query query = em.createQuery(q);
            query.setParameter("photoId", photoId);
            tx.begin();
            int result = query.executeUpdate();
            tx.commit();
            return (result >= 1);
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * 删除某一个相册下的所有图片
     * @param folderId 相册ID
     * @return
     */
    protected boolean deleteByFolderId(Long folderId) {
        if (folderId == null)
            throw new NullPointerException("folderId could not be null!");

        String q = "delete from PhotoEn obj where obj.folder=:folderId";
        EntityManager em = getEM();
        try {
            Query query = em.createQuery(q);
            query.setParameter("folderId", folderId);
            int result = query.executeUpdate();
            return (result >= 0); // 删除了0条记录也算操作成功
        } finally {
            em.close();
        }
    }

    /**
     * 注：该方法不会搜索出所有字段，bytes字段不会取出,bytesMin也不会被取出
     * @param search
     * @param sortField
     * @param asc
     * @param start
     * @param size
     * @return
     */
    @Override
    protected List<PhotoEn> findByObject(PhotoEn search,
            String sortField, Boolean asc, Integer start, Integer size) {
        StringBuilder sb = new StringBuilder("select obj.photoId " +
                ",obj.name" +
                ",obj.contentType" +
                ",obj.suffix" +
                ",obj.fileSize" +
                ",obj.pack" +
                ",obj.createDate" +
                ",obj.folder" +
                ",obj.des" +
                " from PhotoEn obj ");
        EntityManager em = getEM();
        try {
            Query qd = QueryMake2.makeQuery(em, search, sb,
                    sortField, asc, start, size);
            List<Object[]> result = (List<Object[]>) qd.getResultList();
            List<PhotoEn> pes = new ArrayList<PhotoEn>(result.size());
            for (Object[] r : result) {
                PhotoEn pe = new PhotoEn();
                pe.setPhotoId((Long) r[0]);
                pe.setName((String) r[1]);
                pe.setContentType((String) r[2]);
                pe.setSuffix((String) r[3]);
                pe.setFileSize((Long) r[4]);
                pe.setPack((Boolean) r[5]);
                pe.setCreateDate((Date) r[6]);
                pe.setFolder((Long) r[7]);
                pe.setDes((String) r[8]);
                pes.add(pe);
            }
            return pes;
        } finally {
            em.close();
        }
    }


}
