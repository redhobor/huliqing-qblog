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

import java.util.List;
import name.huliqing.qblog.dao.TagArticleDa;
import name.huliqing.qblog.entity.TagArticleEn;
import name.huliqing.qblog.enums.ArticleSecurity;

/**
 *
 * @author huliqing
 */
public class TagArticleCache extends TagArticleDa{
    private final static TagArticleCache ins = new TagArticleCache();
    private TagArticleCache(){}

    public final static TagArticleCache getInstance() {
        return ins;
    }
    
    @Override
    public boolean save(TagArticleEn t) {
        return super.save(t);
    }

    @Override
    public boolean delete(Long tagArticleId) {
        return super.delete(tagArticleId);
    }

    @Override
    public boolean deleteByArticleId(Long articleId) {
        return super.deleteByArticleId(articleId);
    }

    /**
     * 通过标签查询文章列表
     * @param tagName
     * @param sortField
     * @param asc
     * @param start
     * @param size
     * @return
     */
    public final List<TagArticleEn> findByTag(String tagName,
            String sortField, Boolean asc, Integer start, Integer size) {
        if (tagName == null)
            throw new NullPointerException("tagName couldn't be null.");
        TagArticleEn tae = new TagArticleEn();
        tae.setTagName(tagName);
        return findByObject(tae, sortField, asc, start, size);
    }

    /**
     * 查询某Tag下的文章总数
     * @param tagName
     * @return
     */
    public final Integer countByTag(String tagName) {
        if (tagName == null)
            throw new NullPointerException("tagName couldn't be null.");
        TagArticleEn tae = new TagArticleEn();
        tae.setTagName(tagName);
        return countByObject(tae);
    }

    /**
     * 查询tag下的所有<strong>公开</strong>发表的文章列表。
     * @param tagName
     * @param sortField
     * @param asc
     * @param start
     * @param size
     * @return
     */
    public final List<TagArticleEn> findPublicByTag(String tagName,
            String sortField, Boolean asc, Integer start, Integer size) {
        if (tagName == null)
            throw new NullPointerException("tagName couldn't be null.");
        TagArticleEn tae = new TagArticleEn();
        tae.setTagName(tagName);
        tae.setSecurity(ArticleSecurity.PUBLIC);
        return findByObject(tae, sortField, asc, start, size);
    }

    /**
     * 查询某Tag下的“公开”发表的文章总数
     * @param tagName
     * @return
     */
    public final Integer countPublicByTag(String tagName) {
        if (tagName == null)
            throw new NullPointerException("tagName couldn't be null.");
        TagArticleEn tae = new TagArticleEn();
        tae.setTagName(tagName);
        tae.setSecurity(ArticleSecurity.PUBLIC);
        return countByObject(tae);
    }

    /**
     * 查询某文章的所有标签
     * @param articleId
     * @return
     */
    public final List<TagArticleEn> findByArticleId(Long articleId) {
        if (articleId == null)
            throw new NullPointerException("articleId couldn't be null.");
        TagArticleEn searchObj = new TagArticleEn();
        searchObj.setArticleId(articleId);
        return findByObject(searchObj, null, null, null, null);
    }
    
    /**
     * 查询某个标签的所有文章数，这包括任何Security类型的文章。
     * @param tagName
     * @return
     */
    public final Integer countArticles(String tagName) {
        if (tagName == null)
            throw new NullPointerException("tagName couldn't be null.");
        TagArticleEn tae = new TagArticleEn();
        tae.setTagName(tagName);
        return countByObject(tae);
    }
}
