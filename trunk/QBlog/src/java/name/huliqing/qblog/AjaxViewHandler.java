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

import java.io.IOException;
import java.util.Locale;
import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

/**
 *
 * @author huliqing
 */
public class AjaxViewHandler extends ViewHandler{

    private ViewHandler oriHandler;

    public AjaxViewHandler(ViewHandler oriHandler) {
        this.oriHandler = oriHandler;
    }

    @Override
    public Locale calculateLocale(FacesContext fc) {
        return oriHandler.calculateLocale(fc);
    }

    @Override
    public String calculateRenderKitId(FacesContext fc) {
        return oriHandler.calculateRenderKitId(fc);
    }

    @Override
    public UIViewRoot createView(FacesContext fc, String viewId) {
        return oriHandler.createView(fc, viewId);
    }

    @Override
    public String getActionURL(FacesContext fc, String viewId) {
        return oriHandler.getActionURL(fc, viewId);
    }

    @Override
    public String getResourceURL(FacesContext fc, String path) {
        return oriHandler.getResourceURL(fc, path);
    }

    @Override
    public void renderView(FacesContext fc, UIViewRoot viewToRender)
            throws IOException, FacesException {
        oriHandler.renderView(fc, viewToRender);
    }

    @Override
    public UIViewRoot restoreView(FacesContext fc, String viewId) {
        return oriHandler.restoreView(fc, viewId);
    }

    @Override
    public void writeState(FacesContext fc) throws IOException {
        // 这个替换在 UIAjaxModule中进行，请参考相关代码。

        // 尝试写状态信息，如果在Ajax中，这可能发生Exception.所以进行
        // catch, 在处理完成之后交还ViewHandler.
        try {
            oriHandler.writeState(fc);
        } catch (Exception e) {
            // ignore, 这可能发生在Ajax状态写入
        }

        // 2.交还ViewHandler,必要的，因为View为单例，如果不交还ViewHandler,
        // 那么后来的正常请求页面时（非Ajax请求）,将会永远超过状态输出。
        fc.getApplication().setViewHandler(oriHandler);
    }

}
