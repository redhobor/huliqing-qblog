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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.xml.parsers.ParserConfigurationException;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.backup.Restore;
import name.huliqing.qblog.backup.ZipHelper;
import name.huliqing.qblog.backup.converter.ArticleSecurityConverter;
import name.huliqing.qblog.backup.converter.GroupConverter;
import name.huliqing.qblog.backup.converter.TextConverter;
import name.huliqing.qblog.entity.ConfigEn;
import name.huliqing.qblog.entity.FolderEn;
import name.huliqing.qblog.entity.HelpEn;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.entity.PageEn;
import name.huliqing.qblog.entity.PageModuleEn;
import name.huliqing.qblog.enums.ArticleSecurity;
import name.huliqing.qblog.enums.Group;
import name.huliqing.qblog.enums.Security;
import name.huliqing.qblog.service.FolderSe;
import name.huliqing.qblog.service.HelpSe;
import name.huliqing.qblog.service.ModuleSe;
import name.huliqing.qblog.service.PageModuleSe;
import name.huliqing.qblog.service.PageSe;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author huliqing
 */
@ManagedBean
@RequestScoped
public class StartWe {
    private final static Logger logger = Logger.getLogger(StartWe.class.getName());

    /**
     * 初始化QBlog系统
     */
    public void initializeQBlog() {
        
        String bakHelperPath = QBlog.getRealPath() + "/bak/helper.zip";
        String bakConfigPath = QBlog.getRealPath() + "/bak/config.zip";
        boolean error = false;

        // Import helper
        File helper = new File(bakHelperPath);
        if (checkFile(helper, "helper.zip")) {
            try {
                Document helpDoc = convertAsDoc(helper);
                restoreHelperDoc(helpDoc);
            } catch (Exception e) {
                error = true;
                logger.log(Level.SEVERE, "helper.zip信息时遇到错误！", e);
                Messenger.sendError("helper.zip信息时遇到错误！");
            }
        }

        // Import config
        File config = new File(bakConfigPath);
        if (checkFile(config, "config.zip")) {
            try {
                Document configDoc = convertAsDoc(config);
                restoreConfigDoc(configDoc);
            } catch (Exception e) {
                error = true;
                logger.log(Level.SEVERE, "导入config.zip配置时遇到错误！", e);
                Messenger.sendError("导入config.zip配置时遇到错误！");
            }
        }

        if (error) {
            Messenger.sendError("导入过程中遇到错误，请稍后重试。或者查看“首次运行" +
                    "必看”，如果您仍然遇到该问题，请Email给我:" +
                    "huliqing.cn@gmail.com");
        } else {
            Messenger.sendInfo("初始化完整成功,您现在可以开始使用QBlog,如果您" +
                    "在使用过程中遇到任何问题，或者有任何建议，可以随时发Email" +
                    "给我: huliqing.cn@gmail.com");
        }
    }

    private boolean checkFile(File file, String filename) {
        if (!file.exists()) {
            logger.severe("找不到文件:" + filename);
            Messenger.sendError("找不到文件:" + filename + ",无法进行初始化，" +
                    "请检查您的QBlog中是否缺少文件.bak/" + filename);
            return false;
        }
        if (!file.canRead()) {
            logger.severe("不能读取文件:" + filename);
            Messenger.sendError("不能读取文件:" + filename + ",无法进行初始化，" +
                    "这可能是因权限问题引起的。");
            return false;
        }
        return true;
    }

    private Document convertAsDoc(File file) throws
            FileNotFoundException,
            IOException,
            SAXException,
            ParserConfigurationException {
        FileInputStream fis = null;
        ByteArrayOutputStream arr = new ByteArrayOutputStream(2048);
        try {
            fis = new FileInputStream(file);
            byte[] buff = new byte[2048];
            int len;
            while ((len = fis.read(buff, 0, buff.length)) != -1) {
                arr.write(buff, 0, len);
            }
            Document doc = ZipHelper.importAsDoc(arr.toByteArray());
            arr.close();
            return doc;
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(StartWe.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void restoreHelperDoc(Document doc) throws
            IllegalArgumentException,
            IllegalAccessException,
            InstantiationException {

        Restore res = new Restore(doc);
        res.addConverter(Text.class, new TextConverter());
        List<HelpEn> hes = (List<HelpEn>) res.restore(HelpEn.class);
        if (hes != null) {
            for (HelpEn he : hes) {
                HelpSe.delete(he.getHelpId());
                HelpSe.save(he);
            }
            Messenger.sendInfo("初始化帮助信息成功！");
        }

    }

    private void restoreConfigDoc(Document doc) throws ParserConfigurationException,
            SAXException, IOException,
            IllegalArgumentException,
            IllegalAccessException,
            InstantiationException {

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
            Messenger.sendInfo("初始化“系统参数”成功。");
        }

        // Restore pages
        List<PageEn> pes = (List<PageEn>) restore.restore(PageEn.class);
        if (pes != null && !pes.isEmpty()) {
            // 删除旧Page
            PageSe.deleteAll();
            Messenger.sendInfo("删除旧的Page, OK");

            // 导入默认Page
            for (PageEn pe : pes) {
                PageSe.save(pe);
                Messenger.sendInfo("导入页面：" + pe.getName());
            }
            Messenger.sendInfo("页面导入完成。");
        }

        // Restore modules
        List<ModuleEn> mes = (List<ModuleEn>) restore.restore(ModuleEn.class);
        if (mes != null && !mes.isEmpty()) {
            // 删除旧Module
            ModuleSe.deleteAll();
            Messenger.sendInfo("删除旧的Module, OK");

            // 导入默认Module
            for (ModuleEn me : mes) {
                ModuleSe._import(me);
                Messenger.sendInfo("导入模块：" + me.getName());
            }
            Messenger.sendInfo("模块导入完成。");
        }

        // Restore Page and modules
        List<PageModuleEn> pmes = (List<PageModuleEn>) restore.restore(PageModuleEn.class);
        if (pmes != null && !pmes.isEmpty()) {
            // 删除配置
            PageModuleSe.deleteAll();

            // 导入配置
            PageModuleSe.importAll(pmes);

            for (PageModuleEn pme : pmes) {
                Messenger.sendInfo("导入页面及模块配置，Page Id=" + pme.getPageId()
                        + ", Group=" + pme.getModuleGroup().name()
                        + ", Module Id=" + pme.getModuleId()
                        + ", Sort=" + pme.getSort());
            }
            Messenger.sendInfo("导入“页面”与“模块”配置信息完成。");
        }

        // 偿试创建一个默认的相册
        FolderEn album = FolderSe.find(1L);
        if (album == null) {
            Messenger.sendInfo("找不到默认相册，偿试创建...");
            album = new FolderEn();
            album.setFolderId(1L);
            album.setName("默认相册");
            album.setCover("/_res/image/album-cover2.jpg");
            album.setSecurity(Security.PUBLIC);
            if (FolderSe.save(album)) {
                Messenger.sendInfo("创建默认相册成功，相册：" + album.getName());
            } else {
                Messenger.sendError("创建相册失败!");
            }
        }
    }
}
