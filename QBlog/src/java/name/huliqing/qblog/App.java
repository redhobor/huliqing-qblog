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

import java.util.logging.Logger;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import name.huliqing.qblog.entity.ConfigEn;
import name.huliqing.qblog.enums.Config;

/**
 * Application Bean,这个bean放在全局范围
 * @author huliqing
 */
@ManagedBean
@ApplicationScoped
public class App implements java.io.Serializable {
    private final static Logger logger = Logger.getLogger(App.class.getName());

    /**
     * 获取系统的ICO图标地址。
     * @return
     */
    public String getIco() {
        ConfigEn ce = ConfigManager.getInstance().findConfig(Config.CON_SYSTEM_ICO_ADDRESS);
        return ce != null ? ce.getValue() : "";
    }

    /**
     * 获取系统的日期时间格式
     * @return
     */
    public String getDateTimeFormat() {
        ConfigEn ce = ConfigManager.getInstance().findConfig(Config.CON_SYSTEM_DATE_FORMAT);
        return ce.getValue();
    }

    /**
     * 获取系统的时区
     * @return
     */
    public String getTimeZone() {
        ConfigEn ce = ConfigManager.getInstance().findConfig(Config.CON_SYSTEM_TIME_ZONE);
        return ce.getValue();
    }

    /**
     * 系统版本
     * @return
     */
    public String getVersion() {
        return "0.93";
    }

    /**
     * 是否为demo版本
     * @return
     */
    public boolean isDemo() {
        return false;
    }

    /**
     * 获取得当页面的URL,这个URL包含可能存在的Query参数, 这个方法不对query参数作
     * encode<br />
     * Notice:<br />
     * 1.直接在xhtml页面中写 <f:param /> 标签时不应该encode query参数
     * 否则返回值会错误，而不能正确转到原始页面
     * 2.部分参数可能躲在post里(form),比如通过scroller翻页后，可能丢失部分来自
     * URL中的参数，遇到这种情况应该手动分析并处理
     * @return
     */
    public String getCurrentURL() {
        String url = QBlog.getOriginalURI(true, false);
        return url;
    }
}
