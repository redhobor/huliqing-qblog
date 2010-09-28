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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import name.huliqing.common.XmlUtils;
import name.huliqing.qblog.entity.ModuleEn;
import name.huliqing.qblog.processor.attr.Attribute2;
import name.huliqing.qblog.processor.attr.AttributeFinder;
import name.huliqing.qfaces.QFaces;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XmlProcessor以xml格式编码或解码Attribute数组.
 * @author huliqing
 */
public abstract class XmlProcessor2 implements Processor, java.io.Serializable {
    protected final static Logger logger = Logger.getLogger(XmlProcessor2.class.getName());

    public List<Attribute2> decode(String xmlStr) {
        // 获取所有必要的attribute
        List<Attribute2> required = getRequiredAttributes();
        if (required == null)
            return null;
        // 获可用的attribute信息
        Map<String, Attribute2> target = getAsMap(xmlStr);
        // 如果目标信息不存在，则直接返回必要的初始map,
        // 否则将解码到的值设置到required中并返回
        if (target == null || target.isEmpty())
            return required;
        // 将存在的值置换到required中,并返回参数信息
        // t -> target value, r -> required value
        Attribute2 t = null;
        for (Attribute2 r : required) {
            t = target.get(r.getAttrName());
            if (t != null)
                r.setAttrValue(t.getAttrValue());
        }
        return required;
    }

    /**
     * 将给定的Attribute数组编码为xml格式并返回,如果attributes为null或empty,则返
     * 回空字符串
     * @param attributes to encode
     * @return attributes as xml string
     */
    public String encode(List<Attribute2> attributes) {
        if (attributes == null || attributes.size() <= 0)
            return "";
        try {
            Document doc = XmlUtils.newDocument(); 
            Element root = doc.createElement("attributes");
            doc.appendChild(root);
            for (Attribute2 att : attributes) {
                Element ae = doc.createElement("attribute");
                ae.setAttribute("name", att.getAttrName());
                ae.setAttribute("value", att.getAttrValue());
                ae.setAttribute("attrType", att.getAttrType());
                root.appendChild(ae);
            }
            return XmlUtils.toXmlString(doc);
        } catch (TransformerException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return "";
    }

    public String getType() {
        return getClass().getName();
    }

    @Override
    public String toString() {
        return this.getClass().getName();
    }

    /**
     * 将给定的目标字符串转化为Map,只解出其中的name,value属性,
     * @param xmlStr
     *      需要进行decode的字符串
     * @return attribute as map
     */
    private Map<String, Attribute2> getAsMap(String xmlStr) {
        if (xmlStr == null || xmlStr.equals(""))
            return null;
        try {
            Map<String, Attribute2> asMap = new HashMap<String, Attribute2>();
            Document doc = XmlUtils.newDocument(xmlStr);
            NodeList attrs = doc.getElementsByTagName("attribute");
            int len = attrs.getLength();
            if (attrs != null && attrs.getLength() > 0) {
                for (int i = 0; i < len; i++) {
                    Element e = (Element) attrs.item(i);
                    String name = e.getAttribute("name");
                    String value = e.getAttribute("value");
                    String attrType = e.getAttribute("attrType");
                    Attribute2 attr = AttributeFinder.find(attrType);
                    if (attr == null) {
                        logger.warning("Render type not found, attribute name=" + name + ", attrType=" + attrType);
                        continue;
                    }
                    attr.setAttrName(name);
                    attr.setAttrValue(value);
                    asMap.put(name, attr);
                }
            }
            return asMap;
        } catch (ParserConfigurationException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected AttrMap getAttributes(ModuleEn module) {
        List<Attribute2> attributes = decode(module.getAttributes() != null ? module.getAttributes().getValue() : "");
        AttrMap asMap = new AttrMap(attributes.size());
        for (Attribute2 attr : attributes)
            asMap.put(attr.getAttrName(), attr);
        return asMap;
    }

    public Boolean defaultAutoStyle() {
        return Boolean.TRUE;
    }

    public Boolean defaultEnabled() {
        return Boolean.TRUE;
    }

    public Boolean defaultShowName() {
        return Boolean.TRUE;
    }

    protected class AttrMap extends HashMap<String, Attribute2> {

        public AttrMap(int initialCapacity) {
            super(initialCapacity);
        }
        public Boolean getAsBoolean(String attrName, Boolean defValue) {
            Attribute2 a = get(attrName);
            Boolean result = null;
            if (a != null) 
                result = QFaces.convertToBoolean(a.getAttrValue());
            return (result != null ? result : defValue);
        }

        public Long getAsLong(String name, Long defValue) {
            Attribute2 a = get(name);
            Long result = null;
            if (a != null)
                result = QFaces.convertToLong(a.getAttrValue());
            return (result != null ? result : defValue);
        }

        public Integer getAsInteger(String name, Integer defValue) {
            Attribute2 a = get(name);
            Integer result = null;
            if (a != null)
                result = QFaces.convertToInteger(a.getAttrValue());
            return (result != null ? result : defValue);
        }

        public String getAsString(String name, String defValue) {
            Attribute2 a = get(name);
            if (a != null) {
                if (a.getAttrValue() != null && !"".equals(a.getAttrValue())) {
                    return a.getAttrValue();
                }
            }
            return defValue;
        }
    }
}
