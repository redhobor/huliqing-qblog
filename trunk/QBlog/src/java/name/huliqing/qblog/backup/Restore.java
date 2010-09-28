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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 *
 * @author huliqing
 */
public class Restore extends Bak{
    private final static Logger logger = Logger.getLogger(Restore.class.getName());

    private Element root;

    public Restore(Document doc) {
        super(doc);
    }

    /**
     * format:
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
     * @param clazz
     * @return
     */
    public List<?> restore(Class<?> clazz) throws
            IllegalArgumentException,
            IllegalAccessException,
            InstantiationException {
        if (root == null) {
            root = (Element) doc.getElementsByTagName("backup").item(0);
        }

        NodeList entityEles = root.getElementsByTagName(clazz.getSimpleName());
        if (entityEles == null || entityEles.getLength() <= 0)
            return null;

        List<Object> result = new ArrayList<Object>();
        for (int i = 0; i < entityEles.getLength(); i++) {
            Element entityEle = (Element) entityEles.item(i);
            String className = entityEle.getAttribute("class");
            if (className == null || !className.equals(clazz.getName())) {
                continue;
            }
            List<Object> entities = restoreEntities(entityEle);
            if (entities != null) {
                result.addAll(entities);
            }
        }
        return result;
    }

    private List<Object> restoreEntities(Element entityEle) throws
            IllegalArgumentException,
            IllegalAccessException,
            InstantiationException {
        String className = entityEle.getAttribute("class");
        Class<?> c = null;
        try {
            c = Class.forName(className);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "Class not found, class name=" + className, ex);
            return null;
        }
        List<Object> entitys = new ArrayList<Object>();
        AttributeHelper attributeUtil = new AttributeHelper(c, this);
        NodeList dataList = entityEle.getElementsByTagName("data");
        if (dataList != null && dataList.getLength() > 0) {
            for (int i = 0; i < dataList.getLength(); i++) {
                Object o = attributeUtil.decode((Element) dataList.item(i));
                entitys.add(o);
            }
        }
        return entitys;
    }
}
