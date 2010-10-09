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

package name.huliqing.qblog;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private final static Logger logger = Logger.getLogger(StringUtils.class.getName());

    /**
     * 将部分可能出现的html代码转义.
     * @param htmlValue
     * @return
     */
    public final static String convertHTML(String htmlValue) {
        String[][] _chars = {
            {"&", "&amp;"},
            {"<", "&lt;"},
            {">", "&gt;"},
        };
        for (int i = 0; i < _chars.length; i++) {
            htmlValue = htmlValue.replaceAll(_chars[i][0], _chars[i][1]);
        }
        return htmlValue;
    }

    /**
     * 移除给定htmlValue中的所有的html标记，如 <a href="...">My Blog</a>
     * 处理后的结果应该为： My Blog
     * @param htmlValue
     * @return
     */
    public final static String removeHTML(String htmlValue) {
        String regx = "<.+?>"; // "?" 号确保不进行懒惰匹配
        Pattern p = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(htmlValue);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
            // 输出警告信息
//            String temp = htmlValue.substring(m.start(), m.end());
//            logger.warning("移除html节点:" + temp);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 返回转义字符表,返回的数组格式： string[][3]; <br />
     * 第二维的格式为 ["原字符", "十进制转义符", "Html转义符"]
     * @param value
     * @return
     */
    public final static String[][] getEscapeCharacters() {
        String[][] regex = {
            // 字符/十进制/转义
            {"\"",  "&#34;",    "&quot;"},
            {"&",   "&#38;",    "&amp;" },
            {"<",   "&#60;",    "&lt;"  },
            {">",   "&#62;",    "&gt;"  },
            {" ",   "&#160;",   "&nbsp;"},
            {"?",   "&#161;",    "&iexcl;" },
            {"￠",    "&#162;",  "&cent;" },
            {"￡",    "&#163;",  "&pound;" },
            {"¤",    "&#164;",  "&curren;" },
            {"￥",    "&#165;",  "&yen;" },
            {"|",    "&#166;",  "&brvbar;" },
            {"§",    "&#167;",  "&sect;" },
            {"¨",    "&#168;",  "&uml;" },
            {"©",    "&#169;",  "&copy;" },
            {"a",    "&#170;",  "&ordf;" },
            {"?",    "&#171;",  "&laquo;" },
            {"?",    "&#172;",  "&not;" },
            {"\\x7f",    "&#173;",  "&shy;" },
            {"®",    "&#174;",  "&reg;" },
            {"ˉ",    "&#175;",  "&macr;" },
            {"°",    "&#176;",  "&deg;" },
            {"±",    "&#177;",  "&plusmn;" },
            {"2",    "&#178;",  "&sup2;" },
            {"3",    "&#179;",  "&sup3;" },
            {"′",    "&#180;",  "&acute;" },
            {"μ",    "&#181;",  "&micro;" },
            {"?",    "&#182;",  "&para;" },
            {"·",    "&#183;",  "&middot;" },
            {"?",    "&#184;",  "&cedil;" },
            {"1",    "&#185;",  "&sup1;" },
            {"o",    "&#186;",  "&ordm;" },
            {"?",    "&#187;",  "&raquo;" },
            {"?",    "&#188;",  "&frac14;" },
            {"?",    "&#189;",  "&frac12;" },

            {"?",    "&#190;",  "&frac34;" },
            {"?",    "&#191;",  "&iquest;" },
            {"À",    "&#192;",  "&Agrave;" },
            {"Á",    "&#193;",  "&Aacute;" },
            {"Â",    "&#194;",  "&circ;" },
            {"Ã",    "&#195;",  "&Atilde;" },
            {"Ä",    "&#196;",  "&Auml;" },
            {"Å",    "&#197;",  "&ring;" },
            {"Æ",    "&#198;",  "&AElig;" },
            {"Ç",    "&#199;",  "&Ccedil;" },

            {"È",    "&#200;",  "&Egrave;" },
            {"É",    "&#201;",  "&Eacute;" },
            {"Ê",    "&#202;",  "&Ecirc;" },
            {"Ë",    "&#203;",  "&Euml;" },
            {"Ì",    "&#204;",  "&Igrave;" },
            {"Í",    "&#205;",  "&Iacute;" },
            {"Î",    "&#206;",  "&Icirc;" },
            {"Ï",    "&#207;",  "&Iuml;" },
            {"Ð",    "&#208;",  "&ETH;" },
            {"Ñ",    "&#209;",  "&Ntilde;" },

            {"Ò",    "&#210;",  "&Ograve;" },
            {"Ó",    "&#211;",  "&Oacute;" },
            {"Ô",    "&#212;",  "&Ocirc;" },
            {"Õ",    "&#213;",  "&Otilde;" },
            {"Ö",    "&#214;",  "&Ouml;" },
            {"&times;","&#215;",  "&times;" },
            {"Ø",    "&#216;",  "&Oslash;" },
            {"Ù",    "&#217;",  "&Ugrave;" },
            {"Ú",    "&#218;",  "&Uacute;" },
            {"Û",    "&#219;",  "&Ucirc;" },

            {"Ü",    "&#220;",  "&Uuml;" },
            {"Ý",    "&#221;",  "&Yacute;" },
            {"Þ",    "&#222;",  "&THORN;" },
            {"ß",    "&#223;",  "&szlig;" },
            {"à",    "&#224;",  "&agrave;" },
            {"á",    "&#225;",  "&aacute;" },
            {"â",    "&#226;",  "&acirc;" },
            {"ã",    "&#227;",  "&atilde;" },
            {"ä",    "&#228;",  "&auml;" },
            {"å",    "&#229;",  "&aring;" },

            {"æ",    "&#230;",  "&aelig;" },
            {"ç",    "&#231;",  "&ccedil;" },
            {"è",    "&#232;",  "&egrave;" },
            {"é",    "&#233;",  "&eacute;" },
            {"ê",    "&#234;",  "&ecirc;" },
            {"ë",    "&#235;",  "&euml;" },
            {"ì",    "&#236;",  "&igrave;" },
            {"í",    "&#237;",  "&iacute;" },
            {"î",    "&#238;",  "&icirc;" },
            {"ï",    "&#239;",  "&iuml;" },

            {"ð",    "&#240;",  "&ieth;" },
            {"ñ",    "&#241;",  "&ntilde;" },
            {"ò",    "&#242;",  "&ograve;" },
            {"ó",    "&#243;",  "&oacute;" },
            {"ô",    "&#244;",  "&ocirc;" },
            {"õ",    "&#245;",  "&otilde;" },
            {"ö",    "&#246;",  "&ouml;" },
            {"÷",    "&#247;",  "&divide;" },
            {"ø",    "&#248;",  "&oslash;" },
            {"ù",    "&#249;",  "&ugrave;" },

            {"ú",    "&#250;",  "&uacute;" },
            {"û",    "&#251;",  "&ucirc;" },
            {"ü",    "&#252;",  "&uuml;" },
            {"ý",    "&#253;",  "&yacute;" },
            {"þ",    "&#254;",  "&thorn;" },
            {"ÿ",    "&#255;",  "&yuml;" },
        };
        return regex;
    }
}
