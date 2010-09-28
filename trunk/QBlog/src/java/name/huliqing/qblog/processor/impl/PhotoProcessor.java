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

package name.huliqing.qblog.processor.impl;

import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlCommandButton;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlInputText;
import javax.faces.component.html.HtmlOutputText;
import name.huliqing.qblog.QBlog;
import name.huliqing.qblog.entity.FolderEn;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.enums.Security;
import name.huliqing.qblog.processor.XmlProcessor2;
import name.huliqing.qblog.processor.attr.AttrInputText;
import name.huliqing.qblog.processor.attr.AttrSelectBooleanCheckbox;
import name.huliqing.qblog.processor.attr.AttrSelectOneRadio;
import name.huliqing.qblog.processor.attr.Attribute2;
import name.huliqing.qblog.service.FolderSe;
import name.huliqing.qfaces.QFaces;
import name.huliqing.qfaces.component.DynFrame;
import name.huliqing.qfaces.component.Frame;
import name.huliqing.qfaces.component.SaveState;
import name.huliqing.qfaces.component.Scroller;

/**
 *
 * @author huliqing
 */
public class PhotoProcessor extends XmlProcessor2{

    public List<Attribute2> getRequiredAttributes() {
        AttrInputText columns = new AttrInputText("Columns", "4", "每行显示多少列");

        AttrInputText pageSize = new AttrInputText("Page Size", "8", "每页显示多少张图片");

        AttrInputText displayPage = new AttrInputText("Display Page", "9", "最多的可见页码数,默认:3");

        AttrSelectBooleanCheckbox displayJump = new AttrSelectBooleanCheckbox("Display Jump", "false", "是否显示一个输入框用于输入页面进行翻页跳转.");

        AttrSelectOneRadio order = new AttrSelectOneRadio("Order", "createDate", "排序,按图片的上传时间或是按名称进行排序,默认：按时间");
        order.addItem("createDate", "按时间");
        order.addItem("name", "按名称");
        
        AttrSelectOneRadio sort = new AttrSelectOneRadio("Sort", "true", "正序还是倒序");
        sort.addItem("true", "正序");
        sort.addItem("false", "倒序");
        
        AttrSelectOneRadio face = new AttrSelectOneRadio("Face", "1", "翻页条的样式,可选择0/1,默认:2");
        face.addItem("0", "样式1");
        face.addItem("1", "样式2");
        
        AttrSelectOneRadio display = new AttrSelectOneRadio("Display", "bottom", "将翻页栏显示在哪个位置,默认：下面");
        display.addItem("top", "上面");
        display.addItem("bottom", "下面");
        display.addItem("both", "上下");
        
        AttrSelectOneRadio autoScroller = new AttrSelectOneRadio("Scroller", "true", "自动：只在必要时显示翻页栏。");
        autoScroller.addItem("true", "自动");
        autoScroller.addItem("false", "总是显示");
        
        AttrSelectBooleanCheckbox showInfo = new AttrSelectBooleanCheckbox("Show Info", "true", "是否显示图片数及页码总数");

        AttrSelectOneRadio position = new AttrSelectOneRadio("Position", "center", "翻页栏位置");
        position.addItem("left", "左对齐");
        position.addItem("center", "居中");
        position.addItem("right", "右对齐");

        AttrSelectBooleanCheckbox showName = new AttrSelectBooleanCheckbox("Show Name", "true", "显示图片名称");
        AttrSelectBooleanCheckbox showSize = new AttrSelectBooleanCheckbox("Show Size", "true", "显示图片的大小");
        AttrSelectBooleanCheckbox showEdit = new AttrSelectBooleanCheckbox("Show Edit", "true", "在有登录的情况下，允许显示编辑按钮.");
        AttrSelectBooleanCheckbox showLine = new AttrSelectBooleanCheckbox("Show Line", "false", "显示行与行之间的分隔线");

        AttrInputText defaultAlbum = new AttrInputText("Default Album", "",
                "默认的相册，设置一个默认的相册ID，这样在该模块未链接到任何一个相册时，可以默认显示这个相册的图片。" +
                "如果留空，则默认显示最近一个刚创建的“公开”的相册。");

        AttrSelectBooleanCheckbox always = new AttrSelectBooleanCheckbox("Always", "false", "固定显示相册，如果选中这个选项，那么这个图集将总是固" +
                "定显示Default Album中指定相册的图片。如果要这个图集动态链接其它相册则不应该选中这个选项。");

        AttrInputText tableWidth = new AttrInputText("Table Width", "100%", "总宽度, 在某些浏览器下可能需要使用这个参数来限制图集的宽度,只能填整数或百分比（不要加px）默认：100%");
        AttrInputText spacing = new AttrInputText("Spacing", "5", "行间距，每行图片之间的间距。");

        AttrInputText picMaxWidth = new AttrInputText("Pic Max Width", "100", "限制每张图片的最大宽度,只能填整数（不要加px）,默认:100");
        AttrInputText FCRequired = new AttrInputText("FC Required", "您正试图访问受保护的相册，这需要提供“获取码”才能访问.", "关于图片“获取码”的提示信息，对于PROTECTED状态的相册需要输入获取码才能浏览。");
        AttrInputText FCError = new AttrInputText("FC Error", "您输入的“获取码”错误,您可以尝试向博主索取获取码", "关于图片获取码的错误提示！");
        AttrInputText FCColor = new AttrInputText("FC Color", "red", "关于图片获取码提示信息的颜色，如：red,blue,gray,#000000,#C0C0C0等");
        AttrInputText FCPosition = new AttrInputText("FC Position", "500,200", "关于获取码弹出窗口的坐标位置,用半角逗号分格位置，如： “500,200” 表示x位置500,y位置200");
        FCRequired.setStyle("width:99%");
        FCError.setStyle("width:99%");

        List<Attribute2> as = new ArrayList<Attribute2>(24);
        as.add(defaultAlbum);
        as.add(always);
        as.add(columns);
        as.add(order);
        as.add(sort);
        as.add(pageSize);
        as.add(displayPage);
        as.add(displayJump);
        as.add(showInfo);
        as.add(display);
        as.add(face);
        as.add(autoScroller);
        as.add(position);
        as.add(showName);
        as.add(showSize);
        as.add(showEdit);
        as.add(showLine);
        as.add(spacing);
        as.add(tableWidth);
        as.add(picMaxWidth);
        as.add(FCRequired);
        as.add(FCError);
        as.add(FCColor);
        as.add(FCPosition);
        return as;
    }

