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

package name.huliqing.qblog.entity;

import com.google.appengine.api.datastore.Text;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import name.huliqing.qblog.backup.BakFlag;
import org.datanucleus.jpa.annotations.Extension;

/**
 *
 * @author huliqing
 */
@Entity
public class ModuleEn implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    
    // Module 名称,唯一标识
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @SearchField
    @BakFlag
    private Long moduleId;

    // Module 名称，显示于页面
    @SearchField
    @BakFlag
    private String name;
 
    // 是否显示模块名称
    @SearchField
    @BakFlag
    private Boolean displayName;

    // Module的描述说明
    @SearchField
    @BakFlag
    private String des;

    // Module processor,该名称与 ModuleProcess.getName对应
    @SearchField
    @BakFlag
    private String processor;

    // 包含该Module的所有参数,以xml形式表示
    @Basic(fetch = FetchType.EAGER)
    @Enumerated
    @SearchField
    @BakFlag
    private Text attributes;

    // Module 是否为起用的,如果不起用，则不应该在页面中显示
    @SearchField
    @BakFlag
    private Boolean enabled;

    // 是否自动使用来自模版文件的样式定义module title,及module content
    @SearchField
    @BakFlag
    private Boolean autoStyle;

    // 创建时间
    @Extension(vendorName = "datanucleus", key = "is-second-class", value = "false")
    @SearchField
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date createDate;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Text getAttributes() {
        return attributes;
    }

    public void setAttributes(Text attributes) {
        this.attributes = attributes;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getDisplayName() {
        return displayName;
    }

    public void setDisplayName(Boolean displayName) {
        this.displayName = displayName;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public Boolean getAutoStyle() {
        return autoStyle;
    }

    public void setAutoStyle(Boolean autoStyle) {
        this.autoStyle = autoStyle;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    
}
