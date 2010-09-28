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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

public abstract class BaseDao<T, PK extends Serializable> {
    protected final static Logger logger = Logger.getLogger(BaseDao.class.getName());

    /**
     * Dao侦听器Map,这些监听器用于监听相关Entity的save,update,delete
     */
    protected Map<String, DaoListener<T, PK>> listeners;

    protected Class<T> t;

    protected BaseDao(Class<T> t) {
        this.t = t;
    }

    public void addListener(String name, DaoListener<T, PK> listener) {
        if (listener == null) {
            throw new NullPointerException("listener could not be null. name=" + name + ", listener=" + listener);
        }
        if (listeners == null) {
            listeners = new HashMap<String, DaoListener<T, PK>>();
        }
        if (listeners.containsKey(name)) {
            logger.warning("侦听器已经存在, name=" + name);
            return;
        }
        listeners.put(name, listener);
    }

    public DaoListener<T, PK> getListener(String name) {
        if (listeners == null || listeners.isEmpty())
            return null;
        return listeners.get(name);
    }

    private void processBeforeSave(T t) {
        if (listeners != null) {
            Collection<DaoListener<T, PK>> dls = listeners.values();
            for (DaoListener<T, PK> dl : dls) {
                dl.beforeSave(t);
            }
        }
    }
    private void processBeforeUpdate(T t) {
        if (listeners != null) {
            Collection<DaoListener<T, PK>> dls = listeners.values();
            for (DaoListener<T, PK> dl : dls) {
                dl.beforeUpdate(t);
            }
        }
    }
    private void processBeforeDelete(T t) {
        if (listeners != null) {
            Collection<DaoListener<T, PK>> dls = listeners.values();
            for (DaoListener<T, PK> dl : dls) {
                dl.beforeDelete(t);
            }
        }
    }
    private void processAfterSave(T t) {
        if (listeners != null) {
            Collection<DaoListener<T, PK>> dls = listeners.values();
            for (DaoListener<T, PK> dl : dls) {
                dl.afterSave(t);
            }
        }
    }
    private void processAfterUpdate(T t) {
        if (listeners != null) {
            Collection<DaoListener<T, PK>> dls = listeners.values();
            for (DaoListener<T, PK> dl : dls) {
                dl.afterUpdate(t);
            }
        }
    }

    private void processAfterDelete(PK id) {
        if (listeners != null) {
            Collection<DaoListener<T, PK>> dls = listeners.values();
            for (DaoListener<T, PK> dl : dls) {
                dl.afterDelete(id);
            }
        }
    }

    /**
     * 保存Object
     * @param t
     * @return 如果成功则返回true,否则返因false
     */
    protected boolean save(T t) {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try {
            processBeforeSave(t);
            tx.begin();
            em.persist(t);
            tx.commit();
            processAfterSave(t);
            return true;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * 更新Object
     * @param t
     * @return
     */
    protected boolean update(T t) {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try {
            processBeforeUpdate(t);
            tx.begin();
            T _t = em.merge(t);
            tx.commit();
            processAfterUpdate(_t);
            return (_t != null);
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * 通过ID,删除Object
     * @param id
     * @return
     */
    protected boolean delete(PK id) {
        EntityManager em = getEM();
        EntityTransaction tx = em.getTransaction();
        try {
            T _o = em.find(t, id);
            processBeforeDelete(_o);
            tx.begin();
            em.remove(_o);
            tx.commit();
            processAfterDelete(id);
            return true;
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            em.close();
        }
    }

    /**
     * 通过ID查找Object
     * @param id
     * @return
     */
    protected T find(PK id) {
        EntityManager em = getEM();
        try { 
            T _t = em.find(t, id);
            return _t;
        } finally {
            em.close();
        }
    }

    /**
     * 查找size数量的Object, 当sortField不为null时，则按sortField排序，如果asc为true
     * 则按正序，如果asc为false则倒序。
     * @param sortField 排序字段，可为null
     * @param asc 是否正序，可为null
     * @param start 开始记录，可为null
     * @param size 要查找的最高数量,可为null
     * @return
     */
    protected List<T> findAll(String sortField, Boolean asc, Integer start, Integer size) {
        String sql = "select obj from " + this.t.getName() + " obj ";
        if (sortField != null) {
            sql += " order by obj." + sortField;
            if (asc != null) {
                sql += asc ? " asc " : " desc ";
            } else {
                sql += " asc ";
            }
        }
        EntityManager em = getEM();
        try {
            Query q = em.createQuery(sql);
            if (start != null) {
                q.setFirstResult(start);
            }
            if (size != null) {
                q.setMaxResults(size);
            }
            List<T> result = convertAsArrayList(q.getResultList());
            return result;
        } finally {
            em.close();
        }
    }

    /**
     * 通过o搜索，
     * @param o
     * @param sortField 排序字段，可为null
     * @param asc 是否正序? 否则倒序, 可为null
     * @param start 开始记录,可为null
     * @param size 获取最高数量，可为null
     * @return
     */
    protected List<T> findByObject(T o, String sortField, Boolean asc, Integer start, Integer size) {
        EntityManager em = getEM();
        try {
            Query q = QueryMake2.makeSearchQueryData(em, o,
                    sortField, asc, start, size);
            List<T> data = convertAsArrayList(q.getResultList());
            return data;
        } finally {
            em.close();
        }
    }

    /**
     * 查询数量
     * @param o
     * @return
     */
    protected Integer countByObject(T o) {
        EntityManager em = getEM();
        try {
            Query qTotal = QueryMake2.makeSearchQueryTotal(em, o);
            Integer total = (Integer) qTotal.getSingleResult();
            return total;
        } finally {
            em.close();
        }
    }

    protected EntityManager getEM() {
        return EMF.get().createEntityManager();
    }

    /**
     * 从 EMF.get().createEntityManager()中获得的List类型为 StreamQueryResult,
     * 这在大部分情况下，无法通过JSF的状态保存及恢复。在状态恢复时可能产生：
     * java.lang.IllegalStateException: Unknown object type的异常，所以需要转换为
     * 能够支持 javax.faces.component.UIComponentBase.restoreAttachedState 的数据
     * 类型(这里为ArrayList)
     * @param data
     * @return
     */
    protected List<T> convertAsArrayList(List<T> data) {
        if (data == null) {
            return null;
        }
        ArrayList<T> result = new ArrayList<T>(data.size());
        for (T d : data) {
            result.add(d);
        }
        return result;
    }
    
// remove
//    private Query createQuery(EntityManager em, String hql, PageParam pp) {
//        StringBuilder sb = new StringBuilder(hql);
//        if (pp != null) {
//            if (pp.getSortField() != null) {
//                sb.append(" order by obj." + pp.getSortField());
//                if (pp.isAsc() != null)
//                    sb.append(pp.isAsc() ? " asc " : " desc ");
//            }
//        }
//        Query q = em.createQuery(sb.toString());
//        if (pp != null) {
//            if (pp.getStart() != null)
//                q.setFirstResult(pp.getStart().intValue());
//            if (pp.getPageSize() != null)
//                q.setMaxResults(pp.getPageSize());
//        }
//        return q;
//    }
}