    public UIComponent render(ModuleEn module) {
        AttrMap attr = getAttributes(module);

        // 必须为每个组件指定一个ID，否则可能在DataTable组件之内ID一直变动
        // 而被认为是一直需要重新初始化。并且同一个页面可能存在多个同样的组件，
        // 所以必须确保每次渲染时ID都能够唯一。（这里使用了currentTimeMillis)
        String suffix = String.valueOf(System.currentTimeMillis());
        String albumId = "photo" + suffix;

        // form
        HtmlForm form = new HtmlForm();
        form.setStyle("margin:0;padding:0");

        // Create Scroller
        Scroller s = new Scroller();
        s.setFor(albumId);
        s.setListenerAsExpression("#{photoFetch.loadData}");
        s.setPageSize(attr.getAsInteger("Page Size", 20));
        s.setDisplayPage(attr.getAsInteger("Display Page", 3));
        s.setDisplay(attr.getAsString("Display", "bottom"));
        s.setDisplayJump(attr.getAsBoolean("Display Jump", Boolean.FALSE));
        s.setFace(attr.getAsString("Face", "1"));
        s.setDisplayCount(attr.getAsBoolean("Show Info", Boolean.FALSE));
        s.setStrRecordTotal("图片:");
        s.setStrPageTotal("页数:");
        String position = attr.getAsString("Position", "center");
        if (position.equals("center")) {
            s.setStyle("margin:auto;");
        } else {
            s.setStyle("float:" + position);
        }

        // 参数
        Boolean always = attr.getAsBoolean("Always", Boolean.FALSE);
        String order = attr.getAsString("Order", "createDate");
        Boolean sort = attr.getAsBoolean("Sort", Boolean.TRUE);
        Long defaultAlbum = attr.getAsLong("Default Album", null);

        PhotoDataTable table = new PhotoDataTable();
        table.setId(albumId);
        table.setColumns(attr.getAsInteger("Columns", 4));
        table.setAutoScroller(attr.getAsBoolean("Scroller", Boolean.TRUE));
        table.setShowName(attr.getAsBoolean("Show Name", Boolean.TRUE));
        table.setShowSize(attr.getAsBoolean("Show Size", Boolean.TRUE));
        table.setShowEdit(attr.getAsBoolean("Show Edit", Boolean.FALSE));
        table.setShowLine(attr.getAsBoolean("Show Line", Boolean.FALSE));
        table.setWidth(attr.getAsString("Table Width", null));
        table.setCellspacing(attr.getAsString("Spacing", "5"));
        table.setPicMaxWidth(attr.getAsInteger("Pic Max Width", 100));
        table.setAlways(always);
        table.setSortField(order);
        table.setAsc(sort);
        table.setDefaultAlbum(defaultAlbum);

        // 一些必要状态保存的信息
        SaveState ssPageId = new SaveState();
        SaveState ssFolderId = new SaveState();
        ssPageId.setValueExpression("value", QFaces.createValueExpression("#{photoFetch.pageId}", Long.class));
        ssFolderId.setValueExpression("value", QFaces.createValueExpression("#{photoFetch.folderId}", Long.class));

        // 初始化值
        Long folderId = QFaces.convertToLong(QBlog.getParam("folderId"));
        PhotoFetch photoFetch = (PhotoFetch) QBlog.getBean("photoFetch");
        photoFetch.setPageId(QFaces.convertToLong(QBlog.getParam("pageId")));
        photoFetch.setFolderId(folderId);

        // 关于受保护的相册,
        // 1.只有动态图集，才需要去URL中获取folderId，
        // 2.只有动态图集，才需要在URL中渲染fetchCode参数
        // 3.只有动态图集，才需要弹出警告窗口.
        if (folderId != null && !always) {
            FolderEn fe = FolderSe.find(folderId);
            String fetchCode = QBlog.getParam("fetchCode");

            if (fe != null && fe.getSecurity() == Security.PROTECTED) {

                // 处于PROTECTED状态的部分相册需要fetchCode,如果没有提供fetchCode图片将不会渲染出来
                // 只有受保护的相册，才需要在url中添加fetchCode参数，其它类型相册不要添加这个参数，
                // 否则可能造成不能正确缓存的问题(fetchCode可能一直变动，而url会被认为是新的请求。)
                // 并且只有获取码提供正确时才应该在URL中渲染，这可尽量减少向PhotoManager中发送请求的偿试。
                if (fetchCode != null && fetchCode.equals(fe.getFetchCode())) {
                    table.setFetchCode(fetchCode);
                }

                // 使用一个弹出窗口来询问访问者的“获取码”，在窗口中提供获取码并提交。
                if (!QBlog.getCurrentVisitor().isLogin()) {
                    if (fetchCode == null || !fetchCode.equals(fe.getFetchCode())) {
                        // 这需要避免当存在多个图集时可能出现的弹出多个警告窗口的问题。
                        if (!photoFetch.isHasWarnForFetchCode()) {
                            photoFetch.setHasWarnForFetchCode(true);
                            DynFrame frame = new DynFrame();
                            HtmlOutputText mess = new HtmlOutputText();
                            HtmlInputText hit = new HtmlInputText();
                            HtmlCommandButton submit = new HtmlCommandButton();

                            mess.setEscape(false);
                            if (fetchCode == null) {
                                mess.setValue("<div style=\"color:" +
                                        attr.getAsString("FC Color", "red") + ";\" > " +
                                        attr.getAsString("FC Required", "") +"</div>");
                            } else if (!fetchCode.equals(fe.getFetchCode())) {
                                mess.setValue("<div style=\"color:" +
                                        attr.getAsString("FC Color", "red") + ";\" > " +
                                        attr.getAsString("FC Error", "") +"</div>");
                            }
                            // 弹出窗口位置
                            String[] posArr = attr.getAsString("FC Position", "500,200").split(",");
                            if (posArr == null || posArr.length < 2) {
                                posArr = new String[]{"500", "200"};
                            }
                            String frameId = "frame-" + System.currentTimeMillis();
                            frame.setFrame(new Frame());
                            frame.setShade(Boolean.TRUE);
                            frame.getFrame().setFrameId(frameId);
                            frame.setOnOpen("Q.U.move(Q.get('" + frameId + "')," + posArr[0] + "," + posArr[1] + ")");
                            frame.getFrame().setDrag(Boolean.TRUE);
                            frame.getFrame().setLabelHeader("Fetch Code");

                            hit.setValueExpression("value", QFaces.createValueExpression("#{photoFetch.submittedFetchCode}", String.class));
                            submit.setValue("提交");
                            submit.setActionExpression(QFaces.createMethodExpression("#{photoFetch.submit}", String.class, new Class<?>[]{}));

                            frame.getChildren().add(mess);
                            frame.getChildren().add(hit);
                            frame.getChildren().add(submit);
                            form.getChildren().add(frame);
                        }
                    }
                }

            }

        }

        form.getChildren().add(s);
            s.getChildren().add(table);
        form.getChildren().add(ssPageId);
        form.getChildren().add(ssFolderId);
        return form;
    }

    public String getName() {
        return "图片集";
    }

    public String getDescription() {
        return "使用这个模块来显示我的某个相册中的图片。";
    }

}
