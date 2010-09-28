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

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author huliqing
 */
public class MessageFactory {

//    public final static String RESOURCE = "resource";

    private MessageFactory() {
    } 

    /**
     * Get message by messageId
     * @param messageId
     * @param params
     * @return FacesMessage
     */
    public final static FacesMessage getMessage(String messageId,
            String... params) {
        Locale locale = null;
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null && context.getViewRoot() != null) {
            locale = context.getViewRoot().getLocale();
            if (locale == null) {
                locale = Locale.getDefault();
            }
        } else {
            locale = Locale.getDefault();
        }
        return getMessage(locale, messageId, params);
    }

    public final static FacesMessage getMessage(Locale locale,
            String messageId,
            String... params) {
        String summary = null;
        String detail = null;
        ResourceBundle bundle = null;
        String bundleName = getApplication().getMessageBundle();
        // see if we have a user-provided bundle
        if (null != bundleName) {
            bundle = ResourceBundle.getBundle(bundleName, locale, getCurrentLoader(bundleName));
            if (null != bundle) {
                try {
                    summary = bundle.getString(messageId);
                    detail = bundle.getString(messageId + "_detail");
                } catch (MissingResourceException e) {
                    // ignore
                }
            }
        }
        
        // we couldn't find a summary in the user-provided bundle
        if (null == summary) {
            bundle = ResourceBundle.getBundle(
                    FacesMessage.FACES_MESSAGES, locale, getCurrentLoader(bundleName));
            if (null == bundle)
                throw new NullPointerException();
            try {
                summary = bundle.getString(messageId);
                detail = bundle.getString(messageId + "_detail");
            } catch (MissingResourceException e) {
                // ignore
            }
        }
        
        // if summary not found already, see in "name.huliqing.qfaces.resource"
        if (summary == null) {
            try {
                bundle = ResourceBundle.getBundle(
                		Constant.RESOURCE, locale, getCurrentLoader(Constant.RESOURCE));
                summary = bundle.getString(messageId);
                detail = bundle.getString(messageId + "_detail");
            } catch (Exception e) {
            }
        }
        
        if (summary == null) {
            Logger.getLogger(MessageFactory.class.getName()).log(Level.WARNING, "Resource id not found:" + messageId);
            return null;
        }
        
        // At this point, we have a summary and a bundle.     
        summary = getFormattedString(locale, summary, params);
        FacesMessage message = new FacesMessage(summary, detail);
        message.setSeverity(FacesMessage.SEVERITY_ERROR);
        return (message);
    }
    
    private final static String getFormattedString(Locale locale, String mess, String[] params) {
        String results = null;
        if (params == null || mess == null)
            return mess;
        StringBuffer b = new StringBuffer(100);
        MessageFormat mf = new MessageFormat(mess);
        if (locale != null) {
            mf.setLocale(locale);
            b.append(mf.format(params));
            results = b.toString();
        }
        return results;
    }
    
    private final static Application getApplication() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context != null)
            return (FacesContext.getCurrentInstance().getApplication());
        ApplicationFactory afactory = (ApplicationFactory) FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
        return (afactory.getApplication());
    }

    private final static ClassLoader getCurrentLoader(Object fallbackClass) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null)
            loader = fallbackClass.getClass().getClassLoader();
        return loader;
    }

} 
