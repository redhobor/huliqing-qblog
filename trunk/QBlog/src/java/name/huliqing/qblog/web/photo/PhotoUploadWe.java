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

package name.huliqing.qblog.web.photo;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.ZipUtils;
import name.huliqing.qblog.component.UIFileUpload;
import name.huliqing.qblog.entity.PhotoEn;
import name.huliqing.qblog.entity.FolderEn;
import name.huliqing.qblog.service.PhotoSe;
import name.huliqing.qblog.service.FolderSe;
import name.huliqing.qblog.upload.QFile;
import name.huliqing.qblog.web.BaseWe;
import name.huliqing.qfaces.QFaces;

/**
 *
 * @author huliqing
 */
@ManagedBean
@RequestScoped
public class PhotoUploadWe extends BaseWe{

    // 所有可选的文件夹
    private List<SelectItem> folders;

    // 上传组件
    private UIFileUpload uiFileUpload;

    // 上传到的目标文件夹
    private Long folderId;

    // 图片备注
    private String des;

    // 是否对上传文件进行压缩
    private boolean pack;

    public PhotoUploadWe() {
        super();
        Long tempFolderId = QFaces.convertToLong(QBlog.getParam("folderId"));
        if (tempFolderId != null) {
            folderId = tempFolderId;
        }
    }

    public Long getFolderId() {
        return folderId;
    }

    public void setFolderId(Long folderId) {
        this.folderId = folderId;
    }

    public List<SelectItem> getFolders() {
        if (folders == null) {
            List<FolderEn> fs = FolderSe.findAll();
            folders = new ArrayList<SelectItem>(fs.size());
            if (fs != null && !fs.isEmpty()) {
                for (FolderEn f : fs) {
                    folders.add(new SelectItem(f.getFolderId(), f.getName()));
                }
            }
        }
        return folders;
    }

    public void setFolders(List<SelectItem> folders) {
        this.folders = folders;
    }

    public UIFileUpload getUiFileUpload() {
        return uiFileUpload;
    }

    public void setUiFileUpload(UIFileUpload uiFileUpload) {
        this.uiFileUpload = uiFileUpload;
    }

    public boolean isPack() {
        return pack;
    }

    public void setPack(boolean pack) {
        this.pack = pack;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    // ---- Action

    public String upload() throws IOException {
        QFile qf = this.uiFileUpload.getFile();
        if (qf == null || qf.getSize() <= 0) {
            Messenger.sendError("没有上传任何数据!");
            return null;
        }
        if (qf.getSize() > (1024 * 1000)) {
            Messenger.sendError("上传文件不能大于1M");
            return null;
        }
        if (folderId == null) {
            Messenger.sendError("必须选择一个上传相册");
            return null;
        }
        PhotoEn file = new PhotoEn();
        file.setFolder(folderId);
        file.setDes(des);
        file.setCreateDate(new Date());
        file.setName(qf.getFilename());
        file.setSuffix(qf.getSuffix());
        file.setContentType(qf.getContentType());
        if (pack) {
            file.setPack(true);
            file.setBytes(ZipUtils.pack(qf.getBytes()));
        } else {
            file.setPack(false);
            file.setBytes(qf.getBytes());
        }
        file.setFileSize(Integer.valueOf(file.getBytes().length).longValue());

        // 对非gif的图片文件进行缩略图处理，因为gif的缩略处理效果太差，所以只处理其它
        // 图片.
        if (!"gif".equalsIgnoreCase(file.getSuffix())) {
            ImagesService imagesService = ImagesServiceFactory.getImagesService();
            Image oldImage = ImagesServiceFactory.makeImage(file.getUnpackData());
            int width = oldImage.getWidth();
            int height = oldImage.getHeight();
            if (width > 120 || height > 100) {
                Transform resize = ImagesServiceFactory.makeResize(120, 100);
                Image newImage = imagesService.applyTransform(resize, oldImage);
                byte[] bytesMin = newImage.getImageData();
                int total = file.getBytes().length + bytesMin.length;
                if (total >= (1024 * 1000)) {
                    Messenger.sendError(
                            "图片在产生缩略图的过程中遇到错误，图片的总大小（原图+缩略图）可能超过限制." +
                            "请尽量上传小于1M多一些的图片,当前图片大小（原图+缩略图）:" + (total / 1024) + " K");
                    return null;
                }
                file.setBytesMin(bytesMin);
            }
        }

        if (PhotoSe.save(file)) {
            Messenger.sendInfo("上传成功:" + file.getName());
            des = null;
        }
        return null; 
    }

}
