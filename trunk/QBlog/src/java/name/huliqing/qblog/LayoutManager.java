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
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 管理Layout
 * @author huliqing
 */
public class LayoutManager {
    private final static Logger logger = Logger.getLogger(LayoutManager.class.getName());
    private final static LayoutManager ins = new LayoutManager();
    private LayoutManager() {}
    public final static LayoutManager getInstance() {
        return ins;
    }

    private static Map<String, Layout> layouts;

    /**
     * 是否存在指定名称的模版文件。
     * @param name
     * @return
     */
    public final boolean exists(String name) {
        if (layouts == null) {
            loadLayout();
        }
        return layouts.containsKey(name);
    }

    /**
     * 通过layout名称查找layout,layout名称在根目录下文件夹layout下面，如：<BR />
     * /layout/default/index.xhtml <BR />
     * /layout/book/index.xhtml <BR />
     * 其中 "default", "book" 为layout名称，这个方法通过给定的名称，偿试找出相关
     * 模版,如果目标模版不存在，则返回默认模版（default),如果default找不到，则抛出
     * 异常。正常应该不会出现异常，默认模版文件夹default应该一直存在，除非手动删除。
     * @param name
     * @return
     */
    public final Layout findLayout(String name) {
        if (layouts == null) {
            loadLayout();
        }
        if (layouts.containsKey(name)) {
            return layouts.get(name);
        } else {
            logger.warning("找不到模版：name=" + name + ",可能模版文件丢失,将偿试使用默认模版default.");
            if (layouts.containsKey("default")) {
                return layouts.get("default");
            } else {
                throw new RuntimeException("找不到默认模版文件。");
            }
        }
    }

    /**
     * 获取所有模版文件。
     * @return
     */
    public final Map<String, Layout> getAllLayout() {
        if (layouts == null) {
            loadLayout();
        }
        return layouts;
    }

    /**
     * 装载所有可用的layout
     */
    private void loadLayout() {
        layouts = new HashMap<String, Layout>();
        File dir = new File("layout");
        if (!dir.exists() || !dir.isDirectory()) {
            logger.severe("找不到模版文件夹\"layout\"");
            return;
        }
        if (!dir.canRead()) {
            logger.severe("无法读取模版文件夹，权限不足。");
            return;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File layoutDir : files) {
                if (checkLayout(layoutDir)) {
                    Layout layout = loadLayout(layoutDir);
                    layouts.put(layout.getName(), layout);
                }
            }
        }
    }

    private Layout loadLayout(File layoutDir) {
        Layout layout = new Layout(layoutDir.getName());
        return layout;
    }

    // 检查目标文件夹是否为正常可用的layout文件夹
    private boolean checkLayout(File dir) {
        if (dir == null)
            return false;
        if (!dir.isDirectory())
            return false;
        File layout = new File(dir, "index.xhtml");
        if (layout == null) {
            logger.warning("在目标文件夹" + dir.getName() + "中找不到模版文件:index.xhtml");
            return false;
        }
        if (!layout.isFile()) {
            logger.warning("目标非模版文件，file=" + layout.getName());
            return false;
        }
        return true;
    }

    public class Layout implements java.io.Serializable{
        // 模版文件名，同时也是文件夹名
        private String name;
        // 模版作者
        private String author;
        // 模版作者Email
        private String email;

        public Layout(String name) {
            this.name = name;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        /**
         * 返回子模板：articleList.html的信息,如果不存在articleList，则返回null
         * @return
         */
        public String getSubTemplateArticleList() {
            File file = new File("layout/" + getName() + "/articleList.html");
            if (file.exists() && file.isFile()) {
                try {
                    FileReader fr = new FileReader(file);
                    BufferedReader br = new BufferedReader(fr);
                    StringBuilder sb = new StringBuilder();
                    char[] buff = new char[1024];
                    int len;
                    while ((len = br.read(buff, 0, buff.length)) != -1) {
                        sb.append(buff, 0, len);
                    }
                    br.close();
                    return sb.toString();
                } catch (IOException ex) {
                    Logger.getLogger(LayoutManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return null;
        }
    }
}
