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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import name.huliqing.qblog.entity.HelpEn;
import name.huliqing.qblog.service.HelpSe;

/**
 *
 * @author huliqing
 */
@ManagedBean
@ApplicationScoped
public class Helper implements java.io.Serializable {
    
    public String listener(String helpId) {
        HelpEn he = HelpSe.find(helpId);
        if (he != null && he.getContent() != null) {
            return he.getContent().getValue();
        }
        return "<font color=\"red\">WARNING:</font> No help message found, " +
                "找不到帮助信息，可能帮助信息丢失或系统尚未初始化, Help ID:" + helpId;
    }

    public String listenerForAjaxSupport(String[] args) {
        return listener(args[0]);
    }

    public String noticeInFirstDeploy(String helpId)
            throws FileNotFoundException,
            UnsupportedEncodingException,
            IOException {
        File f = new File("_res/help/noticeInFirstDeploy.html");
        FileInputStream fis = new FileInputStream(f);
        InputStreamReader isr = (new InputStreamReader(fis, "utf8"));
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        char[] buff = new char[2048];
        int len = 0;
        try {
            while ((len = br.read(buff)) != -1) {
                sb.append(buff, 0, len);
            }
        } finally {
            br.close();
        }
        return sb.toString();
    }
}
