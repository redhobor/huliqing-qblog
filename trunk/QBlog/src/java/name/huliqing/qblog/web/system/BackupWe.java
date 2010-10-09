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

package name.huliqing.qblog.web.system;

import com.google.appengine.api.datastore.Text;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIData;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import name.huliqing.common.XmlUtils;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.backup.Backup;
import name.huliqing.qblog.backup.Restore;
import name.huliqing.qblog.backup.ZipHelper;
import name.huliqing.qblog.backup.converter.ArticleSecurityConverter;
import name.huliqing.qblog.backup.converter.GroupConverter;
import name.huliqing.qblog.backup.converter.TextConverter;
import name.huliqing.qblog.component.UIFileUpload;
import name.huliqing.qblog.entity.BackupEn;
import name.huliqing.qblog.entity.ConfigEn;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.entity.PageEn;
import name.huliqing.qblog.entity.PageModuleEn;
import name.huliqing.qblog.enums.ArticleSecurity;
import name.huliqing.qblog.enums.Group;
import name.huliqing.qblog.service.BackupSe;
import name.huliqing.qblog.service.ModuleSe;
import name.huliqing.qblog.service.PageModuleSe;
import name.huliqing.qblog.service.PageSe;
import name.huliqing.qblog.upload.QFile;
import name.huliqing.qblog.web.BaseWe;
import name.huliqing.qfaces.QFaces;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author huliqing
 */
@ManagedBean
@RequestScoped
public class BackupWe extends BaseWe {

    // 是否备份“系统参数”
    private boolean backupProperties = true;

    // 是否备份“Page”
    private boolean backupPages = true;

    // 是否备份“Modules”
    private boolean backupModules = true;

    private BackupEn backupEn;

    public BackupWe() {
        super();
    }

    public boolean isBackupModules() {
        return backupModules;
    }

    public void setBackupModules(boolean backupModules) {
        this.backupModules = backupModules;
    }

    public boolean isBackupPages() {
        return backupPages;
    }

    public void setBackupPages(boolean backupPages) {
        this.backupPages = backupPages;
    }

    public boolean isBackupProperties() {
        return backupProperties;
    } 

    public void setBackupProperties(boolean backupProperties) {
        this.backupProperties = backupProperties;
    }

    public BackupEn getBackupEn() {
        if (backupEn == null) {
            backupEn = new BackupEn();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmm");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            backupEn.setName("qblog-bak-config-" + sdf.format(new Date()));
        }
        return backupEn;
    }

    public void setBackupEn(BackupEn backupEn) {
        this.backupEn = backupEn;
    }

    // ---- Backup List

    private UIData uiBackupEns;

    public UIData getUiBackupEns() {
        return uiBackupEns;
    }

    public void setUiBackupEns(UIData uiBackupEns) {
        this.uiBackupEns = uiBackupEns;
    }

    public PageModel<BackupEn> loadData(PageParam pp) {
        if (pp.getSortField() == null) {
            pp.setSortField("createDate");
            pp.setAsc(Boolean.FALSE);
        }
        PageModel<BackupEn> pm = BackupSe.findAll(pp);
        return pm;
    }

    // ---- Delete, delete a backup entity

    public void delete() {
        BackupEn be = (BackupEn) uiBackupEns.getRowData();
        BackupSe.delete(be);
    }

    // ---- Backup

    public void backup() throws ParserConfigurationException,
            IllegalArgumentException,
            IllegalAccessException,
            IOException,
            TransformerConfigurationException,
            TransformerException {

        if (!backupProperties && !backupPages && !backupModules) {
            Messenger.sendError("您没有选中任何备份选项，请至少选择一个进行备份。");
            return;
        }

        Document doc = XmlUtils.newDocument();
        doc.setXmlVersion("1.0");
        doc.setXmlStandalone(true);
        doc.setStrictErrorChecking(false);

        Backup backup = new Backup(doc);
        backup.setDate(new Date());
        backup.setVersion("QBlog1.0");
        backup.addConverter(Group.class, new GroupConverter());
        backup.addConverter(ArticleSecurity.class, new ArticleSecurityConverter());
        backup.addConverter(Text.class, new TextConverter());

        // Backup Properties
        if (backupProperties) {
            List<ConfigEn> configs = ConfigManager.getInstance().findConfigs();
            backup.encode(configs);
        }

        // Backup Pages
        if (backupPages) {
            List<PageEn> pes = PageSe.findAll();
            backup.encode(pes);
        }

        // Backup Modules
        if (backupModules) {
            List<ModuleEn> mes = ModuleSe.findAll();
            backup.encode(mes);
        }

        // Backup Pages and Modules
        if (backupPages && backupModules) {
            List<PageModuleEn> pmes = PageModuleSe.exportAll();
            backup.encode(pmes);
        }

        // Save backup
        backupEn.setCreateDate(new Date());
        backupEn.setBackupData(new Text(XmlUtils.toXmlString(doc)));
        if (!BackupSe.isExistsBackupName(backupEn.getName())) {
            BackupSe.save(backupEn);
            Messenger.sendInfo("Backup OK! Backup Name = " + backupEn.getName());
            this.backupEn = null;
//            this.backupProperties = false;
//            this.backupPages = false;
//            this.backupModules = false;
        } else { 
            Messenger.sendError("该备份名称已经存在，请使用其它名称！");
            return;
        }
    }

    // ---- Export

    public void export() throws IOException {
        BackupEn be = (BackupEn) uiBackupEns.getRowData();
        export(be);
    }

    private void export(BackupEn backupEn) throws IOException {
        // Output
        byte[] bytes = backupEn.getBackupData().getValue().getBytes("UTF-8");
        ZipHelper.export(backupEn.getName(), bytes, "UTF-8");
        QFaces.getFacesContext().responseComplete();
    }

    // ---- Restore, restore from backup entity

    public void restore() {
        try {
            BackupEn be = (BackupEn) uiBackupEns.getRowData();
            if (be.getBackupData() == null || be.getBackupData().getValue() == null) {
                Messenger.sendWarn("没有找到任何可用于恢复的数据!");
                return;
            }
            String xmlValue = be.getBackupData().getValue();
            BackupSe.restore(XmlUtils.newDocument(xmlValue));
            Messenger.sendInfo("恢复完成！Backup Name:" + be.getName());
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BackupWe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(BackupWe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BackupWe.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }

    

    // ---- Import

    private UIFileUpload uiFileUpload;

    public UIFileUpload getUiFileUpload() {
        return uiFileUpload;
    }

    public void setUiFileUpload(UIFileUpload uiFileUpload) {
        this.uiFileUpload = uiFileUpload;
    }

    public void importBackup() {
        try {
            QFile file = this.uiFileUpload.getFile();
            Document doc = ZipHelper.importAsDoc(file.getBytes());
            BackupSe.restore(doc);
        } catch (IOException ex) {
            Logger.getLogger(BackupWe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(BackupWe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BackupWe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
