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
import java.util.List;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.PhotoEn;
import name.huliqing.qfaces.QFaces;

/**
 *
 * @author huliqing
 */
public class UIPhotoDataTable extends HtmlDataTable implements java.io.Serializable{

    @Override
    public void encodeBegin(FacesContext context) throws IOException {}

    @Override
    public void encodeEnd(FacesContext context) throws IOException {}

    @Override
    public void encodeChildren(FacesContext fc) throws IOException {
        List<PhotoEn> pes = (List<PhotoEn>) this.getValue();
        Integer columns = QFaces.convertToInteger(this.getAttributes().get("columns"));
        if (columns == null || columns <= 0)
            columns = 1;

        if (pes != null && !pes.isEmpty()) {
            ResponseWriter rw = fc.getResponseWriter();
            String returnURL = QBlog.getOriginalURI(true, true);
            // 这里不能丢失folderId, 否则在返回时就不知道要显示哪一个相册中的图片
            if (!returnURL.contains("folderId")) {
                returnURL += "%26folderId=" + pes.get(0).getFolder();
            }

            rw.startElement("table", this);
            rw.writeAttribute("border", "0", null);
            rw.writeAttribute("width", "100%", null);
            rw.writeAttribute("cellspacing", "3", null);
            rw.writeAttribute("cellpadding", "3", null);
            rw.writeAttribute("style", "text-align:center;", null);
           
            for (int i = 0; i < pes.size(); i++) {
                if (i % columns == 0) {
                    if (i != 0) {
                        rw.endElement("tr");
                    }
                    rw.startElement("tr", this);
                }
                rw.startElement("td", this);
                rw.writeAttribute("style", "vertical-align:middle;border-bottom:1px dotted gray;", null);
                encodePhoto(rw, pes.get(i), returnURL);
                rw.endElement("td");
            }
            rw.endElement("tr");
            rw.endElement("table");
        }
    }

    private void encodePhoto(ResponseWriter rw, PhotoEn photo, String returnURL) throws IOException {
        String url = "/photo/photoId=" + photo.getPhotoId();
        rw.startElement("div", this);
            rw.startElement("div", this);
            rw.writeAttribute("style", "height:80px;overflow:hidden;", null);
            rw.startElement("a", this);
            rw.writeAttribute("href", url, null);
            rw.writeAttribute("target", "_blank", null);
            rw.writeAttribute("title", "看大图", null);
            rw.startElement("img", this);
            rw.writeAttribute("style", "max-width:100px", null);
            rw.writeAttribute("src", url + ",min=true", null);
            rw.writeAttribute("alt", photo.getName(), null);
            rw.endElement("img");
            rw.endElement("a");
            rw.endElement("div");

            // name
            rw.startElement("div", this);
            rw.writeAttribute("style", "color:gray;height:20px;overflow:hidden;", null);
            rw.writeText(photo.getName() + "." + photo.getSuffix(), null);
            rw.endElement("div");

            // size
            rw.startElement("div", this);
            rw.writeAttribute("style", "color:gray;height:20px;font-size:0.8em", null);
            rw.writeText((photo.getFileSize() / 1024) + " K", null);
            rw.endElement("div");

            // edit
            rw.startElement("div", this);
                String editURL = "/admin/photo/photoEdit.faces?photoId=" + photo.getPhotoId() + "&returnURL=" + returnURL;
                String deleteURL = "/admin/photo/photoDelete.faces?photoId=" + photo.getPhotoId() + "&returnURL=" + returnURL;

                rw.startElement("a", this);
                rw.writeAttribute("style", "margin:0 5px;", null);
                rw.writeAttribute("href", editURL, null);
                rw.writeAttribute("target", "_self", null);
                rw.startElement("img", this);
                rw.writeAttribute("src", "/_res/image/button-edit.gif", null);
                rw.endElement("img");
                rw.endElement("a");

                rw.startElement("a", this);
                rw.writeAttribute("style", "margin:0 5px;", null);
                rw.writeAttribute("href", deleteURL, null);
                rw.writeAttribute("target", "_self", null);
                rw.writeAttribute("onclick", "return confirm('您真的要删除该图片吗？删除后不能恢复。(图片：" + photo.getName() + "." + photo.getSuffix() + ")')", null);
                rw.startElement("img", this);
                rw.writeAttribute("src", "/_res/image/button-delete.gif", null);
                rw.endElement("img");
                rw.endElement("a");
            rw.endElement("div");
            
        rw.endElement("div");
    }
}
