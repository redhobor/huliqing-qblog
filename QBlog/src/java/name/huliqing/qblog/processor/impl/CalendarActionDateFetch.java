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

package name.huliqing.qblog.processor.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.dao.DaoListenerBase;
import name.huliqing.qblog.daocache.ArticleCache;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.entity.ConfigEn;
import name.huliqing.qblog.enums.ArticleSecurity;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qblog.service.ArticleSe;
import name.huliqing.qfaces.model.ActionDate;

/**
 * 后续需要优化
 * @author huliqing
 */
@ManagedBean
@ApplicationScoped
public class CalendarActionDateFetch implements java.io.Serializable{
    private final static Logger logger = Logger.getLogger(CalendarActionDateFetch.class.getName());

    /**
     * 缓存最近几个月的ActionDate,
     * key 的格式为： year-month,
     * 从actionDateMap.get(year:month) 可获得某月份的actionDates
     */
    private Map<String, List<ActionDate>> actionDateMap;

    public List<ActionDate> fetch(Integer year, Integer month) {
        if (year == null || month == null) {
            logger.warning("Null pointer when fetch calendar action date, " +
                    "year=" + year + ", month=" + month);
            return null;
        }
        // 载入map
        if (actionDateMap == null) {
            initActionDateMap();
        }
        // 如果缓存中存在相关数据,则直接返回
        String key = year.toString() + "-" + month.toString();
        if (actionDateMap.containsKey(key)) {
            logger.info("Find actionDates from cache, year=" + year + ", month = " + month);
            return actionDateMap.get(key);
        }
        // 试图访问未来发表的文章是不可能的。
        if (!checkValidMonth(year, month)) {
            return null;
        }
        // 载入数据，从dao中
        logger.info("Action Dates from cache not found, load start... year=" + year + ", month=" + month);
        long start = System.currentTimeMillis();
        List<ActionDate> adsInMonth = load(year, month);
        logger.info("Action Dates load OK, use time=" + (System.currentTimeMillis() - start));

        // 检查是否应该缓存起来
        if (adsInMonth != null && checkIfNeedToCache(year, month)) {
            logger.info("Cache Action Dates, year=" + year + ", month=" + month);
            actionDateMap.put(key, adsInMonth);
        }
        return adsInMonth;
    }

    private boolean checkIfNeedToCache(Integer year, Integer month) {
        Calendar pos = getNowWithSystemTimeZone();
        Calendar cal = getNowWithSystemTimeZone();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        pos.set(Calendar.MONTH, pos.get(Calendar.MONTH) - 3); // 3个月的缓存
        return cal.after(pos);
    }

    // 检查日期正确性
    private boolean checkValidMonth(Integer year, Integer month) {
        Calendar cal = getNowWithSystemTimeZone();
        if (year > cal.get(Calendar.YEAR)) {
            // 查看下一年发表的文章是不可能的
            return false;
        }
        if (year == cal.get(Calendar.YEAR) && month > cal.get(Calendar.MONTH)) {
            // 查看下个月发表的文章是不可能的
            return false;
        }
        return true;
    }

    // 获取今天的日期，
    private Calendar getNowWithSystemTimeZone() {
        ConfigEn ce = ConfigManager.getInstance().findConfig(Config.CON_SYSTEM_TIME_ZONE);
        TimeZone tz = TimeZone.getTimeZone(ce != null ? ce.getValue() : "GMT+8");
        Calendar cal = Calendar.getInstance(tz);
        return cal;
    }

