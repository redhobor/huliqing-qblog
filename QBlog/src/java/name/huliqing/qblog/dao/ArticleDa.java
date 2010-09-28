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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.enums.ArticleSecurity;

public abstract class ArticleDa extends BaseDao<ArticleEn, Long> {

    public ArticleDa() {
        super(ArticleEn.class);
    }

    /**
     * 查询优化版，注：该方法只查询出:articleId, title, summary,createDate,totalView,totalReply,createDay字段,
     * 在不需要其它字段信息的情况下，应该尽量使用Simple方法，这会优化查询速度，并且减少
     * JSF页面状态保存的信息量，特别是Article的Content字段，该字段为Text类型，当存在
     * 大量长篇幅文章时，可能使页面状态信息急剧增大（部分页面需要保存这些状态），这会导
     * 致页面装载时间加长，及CPU的大量消耗。甚至可能出现错误，当使用server端保存状态
     * 信息时，甚至可能触发AppEngine的某些限制。
     * @param sortField 可为null
     * @param asc 可为null
     * @param start 可为null
     * @param size 可为null
     * @return
     */
    protected List<ArticleEn> findByObjectSimple(ArticleEn searchObj,
            String sortField, Boolean asc, Integer start, Integer size) {
        
        StringBuilder sb = new StringBuilder("select obj.articleId " +
                ",obj.title" +
                ",obj.summary" +
                ",obj.createDate" +
                ",obj.totalView" +
                ",obj.totalReply" +
                ",obj.createDay" +
                ",obj.security" +
                ",obj.replyable" +
                ",obj.mailNotice" +
                ",obj.modifyDate" +
                ",obj.tags" +
                " from ArticleEn obj ");

        EntityManager em = getEM();
        try {
            Query q = QueryMake2.makeQuery(em, searchObj, sb, sortField, asc, start, size);
            List<Object[]> result = (List<Object[]>) q.getResultList();
            List<ArticleEn> aes = new ArrayList<ArticleEn>(result.size());
            for (Object[] r : result) {
                ArticleEn ae = new ArticleEn();
                ae.setArticleId((Long) r[0]);
                ae.setTitle((String) r[1]);
                ae.setSummary((String) r[2]);
                ae.setCreateDate((Date) r[3]);
                ae.setTotalView((Long) r[4]);
                ae.setTotalReply((Long) r[5]);
                ae.setCreateDay((Integer) r[6]);
                ae.setSecurity((ArticleSecurity) r[7]);
                ae.setReplyable((Boolean)r[8]);
                ae.setMailNotice((Boolean)r[9]);
                ae.setModifyDate((Date)r[10]);
                ae.setTags((String)r[11]);
                aes.add(ae);
            }
            return aes;
        } finally {
            em.close();
        }
    }

    /**
     * 查询日期范围内发表的文章
     * @param start
     *      表示在这个日期之后（包括这个日期）发表的文章，可为null
     * @param end
     *      表示在这个日期之前（包括这个日期）发表的文章，可为null,
     * @return
     *      [start-end]日期间发表的文章，如果start,end都为null,则查询所有文章
     */
    protected List<ArticleEn> findByDateRange(Date start, Date end) {
        // 如果两个字段同时存在,Fix problem GAE :Only one inequality filter per query is supported
        if (start != null && end != null) {
            List<ArticleEn> all = findAll(null, null, null, null);
            List<ArticleEn> result = null;
            if (all != null && !all.isEmpty()) {
                result = new ArrayList<ArticleEn>();
                for (ArticleEn ae : all) {
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
        if (end != null)
            sb.append(" where obj.createDate <=:end "); 
        EntityManager em = getEM();
        try {
            Query q = em.createQuery(sb.toString());
            if (start != null)
                q.setParameter("start", start);
            if (end != null)
                q.setParameter("end", end);
            List<ArticleEn> result = q.getResultList();
            result.size(); // Fectch data before em close.
            return result;
        } finally {
            em.close();
        }
    }

    /**
     * 查询指定ID的文章，结果以createDate倒序
     * @param articleId
     * @return
     */
    protected List<ArticleEn> findArticlesById(Long... articleId) {
        if (articleId == null || articleId.length <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder("select " +
                " obj.articleId " +
                ",obj.title" +
                ",obj.summary" +
                ",obj.createDate" +
                ",obj.totalView" +
                ",obj.totalReply" +
                ",obj.createDay" +
                ",obj.security" +
                ",obj.replyable" +
                ",obj.mailNotice" +
                ",obj.modifyDate" +
                ",obj.tags" +
                " from ArticleEn obj ");

        int count = 0;
        for (Long aid : articleId) {
            if (count == 0) {
                sb.append(" where obj.articleId=").append(aid);
            } else {
                sb.append(" or obj.articleId=").append(aid);
            }
            count++;
        }

        // 不要加order by 否则所有的优化就没有意义。
//        sb.append(" order by obj.createDate desc ");

        EntityManager em = getEM();
        try {
            String qy = sb.toString();
//            logger.info("query=" + qy);
            Query query = em.createQuery(qy);
            List<Object[]> result = (List<Object[]>) query.getResultList();
            List<ArticleEn> aes = new ArrayList<ArticleEn>(result.size());
            for (Object[] r : result) {
                ArticleEn ae = new ArticleEn();
                ae.setArticleId((Long) r[0]);
                ae.setTitle((String) r[1]);
                ae.setSummary((String) r[2]);
                ae.setCreateDate((Date) r[3]);
                ae.setTotalView((Long) r[4]);
                ae.setTotalReply((Long) r[5]);
                ae.setCreateDay((Integer) r[6]);
                ae.setSecurity((ArticleSecurity) r[7]);
                ae.setReplyable((Boolean)r[8]);
                ae.setMailNotice((Boolean)r[9]);
                ae.setModifyDate((Date)r[10]);
                ae.setTags((String)r[11]);
                aes.add(ae);
            }
            // 按articleId排序
            if (!aes.isEmpty()) {
                Collections.sort(aes, new ArticleCreateDateDesc()); 
            }
            return aes;
        } finally {
            em.close();
        }
    }

    private class ArticleCreateDateDesc implements Comparator<ArticleEn> {
        public int compare(ArticleEn o1, ArticleEn o2) {
            if (o1 == null || o2 == null
                    || o1.getCreateDate() == null
                    || o2.getCreateDate() == null) {
                return 0;
            }
            return o1.getCreateDate().before(o2.getCreateDate()) ? 1 : 0;
        }
    }
    
// // remove
//    private class ArticleIdDesc implements Comparator<ArticleEn> {
//        public int compare(ArticleEn o1, ArticleEn o2) {
//            return o1.getArticleId() > o2.getArticleId() ? 0 : 1;
//        }
//    }
}
