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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import name.huliqing.common.XmlUtils;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.entity.TagArticleEn;
import name.huliqing.qblog.entity.TagEn;
import name.huliqing.qblog.processor.RSS;
import name.huliqing.qblog.processor.XmlProcessor2;
import name.huliqing.qblog.processor.attr.AttrInputText;
import name.huliqing.qblog.processor.attr.AttrSelectBooleanCheckbox;
import name.huliqing.qblog.processor.attr.Attribute2;
import name.huliqing.qblog.processor.attr.AttrSelectManyCheckbox;
import name.huliqing.qblog.processor.attr.AttrSelectOneRadio;
import name.huliqing.qblog.service.ArticleSe;
import name.huliqing.qblog.service.TagArticleSe;
import name.huliqing.qblog.service.TagSe;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author huliqing
 */
public class RSSProcessor extends XmlProcessor2 implements RSS{
    private final static Logger logger = Logger.getLogger(RSSProcessor.class.getName());

    public List<Attribute2> getRequiredAttributes() {
        List<TagEn> tags = TagSe.findAll();
        List<Attribute2> attrs = new ArrayList<Attribute2>(4);

        // 标签选项
        AttrSelectManyCheckbox tagItems = new AttrSelectManyCheckbox("TAG", "",
                "选择允许提供订阅连接的文章标签。关于系统RSS功能的开启需要设置" +
                "系统参数：\"CON_ARTICLE_RSS_ENABLE\"," +
                " 只有开启了这个参数，这个模块功能才有效。");
        if (tags != null && !tags.isEmpty()) {
            tagItems.addItem("all", "所有类型");
            for (TagEn te : tags)
                tagItems.addItem(te.getName(), te.getName());
        }

        // 序号
        AttrSelectBooleanCheckbox showIndex =
                new AttrSelectBooleanCheckbox("Show Index", "false", "是否显示序号,默认：否");
        
        // 计算文章数
        AttrSelectBooleanCheckbox sumArticle =
                new AttrSelectBooleanCheckbox("Sum Article", "false", "是否计算相关标签的文章数,默认：否");

        // 目标窗口
        AttrSelectOneRadio target = new AttrSelectOneRadio("Target", "_self",
                "选择打开TAG连接的目标窗口.默认：当前窗口");
        target.addItem("_self", "当前窗口");
        target.addItem("_blank", "新窗口");

        // RSS图标
        AttrInputText rssImgSrc = new AttrInputText("RSS Image", "/_res/image/rss.jpg", "另外换个RSS图标看看。");
        
        // 文章数
        AttrInputText size = new AttrInputText("Size", "20", "每次请求向客户端返回最高多少条记录，" +
                "举例：如果您置为10,那么意思是说来自于客户端的请求，每次只返回最新的10篇文章。" +
                "一般以一页数据左右为限制较好,数量太大会影响性能，默认:20");

        // Channel
        AttrInputText channelTitle = new AttrInputText("Title", "QBlog - RSS", "RSS Title");
        AttrInputText channelLink = new AttrInputText("Link", "", "Website,留空，则每次自动指向当前网站。");
        AttrInputText channelDes = new AttrInputText("Description", "", "相关备注");
        AttrInputText channelCopyright = new AttrInputText("Copyright", "Copy right © 2010 QBlog", "版权信息,填上您的版权信息");
        AttrInputText timeZone = new AttrInputText("Time Zone", "GMT+8", "时区");
 
        attrs.add(tagItems);
        attrs.add(showIndex);
        attrs.add(sumArticle);
        attrs.add(target);
        attrs.add(rssImgSrc);
        attrs.add(size);
        attrs.add(channelTitle);
        attrs.add(channelLink);
        attrs.add(channelDes);
        attrs.add(channelCopyright);
        attrs.add(timeZone);
        return attrs;
    }
    
    @Override
    public UIComponent render(ModuleEn module) {
        AttrMap attr = getAttributes(module);
        RSSDataTable table = new RSSDataTable();
        String[] tagsRSS = attr.getAsString("TAG", "").split(",");
        Boolean showIndex = attr.getAsBoolean("Show Index", Boolean.FALSE);
        Boolean sumArticle = attr.getAsBoolean("Sum Article", Boolean.FALSE);
        String target = attr.getAsString("Target", "_self");
        String rssImage = attr.getAsString("RSS Image", "/_res/image/rss.jpg");

        // 查找所有文章标签
        List<TagEn> tagEns = TagSe.findAll();
        List<String> tagsAll = null;
        if (tagEns != null && !tagEns.isEmpty()) {
            tagsAll = new ArrayList<String>(tagEns.size() + 1);
            tagsAll.add("all");
            for (TagEn te : tagEns) {
                tagsAll.add(te.getName());
            }
        }

        // 计算相关标签的文章数, sumArticleMap: K -> tagName, V -> total
        if (sumArticle && tagEns != null && !tagEns.isEmpty()) {
            Map<String, Integer> sumArticleMap = new HashMap<String, Integer>(tagsAll.size());
            for (TagEn te : tagEns) {
                sumArticleMap.put(te.getName(), te.getTotal() != null ? te.getTotal() : 0);
            }
            table.setSumArticleMap(sumArticleMap);
        }

        table.setModuleId(module.getModuleId());
        table.setTagsAll(tagsAll); // tagsRSS 中可能包含一个“all”额外标签
        table.setTagsRSS(Arrays.asList(tagsRSS));
        table.setShowIndex(showIndex);
        table.setTarget(target);
        table.setRssImage(rssImage);
        return table;
    }

