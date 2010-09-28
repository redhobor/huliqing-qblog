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

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public final class Messenger {
	
    public static void sendInfo(String id, Object... params) {
        addMessage(FacesMessage.SEVERITY_INFO, id, params);
    }
    public static void sendWarn(String id, Object... params) {
        addMessage(FacesMessage.SEVERITY_WARN, id, params);
    }
    public static void sendError(String id, Object... params) {
        addMessage(FacesMessage.SEVERITY_ERROR, id, params);
    } 
    public static void sendFatal(String id, Object... params) {
        addMessage(FacesMessage.SEVERITY_FATAL, id, params);
    }
    
    // ---- private

    private static void addMessage(Severity type, String id, Object[] params) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        String detail = getDisplayString(Constant.RESOURCE, id, params);
        FacesMessage message = null;
        message = new FacesMessage(type, null, detail);
        facesContext.addMessage(null, message);
    }

    /**
     * 通过给定的id获取资源文件的信息（使用默认的bundleName:"resource"）
     * @param id
     * @return
     */
    public static String getDisplayString(String id) {
    	return getDisplayString(null, id, null);
    }
    
    /**
     * 通过给定的资源文件名称，id，及参数获取资源文件的信，
     * 如果bundleName为null,则使用默认的资源文件名称："resource";
     * 如果没有参数，则params设为null.
     * @param bundleName
     * @param id
     * @param params
     * @return
     */
    public static String getDisplayString(String bundleName,
                    String id,
                    Object[] params) {
        String text = null;
        if (bundleName == null)
        	bundleName = "resource";
        
        ResourceBundle bundle;
        try {
            bundle = ResourceBundle.getBundle(bundleName, getLocale(),
                            getCurrentClassLoader(params));
        } catch (Exception e) {
            return id;
        }
        try {
            text = bundle.getString(id);
        } catch (MissingResourceException e) {
            text = "[" + id + "]";
        }
        if (params != null) {
            MessageFormat mf = new MessageFormat(text, getLocale());
            text = mf.format(params, new StringBuffer(), null).toString();
        }
        return text;
    }

    private static Locale getLocale() {
        return FacesContext.getCurrentInstance().getViewRoot().getLocale();
    }

    private static ClassLoader getCurrentClassLoader(Object defaultObject) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = defaultObject.getClass().getClassLoader();
        }
        return loader;
    }
}
