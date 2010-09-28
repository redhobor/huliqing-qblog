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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

@ManagedBean
@SessionScoped
public class Language implements java.io.Serializable {
    private static final long serialVersionUID = -4321119929397676240L;

    private String locale;
    private List<SelectItem> localeList;

    public Language() {
    }

    /**
     * @param locale 
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * @return String
     */
    public String getLocale() {
        // 设置语言,如果url中存在参数
        String language = QBlog.getParam("language");
        if (language != null) {
            UIViewRoot view = FacesContext.getCurrentInstance().getViewRoot();
            view.setLocale(new Locale(language));
            this.locale = view.getLocale().toString();
        }
        if (locale == null) {
            locale = FacesContext.getCurrentInstance().
                    getViewRoot().getLocale().toString();
        }
        return locale;
    }

    /**
     * @return localeList
     */
    public List<SelectItem> getLocaleList() {
        // 获取本地支持的语言
        if (localeList != null) {
            return localeList;
        } else {
            localeList = new ArrayList<SelectItem>();
            Application app = FacesContext.getCurrentInstance().getApplication();
            Iterator<Locale> locales = app.getSupportedLocales();
            while (locales.hasNext()) {
                Locale _locale = locales.next();
                String displayName = Messenger.getDisplayString("language", "language." + _locale.toString(), null);
                SelectItem language = new SelectItem(_locale.toString(), displayName);
                localeList.add(language);
            }
            if (localeList.size() == 0) {
                Locale defaultLocale = app.getDefaultLocale();
                localeList.add(new SelectItem(defaultLocale.toString(), defaultLocale.getDisplayName()));
            }
        }
        return localeList;
    }
}
