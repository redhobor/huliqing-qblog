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

package name.huliqing.qblog.backup.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import name.huliqing.qblog.backup.TypeConverter;

/**
 *
 * @author huliqing
 */
public class DateTimeConverter implements TypeConverter{

    private SimpleDateFormat sdf;

    public DateTimeConverter() {
        this("yyyy-MM-dd HH:mm:ss", null);
    }

    public DateTimeConverter(String format) {
        this(format, null);
    }

    public DateTimeConverter(String format, TimeZone timeZone) {
        if (format != null) {
            try {
                this.sdf = new SimpleDateFormat(format);
            } catch (IllegalArgumentException e) {
                this.sdf = new SimpleDateFormat();
            }
        } else {
            this.sdf = new SimpleDateFormat();
        }
        if (timeZone != null) {
            this.sdf.setTimeZone(timeZone);
        }
    }

    public Object getAsObject(String strValue) {
        if (strValue == null || "".equals(strValue))
            return null;
        Date result = null;
        try {
            result = this.sdf.parse(strValue);
        } catch (ParseException ex) {
            // ignore
        }
        return result;
    }

    public String getAsString(Object objValue) {
        if (objValue == null)
            return "";
        if (objValue instanceof java.util.Date) {
            return this.sdf.format((Date) objValue);
        }
        return "";
    }

}
