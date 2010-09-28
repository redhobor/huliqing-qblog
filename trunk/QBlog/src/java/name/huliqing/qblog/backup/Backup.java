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

package name.huliqing.qblog.backup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <backup version="{The version}" date="{Date of backup}" >
 *      <Entity class="...">
 *          <data {Attributes from entity} />
 *          <data {Attributes from entity} />
 *          <data {Attributes from entity} />
 *      </Entity>
 * 
 *      <Entity class="...">
 *          <data {Attributes from entity} />
 *          <data {Attributes from entity} />
 *          <data {Attributes from entity} />
 *      </Entity>
 * </backup>
 * @author huliqing
 */
public class Backup extends Bak{
    private final static Logger logger = Logger.getLogger(Backup.class.getName());

    // Version of app
    private String version;

    // Backup Date
    private Date date;

    // The root node of the doc.
    private Element root;
    
    public Backup(Document doc) {
        super(doc);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * e.g. ? = ArticleEn, Encode format: <BR />
     * <ArticleEn class="package.ArticleEn" >
     *      <data attributes from ArticleEn />
     *      <data attributes from ArticleEn />
     *      <data attributes from ArticleEn />
     * </ArticleEn>
     * @param dataArr
     * @return
     * @throws ParserConfigurationException
     */
    public void encode(List<?> dataArr) throws 
            ParserConfigurationException,
            IllegalArgumentException,
            IllegalAccessException {
        if (dataArr == null || dataArr.isEmpty())
            return;
        if (doc == null)
            throw new NullPointerException("Document couldn't be null.");

        if (root == null) {
            root = doc.createElement("backup");
            doc.appendChild(root);
            root.setAttribute("version", version != null ? version : "");
            root.setAttribute("date", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date != null ? date : new Date()));
        }

        Class<?> clazz = dataArr.get(0).getClass();
        Element ele = doc.createElement(clazz.getSimpleName());
        root.appendChild(ele);
        ele.setAttribute("class", clazz.getName());
        AttributeHelper attributeUtil = new AttributeHelper(clazz, this);
        for (Object d : dataArr) {
            Element data = doc.createElement("data");
            ele.appendChild(attributeUtil.encode(d, data));
        }
    }
}
