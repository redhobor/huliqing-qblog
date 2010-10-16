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
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qblog.processor.impl.ArticlesDataTable;
import name.huliqing.qfaces.component.Scroller;

/**
 * 这个组件主要是为了确保"标签文章列表"与"文章列表"所生成的模块的渲染逻辑一致。
 * 即点击“标签”或“日历”后出现的文章列表与“文章列表”模块生成的文章列表的样式必须
 * 尽量保持样式一致.
 * @author huliqing
 */
public class UIArticlesPanel extends UIComponentBase{

    private Boolean hasLoaded;

    @Override
    public String getFamily() {
        return null;
    }

    private Object[] _values;
    @Override
    public void restoreState(FacesContext fc, Object state) {
        _values = (Object[]) state;
        super.restoreState(fc, _values[0]);
        this.hasLoaded = (Boolean) _values[1];
    }

    @Override
    public Object saveState(FacesContext fc) {
        if (_values == null) {
            _values = new Object[2];
        }
        _values[0] = super.saveState(fc);
        _values[1] = this.hasLoaded;
        return _values;
    }


    @Override
    public boolean getRendersChildren() {
        return true;
    }

    @Override
    public void encodeBegin(FacesContext fc) throws IOException {
        if (hasLoaded == null || !hasLoaded) {
            hasLoaded = Boolean.TRUE;
            // 必须为每个组件指定一个ID，否则可能在DataTable组件之内ID一直变动
            // 而被认为是一直需要重新初始化。并且同一个页面可能存在多个同样的组件，
            // 所以必须确保每次渲染时ID都能够唯一。（这里使用了currentTimeMillis)
            String suffix = String.valueOf(System.currentTimeMillis());
            String dId = "did" + suffix;

            // Create Scroller
            Scroller scroller = new Scroller();
            scroller.setFor(dId);
            scroller.setVar("article"); // 这个var与DataTable的var意义相同
            scroller.setListenerAsExpression("#{articlesWe.loadData}");

            // Load attribute
            scroller.setPageSize(10);
            scroller.setDisplayPage(9);
            scroller.setFace("1");
            scroller.setDisplayJump(Boolean.FALSE);
            scroller.setDisplay("bottom");

            ArticlesDataTable table = new ArticlesDataTable();
            table.setId(dId);
            table.setShowSummary(Boolean.TRUE);
            table.setShowFooter(Boolean.TRUE);
            table.setTarget("_self");
            table.setPattern(ConfigManager.getInstance().getAsString(Config.CON_SYSTEM_DATE_FORMAT));
            table.setTimeZone(ConfigManager.getInstance().getAsString(Config.CON_SYSTEM_TIME_ZONE));

            scroller.getChildren().add(table);
            this.getChildren().clear();
            this.getChildren().add(scroller);
        }
    }

    @Override
    public void encodeEnd(FacesContext fc) throws IOException {}

    @Override
    public void encodeChildren(FacesContext fc) throws IOException {
        super.encodeChildren(fc);
    }

}
