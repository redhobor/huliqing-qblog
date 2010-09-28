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

import java.util.Date;
import name.huliqing.qblog.entity.CounterEn;

/**
 *
 * @author huliqing
 */
public abstract class CounterDa extends BaseDao<CounterEn, String> {
    public CounterDa() {
        super(CounterEn.class);
    }

    public enum CounterType {

        /** 所有public类型的文章的总数 */
        article_public,

        /** 所有private类型的文章总数 */
        article_private,

        /** 所有draft类型的文章总数 */
        article_draft;
    }

    private CounterEn init(CounterType ct) {
        CounterEn ce = new CounterEn();
        ce.setCounterType(ct.name());
        ce.setTotal(synchronize(ct));
        ce.setLastDate(new Date());
        super.save(ce);
        return ce;
    }

    /**
     * 递增,并返回操作后的结果数量
     * @param ct
     * @return total after increase
     */
    protected Integer increase(CounterType ct) {
        CounterEn ce = super.find(ct.name());
        if (ce == null) {
            ce = init(ct);
        } else {
            ce.setTotal(ce.getTotal().intValue() + 1);
            super.update(ce);
        }
        return ce.getTotal();
    }

    /**
     * 递减,并返回操作后的结果数量
     * @param ct
     * @return total after decrease
     */
    protected Integer decrease(CounterType ct) {
        CounterEn ce = super.find(ct.name());
        if (ce == null) {
            ce = init(ct);
        } else {
            if (ce.getTotal().intValue() >= 1) {
                ce.setTotal(ce.getTotal().intValue() - 1);
                super.update(ce);
            }
        }
        return ce.getTotal();
    }
    
    protected CounterEn find(CounterType ct) {
        CounterEn ce = super.find(ct.name());
        if (ce == null) {
            ce = init(ct);
        }
        return ce;
    }

    abstract protected Integer synchronize(CounterType ct);
    
}
