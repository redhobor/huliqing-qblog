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

package name.huliqing.qblog.component;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletResponse;
import name.huliqing.qblog.AjaxResponseWriter;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.service.ModuleSe;
import name.huliqing.qfaces.QFaces;
import name.huliqing.qfaces.ResLoader2.LoadType;
import name.huliqing.qfaces.component.Ajax;

/**
 * 该类用于动态Ajax方式生成Module
 * @author huliqing
 */
public class AjaxModule implements Ajax{
    private final static Logger logger = Logger.getLogger(AjaxModule.class.getName());
    
    public void ajax(Map<String, String> params, FacesContext fc) {
        Long moduleId = QFaces.convertToLong(params.get("moduleId"));
        HttpServletResponse response = (HttpServletResponse)
                fc.getExternalContext().getResponse();
        // 这里一定要设置编码，否则客户端将可能出现中文乱码
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Cache-Control", "no-cache");
        try {
            // 必须手动生成ResponseWriter, 在Ajax请求中，fc.getResponseWriter为Null
            Writer oldWriter = response.getWriter();
            ResponseWriter rw = new AjaxResponseWriter(oldWriter, "text/html", "utf-8");
            fc.setResponseWriter(rw);

            // 下面代码用于Fix bug: 在JSF1.2下，Ajax 渲染组件在ViewHandler的writeState
            // 阶段存在异常。
            
//            // 重要，在这里装饰ViewHandler,在writerState方法中归还ViewHandler
//            // 这个装饰只是作为Ajax生成组件而用。不要在正常的请求页面中也替换它
//            ViewHandler oldHandler = fc.getApplication().getViewHandler();
//            if (oldHandler instanceof name.huliqing.qblog.AjaxViewHandler) {
//                // ignore,不要再进行装饰，否则会造成无限嵌套。
//            } else {
//                AjaxViewHandler newHandler = new AjaxViewHandler(oldHandler);
//                fc.getApplication().setViewHandler(newHandler);
//            }
 
            // ---- 渲染组件

            // Find module data
            ModuleEn me = ModuleSe.find(moduleId);

            // Generate module UI
            UIModule uim = new UIModule();
            // Binding module data 
            uim.getAttributes().put("module", me);
            // 在Ajax生成组件时需要用到这个参数，这个参数只对QFaces组件有效
            uim.getAttributes().put("loadType", LoadType.ENERGY.name());
            // Note to load data.
            uim.setReload(Boolean.TRUE);
            // Render all
            uim.encodeAll(fc);

        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
