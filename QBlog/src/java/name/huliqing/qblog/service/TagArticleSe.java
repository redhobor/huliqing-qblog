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

import java.util.List;
import name.huliqing.qblog.daocache.ArticleCache;
import name.huliqing.qblog.daocache.TagArticleCache;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.entity.TagArticleEn;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;

/**
 *
 * @author huliqing
 */
public class TagArticleSe {

    public final static boolean save(TagArticleEn tae) {
        return TagArticleCache.getInstance().save(tae);
    }

    public final static boolean delete(Long tagArticleId) {
        return TagArticleCache.getInstance().delete(tagArticleId);
    }

    /**
     * 更新文章的标签信息
     * @param articleId 文章id
     * @return
     */
    public final static boolean updateArticleTags(Long articleId) {
        // 移除旧的TagArticle
        TagArticleCache.getInstance().deleteByArticleId(articleId);

        // 重新创建Tag与Article的关系
        ArticleEn ae = ArticleCache.getInstance().find(articleId);
        if (ae != null && ae.getTags() != null) {
            String[] tagNames = ae.getTags().split(",");
            for (String tagName : tagNames) {
                if ("".equals(tagName))
                    continue;
                TagArticleEn tae = new TagArticleEn();
                tae.setArticleId(ae.getArticleId());
                tae.setCreateDate(ae.getCreateDate());
                tae.setSecurity(ae.getSecurity());
                tae.setSummary(ae.getSummary());
                tae.setTagName(tagName);
                tae.setTitle(ae.getTitle());
                tae.setTotalReply(ae.getTotalReply());
                tae.setTotalView(ae.getTotalView());
                TagArticleCache.getInstance().save(tae);
            }
        }
        return true;
    }
    
    /**
     * 查询某文章的所有标签列表。
     * @param articleId
     * @return
     */
    public final static List<TagArticleEn> findByArticleId(Long articleId) {
        return TagArticleCache.getInstance().findByArticleId(articleId);
    }

    /**
     * 通过Tag查询该类Tag下的文章列表。只查询Security为PUBLIC的文章。并且结果
     * 以文章的createDate的倒序排列,
     * @param tagName tag名称
     * @param size 允许返回的文章最高数量
     * @return
     */
    public final static List<TagArticleEn> findPublicByTag(String tagName, Integer size) {
        return TagArticleCache.getInstance().findPublicByTag(tagName, "createDate", Boolean.FALSE, 0, size);
    }

    /**
     * 通过Tag查询该类Tag下的文章列表。只查询Security为PUBLIC的文章。并且结果
     * 以文章的createDate的倒序排列,
     * @param tagName tag名称
     * @param size 允许返回的文章最高数量
     * @param asc 是否按createDate正序(否则为倒序)
     * @return
     */
    public final static List<TagArticleEn> findPublicByTag(String tagName, Integer size, boolean asc) {
        return TagArticleCache.getInstance().findPublicByTag(tagName, "createDate", asc, 0, size);
    }

    /**
     * 分页查询，通过Tag查询该类Tag下的文章列表。只查询Security为PUBLIC的文章
     * @param tagName tag名称
     * @param pp 分页查询参数
     * @return
     */
    public final static PageModel<TagArticleEn> findPublicByTag(String tagName, PageParam pp) {
        PageModel<TagArticleEn> pm = new PageModel<TagArticleEn>();
        pm.setPageData(TagArticleCache.getInstance().findPublicByTag(tagName,
                pp.getSortField(), pp.getAsc(), pp.getStart(), pp.getPageSize()));
        pm.setTotal(TagArticleCache.getInstance().countPublicByTag(tagName));
        return pm;
    }

    /**
     * 查询某个标签的所有文章数，这包括任何Security类型的文章。
     * @param tagName
     * @return
     */
    public final static Integer countArticles(String tagName) {
        return TagArticleCache.getInstance().countArticles(tagName);
    }
}