    // 从Dao中载入某个月分的数据，只有从actionDateMap中找不到时才从这里载入
    private List<ActionDate> load(Integer year, Integer month) {
        List<ActionDate> ads = null;
        List<ArticleEn> articlesInMonth = ArticleSe.findByMonth(year, month, 1000); // 只取最近的最多1000条数据
        if (articlesInMonth != null && !articlesInMonth.isEmpty()) {
            Map<Integer, CustomActionDate> cadsMap = new HashMap<Integer, CustomActionDate>();
            for (ArticleEn ae : articlesInMonth) {
                Integer day = ae.getCreateDay(); //
                CustomActionDate cad = cadsMap.get(day);
                if (cad == null) {
                    cad = new CustomActionDate(day);
                    cadsMap.put(day, cad);
                }
                // 每天只以前10条记录
                if (cad.size() < 10) {
                    cad.addArticle(ae, false);
                }
            }
            ads = new ArrayList<ActionDate>(cadsMap.size());
            for (ActionDate ad : cadsMap.values()) {
                ads.add(ad);
            }
        }
        // 如果找不到任何数据，则创建一个空的，这使得它可被缓存起来,
        // 这样不需要每次都去load数据
        if (ads == null) {
            ads = new ArrayList<ActionDate>(1);
        }
        return ads;
    }

    private synchronized void initActionDateMap() {
        actionDateMap = new HashMap<String, List<ActionDate>>();
        // 添加一个侦听器，用于监听文章的添加，修改，删除操作
        if (ArticleCache.getInstance().getListener(ArticleDaoListener.class.getName()) == null) {
            ArticleCache.getInstance().addListener(ArticleDaoListener.class.getName(), new ArticleDaoListener());
        }
    }

    public class CustomActionDate extends ActionDate {

        // 当天发表的所有文章列表,key -> articleId
        private List<ArticleEn> articles;

        public CustomActionDate(Integer day) {
            super(day, null);
        }

        /**
         * 添加articles到列表中
         * @param ae
         * @param top 是否插入到前面
         */
        public synchronized void addArticle(ArticleEn ae, boolean top) {
            if (articles == null) {
                articles = new ArrayList<ArticleEn>();
            }
            // 不要其它字段信息，这可以提高性能,并减少页面状态保存时的信息量
            ArticleEn _ae = new ArticleEn();
            _ae.setArticleId(ae.getArticleId());
            _ae.setTitle(ae.getTitle());
            if (top) {
                articles.add(0, _ae);
            } else {
                articles.add(_ae);
            }
            while(articles.size() > 10) {
                articles.remove(articles.size() - 1);
            }
        }

        // 从列表中更新article,主要是更新article title
        public synchronized void updateArticle(ArticleEn ae) {
            if (articles == null) {
                articles = new ArrayList<ArticleEn>();
            }
            // 找出与给定的article配匹的article,并更新标题
            ArticleEn found = null;
            for (ArticleEn a : articles) {
                if (a.getArticleId().longValue() == ae.getArticleId().longValue()) {
                    found = a;
                    break;
                }
            }
            // 更新标题
            if (found != null) {
                found.setTitle(ae.getTitle());
            } else {
                found = new ArticleEn();
                found.setArticleId(ae.getArticleId());
                found.setTitle(ae.getTitle());
                articles.add(found);
            }
        }

        // 从列表中删除article
        public synchronized void removeArticle(Long articleId) {
            if (articles == null)
                return;
            ArticleEn found = null;
            for (ArticleEn a : articles) {
                if (a.getArticleId().longValue() == articleId.longValue()) {
                    found = a;
                    break;
                }
            }
            if (found != null) {
                articles.remove(found);
            }
        }

        public boolean isEmpty() {
            return (articles == null || articles.isEmpty());
        }

        public int size() {
            if (articles == null || articles.isEmpty())
                return 0;
            return articles.size();
        }

        @Override
        public String getDescription() {
            if (articles != null && !articles.isEmpty()) {
                StringBuilder sb = new StringBuilder("<ul>");
                for (ArticleEn ae : articles) {
                    sb.append(buildRecord(ae));
                }
                sb.append("</ul>");
                return sb.toString();
            } else {
                return "";
            }
        }
        private String buildRecord(ArticleEn ae) {
            return "<li style=\"margin:3px 0 0 -12px;line-height:1.5em;\"><a href=\"/article/articleId="+ ae.getArticleId() + "\" >" + ae.getTitle() + "</a></li>";
        }
    }

