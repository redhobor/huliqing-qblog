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

package name.huliqing.qblog.enums;

import java.util.ArrayList;
import java.util.List;
import javax.faces.model.SelectItem;

public enum ArticleSecurity {

    /**
     * 草稿，不会出现在文章列表中。
     * 不会被订阅到，不会被访客查看到。
     */
    DRAFT,

    /**
     * 一般的文章类型，公开的，可订阅的。
     */
    PUBLIC,

    /**
     * 隐私的，
     * 不会公开的，只有自己及管理员才能
     * 查看及编辑。
     */
    PRIVATE;

    public static List<SelectItem> generateItems() {
        List<SelectItem> items = new ArrayList<SelectItem>(3);
        items.add(new SelectItem(DRAFT.name(), "草稿"));
        items.add(new SelectItem(PUBLIC.name(), "公开的"));
        items.add(new SelectItem(PRIVATE.name(), "隐私的"));
        return items;
    }

    public final static ArticleSecurity parse(String name) {
        if (name == null)
            return null;
        ArticleSecurity[] as = values();
        for (ArticleSecurity s : as) {
            if (s.name().equals(name)) {
                return s;
            }
        }
        return null;
    }

}
