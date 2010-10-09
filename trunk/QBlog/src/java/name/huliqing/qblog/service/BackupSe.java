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

package name.huliqing.qblog.service;

import com.google.appengine.api.datastore.Text;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import name.huliqing.common.XmlUtils;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.backup.Restore;
import name.huliqing.qblog.backup.converter.ArticleSecurityConverter;
import name.huliqing.qblog.backup.converter.GroupConverter;
import name.huliqing.qblog.backup.converter.TextConverter;
import name.huliqing.qblog.daocache.BackupCache;
import name.huliqing.qblog.entity.BackupEn;
import name.huliqing.qblog.entity.ConfigEn;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.entity.PageEn;
import name.huliqing.qblog.entity.PageModuleEn;
import name.huliqing.qblog.enums.ArticleSecurity;
import name.huliqing.qblog.enums.Group;
import name.huliqing.qfaces.model.PageModel;
import name.huliqing.qfaces.model.PageParam;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author huliqing
 */
public class BackupSe {

    public final static Boolean save(BackupEn backup) {
        return BackupCache.getInstance().save(backup);
    }

    public final static Boolean delete(BackupEn backup) {
        return BackupCache.getInstance().delete(backup.getName());
    }

    public final static Boolean update(BackupEn backup) {
        return BackupCache.getInstance().update(backup);
    }

    public final static BackupEn find(String name) {
        return BackupCache.getInstance().find(name);
    }

    public final static Boolean isExistsBackupName(String name) {
        return (BackupCache.getInstance().find(name) != null);
    }

    public final static List<BackupEn> findAll() {
        return BackupCache.getInstance().findAll("createDate", Boolean.FALSE, null, null);
    }

    public final static PageModel<BackupEn> findAll(PageParam pp) {
        PageModel<BackupEn> pm = new PageModel<BackupEn>();
        pm.setPageData(BackupCache.getInstance().findAll(pp.getSortField(), pp.getAsc(), pp.getStart(), pp.getPageSize()));
        pm.setTotal(BackupCache.getInstance().countAll());
        return pm;
    }

    public final static void restore(BackupEn backup) {
        try {
            String xmlValue = backup.getBackupData().getValue();
            restore(XmlUtils.newDocument(xmlValue));
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(BackupSe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(BackupSe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BackupSe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final static void restore(Document doc) {
        // 未登录，一定不能被操作
        if (!QBlog.getCurrentVisitor().isLogin()) {
            Logger.getLogger(BackupSe.class.getName()).warning("试图在未登录状态" +
                    "进行切换备份设置, remote=" + QBlog.getCurrentVisitor().getRemoteAddr());
            return;
        }
        try {
            Restore restore = new Restore(doc);
            restore.addConverter(Group.class, new GroupConverter());
            restore.addConverter(ArticleSecurity.class, new ArticleSecurityConverter());
            restore.addConverter(Text.class, new TextConverter());
            // restore config
            List<ConfigEn> ces = (List<ConfigEn>) restore.restore(ConfigEn.class);
            if (ces != null && !ces.isEmpty()) {
                for (ConfigEn ce : ces) {
                    ConfigManager.getInstance().saveOrUpdate(ce);
                }
            }
            List<PageEn> pes = (List<PageEn>) restore.restore(PageEn.class);
            if (pes != null && !pes.isEmpty()) {
                // 删除旧的Pages
                PageSe.deleteAll();
                Messenger.sendInfo("删除旧的Page, OK");
                // 导入默认Page
                for (PageEn pe : pes) {
                    PageSe.save(pe);
                }
                Messenger.sendInfo("恢复页面成功。");
            }
            List<ModuleEn> mes = (List<ModuleEn>) restore.restore(ModuleEn.class);
            if (mes != null && !mes.isEmpty()) {
                // 删除旧Module
                ModuleSe.deleteAll();
                Messenger.sendInfo("删除旧的Module, OK");
                // 导入默认Module
                for (ModuleEn me : mes) {
                    ModuleSe._import(me);
                }
                Messenger.sendInfo("恢复模块成功。");
            }
            List<PageModuleEn> pmes = (List<PageModuleEn>) restore.restore(PageModuleEn.class);
            if (pmes != null && !pmes.isEmpty()) {
                // 删除旧的Page及Module配置信息
                PageModuleSe.deleteAll();
                // 导入配置
                PageModuleSe.importAll(pmes);
                Messenger.sendInfo("恢复页面与模块配置成功。");
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(BackupSe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(BackupSe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(BackupSe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
