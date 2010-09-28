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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.w3c.dom.Element;

/**
 *
 * @author huliqing
 */
public class AttributeHelper {
    private final static Logger logger = Logger.getLogger(AttributeHelper.class.getName());

    private Bak bak;

    private Class<?> t;

    private List<Field> fields;

    public AttributeHelper(Class<?> t, Bak bak) {
        this.t = t;
        this.bak = bak;
        Field[] fs = t.getDeclaredFields();
        if (fs == null || fs.length <= 0) {
            logger.warning("No declared fields found. class type=" + t);
            return;
        }
        fields = new ArrayList<Field>(fs.length);
        for (Field f : fs) {
            if (f.getAnnotation(BakFlag.class) != null) {
                f.setAccessible(true);
                fields.add(f);
            }
        }
    }

    /**
     * 将T的相关字段属性编码为Element(XML format)的相关属性
     * @param t
     * @param e
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public Element encode(Object t, Element e) throws
            IllegalArgumentException,
            IllegalAccessException {
        if (t == null || e == null) {
            logger.warning("No pointer, t=" + t + ", e=" + e);
            return e;
        }
        if (fields == null) {
            logger.warning("No declared fields found.");
            return e;
        }
        for (Field f : fields) {
            Object value = f.get(t);
            if (value == null) {
                e.setAttribute(f.getName(), "");
            } else {
                TypeConverter tc = bak.getConverters().get(f.getType());
                if (tc != null) {
                    e.setAttribute(f.getName(), tc.getAsString(value));
                } else {
                    logger.severe("Missing type converter, type=" + f.getType());
                    e.setAttribute(f.getName(), value.toString());
                }
            }
        }
        return e;
    }

    /**
     * Restore element as entity
     * @param e
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public Object decode(Element e) throws
            IllegalArgumentException,
            IllegalAccessException,
            InstantiationException {
        Object o = t.newInstance();
        if (fields == null) {
            logger.warning("No declared fields found.");
            return null;
        }
        for (Field f : fields) {
            String strValue = e.getAttribute(f.getName());
            if (strValue == null) {
                f.set(o, null);
            } else {
                TypeConverter tc = bak.getConverters().get(f.getType());
                if (tc != null) {
                    f.set(o, tc.getAsObject(strValue));
                } else {
                    logger.warning("Field type cann't be handled, type=" + f.getType());
                    f.set(o, strValue);
                }
            }
        }
        return o;
    }

}
