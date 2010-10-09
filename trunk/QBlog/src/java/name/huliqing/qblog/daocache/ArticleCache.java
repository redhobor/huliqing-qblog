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
import java.util.Iterator;
import java.util.List;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.dao.ArticleCounter;
import name.huliqing.qblog.dao.ArticleDa;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.enums.ArticleSecurity;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qfaces.QFaces;

/**
 *
 * @author huliqing
 */
public class ArticleCache extends ArticleDa{
    private final static ArticleCache ins = new ArticleCache();
    private ArticleCache(){}
    public final static ArticleCache getInstance() {
        return ins;
    }

    // 缓存最近发表的文章,这个列表使用createDate倒序
    private List<ArticleEn> lastPost;
   
    @Override
    public final synchronized boolean save(ArticleEn t) {
        if (t.getCreateDate() == null) 
            throw new NullPointerException("Create Date不能为null.");
        if (t.getSecurity() == null)
            throw new NullPointerException("Security 不能为null");

        if (super.save(t)) {
            // 更新索引及counter
            ArticleCounter.getInstance().eventSave(t.getSecurity());
            ArticleDraftIndexHelper.getInstance().eventSave(t);
            ArticlePublicIndexHelper.getInstance().eventSave(t);
            ArticlePrivateIndexHelper.getInstance().eventSave(t);

            // cache last post
            if (lastPost == null) {
                cacheLastPost();
            } else {
                lastPost.add(0, t);
            }
            int limit = getCacheLimit();
            while (lastPost.size() > limit) {
                lastPost.remove(lastPost.size() - 1);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public final synchronized boolean update(ArticleEn t) {
        // 不能让update方法变成save方法（在articleId为null时可能出现这种情况）
        if (t.getArticleId() == null)
            throw new NullPointerException("Article Id couldn't be null");
        if (t.getCreateDate() == null)
            throw new NullPointerException("Create Date不能为null.");
        if (t.getSecurity() == null)
            throw new NullPointerException("Security 不能为null");

        // update之前之后的security
        ArticleEn oldValue = super.find(t.getArticleId());
        ArticleSecurity secBefore = oldValue.getSecurity();
        ArticleSecurity secAfter = t.getSecurity();

        // 更新数据
        if (super.update(t)) {

            // 更新索引及Counter
            ArticleCounter.getInstance().eventUpdate(secBefore, secAfter);
            ArticlePublicIndexHelper.getInstance().eventUpdate(t, secBefore, secAfter);
            ArticleDraftIndexHelper.getInstance().eventUpdate(t, secBefore, secAfter);
            ArticlePrivateIndexHelper.getInstance().eventUpdate(t, secBefore, secAfter);

            // cache last post
            if (lastPost == null) {
                cacheLastPost();
            }

            int index = -1;
            boolean found = false;
            for (ArticleEn ae : lastPost) {
                index++;
                if (ae.getArticleId().longValue() == t.getArticleId().longValue()) {
                    found = true;
                    break;
                }
            }
            if (found) {
                lastPost.remove(index);
                lastPost.add(index, t);
            }
            return true;
        }
        return false;
    }

    @Override
    public final synchronized boolean delete(Long id) {
        ArticleEn oldValue = super.find(id);
        if (oldValue == null) {
            return true;
        }
        if (super.delete(id)) {

            // 更新索引及Counter
            ArticleCounter.getInstance().eventDelete(oldValue.getSecurity());
            ArticleDraftIndexHelper.getInstance().eventDelete(id);
            ArticlePublicIndexHelper.getInstance().eventDelete(id);
            ArticlePrivateIndexHelper.getInstance().eventDelete(id);

            // 查找并删除缓存中的Object
            if (lastPost == null) {
                cacheLastPost();
            }
            Iterator<ArticleEn> it = lastPost.iterator();
            while (it.hasNext()) {
                if (it.next().getArticleId().longValue() == id.longValue()) {
                    it.remove();
                    break;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public ArticleEn find(Long id) {
        return super.find(id);
    }

    /**
     * 查询所有公开发表的文章,只查询部分字段,结果以createDate倒序
     * @param start 启始记录
     * @param size 获取的允许最高数量
     * @return
     */
    public final List<ArticleEn> findAllPublic(Integer start, Integer size) {
        long startTime = System.currentTimeMillis();
        if (lastPost == null) {
            cacheLastPost();
        }
        List<ArticleEn> result = new ArrayList<ArticleEn>(size);
        // 从缓存中获取
        for (int i = start; i < lastPost.size(); i++) {
            ArticleEn ae = lastPost.get(i);
            if (ae.getSecurity() == ArticleSecurity.PUBLIC) {
                result.add(ae);
            }
            if (result.size() >= size) {
                break;
            }
        }
        // 如果缓存中不够，再从datastore中获取
        if (result.size() < size) {
            int needToGet = size - result.size();
            int nextStart = start + result.size();
            // (如果 nextStart >= publicTotal,则返回结果只会是empty,而且浪费操作时间)
            if (nextStart < ArticleCounter.getInstance().getTotalPublic().intValue()) {
                Long[] aids = ArticlePublicIndexHelper.getInstance().findByDesc(nextStart, needToGet);
                List<ArticleEn> other = findArticlesById(aids);
                if (other != null && !other.isEmpty()) {
                    result.addAll(other);
                }
            }
        }
        logger.info("find all public article use time=" + (System.currentTimeMillis() - startTime));
        return result;
    }

    /**
     * @param start 起始记录
     * @param size 获取的允许最高数量
     * @return
     */
    public final List<ArticleEn> findAllDraft(Integer start, Integer size) {
        if (lastPost == null) {
            cacheLastPost();
        }
        List<ArticleEn> result = new ArrayList<ArticleEn>(size);
        // 从缓存中获取
        for (int i = start; i < lastPost.size(); i++) {
            ArticleEn ae = lastPost.get(i);
            if (ae.getSecurity() == ArticleSecurity.DRAFT) {
                result.add(ae);
            }
            if (result.size() >= size) {
                break;
            }
        }
        if (result.size() < size) {
            int needToGet = size - result.size();
            int nextStart = start + result.size();
            if (nextStart < ArticleCounter.getInstance().getTotalDraft().intValue()) {
                Long[] aids = ArticleDraftIndexHelper.getInstance().findByDesc(nextStart, needToGet);
                List<ArticleEn> other = findArticlesById(aids);
                if (other != null && !other.isEmpty()) {
                    result.addAll(other);
                }
            }
        }
        return result;
    }

    /**
     * 查询所有隐私的文章,只查询部分字段,结果以createDate倒序
     * @param start 启始记录
     * @param size 获取的允许最高数量
     * @return
     */
    public final List<ArticleEn> findAllPrivate(Integer start, Integer size) {
        if (lastPost == null) {
            cacheLastPost();
        }
        List<ArticleEn> result = new ArrayList<ArticleEn>(size);
        // 从缓存中获取
        for (int i = start; i < lastPost.size(); i++) {
            ArticleEn ae = lastPost.get(i);
            if (ae.getSecurity() == ArticleSecurity.PRIVATE) {
                result.add(ae);
            }
            if (result.size() >= size) {
                break;
            }
        }
        if (result.size() < size) {
            int needToGet = size - result.size();
            int nextStart = start + result.size();
            if (nextStart < ArticleCounter.getInstance().getTotalPrivate().intValue()) {
                Long[] aids = ArticlePrivateIndexHelper.getInstance().findByDesc(nextStart, needToGet);
                List<ArticleEn> other = findArticlesById(aids);
                if (other != null && !other.isEmpty()) {
                    result.addAll(other);
                }
            }
        }
        return result;
    }

    /**
     * 查询所有“公开”发表的文章总数
     * @return
     */
    public final Integer countAllPublic() {
        return ArticleCounter.getInstance().getTotalPublic();
    }

    public final Integer countAllPrivate() {
        return ArticleCounter.getInstance().getTotalPrivate();
    }
    
    public final Integer countAllDraft() {
        return ArticleCounter.getInstance().getTotalDraft();
    }

    /**
     * 查询某一天所发表的“公开的”文章，按createDate desc
     * @param year
     * @param month
     * @param day
     * @param start
     * @param size
     * @return
     */
    public final List<ArticleEn> findByDay(Integer year, Integer month, Integer day,
            Integer start, Integer size) {
        ArticleEn search = new ArticleEn();
        search.setSecurity(ArticleSecurity.PUBLIC);
        search.setCreateYear(year);
        search.setCreateMonth(month);
        search.setCreateDay(day);
        return findByObjectSimple(search, "createDate", Boolean.FALSE, start, size);
    }

    public final Integer countByDay(Integer year, Integer month, Integer day) {
        ArticleEn search = new ArticleEn();
        search.setSecurity(ArticleSecurity.PUBLIC);
        search.setCreateYear(year);
        search.setCreateMonth(month);
        search.setCreateDay(day);
        return countByObject(search);
    }

    /**
     * 查询某个月份内发表的文章，只查询ArticleSecurity.PUBLIC的文章,按createDate倒序
     * 注：该方法只查询部分字段
     * @param year
     * @param month
     * @param size 取出多少条数据，如果为null,则取出当前所有数据
     * @return
     */
    public final List<ArticleEn> findByMonth(Integer year, Integer month, Integer size) {
        ArticleEn search = new ArticleEn();
        search.setSecurity(ArticleSecurity.PUBLIC);
        search.setCreateYear(year);
        search.setCreateMonth(month);
        return findByObjectSimple(search, "createDate", Boolean.FALSE, 0, size);
    }

    /**
     * 查询日期时间段内的所有文章，该方法返回所有article的完整字段信息
     * @param start
     * @param end
     * @return
     */
    @Override
    public List<ArticleEn> findByDateRange(Date start, Date end) {
        return super.findByDateRange(start, end);
    }

    /**
     * 查找最近发表的文章，这包含所有类别的文章，该方法以发表时间倒序进行查询。
     * @param size 允许返回的最高数量
     * @return
     */
    public List<ArticleEn> findRecentPost(Integer size) {
        return super.findAll("createDate", Boolean.FALSE, 0, size);
    }

    // ---- Private.............................................................

    // 获取文章缓存数限制
    private final int getCacheLimit() {
        String value = ConfigManager.getInstance()
                .findConfig(Config.CON_ARTICLE_CACHE_LIMIT).getValue();
        Integer limit = QFaces.convertToInteger(value);
        if (limit == null || limit < 0) {
            limit = QFaces.convertToInteger(Config.CON_ARTICLE_CACHE_LIMIT.getValue());
        }
        return limit;
    }

    /**
     * 缓存最近发布的数据
     */
    private final synchronized void cacheLastPost() {
        int limit = getCacheLimit();
        if (limit <= 0) {
            lastPost = new ArrayList<ArticleEn>(0);
            return;
        } else {
            lastPost = new ArrayList<ArticleEn>(limit);
            List<ArticleEn> aes = super.findAll("createDate", Boolean.FALSE, 0, limit);
            if (aes != null && !aes.isEmpty()) {
                lastPost.addAll(aes);
            }
        }
    }
}
