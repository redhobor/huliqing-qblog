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
import name.huliqing.qfaces.model.PageParam;

/**
 * @deprecated use QueryMake2 instead
 * @author huliqing
 */
public class QueryMake {

    public static Query makeQueryTotal(EntityManager em, Object o) {
        StringBuilder sb = new StringBuilder("select count(obj) from " + o.getClass().getName() + " obj");
        return makeQuery(em, o, sb, null);
    }

    public static Query makeQueryData(EntityManager em, Object o, PageParam pp) {
        StringBuilder sb = new StringBuilder("select obj from " + o.getClass().getName() + " obj");
        return makeQuery(em, o, sb, pp);
    }

    private static Query makeQuery(EntityManager em, Object o, StringBuilder sb, PageParam pp) {
        Map<String, Object> mappings = FindAvailableFields(o);
        Set<String> keys = mappings.keySet();
        Query q = null;
        if (!keys.isEmpty()) {
            sb.append(" where");
            int num = 0;
            for (String key : keys) {
                if (num != 0) {
                    sb.append(" and");
                }
                sb.append(" obj.").append(key).append("=:").append(key);
                num++;
            }
            q = createQuery(em, sb, pp);
            for (String key : keys) {
                q.setParameter(key, mappings.get(key));
            }
        } else {
            q = createQuery(em, sb, pp);
        }
//        Logger.fine("sb.toString = " + sb.toString());
        return q;
    }

    private static Query createQuery(EntityManager em, StringBuilder sb, PageParam pp) {
        if (pp != null) {
            if (pp.getSortField() != null) {
                sb.append(" order by obj." + pp.getSortField());
                if (pp.isAsc() != null) {
                    sb.append(pp.isAsc() ? " asc" : " desc");
                }
            }
        }
        Query q = em.createQuery(sb.toString());
        if (pp != null) {
            if (pp.getStart() != null) {
                q.setFirstResult(pp.getStart().intValue());
            }
            if (pp.getPageSize() != null) {
                q.setMaxResults(pp.getPageSize());
            }
        }
        return q;
    }

    /**
     * o中不为null的字段才可用
     * @return
     */
    private static Map<String, Object> FindAvailableFields(Object o) {
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

    private static Object getFieldValue(Field field, Object o) {
        String fieldName = field.getName();
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        String gm = "get" + methodName;
        Method m = null;
        try {
            m = o.getClass().getMethod(gm);
            if (m != null)
                return m.invoke(o);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(QueryMake.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(QueryMake.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(QueryMake.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(QueryMake.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(QueryMake.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (m == null && field.getType().getName().toLowerCase().equals("boolean")) {
            try {
                m = o.getClass().getMethod("is" + methodName);
                if (m != null)
                    return m.invoke(o);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(QueryMake.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(QueryMake.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(QueryMake.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchMethodException ex) {
                Logger.getLogger(QueryMake.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
                Logger.getLogger(QueryMake.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
