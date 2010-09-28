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
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import name.huliqing.qblog.dao.ArticleDa;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.enums.ArticleSecurity;

/**
 * 对所有文章的索引缓存
 * @author huliqing
 */
public abstract class ArticleIndexHelper extends ArticleDa implements IndexHelper {
    public ArticleIndexHelper(ArticleSecurity security) {
        this.security = security;
    }

    // 文章索引,这个列表是按Article的CreateDate 倒序的索引
    //（不要用articleId,articleId并不一定会按自增长排序）
    protected final List<Long> indexes = new ArrayList<Long>(200);

    protected ArticleSecurity security;

    // 最后一个被载入的index的article的createDate,下次获取将从这个createDate所标记
    // 的位置开始载入（注：只有isFullOK == false的时候才需要断点续load）
    protected Date lastCreateDate;

    // 表示当前整个个index是否完整载入
    protected Boolean isOK = Boolean.FALSE;

    /**
     * 需要重新去载入所有index
     */
    public void needToReload() {
        isOK = Boolean.FALSE;
        lastCreateDate = null;
        indexes.clear();
    }

    /**
     * 载入article index
     * @param sizeAtLeast 如果数据足够，那么至少载入的总数应该达到这个数量
     */
    private synchronized void reload(int sizeAtLeast) {
        if (isOK) {
            return;
        }

        List<Long> tempStore = new ArrayList<Long>(200);
        Date tempLastCreateDate = new Date();
        // 如果上次载入过程出错或未能完整完成，则继续上一次的载入.
        if (!isOK) {
            if (lastCreateDate == null) {
                lastCreateDate = new Date();
            }
            tempLastCreateDate = lastCreateDate;
        }

        while ((tempLastCreateDate = findByCreateDateDesc(tempLastCreateDate, tempStore)) != null) {
            indexes.addAll(tempStore);
            lastCreateDate = tempLastCreateDate;
            tempStore.clear();
            if (indexes.size() >= sizeAtLeast) {
                // 按需要装载，不要用break.因为还未完整装载完成
                logger.info("已载入indexes (" + this.security.name() + "), 当前size=" + indexes.size());
                return;
            }
        }

        // 标记为载入完整成功。
        isOK = Boolean.TRUE;
        logger.info("载入所有 Index OK(" + this.security.name() + "), size=" + indexes.size());
    }

    private Date findByCreateDateDesc(Date beforeDate, List<Long> store) {
        long startTime = System.currentTimeMillis();
        String ql = "select obj.articleId, obj.createDate " +
                " from ArticleEn obj" +
                " where obj.createDate < :createDate " +
                " and obj.security = :security " +
                " order by obj.createDate desc ";
        EntityManager em = getEM();
        Date lcd = null; // lastCreateDate
        try {
            Query q = em.createQuery(ql);
            q.setParameter("createDate", beforeDate);
            q.setParameter("security", this.security);
            q.setFirstResult(0);
            q.setMaxResults(1000);
            List<Object[]> result = q.getResultList();
            for (Object[] o : result) {
                final Long articleId = (Long) o[0];
                store.add(articleId);
                lcd = (Date) o[1]; 
            }
        } finally {
            em.close();
        }
        logger.info("载入" + this.security.name() + " indexes数据，size=" + store.size() + ", use time=" + (System.currentTimeMillis() - startTime));
        return lcd;
    }

    /**
     * @param start
     * @param size
     * @return 实际读取的index数组,长度不一定与size相符
     */
    public Long[] findByDesc(int start, int size) {
        if (start < 0 || size < 0)
            throw new RuntimeException("start/size 不能小于0, start=" + start + ", size=" + size);

        // 载入必要的index，如果数量不足
        if (start + size > indexes.size()) {
            reload(start + size);
        }

        // 实际能够获取的数量size
        if (start + size > indexes.size()) {
            size = indexes.size() - start;
        }

        Long[] result = new Long[size];
        int count = 0;
        while (count < size) {
            result[count++] = (indexes.get(start++));
        }
        return result;
    }

    /**
     * 当添加了一篇文章时
     * @param article
     */
    synchronized void eventSave(ArticleEn article) {
        if (article != null 
                && article.getArticleId() != null 
                && article.getSecurity() == this.security) {
                indexes.add(0, article.getArticleId());
        }
    }

    /**
     *
     * @param article 被更新的article
     * @param before 状态改变之前的security
     * @param after 状态改变之后的security
     */
    synchronized void eventUpdate(ArticleEn article, ArticleSecurity before, ArticleSecurity after) {
        // Security状态未改变，则不需要处理
        if (before == after) {
            return;
        }
        // 存在Security状态改变
        if (indexes.contains(article.getArticleId())) {
            indexes.remove(article.getArticleId());
        } else {
            // 状态改变后与当前index所存在的security状态相同,那么可能需要将这个articleId
            // 插入到当前的indexes中
            // 1.找到这篇文章的前一篇文章beforeArticle（必须是相同security的）
            // 2.如果找到这篇文章beforeArticle，则查看文章是否存在于当前indexes中。
            // 3.如果beforeArticle存在indexes中，那么应该将文章插入beforeArticle之前
            // 的位置
            if (this.security == after) {
                ArticleEn beforeArticle = findBefore(article.getCreateDate(), this.security);
                if (beforeArticle != null && indexes.contains(beforeArticle.getArticleId())) {
                    int i = 0;
                    boolean found = false;
                    for (Long aid : indexes) {
                        if (aid.longValue() == beforeArticle.getArticleId().longValue()) {
                            found = true;
                            break;
                        }
                        i++;
                    }
                    if (found) {
                        // 插入index
                        indexes.add(i, article.getArticleId());
                    }
                }
            }
        }
    }

    /**
     * 当删除了一篇文章时
     * @param articleId
     */
    synchronized void eventDelete(Long articleId) {
        indexes.remove(articleId);
    }

    /**
     * 找出指定时间之前发表的一篇文章
     * @param createDate 在这个时间之前发表的第一篇文章
     * @param security 文章类型
     * @return
     */
    private ArticleEn findBefore(Date createDate, ArticleSecurity security) {
        String ql = "select from ArticleEn obj " +
                        " where obj.createDate < :createDate and obj.security = :security order by obj.createDate desc ";
        EntityManager em = getEM();
        ArticleEn result = null;
        try {
            Query q = em.createQuery(ql);
            q.setParameter("createDate", createDate);
            q.setParameter("security", security);
            q.setMaxResults(1);
            List<ArticleEn> aes = q.getResultList();
            if (aes != null && !aes.isEmpty()) {
                result = aes.get(0);
            }
        } finally {
            em.close();
        }
        return result;
    }
}
