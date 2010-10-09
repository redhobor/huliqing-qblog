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

package name.huliqing.qblog.xmlrpc;

import java.util.Hashtable;

/**
 * The server must ignore all elements that it doesn't understand.In a call to
 * metaWeblog.newPost or metaWeblog.editPost, if the struct contains a boolean
 * named flNotOnHomePage, then the post does not appear on the home page,
 * and only appears on the specified category pages. 
 * @author huliqing
 */
public interface MetaWeblogAPI {

    /**
     * In newPost and editPost, content is not a string, as it is in the
     * Blogger API, it's a struct. The defined members of struct are the
     * elements of <item> in RSS 2.0, providing a rich variety of item-level
     * metadata, with well-understood applications. <br />
     * The three basic elements are title, link and description. For blogging
     * tools that don't support titles and links, the description element
     * holds what the Blogger API refers to as "content."<br />
     * @param blogid
     * @param username
     * @param password
     * @param struct
     * @param publish
     * @return postid
     */
    public String newPost (String blogid, String username, String password,
            Hashtable<String, Object> struct, boolean publish) throws Exception ;

    /**
     * @param postid
     * @param username
     * @param password
     * @param struct
     * @param publish
     * @see #newPost(java.lang.String, java.lang.String, java.lang.String,
     * java.util.Map, java.lang.Boolean) ;
     * @return
     */
    public boolean editPost (String postid, String username, String password,
            Hashtable<String, Object> struct, boolean publish) throws Exception ;

    /**
     * 返回struct
     * struct basic elements: title, link, description. <br />
     * In getPost, the returned value is a struct, as with the Blogger API,
     * but it contains extra elements corresponding to the struct passed to
     * newPost and editPost.
     * @param postid
     * @param username
     * @param password
     * @see #newPost(java.lang.String, java.lang.String, java.lang.String,
     * java.util.Map, java.lang.Boolean) ;
     * @return
     */
    public Hashtable<String, Object> getPost (String postid, String username,
            String password) throws Exception ;

    /**
     * 返回struct, struct必需至少包含以下element: name, type, bits.<br />
     * name:  is a string, it may be used to determine the name of the file
     *      that stores the object, or to display it in a list of objects.
     *      It determines how the weblog refers to the object.
     *      If the name is the same as an existing object stored in the weblog,
     *      it may replace the existing object.<br />
     * type: is a string, it indicates the type of the object,
     *      it's a standard MIME type,
     *      like audio/mpeg or image/jpeg or video/quicktime.<br />
     * bits: is a base64-encoded binary value containing the content of the 
     *      object.<br />
     *
     * The struct may contain other elements, which may or may not be stored
     * by the content management system.If newMediaObject fails,
     * it throws an error. If it succeeds, it returns a struct,
     * which must contain at least one element, url, which is the url through
     * which the object can be accessed. It must be either an FTP or HTTP url.
     * <br />
     * @param blogid
     * @param username
     * @param password
     * @param struct
     * @return
     */
    public Hashtable<String, Object> newMediaObject (String blogid, String username,
            String password, Hashtable<String, Object> struct) throws Exception;

    /**
     * return struct <br />
     * The struct returned contains one struct for each category, 
     * containing the following elements: description, htmlUrl and rssUrl.
     * This entry-point allows editing tools to offer category-routing as a
     * feature.
     * @param blogid
     * @param username
     * @param password
     * @return
     */
    public Hashtable[] getCategories (String blogid, String username,
            String password) throws Exception ;

    /**
     * Each struct represents a recent weblog post, containing the same
     * information that a call to metaWeblog.getPost would return.
     * numberOfPosts is 1, you get the most recent post.
     * If it's 2 you also get the second most recent post, as the second array
     * element. If numberOfPosts is greater than the number of posts in the
     * weblog you get all the posts in the weblog. 
     * @param blogid
     * @param username
     * @param password
     * @param numberOfPosts
     * @return List<Struct> struct is a map witch represents a struct like newPost
     */
    public Hashtable[] getRecentPosts (String blogid, String username,
            String password, int numberOfPosts) throws Exception ;

}
