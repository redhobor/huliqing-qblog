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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.daocache.ArticleCache;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.enums.ArticleSecurity;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qfaces.QFaces;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;

public class ArticleSe {
    protected final static Logger logger = Logger.getLogger(ArticleSe.class.getName());

    /**
     * 创建文章，要求登录状态,否则反回false
     * @param article
     * @return
     */
    public final static Boolean save(ArticleEn article) {
        if (!QBlog.getCurrentVisitor().isLogin()) {
            logger.warning("试图在未登录状态下创建文章,remote=" + QBlog.getCurrentVisitor().getRemoteAddr());
            return Boolean.FALSE;
        }
        // 文章默认发表日期
        Calendar cal = Calendar.getInstance();
        article.setCreateDate(cal.getTime());

        // 关于createYear,createMonth,createDay使用的是当前系统时区内的时间，所以
        // 可能与createDate的各项值不一定相同。
        cal.setTimeZone(TimeZone.getTimeZone(
                ConfigManager.getInstance().findConfig(Config.CON_SYSTEM_TIME_ZONE).getValue()));
        article.setCreateYear(cal.get(Calendar.YEAR));
        article.setCreateMonth(cal.get(Calendar.MONTH));
        article.setCreateDay(cal.get(Calendar.DAY_OF_MONTH));

        // 其它默认值
        article.setTotalReply(0L);
        article.setTotalView(0L);
        article.setAuthor(QBlog.getCurrentVisitor().getAccount().getAccount());
        ArticleCache.getInstance().save(article);

        // 更新文章的标签信息
        TagArticleSe.updateArticleTags(article.getArticleId());
        return true;
    }

    /**
     * 该方法用于测试,不要用于正式应用。
     * @param article
     * @return
     */
    public final static Boolean testSave(ArticleEn article) {
        // 文章默认发表日期
        Calendar cal = Calendar.getInstance();
        article.setCreateDate(cal.getTime());

        // 关于createYear,createMonth,createDay使用的是当前系统时区内的时间，所以
        // 可能与createDate的各项值不一定相同。
        cal.setTimeZone(TimeZone.getTimeZone(
                ConfigManager.getInstance().findConfig(Config.CON_SYSTEM_TIME_ZONE).getValue()));
        article.setCreateYear(cal.get(Calendar.YEAR));
        article.setCreateMonth(cal.get(Calendar.MONTH));
        article.setCreateDay(cal.get(Calendar.DAY_OF_MONTH));

        // 其它默认值
        article.setTotalReply(0L);
        article.setTotalView(0L);
        ArticleCache.getInstance().save(article);

        // 更新文章的标签信息
        TagArticleSe.updateArticleTags(article.getArticleId());
        return true;
    }
    
    /**
     * 更新文章信息，要求登录状态，未登录则直接反回false.
     * @param article
     * @return
     */
    public final static Boolean update(ArticleEn article) {
        if (!QBlog.getCurrentVisitor().isLogin()) {
            logger.warning("试图在未登录状态下更新文章,remote=" + QBlog.getCurrentVisitor().getRemoteAddr());
            return Boolean.FALSE;
        }
        article.setModifyDate(new Date());
        ArticleCache.getInstance().update(article);
        // 更新文章的标签信息
        TagArticleSe.updateArticleTags(article.getArticleId());
        return Boolean.TRUE;
    }

    /**
     * 删除文章，要求登录状态,未登录则直接反回false
     * @param articleId
     * @return
     */
    public final static Boolean delete(Long articleId) {
        if (!QBlog.getCurrentVisitor().isLogin()) {
            logger.warning("试图未登录状态下删除文章,remote=" + QBlog.getCurrentVisitor().getRemoteAddr());
            return Boolean.FALSE;
        }
        ArticleCache.getInstance().delete(articleId);
        // 更新文章的标签信息
        TagArticleSe.updateArticleTags(articleId);
        return Boolean.TRUE;
    }

    /**
     * 导入文章，要求登录状态。如果未登录则直接返回false.
     * 注：不要使用save来导入文章，save会改变文章字段信息.
     * @param article
     * @return
     */
    public final static Boolean _import(ArticleEn article) {
        if (!QBlog.getCurrentVisitor().isLogin()) {
            logger.warning("试图在未登录状态下导入文章,remote=" + QBlog.getCurrentVisitor().getRemoteAddr());
            return Boolean.FALSE;
        }
        ArticleCache.getInstance().save(article);
        // 更新文章的标签信息
        TagArticleSe.updateArticleTags(article.getArticleId());
        return Boolean.TRUE;
    }

    /**
     * 通过文章ID查找文章，如果为登录状态，则查询所有Security类型的文章，否则只查询
     * ArticleSecurity.PUBLIC的文章。
     * @param articleId
     * @return
     */
    public final static ArticleEn find(Long articleId) {
        ArticleEn ae = ArticleCache.getInstance().find(articleId);
        if (ae != null && ae.getSecurity() != ArticleSecurity.PUBLIC) {
            if (!QBlog.getCurrentVisitor().isLogin()) {
                return null;
            }
        }
        return ae;
    }

