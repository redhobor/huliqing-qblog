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

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import name.huliqing.qblog.entity.FolderEn;
import name.huliqing.qblog.enums.Security;

/**
 *
 * @author huliqing
 */
public abstract class FolderDa extends BaseDao<FolderEn, Long>{
    public FolderDa() {
        super(FolderEn.class);
    }

    /**
     * 查询所有除了隐私之外的相册
     * @param sortField
     * @param asc
     * @param start
     * @param size
     * @return
     */
    protected List<FolderEn> findAllExceptPrivate(String sortField, Boolean asc, Integer start, Integer size) {
        EntityManager em = getEM();
        try {
            StringBuilder sb = new StringBuilder("select obj from FolderEn obj " +
                    " where obj.security=:public or obj.security=:protected ");
            if (sortField != null) {
                sb.append(" order by obj.").append(sortField);
                if (asc != null) {
                    sb.append(asc ? " asc " : " desc ");
                } else {
                    sb.append(" asc ");
                }
            }
            Query query = em.createQuery(sb.toString());
            query.setParameter("public", Security.PUBLIC.name());
            query.setParameter("protected", Security.PROTECTED.name());
            if (start != null) {
                query.setFirstResult(start);
            }
            if (size != null) {
                query.setMaxResults(size);
            }
            List<FolderEn> data = convertAsArrayList(query.getResultList());
            return data;
        } finally {
            em.close();
        }
    }

    protected Integer countAllExceptPrivate() {
        EntityManager em = getEM();
        try {
            StringBuilder sb = new StringBuilder("select count(obj) from FolderEn obj " +
                    " where obj.security=:public or obj.security=:protected ");
            Query query = em.createQuery(sb.toString());
            query.setParameter("public", Security.PUBLIC.name());
            query.setParameter("protected", Security.PROTECTED.name());
            Integer total = (Integer) query.getSingleResult();
            return total;
        } finally {
            em.close();
        }
    }
}
