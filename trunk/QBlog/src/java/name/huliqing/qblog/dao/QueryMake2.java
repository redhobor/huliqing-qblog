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

package name.huliqing.qblog.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import name.huliqing.qblog.entity.SearchField;

public class QueryMake2 {

    /**
     * Query生成，用于findByObject时
     * @param em
     * @param searchObj 用于查询的entity,包含相关的查询数据
     * @return
     */
    public final static Query makeSearchQueryTotal(EntityManager em, Object searchObj) {
        StringBuilder sb = new StringBuilder("select count(obj) from " + searchObj.getClass().getName() + " obj");
        return makeQuery(em, searchObj, sb, null, null, null, null);
    }

    /**
     * Query生成，用于findByObject时
     * @param em
     * @param searchObj 用于查询的entity,包含相关的查询数据
     * @param sortField
     * @param asc
     * @param start
     * @param size
     * @return
     */
    public final static Query makeSearchQueryData(EntityManager em, Object searchObj,
            String sortField, Boolean asc, Integer start, Integer size) {
        StringBuilder sb = new StringBuilder("select obj from " + searchObj.getClass().getName() + " obj");
        return makeQuery(em, searchObj, sb, sortField, asc, start, size);
    }

    /**
     * Query生成，用于findByObject时。
     * @param em
     * @param searchObj 用于查询的entity,包含相关的查询数据
     * @param sb
     * @param sortField 可为null
     * @param asc 可为null
     * @param start 可为null
     * @param size 可为null
     * @return
     */
    public final static Query makeQuery(EntityManager em, Object searchObj, StringBuilder sb,
            String sortField, Boolean asc, Integer start, Integer size) {
        Map<String, Object> mappings = FindAvailableFields(searchObj);
        Set<String> keys = mappings.keySet();
        Query q = null;
        if (!keys.isEmpty()) {
            sb.append(" where ");
            int num = 0;
            for (String key : keys) {
                if (num != 0) {
                    sb.append(" and ");
                }
                sb.append(" obj.").append(key).append("=:").append(key);
                num++;
            }
            q = createQuery(em, sb, sortField, asc, start, size);
            for (String key : keys) {
                q.setParameter(key, mappings.get(key));
            }
        } else {
            q = createQuery(em, sb, sortField, asc, start, size);
        }
//        Logger.fine("sb.toString = " + sb.toString());
        return q;
    }

    // ---- Private

    private final static Query createQuery(EntityManager em, StringBuilder sb,
            String sortField, Boolean asc, Integer start, Integer size) {
        if (sortField != null && !"".equals(sortField)) {
            sb.append(" order by obj." + sortField);
            if (asc != null) {
                sb.append(asc ? " asc " : " desc ");
            }
        }
        Query q = em.createQuery(sb.toString());
        if (start != null) {
            q.setFirstResult(start.intValue());
        }
        if (size != null) {
            q.setMaxResults(size.intValue());
        }
        return q;
    }

    /**
     * o中不为null的字段才可用
     * @return
     */
    private final static Map<String, Object> FindAvailableFields(Object o) {
        Map<String, Object> mappings = new HashMap<String, Object>();
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field f : fields) {
            Object searchField = f.getAnnotation(SearchField.class);
            if (searchField != null) {
                Object fieldValue = getFieldValue(f, o);
                if (fieldValue != null) {
                    mappings.put(f.getName(), fieldValue);
                }
            }
        }
        return mappings;
    }

    private final static Object getFieldValue(Field field, Object o) {
        String fieldName = field.getName();
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String gm = "get" + methodName;
        Method m = null;
        try {
            m = o.getClass().getMethod(gm);
            if (m != null)
                return m.invoke(o);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(QueryMake2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(QueryMake2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(QueryMake2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(QueryMake2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(QueryMake2.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (m == null && field.getType().getName().toLowerCase().equals("boolean")) {
            try {
                m = o.getClass().getMethod("is" + methodName);
                if (m != null)
                    return m.invoke(o);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(QueryMake2.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(QueryMake2.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(QueryMake2.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(QueryMake2.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(QueryMake2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
