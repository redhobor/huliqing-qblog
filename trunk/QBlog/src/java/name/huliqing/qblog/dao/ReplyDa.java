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
import javax.persistence.Query;
import name.huliqing.qblog.entity.ReplyEn;

public abstract class ReplyDa extends BaseDao<ReplyEn, Long> {

    public ReplyDa() {
        super(ReplyEn.class);
    }

    /**
     * 查询指定日期范围内发表的回复
     * @param start
     * @param end
     * @return
     */
    public List<ReplyEn> findByDateRange(Date start, Date end) {
        // 如果两个字段同时存在,Fix problem GAE :Only one inequality filter per query is supported
        if (start != null && end != null) {
            List<ReplyEn> all = findAll(null, null, null, null);
            List<ReplyEn> result = null;
            if (all != null && !all.isEmpty()) {
                result = new ArrayList<ReplyEn>();
                for (ReplyEn ae : all) {
                    if (!ae.getCreateDate().before(start) && !ae.getCreateDate().after(end)) {
                        result.add(ae);
                    }
                }
            }
            return result;
        }
        // 只有start 或 end存在的情况
        StringBuilder sb = new StringBuilder("select obj from " + t.getName() + " obj ");
        if (start != null)
            sb.append(" where obj.createDate >=:start ");
        if (end != null) {
            sb.append(" where obj.createDate <=:end ");
        }
        EntityManager em = getEM();
        try {
            Query q = em.createQuery(sb.toString());
            if (start != null)
                q.setParameter("start", start);
            if (end != null)
                q.setParameter("end", end);
            List<ReplyEn> result = q.getResultList();
            result.size(); // Fectch data before em close.
            return result;
        } finally {
            em.close();
        }
    }
}
