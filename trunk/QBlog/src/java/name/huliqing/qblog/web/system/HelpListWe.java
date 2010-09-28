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
import java.io.UnsupportedEncodingException;
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
import javax.xml.transform.TransformerException;
import name.huliqing.common.XmlUtils;
import name.huliqing.qblog.Messenger;
import name.huliqing.qblog.backup.Backup;
import name.huliqing.qblog.backup.Restore;
import name.huliqing.qblog.backup.ZipHelper;
import name.huliqing.qblog.backup.converter.TextConverter;
import name.huliqing.qblog.component.UIFileUpload;
import name.huliqing.qblog.entity.HelpEn;
import name.huliqing.qblog.service.HelpSe;
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
public class HelpListWe extends BaseWe{

    private boolean editable;
    private UIData uiHelps;

    public HelpListWe() {
        super();
        String temp = QFaces.getParam("editable");
        if (temp != null) {
            editable = true;
        }
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public UIData getUiHelps() {
        return uiHelps;
    }

    public void setUiHelps(UIData uiHelps) {
        this.uiHelps = uiHelps;
    }
    
    public PageModel<HelpEn> loadData(PageParam pp) {
        if (pp.getSortField() == null) {
            pp.setSortField("helpId");
            pp.setAsc(Boolean.TRUE);
        }
        return HelpSe.findAll(pp);
    }

    public void delete() {
        HelpEn he = (HelpEn) uiHelps.getRowData();
        HelpSe.delete(he.getHelpId());
        Messenger.sendInfo("Delete OK! Help ID=" + he.getHelpId());
    }

    public void updateAll() {
        List<HelpEn> hes = (List<HelpEn>) uiHelps.getValue();
        if (hes != null && !hes.isEmpty()) {
            for (HelpEn he : hes) {
                HelpSe.update(he);
            }
            Messenger.sendInfo("Update all OK.");
        }
    }

    public void export() throws ParserConfigurationException,
            IllegalArgumentException,
            IllegalAccessException,
            IOException,
            UnsupportedEncodingException,
            TransformerException {
        List<HelpEn> hs = HelpSe.findAll();
        if (hs == null || hs.isEmpty()) {
            Messenger.sendError("No HelpEn found!");
            return;
        }
        Document doc = XmlUtils.newDocument();
        Backup backup = new Backup(doc);
        backup.setDate(new Date());
        backup.setVersion("QBlog1.0");
        backup.addConverter(Text.class, new TextConverter());
        backup.encode(hs);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String filename = "qblog-bak-helper-" + sdf.format(new Date());
        ZipHelper.export(filename, doc);
        QFaces.getFacesContext().responseComplete();
    }

    // ---- Import

    private UIFileUpload uiFileUpload;

    public UIFileUpload getUiFileUpload() {
        return uiFileUpload;
    }

    public void setUiFileUpload(UIFileUpload uiFileUpload) {
        this.uiFileUpload = uiFileUpload;
    }

    public void importHelp() {
        try {
            QFile file = this.uiFileUpload.getFile();
            if (file == null) {
                return;
            }
            Document doc = ZipHelper.importAsDoc(file.getBytes());
            Restore res = new Restore(doc);
            res.addConverter(Text.class, new TextConverter());
            List<HelpEn> hes = (List<HelpEn>) res.restore(HelpEn.class);
            if (hes != null) {
                for (HelpEn he : hes) {
                    HelpSe.delete(he.getHelpId());
                    HelpSe.save(he);
                }
                Messenger.sendInfo("Import helps OK.");
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(HelpListWe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(HelpListWe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(HelpListWe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(HelpListWe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(HelpListWe.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(HelpListWe.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