    private class ArticleDaoListener extends DaoListenerBase<ArticleEn, Long> {

        @Override
        public void afterSave(ArticleEn t) {
            if (!checkValid(t))
                return;
            // 不处理非public的
            if (t.getSecurity() != ArticleSecurity.PUBLIC) {
                return;
            }
            // 如果缓存中不存在当前月份的ActionDate则ignre，不作任何处理
            List<ActionDate> ads = findFromCache(t.getCreateYear(), t.getCreateMonth());
            if (ads == null)
                return;
            // 如果缓存中存在，则需要将当前文章添加进去。
            CustomActionDate found = null;
            for (ActionDate ad : ads) {
                if (ad.getDayOfMonth().intValue() == t.getCreateDay().intValue()) {
                    found = (CustomActionDate) ad;
                    break;
                }
            }
            // 如果缓存中存在当月数据，而没有今天的数据，直需要添加新的actionDate进去
            if (found == null) {
                found = new CustomActionDate(t.getCreateDay());
                ads.add(found);
            }
            found.addArticle(t, true);
        }

        @Override
        public void afterUpdate(ArticleEn t) {
            if (!checkValid(t))
                return;
            List<ActionDate> ads = findFromCache(t.getCreateYear(), t.getCreateMonth());
            if (ads == null)
                return;
            CustomActionDate found = null;
            for (ActionDate ad : ads) {
                if (ad.getDayOfMonth().intValue() == t.getCreateDay().intValue()) {
                    found = ((CustomActionDate) ad);
                    break;
                }
            }
            if (found == null) {
                found = new CustomActionDate(t.getCreateDay());
                ads.add(found);
            }
            if (t.getSecurity() != ArticleSecurity.PUBLIC) {
                found.removeArticle(t.getArticleId());
                if (found.isEmpty()) {
                    ads.remove(found);
                }
            } else {
                found.updateArticle(t);
            }
        }

        private ArticleEn tempDel;

        @Override
        public void beforeDelete(ArticleEn t) {
            if (t == null) {
                return;
            }
            tempDel = new ArticleEn();
            tempDel.setArticleId(t.getArticleId());
            tempDel.setCreateYear(t.getCreateYear());
            tempDel.setCreateMonth(t.getCreateMonth());
            tempDel.setCreateDay(t.getCreateDay());
        }

        @Override
        public void afterDelete(Long id) {
            if (!checkValid(tempDel))
                return;
            List<ActionDate> ads = findFromCache(tempDel.getCreateYear(), tempDel.getCreateMonth());
            if (ads == null)
                return;
            CustomActionDate found = null;
            for (ActionDate ad : ads) {
                if (ad.getDayOfMonth().intValue() == tempDel.getCreateDay().intValue()) {
                    found = ((CustomActionDate) ad);
                    break;
                }
            }
            if (found != null) {
                found.removeArticle(tempDel.getArticleId());
                if (found.isEmpty()) {
                    ads.remove(found);
                }
            }
        }

        private boolean checkValid(ArticleEn t) {
            if (actionDateMap == null || t == null)
                return false;
            Integer year = t.getCreateYear();
            Integer month = t.getCreateMonth();
            Integer day = t.getCreateDay();
            if (year == null || month == null || day == null) {
                logger.warning("Found bad data, articleId=" + t.getArticleId() +
                        ", createYear = " + t.getCreateYear() +
                        ", createMonth = " + t.getCreateMonth() +
                        ", createDay = " + t.getCreateDay());
                return false;
            }
            return true;
        }

        private List<ActionDate> findFromCache(Integer year, Integer month) {
            if (actionDateMap == null)
                return null;
            return actionDateMap.get(year.toString() + "-" + month.toString());
        }
    }
}
