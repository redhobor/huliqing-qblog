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
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Logger;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import name.huliqing.qblog.entity.FolderEn;
import name.huliqing.qblog.entity.PhotoEn;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qblog.enums.Security;
import name.huliqing.qblog.service.FolderSe;
import name.huliqing.qblog.service.PhotoSe;
import name.huliqing.qfaces.QFaces;

/**
 *
 * @author huliqing
 */
public class PhotoManager {
    private final static Logger logger = Logger.getLogger(PhotoManager.class.getName());

    public final static void outputPhoto(ServletRequest request, ServletResponse response) throws IOException {
        HttpServletRequest hsr = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 直接发送304,图片只有bytes数据，在上传之后将一直不变
        // 图片数据只有创建，删除一说,没有更新。所以不用比较时间。
        // (修改图片名称，备注等不会影响这里)
        String ims = hsr.getHeader("If-Modified-Since");
        if (ims != null) {
            res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            res.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        Long photoId = QFaces.convertToLong(QBlog.getParam("photoId", request));
        String min = QBlog.getParam("min", request);

        PhotoEn photo = PhotoSe.find(photoId);
        if (photo == null) {
            logger.warning("datastore中找不到指定的图片，photoId=" + photoId);
            return;
        }
        FolderEn folder = FolderSe.find(photo.getFolder());
        if (folder == null) {
            logger.warning("datastore中找不到指定的文件夹,folderId=" + photo.getFolder());
            return;
        }
        // 在不正确update数据的时候，可能会出现这种情况
        if (photo.getBytes() == null) {
            logger.warning("数据丢失，指定的photo找不到bytes, photo id=" + photo.getPhotoId() + ", photo name=" + photo.getName());
            return;
        }

        Visitor visitor = (Visitor) hsr.getSession(true).getAttribute("visitor");
        if (visitor == null || !visitor.isLogin()) {
            // 完全隐私类型的相册，并且非管理者
            if (Security.PRIVATE == folder.getSecurity()) {
                logger.warning("试图在未登录情况下访问隐私文件夹,folder=" + folder.getName() + ",folderId=" + folder.getFolderId());
                return;
            }
            if (Security.PROTECTED == folder.getSecurity()) {
                String fetchCode = QBlog.getParam("fetchCode", request);
                if (folder.getFetchCode() == null || !folder.getFetchCode().equals(fetchCode)) {
                    logger.warning("错误的获取码，目标fetchCode=" + fetchCode + ", 必要的fetchCode=" + folder.getFetchCode());
                    return;
                }
            }
        }

        // 图片缓存时间
        Integer days = ConfigManager.getInstance().getAsInteger(Config.CON_PHOTO_CACHE_DAYS);
        if (days != null && days.intValue() <= 0) {
            days = null;
        }

        // output
        res.setContentType(photo.getContentType());
        if (days != null) {
            res.setHeader("Cache-Control", "max-age=" + (3600 * 24 * days.intValue()));
        } else {
            res.setHeader("Cache-Control", "no-cache");
        }

        // 添加Last-Modified头可以确保即使用户在刷新浏览器也不用重新发送图片数据。
        // 除非用户清除了客户端缓存。
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        res.setHeader("Last-Modified", sdf.format(photo.getCreateDate()));

        OutputStream os = res.getOutputStream();
        // 输出缩略图必须保证缩略图数据存在.
        os.write((min != null && photo.getBytesMin() != null) ? photo.getBytesMin() : photo.getUnpackData());
        os.close();
    }

}
