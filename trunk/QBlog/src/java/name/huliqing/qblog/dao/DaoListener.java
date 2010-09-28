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

/**
 * 使用DaoListener时需要相当注意，处理不好，性能可能很糟糕:<BR />
 * 1.不要在实现方法中再次调用相关的save,update,delete,这会造成无限loop.
 * 2.应该尽量不要modify传递进来的数据。这可能造成数据不一致。
 * 
 * @deprecated 可能在未来移除，这个实现可能会造成潜在风险。暂时未找到更好的实现办法
 * @author huliqing
 */
public interface DaoListener<T, PK extends Serializable> {

    /**
     * Before dao save an entity.
     * @param t 被保存前的Entity,注：ID属性可能仍为null
     */
    void beforeSave(T t);

    /**
     * Before dao update an entity
     * @param t 被更新前的entity
     */
    void beforeUpdate(T t);

    /**
     * Before dao delete an entity
     * @param t 被delete前的entity
     */
    void beforeDelete(T t);

    /**
     * After dao save an entity
     * @param t 保存后的entity
     */
    void afterSave(T t);

    /**
     * After dao update an entity
     * @param t 更新后的entity
     */
    void afterUpdate(T t);

    /**
     * After dao delete an entity
     * @param id 被删除的entity的id
     */
    void afterDelete(PK id);
}
