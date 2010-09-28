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

package name.huliqing.qblog.service;

import java.util.Date;
import java.util.List;

import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.daocache.ReplyCache;
import name.huliqing.qblog.entity.ReplyEn;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;

public class ReplySe {

    public final static boolean save(ReplyEn reply) {
        ArticleSe.increaseTotalReply(reply.getArticle(), 1);
        reply.setCreateDate(new Date());
        return ReplyCache.getInstance().save(reply);
    }

    /**
     * 导入数据(不要使用save(ReplyEn))
     * @param reply
     * @return
     */
    public final static boolean _import(ReplyEn reply) {
        return ReplyCache.getInstance().save(reply);
    }

    public final static boolean update(ReplyEn reply) {
        reply.setModifyDate(new Date());
        return ReplyCache.getInstance().update(reply);
    }

    public final static Boolean delete(Long replyId) {
        ReplyEn re = ReplyCache.getInstance().find(replyId);
        if (re != null) {
            ArticleSe.increaseTotalReply(re.getArticle(), -1);
            ReplyCache.getInstance().delete(re.getReplyId());
            return true;
        }
        return false;
    }

    public final static ReplyEn find(Long replyId) {
        return ReplyCache.getInstance().find(replyId);
    }

    public final static PageModel<ReplyEn> find(PageParam pp, Long articleId) {
        PageModel<ReplyEn> pm = new PageModel<ReplyEn>();
        pm.setPageData(ReplyCache.getInstance().findByArticle(articleId,
                pp.getSortField(), pp.getAsc(), pp.getStart(), pp.getPageSize()));
        pm.setTotal(ReplyCache.getInstance().countByArticle(articleId));
        return pm;
    }

    public final static List<ReplyEn> findAll() {
        return ReplyCache.getInstance().findAll();
    }

    /**
     * 查找创建日期(createDate)在start,end之间的所有评论信息
     * @param start
     * @param end
     * @return
     */
    public final static List<ReplyEn> findByDateRange(Date start, Date end) {
        return ReplyCache.getInstance().findByDateRange(start, end);
    }

    /**
     * 返回最近回复的信息，该列表按createDate倒序排列.
     * @param size 获取的数量
     * @return
     */
    public final static List<ReplyEn> findLastReplies(Integer size) {
        return ReplyCache.getInstance().findLastReplies(size);
    }

    /**
     * 判断当前用户是否有权限编辑该reply.
     * 规则如下：
     * 1.如果用户已经登录，则返回true;
     * 2.如果replyBy为null,并且客户端IP与reply的IP一致则认为可编辑,
     * 注：必须replyBy为null,因为replyBy!=null时，说明该reply记录是登录后才发表的。
     * 所以必须确保登录用户才可修改。
     * 3.其它情况则返回false
     * @return
     */
    public final static boolean isEditable(ReplyEn reply) {
        // 登录用户可edit/delete
        if (QBlog.getCurrentVisitor().isLogin()) {
            return true;
        }
        // IP相同并且在设定的时间限制之内即可edit/delete回复信息
        if (QBlog.getRemoteAddress().equalsIgnoreCase(reply.getReplyIp())) {
            Date now = new Date();
            Date postDate = reply.getCreateDate();

            Integer limit = ConfigManager.getInstance().getAsInteger(Config.CON_REPLY_EDIT_LIMIT);
            if (now.getTime() - postDate.getTime() < limit * 1000) {
                return true;
            }
        }
        return false;
    }
}
