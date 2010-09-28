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

import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import name.huliqing.qblog.entity.CounterEn;
import name.huliqing.qblog.enums.ArticleSecurity;

/**
 *
 * @author huliqing
 */
public class ArticleCounter extends CounterDa{
    private final static ArticleCounter ins = new ArticleCounter();
    public final static ArticleCounter getInstance() {
        return ins;
    }

    /**
     * 同步统计
     * @param ce
     */
    protected Integer synchronize(CounterType ct) {
        long startTime = System.currentTimeMillis();
        logger.info("数量同步统计开始：CounterType=" + ct.name());
        Store store = new Store();
        Long startId = 1L;
        Long lastId = null;
        if (ct == CounterType.article_draft) {
            while ((lastId = countArticle(ArticleSecurity.DRAFT, startId, store)) != null) {
                startId = lastId + 1;
            }
        } else if (ct == CounterType.article_public) {
            while ((lastId = countArticle(ArticleSecurity.PUBLIC, startId, store)) != null) {
                startId = lastId + 1;
            }
        } else if (ct == CounterType.article_private) {
            while ((lastId = countArticle(ArticleSecurity.PRIVATE, startId, store)) != null) {
                startId = lastId + 1;
            }
        }
        logger.info("数量同步统计完成：CounterType=" + ct.name() + ", 当前total=" + store.getValue() + ", 总用时：" + (System.currentTimeMillis() - startTime));
        return store.getValue();
    }

    /**
     * 统计文章数量
     * @param security 文章类型
     * @param startId 起始计数的articleId
     * @param store 存放单次查询的结果集
     * @return
     */
    public Long countArticle(ArticleSecurity security, Long startId, Store store) {
        long startTime = System.currentTimeMillis();
        String hql = "select obj.articleId from ArticleEn obj " +
                " where obj.articleId >=:startId " +
                " and obj.security =:security " +
                " order by obj.articleId asc ";
        EntityManager em = getEM();
        int size = 0;
        Long lastId = null;
        try {
            Query q = em.createQuery(hql);
            q.setParameter("startId", startId);
            q.setParameter("security", security);
            q.setFirstResult(0);
            q.setMaxResults(1000);
            List<Long> aids = q.getResultList();
            if (aids != null && !aids.isEmpty()) {
                store.add(aids.size());
                lastId = aids.get(aids.size() - 1);
                size = aids.size();
            }
        } finally {
            em.close();
        }
        logger.info("count=" + size + ", use time=" + (System.currentTimeMillis() - startTime));
        return lastId;
    }

    public void eventSave(ArticleSecurity security) {
        if (security == ArticleSecurity.DRAFT) {
            increase(CounterType.article_draft);
        } else if (security == ArticleSecurity.PRIVATE) {
            increase(CounterType.article_private);
        } else if (security == ArticleSecurity.PUBLIC) {
            increase(CounterType.article_public);
        }
    }
    
    public void eventDelete(ArticleSecurity security) {
        if (security == ArticleSecurity.DRAFT) {
            decrease(CounterType.article_draft);
        } else if (security == ArticleSecurity.PRIVATE) {
            decrease(CounterType.article_private);
        } else if (security == ArticleSecurity.PUBLIC) {
            decrease(CounterType.article_public);
        }
    }

    /**
     * 在文章Security类型改变之后需要增加或减少相应类型的数量
     * @param before 这个Security类型的Article减少了
     * @param after 这个Security类型的Article增加了
     */
    public void eventUpdate(ArticleSecurity before, ArticleSecurity after) {
        if (before == after) {
            return;
        }
        eventDelete(before);
        eventSave(after);
    }

    // ----

    public CounterEn findCounter(CounterType ct) {
        return super.find(ct);
    }

    public Integer getTotalDraft() {
        CounterEn ce = find(CounterType.article_draft);
        return ce.getTotal();
    }

    public Integer getTotalPrivate() {
        CounterEn ce = find(CounterType.article_private);
        return ce.getTotal();
    }

    public Integer getTotalPublic() {
        CounterEn ce = find(CounterType.article_public);
        return ce.getTotal();
    }

    // ----

    /**
     * 重置Draft计数器的总数
     * @param total
     */
    public void updateDraftCounter(int total) {
        CounterEn ce = find(CounterType.article_draft);
        ce.setTotal(total);
        ce.setLastDate(new Date());
        super.update(ce);
    }
    /**
     * 重置Public计数器的总数
     * @param total
     */
    public void updatePublicCounter(int total) {
        CounterEn ce = find(CounterType.article_public);
        ce.setTotal(total);
        ce.setLastDate(new Date());
        super.update(ce);
    }
    /**
     * 重置Private计数器的总数
     * @param total
     */
    public void updatePrivateCounter(int total) {
        CounterEn ce = find(CounterType.article_private);
        ce.setTotal(total);
        ce.setLastDate(new Date());
        super.update(ce);
    }
}