    public String getName() {
        return "RSS 聚阅";
    }

    public String getDescription() {
        return "创建一个RSS聚阅模块.";
    }

    // ---- RSS Output

    public void rssOutput(HttpServletRequest request, HttpServletResponse response,
            ModuleEn module) {
        String tag = request.getParameter("tag");
        if (tag == null || "".equals(tag))
            return;
        AttrMap attr = getAttributes(module);
 
        String[] tagsRSS = attr.getAsString("TAG", "").split(",");
        if (!Arrays.asList(tagsRSS).contains(tag)) {
            logger.warning("Not a rss supported tag. tagName=" + tag + ", moduleId=" + module.getModuleId());
            return;
        }

        List<ArticleWrap> articles = findArticles(tag, attr.getAsInteger("Size", 20));
        if (articles == null || articles.isEmpty())
            return;

        // Start Document
        String host = "http://" + request.getHeader("host");
        String contextPath = request.getContextPath();
        // RSS Date format, don't change it!
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone(attr.getAsString("Time Zone", "GMT+8")));
        Document doc = null;

        try {
            doc = XmlUtils.newDocument();
        } catch (ParserConfigurationException ex) {
            logger.log(Level.SEVERE, "Couldn't create document.", ex);
            return;
        }

        // "rss" Element
        Element rss = doc.createElement("rss");
        doc.appendChild(rss);
        rss.setAttribute("version", "2.0");

        // "channel" Element
        Element channel = doc.createElement("channel");
        rss.appendChild(channel);

        // "title", "link", "description" Element
        Element title = doc.createElement("title");
        Element link = doc.createElement("link");
        Element description = doc.createElement("description");
        Element copyright = doc.createElement("copyright");
        channel.appendChild(title);
        channel.appendChild(link);
        channel.appendChild(description);
        channel.appendChild(copyright);
        title.appendChild(doc.createTextNode(attr.getAsString("Title", "")));
        link.appendChild(doc.createTextNode(attr.getAsString("Link", host)));
        description.appendChild(doc.createTextNode(attr.getAsString("Description", "")));
        copyright.appendChild(doc.createTextNode(attr.getAsString("Copyright", "")));
        for (ArticleWrap aw : articles) {
            Element _item = doc.createElement("item");
            Element _title = doc.createElement("title");
            Element _link = doc.createElement("link");
            Element _description = doc.createElement("description");
            Element _pubDate = doc.createElement("pubDate");
            Element _comments = doc.createElement("comments");
            channel.appendChild(_item);
            _item.appendChild(_title);
            _item.appendChild(_link);
            _item.appendChild(_description);
            _item.appendChild(_pubDate);
            _item.appendChild(_comments);

            String url = host + contextPath + "/article/articleId=" + aw.articleId;
            String href = "...[<a href=\"" + url + "\" >阅读全文</a>]";

            String summary = "<font color=\"gray\">摘要：" + aw.summary + "</font>" + href;

            _title.appendChild(doc.createTextNode(aw.title));
            _link.appendChild(doc.createTextNode(url));
            _description.appendChild(doc.createTextNode(summary));
            _pubDate.appendChild(doc.createTextNode(aw.createDate != null ? sdf.format(aw.createDate) : ""));
            _comments.appendChild(doc.createTextNode("View:" + aw.totalView + ", Comments:" + aw.totalReply));
        }
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        try {
            XmlUtils.write(doc, response.getWriter());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (TransformerConfigurationException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    private List<ArticleWrap> findArticles(String tag, Integer size) {
        List<ArticleWrap> wraps;
        if ("all".equals(tag)) {
            List<ArticleEn> aes = ArticleSe.findAllPublic(size);
            if (aes != null && !aes.isEmpty()) {
                wraps = new ArrayList<ArticleWrap>(aes.size());
                for (ArticleEn ae : aes) {
                    ArticleWrap w = new ArticleWrap();
                    w.articleId = ae.getArticleId();
                    w.createDate = ae.getCreateDate();
                    w.summary = ae.getSummary();
                    w.title = ae.getTitle();
                    w.totalView = ae.getTotalView();
                    w.totalReply = ae.getTotalReply();
                    wraps.add(w);
                }
                return wraps;
            }
        } else {
            TagEn tagEn = TagSe.find(tag);
            if (tagEn == null)
                return null;
            List<TagArticleEn> taes = TagArticleSe.findPublicByTag(tagEn.getName(), size);
            if (taes != null && !taes.isEmpty()) {
                wraps = new ArrayList<ArticleWrap>(taes.size());
                for (TagArticleEn tae : taes) {
                    ArticleWrap w = new ArticleWrap();
                    w.articleId = tae.getArticleId();
                    w.createDate = tae.getCreateDate();
                    w.summary = tae.getSummary();
                    w.title = tae.getTitle();
                    w.totalView = tae.getTotalView();
                    w.totalReply = tae.getTotalReply();
                    wraps.add(w);
                }
                return wraps;
            }
        }
        return null;
    }

    private class ArticleWrap {
        public String title;
        public Long articleId;
        public String summary;
        public Date createDate;
        public Long totalView;
        public Long totalReply;
    }
}
