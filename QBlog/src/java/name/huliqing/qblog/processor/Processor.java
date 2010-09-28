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

package name.huliqing.qblog.processor;

import java.util.List;
import javax.faces.component.UIComponent;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.processor.attr.Attribute2;

/**
 *
 * @author huliqing
 */
public interface Processor {

    /**
     * 获取该处理器必要的所有参数
     * @return
     */
    List<Attribute2> getRequiredAttributes();

    /**
     * 将目标字符串解码为Attribute数组,如果目标字符串不能转换或者
     * getRequiredAttributes为null,则返回null.
     * @return attributes
     */
    List<Attribute2> decode(String str);

    /**
     * 将Attribute数组件转化一定格式的字符串
     * @param attributes
     * @return encode string
     */
    String encode(List<Attribute2> attributes);


//    /**
//     * @deprecated
//     * 将解码后的Attributes渲染为界面组件，并返回
//     * @param attributes
//     * @return
//     */
//    UIComponent render(List<Attribute2> attributes);

    UIComponent render(ModuleEn module);

    /**
     * 返回Module渲染器的名称,这是作为渲染器的唯一标识。
     * @return
     */
    String getType();

    /**
     * 返回Module渲染器的描述名称。
     * @return
     */
    String getName();

    /**
     * 关于该处理器的描述说明
     * @return
     */
    String getDescription();

    /**
     * 给一个建议，是否显示模块名称。这只是返回一个默认的建议值。比如：对于导航栏,
     * 返回false,即不显示模块名称会更好。相关建议值的方法名称都由“default"开头，
     * 好defaultXXX,这些建议值应该在帮助用户创建模块是使用，即自动填入，减少用户
     * 输入过程的麻烦及误填值。
     * @return
     */
    Boolean defaultShowName();

    /**
     * 返回建议值。
     * 是否自动应用来自模版文件的样式，<BR />
     * 如果返回true <BR />
     * 则系统在渲染这个模块时，将自动使用目标模版文件定义的
     * module title，module content样式(css)来渲染Module.<BR />
     *
     * 如果返回false,则系统将对当前渲染器的渲染输出不作任何处理。
     * 由渲染器自行处理所有样式。<BR />
     * 为了配合达到最好的样式一致，最好根据实现的功能不同，使用来自
     * {@link name.huliqing.qblog.enums.Style Style}
     * 中指定的样式名称渲染Module <BR />
     * 参考 {@link name.huliqing.qblog.processor.impl.NavigationProcessor
     * NavigationProcessor }
     * @return true/false
     */
    Boolean defaultAutoStyle();

    /**
     * 给一个建议值，是否默认开启模块。
     * @return
     */
    Boolean defaultEnabled();

    
}
