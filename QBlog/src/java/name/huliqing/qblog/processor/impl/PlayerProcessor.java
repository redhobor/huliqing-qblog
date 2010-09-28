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
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.processor.HtmlProcessor;
import name.huliqing.qblog.processor.attr.AttrInputText;
import name.huliqing.qblog.processor.attr.AttrSelectBooleanCheckbox;
import name.huliqing.qblog.processor.attr.AttrSelectOneRadio;
import name.huliqing.qblog.processor.attr.Attribute2;

/**
 * 该处理器渲染用于播放Flash视频的播放器
 * @author huliqing
 */
public class PlayerProcessor extends HtmlProcessor{

    public List<Attribute2> getRequiredAttributes() {
        ArrayList<Attribute2> attrs = new ArrayList<Attribute2>(12);

        AttrSelectOneRadio type = new AttrSelectOneRadio("Type", "auto", "" +
                "选择作为Flash播放器还是Media Play播放器,使用“自动判断”会根据URL连接来判断文件类型及应该使用的播放器，" +
                "注意：部分浏览器可能不支持Media Player，而Flash Player要求浏览器安装有Flash插件。");
        type.addItem("auto", "自动判断");
        type.addItem("flash", "Flash Player");
        type.addItem("media", "Media Player");

        // url
        AttrInputText url = new AttrInputText("URL", "", "媒体文件地址，如：http://xxx.swf 或 http://xxx.wmv");
        url.setStyle("width:100%");

        // width
        AttrInputText width = new AttrInputText("Width", "480", "宽度,默认：480");

        // height
        AttrInputText height = new AttrInputText("Height", "400", "高度,默认：400");

        // AS media play
        AttrSelectBooleanCheckbox autoStart = new AttrSelectBooleanCheckbox("Auto Start", "false", "是否自动播放,默认:否");

        // quality
        AttrSelectOneRadio quality = new AttrSelectOneRadio("Quality", "high", "播放质量,默认:高");
        quality.addItem("low", "低");
        quality.addItem("medium", "一般");
        quality.addItem("high", "高");
        quality.addItem("best", "最好");

        // wmode
        AttrSelectOneRadio wmode = new AttrSelectOneRadio("Wmode", "opaque", "[Flash Player]显示模式。");
        wmode.addItem("window", "窗口");
        wmode.addItem("opaque", "不透明");
        wmode.addItem("transparent", "透明");

        // full screen
        AttrSelectBooleanCheckbox allowfullscreen = new AttrSelectBooleanCheckbox("Allow Fullscreen", "true", "[Flash Player]是否允许全屏播放,默认:true");

        // allowscriptaccess
        AttrSelectOneRadio allowscriptaccess = new AttrSelectOneRadio("Allow Script Access", "samedomain", "[Flash Player]是否允许Flash应用程序可与HTML页面通信, 默认:samedomain");
        allowscriptaccess.addItem("always", "总是");
        allowscriptaccess.addItem("never", "从不");
        allowscriptaccess.addItem("samedomain", "只允许域内");

        // loop
        AttrSelectBooleanCheckbox mute = new AttrSelectBooleanCheckbox("Mute", "false", "[Media Player]是否静音，默认：否");
        AttrInputText playCount = new AttrInputText("Play Count", "1", "[Media Player]播放次数,默认：1");
        AttrSelectOneRadio uiMode = new AttrSelectOneRadio("UI Mode", "mini", "[Media Player]显示模式, 默认:mini");
        uiMode.addItem("mini", "最简");
        uiMode.addItem("full", "全部");
        uiMode.addItem("none", "无按钮");
        uiMode.addItem("invisible", "全隐藏");
 
        attrs.add(type);
        attrs.add(url);
        attrs.add(width);
        attrs.add(height);
        attrs.add(autoStart);
        attrs.add(quality);
        attrs.add(wmode);
        attrs.add(allowfullscreen);
        attrs.add(allowscriptaccess);

        attrs.add(mute);
        attrs.add(playCount);
        attrs.add(uiMode);
        return attrs;
    }

    @Override
    public String makeHTML(ModuleEn module) {
        AttrMap attrs = getAttributes(module);
        String type = attrs.getAsString("Type", "auto"); // flash or media
        String url = attrs.getAsString("URL", "");
        String width = attrs.getAsString("Width", "");
        String height = attrs.getAsString("Height", "");
        String quality = attrs.getAsString("Quality", "");
        String wmode = attrs.getAsString("Wmode", "");
        String allowfullscreen = attrs.getAsString("Allow Fullscreen", "true");
        String allowscriptaccess = attrs.getAsString("Allow Script Access", "always");
        
        // AS Media Player
        String autoStart = attrs.getAsString("Auto Start", "false");
        String mute = attrs.getAsString("Mute", "false");
        String playCount = attrs.getAsString("Play Count", "-1");
        String uiMode = attrs.getAsString("UI Mode", "mini");

        //
        String player = "flash";
        if ("auto".equals(type)) {
            if (url.indexOf(".wmv") != -1
                    || url.indexOf(".midi") != -1
                    || url.indexOf(".mp3") != -1
                    || url.indexOf(".mp4") != -1
                    || url.indexOf(".wma") != -1
                    || url.indexOf(".wav") != -1
                    || url.indexOf(".mpg") != -1
                    || url.indexOf(".mpeg") != -1
                    || url.indexOf(".asf") != -1
                    || url.indexOf(".cd") != -1
                    || url.indexOf(".avi") != -1
                    || url.indexOf(".aiff") != -1
                    || url.indexOf(".au") != -1
                    || url.indexOf(".rm") != -1
                    ) {
                player = "media";
            } else {
                player = "flash";
            }
        } else {
            player = type;
        }

        StringBuilder obj = new StringBuilder();
        if ("media".equals(player)) {
            obj.append("<object ")
                    .append(" width=\"").append(width).append("\" ")
                    .append(" height=\"").append(height).append("\" ")
                    .append(" classid=\"CLSID:6BF52A52-394A-11d3-B153-00C04F79FAA6\" ")
                    .append(" >");
            obj.append("<param name=\"url\" value=\"").append(url).append("\" />");
            obj.append("<param name=\"autoStart\" value=\"").append(autoStart).append("\" />");
            obj.append("<param name=\"quality\" value=\"").append(quality).append("\" />");
            obj.append("<param name=\"uiMode\" value=\"").append(uiMode).append("\" />");
            obj.append("<param name=\"mute\" value=\"").append(mute).append("\" />");
            obj.append("<param name=\"playCount\" value=\"").append(playCount).append("\" />");
            obj.append("</object>");
        } else if ("flash".equals(player)) {
            obj.append("<object ")
                    .append(" width=\"").append(width).append("\" ")
                    .append(" height=\"").append(height).append("\"")
                    .append(" >");
            obj.append("<embed src=\"").append(url).append("\" ")
                .append(" type=\"application/x-shockwave-flash\" ")
                .append(" autostart=\"").append(autoStart).append("\" ")
                .append(" width=\"").append(width).append("\" ")
                .append(" height=\"").append(height).append("\" ")
                .append(" quality=\"").append(quality).append("\" ")
                .append(" allowfullscreen=\"").append(allowfullscreen).append("\" ")
                .append(" allowscriptaccess=\"").append(allowscriptaccess).append("\" ")
                .append(" wmode=\"").append(wmode).append("\" ")
                .append(" ></embed>");
             obj.append("</object>");
        }
        return obj.toString();
    }

    public String getName() {
        return "播放器";
    }

    public String getDescription() {
        return "该处理器用于产生一个可播放Flash视频或Media视频的播放器。";
    }

}
