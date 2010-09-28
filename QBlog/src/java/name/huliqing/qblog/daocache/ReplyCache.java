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

package name.huliqing.qblog.daocache;

import java.util.ArrayList;
import java.util.List;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.dao.ReplyDa;
import name.huliqing.qblog.entity.ReplyEn;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qfaces.QFaces;

/**
 *
 * @author huliqing
 */
public class ReplyCache extends ReplyDa{
    private final static ReplyCache ins = new ReplyCache();
    private ReplyCache() {}
    public final static ReplyCache getInstance() {
        return ins;
    }

    /**
     * 最近的回复，这里缓存了一定数量的回复信息.具体数量由参数：CON_REPLY_CACHE_LAST_REPLY
     * 定义。最高不超过30记录。该列表使用createDate倒序,即index=0为最近一个刚回复的记录。
     */
    private List<ReplyEn> lastReplies = new ArrayList<ReplyEn>(30);

    @Override
    public boolean save(ReplyEn t) {
        super.save(t);
        // 添加记录到缓存中
        lastReplies.add(0, t);
        int limit = getCacheLimit();
        while (lastReplies.size() > limit) {
            lastReplies.remove(lastReplies.size() - 1);
        }
        return true;
    }

    @Override
    public boolean update(ReplyEn t) {
        if (super.update(t)) {
            // 更新缓存中的记录
            int index = -1;
            boolean found = false;
            for (ReplyEn re : lastReplies) {
                index++;
                if (re.getReplyId() == t.getReplyId()) {
                    found = true;
                    break;
                }
            }
            if (found && index != -1 && index < lastReplies.size()) {
                lastReplies.remove(index);
                lastReplies.add(index, t);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        super.delete(id);
        // 查找并删除缓存中的Object
        int index = -1;
        boolean found = false;
        for (ReplyEn re : lastReplies) {
            index++;
            if (re.getReplyId() == id) {
                found = true;
                break;
            }
        }
        if (found && index != -1 && index < lastReplies.size()) {
            lastReplies.remove(index);
        }
        return true;
    }

    @Override
    public ReplyEn find(Long id) {
        return super.find(id);
    }

    public List<ReplyEn> findAll() {
        return findAll(null, null, null, null);
    }

    /**
     * 查询最近的回复信息,按createDate倒序
     * @param size 查询的数量,当这个数量小于缓存中的数量时，直接从缓存中取出。
     * 如果查询的数量大于缓存中的数量，则返回缓存中的所有数据，其余的再从Dao中取出。
     * @return
     */
    public List<ReplyEn> findLastReplies(Integer size) {
        if (size == null || size <= 0)
            return null;
        ArrayList<ReplyEn> result = new ArrayList<ReplyEn>(size);
        for (int i = 0; i < lastReplies.size(); i++) {
            result.add(lastReplies.get(i));
            if (result.size() >= size) {
                break;
            }
        }
        if (result.size() < size) {
            int needToGet = size - result.size();
            int start = result.size();
            List<ReplyEn> other = findByObject(new ReplyEn(),
                    "createDate", Boolean.FALSE, start, needToGet);
            if (other != null && !other.isEmpty()) {
                result.addAll(other);
            }
        }
        return result;
    }

    private final int getCacheLimit() {
        String value = ConfigManager.getInstance()
                .findConfig(Config.CON_REPLY_CACHE_LAST_REPLY).getValue();
        Integer limit = QFaces.convertToInteger(value);
        if (limit == null || limit > 30 || limit < 0) {
            limit = 10;
        }
        return limit;
    }

    /**
     * 分页查询某文章下的所有回复
     * @param articleId
     * @param sortField
     * @param asc
     * @param start
     * @param size
     * @return
     */
    public final List<ReplyEn> findByArticle(Long articleId,
            String sortField, Boolean asc, Integer start, Integer size) {
        ReplyEn reply = new ReplyEn();
        reply.setArticle(articleId);
        return findByObject(reply, sortField, asc, start, size);
    }

    /**
     * 查询某文章的回复数
     * @param articleId
     * @return
     */
    public final Integer countByArticle(Long articleId) {
        ReplyEn reply = new ReplyEn();
        reply.setArticle(articleId);
        return countByObject(reply);
    }

    // remove
//    /**
//     * 查询某文章的所有回复(分页)
//     * @param pp
//     * @param articleId
//     * @return
//     */
//    public final PageModel<ReplyEn> findByArticleId(PageParam pp, Long articleId) {
//        ReplyEn reply = new ReplyEn();
//        reply.setArticle(articleId);
//        return findByObject(reply, pp);
//    }
}
