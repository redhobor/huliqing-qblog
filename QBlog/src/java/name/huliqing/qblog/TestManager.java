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

package name.huliqing.qblog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.enums.ArticleSecurity;
import name.huliqing.qblog.service.ArticleSe;

/**
 *
 * @author huliqing
 */
public class TestManager {
    private final static Logger logger = Logger.getLogger(TestManager.class.getName());
    private final static TestManager ins = new TestManager();
    private TestManager() {}
    public final static TestManager getInstance() {
        return ins;
    }

    private boolean doing = true;

    public final void process(ServletRequest request, ServletResponse response) {
        String action = QBlog.getParam("action", request);
        if ("stop".equals(action)) {
            logger.info("[stop]停止测试任务.");
            doing = false;
        }

        if (doing) {
            logger.info("执行测试 test()");
            long startTime = System.currentTimeMillis();
            test();
            logger.info("用时：" + (System.currentTimeMillis() - startTime));
        }
    }

    private final void test() {
        ArticleEn temp = ArticleSe.find(436L);
        if (temp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            String time = sdf.format(new Date());
            ArticleEn ae = new ArticleEn();
            ae.setAuthor(temp.getAuthor());
            ae.setCategory(temp.getCategory());
            ae.setContent(temp.getContent());
            ae.setMailNotice(temp.getMailNotice());
            ae.setModifyDate(temp.getModifyDate());
            ae.setReplyable(temp.getReplyable());
            ae.setSecurity(ArticleSecurity.PUBLIC);
            ae.setSummary(time + "-> STest:这是一篇由系统自动生成的文章，该文章用于测试。" + temp.getSummary());
            ae.setTags("随笔,情感,画集,爱情,编程,Java,C,C++,C#,哲学");
            ae.setTitle(time + "-> STest:" + temp.getTitle());
            ArticleSe.testSave(ae);
        }
    }
}
