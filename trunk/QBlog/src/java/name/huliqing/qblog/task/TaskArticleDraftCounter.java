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

import java.util.logging.Logger;
import name.huliqing.qblog.dao.ArticleCounter;
import name.huliqing.qblog.dao.CounterDa.CounterType;
import name.huliqing.qblog.dao.Store;
import name.huliqing.qblog.entity.CounterEn;
import name.huliqing.qblog.enums.ArticleSecurity;

/**
 * 统计article数量
 * @author huliqing
 */
public class TaskArticleDraftCounter implements Task{
    private final static Logger logger = Logger.getLogger(TaskArticleDraftCounter.class.getName());

    // 存取计数
    private int count;

    // 是否同步计数器完毕
    private boolean isContinue = false;

    // 最后一个取到的articleId位置
    private Long lastArticleId;

    public boolean execute() {
        CounterEn ce = ArticleCounter.getInstance().findCounter(CounterType.article_draft);
        if (ce.getLastDate() != null) {
            long diff = System.currentTimeMillis() - ce.getLastDate().getTime();
            if (diff < 86400000) { // 1000 * 60 * 60 * 24
                // 每隔一天作一次统计
                logger.info("距离上次更新draft article计数器的时间为: " + (diff / 3600000) + " Hours，本次不执行统计.");
                return true;
            }
        }
        logger.info("开始同步draft article计数器.");
        if (!isContinue) {
            // 清0,并初始lastArticleId
            count = 0;
            lastArticleId = 1L;
            isContinue = true;
        }
        Long startId = lastArticleId;
        Long lastId = null;
        Store store = new Store();
        while ((lastId = ArticleCounter.getInstance().countArticle(ArticleSecurity.DRAFT, startId, store)) != null) {
            count += store.getValue();
            lastArticleId = lastId;
            startId = lastId + 1;
            store.clear();
        }
        ArticleCounter.getInstance().updateDraftCounter(count);
        lastArticleId = null;
        isContinue = false;
        logger.info("同步draft article 计数器完成,总数：" + count);
        return true;
    }
}
