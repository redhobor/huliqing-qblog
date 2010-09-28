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

package name.huliqing.qblog.task;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import name.huliqing.qblog.daocache.TagArticleCache;
import name.huliqing.qblog.daocache.TagCache;
import name.huliqing.qblog.entity.TagEn;
import name.huliqing.qblog.service.TagSe;

/**
 * 重新计算每个标签下的文章数
 */
public class TaskUpdateTagCounts implements Task{
    private final static Logger logger = Logger.getLogger(TaskArticlePrivateCounter.class.getName());

    public TaskUpdateTagCounts() {}

    public boolean execute() {
        List<TagEn> teAll = TagSe.findAll();
        if (teAll != null && !teAll.isEmpty()) {
            for (TagEn te : teAll) {
                if (te.getModifyDate() != null) {
                    long diff = System.currentTimeMillis() - te.getModifyDate().getTime();
                    if (diff < 86400000) { // 1000 * 60 * 60 * 24
                        // 每隔一天作一次统计
                        logger.info("距离上次更新Tag(" + te.getName() + ")的时间为: " + (diff / 3600000) + " Hours，本次不执行统计.");
                        continue;
                    }
                }
                Integer total = TagArticleCache.getInstance().countArticles(te.getName());
                if (total != te.getTotal()) {
                    te.setTotal(total);
                    te.setModifyDate(new Date());
                    TagCache.getInstance().update(te);
                }
            }
        }
        return true;
    }
}
