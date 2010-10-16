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

package name.huliqing.qblog.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.html.HtmlPanelGroup;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.entity.TagArticleEn;
import name.huliqing.qblog.processor.impl.ArticlesDataTable;
import name.huliqing.qblog.service.ArticleSe;
import name.huliqing.qblog.service.TagArticleSe;
import name.huliqing.qfaces.QFaces;
import name.huliqing.qfaces.component.Scroller;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;

/**
 *
 * @author huliqing
 */
@ManagedBean
@RequestScoped
public class ArticlesWe extends BaseWe {

    private Long pageId;
    private String tag;
    private Integer year;
    private Integer month;
    private Integer day;

    public ArticlesWe() {
        super();
        Long tempPageId = QBlog.getPageId();
        if (tempPageId != null) {
            pageId = tempPageId;
        }

        String tempTag = QBlog.getParam("tag");
        if (tempTag != null) {
            try {
                // 确保中文及其它特殊字符的编码正确
                tag = URLDecoder.decode(tempTag, "utf8");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(ArticlesWe.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Integer tempYear = QFaces.convertToInteger(QBlog.getParam("year"));
            Integer tempMonth = QFaces.convertToInteger(QBlog.getParam("month"));
            Integer tempDay = QFaces.convertToInteger(QBlog.getParam("day"));
            if (tempYear != null && tempMonth != null && tempDay != null) {
                year = tempYear;
                month = tempMonth;
                day = tempDay;
            }
        }
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public PageModel<ArticleEn> loadData(PageParam pp) {
        if (tag != null) {
            return searchByTag(pp, tag);
        } else {
            return ArticleSe.findByDay(year, month, day, pp);
        }
    }
    
    private PageModel<ArticleEn> searchByTag(PageParam pp, String tagName) {
        // 以发表时间的倒序显示tag所查询的文章信息
        pp.setSortField("createDate");
        pp.setAsc(Boolean.FALSE);

        PageModel<TagArticleEn> tempPM = TagArticleSe.findPublicByTag(tagName, pp);
        if (tempPM.getTotal() <= 0) {
            return null;
        } 
        List<ArticleEn> aes = new ArrayList<ArticleEn>(tempPM.getTotal());
        List<TagArticleEn> taes = tempPM.getPageData();
        for (TagArticleEn tae : taes) {
            ArticleEn ae = new ArticleEn();
            ae.setArticleId(tae.getArticleId());
            ae.setCreateDate(tae.getCreateDate());
            ae.setSecurity(tae.getSecurity());
            ae.setSummary(tae.getSummary());
            ae.setTitle(tae.getTitle());
            ae.setTotalReply(tae.getTotalReply());
            ae.setTotalView(tae.getTotalView());
            aes.add(ae);
        }
        PageModel<ArticleEn> pm = new PageModel<ArticleEn>();
        pm.setTotal(tempPM.getTotal());
        pm.setPageData(aes);
        return pm;
    }

}