    /**
     * 翻页版查询所有Draft文章，注：该方法不会查询content字段
     * @param pp 分页参数
     * @return
     */
    public final static PageModel<ArticleEn> findAllDraft(PageParam pp) {
        PageModel<ArticleEn> pm = new PageModel<ArticleEn>();
        List<ArticleEn> data = ArticleCache.getInstance().findAllDraft(
                pp.getStart(), pp.getPageSize());
        Integer total = ArticleCache.getInstance().countAllDraft();
        pm.setPageData(data);
        pm.setTotal(total);
        return pm;
    }

    public final static PageModel<ArticleEn> findAllPrivate(PageParam pp) {
        PageModel<ArticleEn> pm = new PageModel<ArticleEn>();
        List<ArticleEn> data = ArticleCache.getInstance().findAllPrivate(
                pp.getStart(), pp.getPageSize());
        Integer total = ArticleCache.getInstance().countAllPrivate();
        pm.setPageData(data);
        pm.setTotal(total);
        return pm;
    }

    public final static PageModel<ArticleEn> findAllPublic(PageParam pp) {
        PageModel<ArticleEn> pm = new PageModel<ArticleEn>();
        List<ArticleEn> data = ArticleCache.getInstance().findAllPublic(
                pp.getStart(), pp.getPageSize());
        Integer total = ArticleCache.getInstance().countAllPublic();
        pm.setPageData(data);
        pm.setTotal(total);
        return pm;
    }

    /**
     * 查询所有<strong>公开</strong>发表的文章，注：该方法只查询部分字段,结果以
     * articleId的倒序排列
     * @param size 返回的最高数量
     * @return
     */
    public final static List<ArticleEn> findAllPublic(Integer size) {
        return ArticleCache.getInstance().findAllPublic(0, size);
    }

    /**
     * 分页查询某日所发表的所有文章,只查询公开发表的
     * @param year
     * @param month
     * @param day
     * @param pp
     * @return
     */
    public final static PageModel<ArticleEn> findByDay(Integer year, Integer month, Integer day, PageParam pp) {
        PageModel<ArticleEn> pm = new PageModel<ArticleEn>();
        pm.setPageData(ArticleCache.getInstance().findByDay(year, month, day, pp.getStart(), pp.getPageSize()));
        pm.setTotal(ArticleCache.getInstance().countByDay(year, month, day));
        return pm;
    }

    /**
     * 查询某个月份内发表的文章，只查询ArticleSecurity.PUBLIC的文章，不管
     * 登录与否。注：该方法只查询部分字段
     * @param year
     * @param month
     * @param size 取多少条数据,可为null, 如果为null则取出当前所有发表的文章，
     *      如果如果数据太多，则应该有所限制。
     * @return
     */
    public final static List<ArticleEn> findByMonth(Integer year, Integer month, Integer size) {
        return ArticleCache.getInstance().findByMonth(year, month, size);
    }

    /**
     * 导出数据,要求登录状态，否则反回null,导出的文章的创建日期必须介于start及end Date
     * 之间。start及end可为null,如果为null,则不作任何限制，如：两个都为null,则导出
     * 所有文章。
     * @param start 开始日期
     * @param end 结束日期
     * @return
     */
    public final static List<ArticleEn> exportByDateRange(Date start, Date end) {
        if (!QBlog.getCurrentVisitor().isLogin()) {
            logger.warning("试图在未登录状态下导出文章,remote=" + QBlog.getCurrentVisitor().getRemoteAddr());
            return null;
        }
        return ArticleCache.getInstance().findByDateRange(start, end);
    }

    /**
     * 增加文章的阅读数
     * @param articleId
     * @param num 数量
     */
    public final static void increaseTotalView(Long articleId, int num) {
        assert num >= 0;
        ArticleEn ae = ArticleCache.getInstance().find(articleId);
        if (ae != null) {
            long totalView = ae.getTotalView() != null ? ae.getTotalView() : 0;
            ae.setTotalView(totalView + num);
            ArticleCache.getInstance().update(ae);
        }
    }
 
    /**
     * 增加文章的评论数
     * @param articleId
     * @param num 数量,可为负
     */
    public final static void increaseTotalReply(Long articleId, int num) {
        ArticleEn ae = ArticleCache.getInstance().find(articleId);
        if (ae != null) {
            long totalReply = ae.getTotalReply() != null ? ae.getTotalReply() : 0;
            totalReply += num;
            if (totalReply < 0) {
                totalReply = 0;
            }
            ae.setTotalReply(totalReply);
            ArticleCache.getInstance().update(ae);
        }
    }

    /**
     * 查询文章是否为可回复的。
     * @param article
     * @return
     */
    public final static Boolean isReplyable(ArticleEn article) {
        if (article == null)
            return Boolean.FALSE;
        Boolean rb = QFaces.convertToBoolean(ConfigManager.getInstance()
                .findConfig(Config.CON_REPLY_ENABLE).getValue());
        if (rb == null) {
            rb = QFaces.convertToBoolean(Config.CON_REPLY_ENABLE.getValue());
        }
        return (rb && article.getReplyable());
    }

}
