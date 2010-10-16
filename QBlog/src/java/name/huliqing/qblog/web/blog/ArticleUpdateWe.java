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

package name.huliqing.qblog.web.blog;

import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;
import name.huliqing.qblog.web.BaseWe;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.enums.ArticleSecurity;
import name.huliqing.qblog.service.ArticleSe;
import name.huliqing.qblog.service.CategorySe;
import name.huliqing.qblog.service.TagSe;
import name.huliqing.qfaces.QFaces;

@ManagedBean
@RequestScoped
public class ArticleUpdateWe extends BaseWe {

    private ArticleEn article;
    private String[] tagsSelected;

    public ArticleUpdateWe() {
        super();
        Long articleId = QFaces.convertToLong(QBlog.getParam("articleId"));
        if (articleId != null) {
            this.article = ArticleSe.find(articleId);
            if (this.article != null && this.article.getTags() != null) {
                this.tagsSelected = this.article.getTags().split(",");
            }
        }
    }

    // ---- Getter and Setter

    public ArticleEn getArticle() {
        if (article == null) {
            article = new ArticleEn();
            article.setReplyable(true);
            article.setMailNotice(true);
            article.setCreateDate(new Date());
            article.setSecurity(ArticleSecurity.PUBLIC);
        }
        return article;
    }

    public void setArticle(ArticleEn article) {
        this.article = article;
    }

    public String[] getTagsSelected() {
        return tagsSelected;
    }

    public void setTagsSelected(String[] tagsSelected) {
        this.tagsSelected = tagsSelected;
    }
    
    public List<SelectItem> getCategorys() {
        return CategorySe.findAllAsSelectItem();
    }

    public List<SelectItem> getArticleTypes() {
        return ArticleSecurity.generateItems();
    }

    public List<SelectItem> getTagsAll() {
        return TagSe.findAllAsSelectItems();
    }

    // ---- Action
    public String add() {
        this.article.setTags(makeTags());
        this.article.setAuthor(QBlog.getCurrentVisitor().getAccount().getAccount());
        if (ArticleSe.save(this.article)) {
            if (this.article.getSecurity() == ArticleSecurity.DRAFT) {
                QBlog.redirect("articleDraftList.faces");
            } else if (this.article.getSecurity() == ArticleSecurity.PRIVATE) {
                QBlog.redirect("articlePrivateList.faces");
            } else if (this.article.getSecurity() == ArticleSecurity.PUBLIC) {
                QBlog.redirect("articlePublicList.faces");
            }
        }
        return null;
    }

    public String edit() {
        this.article.setTags(makeTags());
        if (ArticleSe.update(this.article) != null) {
            if (this.returnURL != null) {
                QBlog.redirect(returnURL);
            } else if (this.article.getSecurity() == ArticleSecurity.DRAFT) {
                QBlog.redirect("articleDraftList.faces");
            } else if (this.article.getSecurity() == ArticleSecurity.PRIVATE) {
                QBlog.redirect("articlePrivateList.faces");
            } else if (this.article.getSecurity() == ArticleSecurity.PUBLIC) {
                QBlog.redirect("articlePublicList.faces");
            }
        }
        return null;
    }

    private String makeTags() {
        StringBuilder sb = null;
        if (tagsSelected != null && tagsSelected.length > 0) {
            sb = new StringBuilder("");
            for (String tag : tagsSelected) {
                sb.append(tag).append(",");
            }
        }
        String tags = sb != null ? sb.toString() : "";
        if (tags.endsWith(",")) {
            tags = tags.substring(0, tags.length() - 1);
        }
        return tags;
    }
}
