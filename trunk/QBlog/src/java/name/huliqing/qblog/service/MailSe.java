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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.entity.ReplyEn;
import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qfaces.QFaces;

public class MailSe {
    private final static Logger logger = Logger.getLogger(MailSe.class.getName());

    /**
     * 发送Email给自己
     * @param content
     */
    public final static boolean sendToSelf(String title, String content) {
        String sender = findAndCheckSender();
        if (sender == null)
            return false;
        MailService.Message message = new MailService.Message();
        message.setSender(sender);
        message.setTo(sender);
        message.setSubject(title);
        message.setHtmlBody(content);
        return (MailSe.send(message));
    }

    /**
     * 发送文章的回复提醒。
     * @param reply
     * @param articleId
     */
    public final static void sendByReply(ReplyEn reply, Long articleId) {
        // 获取发送Email的地址
        String sender = findAndCheckSender();
        if (sender == null)
            return;

        ArticleEn article = ArticleSe.find(articleId);

        // 获取作者的邮件接收地址
        String authorAddress = ConfigManager.getInstance().findConfig(Config.CON_EMAIL_ADDR_SERVER).getValue();
        // 分析出邮件接收者的列表(对于回复者的回复)
        List<String> receives = parseReply(reply, authorAddress);
        // 在邮件中包含原文章连接信息 
        String articleURL = QBlog.getHostRequest() + "/article/artileId=" + reply.getArticle();

        // 邮件主体
        StringBuilder hb = new StringBuilder().append("<pre>")
                .append(reply.getContent())
                .append("</pre>")
                .append("<hr />")
                .append("REPLY By:" + reply.getReplyBy())
                .append("<br />")
                .append("REPLY IP:" + reply.getReplyIpRemake())
                .append("<br />")
                .append("ARTICLE : <a href='" + articleURL + "'>" + article.getTitle())
                .append("</a> <br />");

        // 对文章作者进行email提醒
        if (article.getMailNotice()) {
            MailService.Message message = new MailService.Message();
            message.setSender(sender);
            message.setTo(authorAddress);
            message.setSubject(reply.getTitle());
            message.setReplyTo(reply.getEmail());
            message.setHtmlBody(hb.toString());
            MailSe.send(message);
        }

        // 对回复者进行提醒
        if (receives != null && !receives.isEmpty()) {
            MailService.Message message = new MailService.Message();
            message.setSender(sender);
            message.setTo(receives);
            message.setSubject(reply.getTitle());
            message.setReplyTo(reply.getEmail());
            message.setHtmlBody(hb.toString());
            MailSe.send(message);
        }
    }

    /**
     * 分析出应该发送邮件提醒的email地址.
     * @param reply
     * @param authorAddress
     * @return
     */
    private final static List<String> parseReply(ReplyEn reply, String authorAddress) {
        if (reply == null || reply.getContent() == null || reply.getContent().getValue() == null) {
            return null;
        }
        List<String> list = new ArrayList<String>();
        String[] temp = reply.getContent().getValue().split("\n");
        //@119,admin
        for (String t : temp) {
            String val = t.trim();
            if (val.startsWith("@") && val.indexOf(",") > 0) {
                Long targetReplyId = QFaces.convertToLong(val.substring(1, val.indexOf(",")));
                if (targetReplyId != null) {
                    ReplyEn targetReply = ReplySe.find(targetReplyId);
                    if (targetReply != null) {
                        String receive = targetReply.getEmail();
                        // 接收地址
                        // 1. 排除作者的email地址
                        // 2. 排除重复的email地址
                        if (receive != null
                                && !receive.equals(authorAddress)
                                && !list.contains(receive)) {
                            list.add(receive);
                        }
                    }
                }
            } else {
                // 在遇到首行不以@开头时即跳出，不再分析。
                // 也就是说，对于要回复的目标地址:
                // 1.必须出现在content的前面。
                // 2.必须以"@"开头
                // 3.每行一个地址,格式如下:
                //	@119,admin
                //	@120,huliqing
                //	开始回复的内容...
                break;
            }
        }
        return list;
    }

    private final static String findAndCheckSender() {
        String sender = ConfigManager.getInstance().findConfig(Config.CON_EMAIL_ADDR_SERVER).getValue();
        if (sender == null || "".equals(sender)) {
            logger.warning("Sender not found, couldn't send email, check system property:" + Config.CON_EMAIL_ADDR_SERVER.name());
            Messenger.sendError("邮件发送失败，原因：没有设置CON_EMAIL_ADDR_SERVER参数.");
            return null;
        }
        return sender;
    }

    private final static boolean send(MailService.Message message) {
        try {
            MailServiceFactory.getMailService().send(message);
            return true;
        } catch (IOException e) {
            Logger.getLogger(MailSe.class.getName()).log(Level.SEVERE, "发送Email时遇到错误", e);
            Messenger.sendError("发送Email时遇到错误, error=" + e.getMessage());
        } catch (IllegalArgumentException e) {
            Logger.getLogger(MailSe.class.getName()).log(Level.SEVERE, "发送Email时遇到错误", e);
            Messenger.sendError("发送Email时遇到错误,请检查您的邮件设置是否正确! error=" + e.getMessage());
        }
        return false;
    }
}
