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

import com.google.appengine.api.datastore.Text;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Logger;
import name.huliqing.common.StringUtils;
import name.huliqing.qblog.ConfigManager;
import name.huliqing.qblog.entity.AccountEn;
import name.huliqing.qblog.entity.ArticleEn;
import name.huliqing.qblog.entity.ConfigEn;
import name.huliqing.qblog.entity.FolderEn;
import name.huliqing.qblog.entity.PhotoEn;
import name.huliqing.qblog.entity.TagEn;
import name.huliqing.qblog.enums.ArticleSecurity;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qblog.enums.Security;
import name.huliqing.qblog.service.AccountSe;
import name.huliqing.qblog.service.ArticleSe;
import name.huliqing.qblog.service.FolderSe;
import name.huliqing.qblog.service.PhotoSe;
import name.huliqing.qblog.service.TagSe;

/**
 *
 * @author huliqing
 */
public class QBlogRPC implements MetaWeblogAPI, BloggerAPI{
    private final static Logger logger = Logger.getLogger(QBlogRPC.class.getName());

    public String newPost(String blogid, String username, String password,
            Hashtable<String, Object> struct, boolean publish) throws Exception {
        validateAccount(username, password);
        String title = (String) struct.get("title");
        String description = (String) struct.get("description");

        ArticleEn article = new ArticleEn();
        article.setAuthor(username);
        article.setTitle(title);
        article.setContent(new Text(description));
        article.setSecurity(publish ? ArticleSecurity.PUBLIC : ArticleSecurity.DRAFT);
        article.setMailNotice(ConfigManager.getInstance().getAsBoolean(Config.CON_REPLY_NOTICE));
        article.setReplyable(ConfigManager.getInstance().getAsBoolean(Config.CON_REPLY_ENABLE));
        article.setTags(buildTags((Object[]) struct.get("categories")));
        if (ArticleSe.save(article)) {
            return article.getArticleId().toString();
        }
        throw new RuntimeException("Can't post articles(unknow)");
    }

    public boolean editPost(String postid, String username, String password,
            Hashtable<String, Object> struct, boolean publish) throws Exception {
        validateAccount(username, password);
        String title = (String) struct.get("title");
        String description = (String) struct.get("description");
//      Date dateCreated = (Date) struct.get("dateCreated");
//      Date pubDate = (Date) struct.get("pubDate");
        
        ArticleEn ae = ArticleSe.find(Long.parseLong(postid));
        if (ae == null)
            throw new RuntimeException("Article not found with article " +
                    "id=" + postid + ", the article maybe deleted.");
        
        ae.setTitle(title);
        ae.setContent(new Text(description));
        ae.setSummary(ArticleSe.generateSummary(description, 300));
        ae.setSecurity(publish ? ArticleSecurity.PUBLIC : ArticleSecurity.DRAFT);
        ae.setTags(buildTags((Object[]) struct.get("categories")));
        return ArticleSe.update(ae);
    }

    public Hashtable<String, Object> getPost(String postid, String username,
            String password) throws Exception {
        validateAccount(username, password);
        String site = ConfigManager.getInstance().getAsString(Config.CON_METAWEBLOG_SITE);
        ArticleEn ae = ArticleSe.find(Long.parseLong(postid));
        Hashtable<String, Object> struct = buildArticleStruct(ae, site);
        return struct;
    }

    public boolean deletePost(String appkey, String postid, String username,
            String password, boolean publish) throws Exception {
        validateAccount(username, password);
        if (postid == null)
            throw new RuntimeException("post id could not be null!");
        return ArticleSe.delete(Long.parseLong(postid));
    }

    public Hashtable<String, Object> newMediaObject(String blogid, String username,
            String password, Hashtable<String, Object> struct) throws Exception {
        validateAccount(username, password);

        // 获取默认folderId, 这里需要确保一个真正存在的相册
        Long folderId = ConfigManager.getInstance().getAsLong(Config.CON_METAWEBLOG_ALBUM);
        if (folderId == null || FolderSe.find(folderId) == null) {
            logger.warning("指定的默认相册不存在，偿试查找默认相册(Default Album)...");
            String defAlbum = "Default Album";
            List<FolderEn> temps = FolderSe.findByName(defAlbum);
            if (temps != null && !temps.isEmpty()) {
                folderId = temps.get(0).getFolderId();
                logger.warning("找到默认相册Default Album. folderId=" + folderId);
            } else {
                logger.warning("找不到默认相册,将偿试创建默认相册用于存放附件.");
                FolderEn nf = new FolderEn();
                nf.setCreateDate(new Date());
                nf.setName(defAlbum);
                nf.setSecurity(Security.PUBLIC);
                FolderSe.save(nf);
                folderId = nf.getFolderId();
                logger.warning("默认相册创建成功，名称：" + defAlbum + ", folderId=" + folderId);
            }
            // 自动更新系统参数
            ConfigEn ce = ConfigManager.getInstance().findConfig(Config.CON_METAWEBLOG_ALBUM);
            ce.setValue(folderId.toString());
            ConfigManager.getInstance().updateConfig(ce);
        }

        // 检查相册权限
        FolderEn folder = FolderSe.find(folderId);
        if (folder.getSecurity() != Security.PUBLIC) {
            throw new RuntimeException("你所指定的用于存放附件的相册文件夹为：" +
                    folder.getName() +
                    ", 但是该相册所指定的权限为：" + folder.getSecurity() +
                    ", 这可能使你无法正常浏览该相册内的图片。" +
                    " 登录网站，偿试将你的相册文件夹的权限设置为公开的(PUBLIC)来解决问题.");
        }

        // 保存数据到folder
        String name = (String) struct.get("name");
        String type = (String) struct.get("type");
        byte[] data = (byte[]) struct.get("bits");

        String suffix = null;
        if (name.indexOf(".") != -1) {
            suffix = name.substring(name.lastIndexOf(".") + 1);
            name = name.substring(0, name.lastIndexOf("."));
        }
        if (name.indexOf("/") != -1) {
            name = name.substring(name.lastIndexOf("/") + 1);
        }
        if (name.length() > 16) {
            name = name.substring(0, 16);
        }

        PhotoEn file = new PhotoEn();
        file.setFolder(folderId);
        file.setDes("");
        file.setCreateDate(new Date());
        file.setName(name);
        file.setContentType(type);
        file.setPack(false);
        file.setBytes(data);
        file.setSuffix(suffix);
        file.setFileSize(Integer.valueOf(data.length).longValue());

        // 不处理缩略图，一些客户端程序会自动对上传图片作缩略图处理，所以上传的时候可
        // 能会看到两张图片。这里不再处理缩略图也可以节省空间

        PhotoSe.save(file);

        // 返回struct
        String site = ConfigManager.getInstance().getAsString(Config.CON_METAWEBLOG_SITE);
        Hashtable<String, Object> result = new Hashtable<String, Object>(1);
        result.put("url", site + "/photo/photoId=" + file.getPhotoId());
        return result;
    }

    public Hashtable[] getCategories(String blogid, String username,
            String password) throws Exception {
        validateAccount(username, password);
        List<TagEn> tags = TagSe.findAll();
        if (tags == null || tags.isEmpty()) { 
            return null;
        }
        Hashtable[] result = new Hashtable[tags.size()];
        String link = ConfigManager.getInstance().getAsString(Config.CON_METAWEBLOG_SITE) + "/articles/tag=";
        for (int i = 0; i < tags.size(); i++) {
            Hashtable<String, Object> cateStruct = new Hashtable<String, Object>(2);
            TagEn te = tags.get(i);
            cateStruct.put("title", te.getName());
            cateStruct.put("description", te.getName());
            cateStruct.put("htmlUrl", link + te.getName());
            result[i] = cateStruct;
        }
        return result;
    }

    public Hashtable[] getRecentPosts(String blogid,
            String username, String password, int numberOfPosts) throws Exception {
        validateAccount(username, password);
        List<ArticleEn> aes = ArticleSe.findRecentPost(numberOfPosts);
        if (aes == null || aes.isEmpty()) {
            return null;
        }
        String site = ConfigManager.getInstance().getAsString(Config.CON_METAWEBLOG_SITE);
        Hashtable[] result = new Hashtable[aes.size()];
        for (int i = 0; i < result.length; i++) {
            Hashtable<String, Object> struct = buildArticleStruct(aes.get(i), site);
            result[i] = struct;
        }
        return result;
    }

    public Hashtable[] getUsersBlogs(String appkey, String username,
            String password) throws Exception {
        validateAccount(username, password);
        String blogName = ConfigManager.getInstance().getAsString(Config.CON_METAWEBLOG_BLOG_NAME);
        if (blogName == null || "".equals(blogName)) {
             ConfigEn ce = ConfigManager.getInstance().findConfig(Config.CON_METAWEBLOG_BLOG_NAME);
             ce.setValue("Your Blog Name");
             ConfigManager.getInstance().updateConfig(ce);
        }
        Hashtable[] result = new Hashtable[1];

        Hashtable<String, Object> struct = new Hashtable<String, Object>(3);
        struct.put("url", ConfigManager.getInstance().getAsString(Config.CON_METAWEBLOG_SITE));
        struct.put("blogid", username);
        struct.put("blogName", ConfigManager.getInstance().getAsString(Config.CON_METAWEBLOG_BLOG_NAME));
        result[0] = struct;
        
        return result;
    }

    public Hashtable[] getUserInfo(String appkey, String username,
            String password) throws Exception {
        validateAccount(username, password);
        AccountEn ae = AccountSe.find(username);
        Hashtable<String, Object> struct = new Hashtable<String, Object>();
        struct.put("userid", ae.getAccount());
        struct.put("firstname", "");
        struct.put("lastname", "");
        struct.put("url", ConfigManager.getInstance().getAsString(Config.CON_METAWEBLOG_SITE));
        struct.put("email", ConfigManager.getInstance().getAsString(Config.CON_EMAIL_ADDR_SERVER));
        struct.put("nickname", ae.getNickname());
        Hashtable[] result = new Hashtable[1];
        result[0] = struct;
        return result;
    }

    private void validateAccount(String username, String password) throws Exception {
        AccountEn account = new AccountEn();
        account.setAccount(username);
        account.setPassword(StringUtils.getInstance().encodeByMd5(password));
        String result = AccountSe.loginWithPasswordEncodedRPC(account);
        if (result != null)
            throw new RuntimeException(result);
    }

    private Hashtable<String, Object> buildArticleStruct(ArticleEn ae, String site) {
        Hashtable<String, Object> struct = new Hashtable<String, Object>();
        struct.put("postid", ae.getArticleId());
        struct.put("dateCreated", ae.getCreateDate());
        struct.put("title", ae.getTitle());
        struct.put("description", (ae.getContent() != null ? ae.getContent().getValue() : ""));
        struct.put("publish", (ArticleSecurity.PUBLIC == ae.getSecurity()));
        struct.put("permaLink", site + "/article/articleId=" + ae.getArticleId());
        struct.put("author", ae.getAuthor());
        if (ae.getTags() != null) {
            struct.put("categories", ae.getTags().split(","));
        }
        return struct;
    }

    private String buildTags(Object[] categories) {
        String tags = "";
        if (categories != null && categories.length > 0) {
            StringBuilder sb = new StringBuilder("");
            for (Object c : categories) {
                sb.append(c).append(",");
            }
            tags = sb.toString();
            if (tags.endsWith(",")) {
                tags = tags.substring(0, tags.length() - 1);
            }
        }
        return tags;
    }

// metaWeblog.getTemplate
// metaWeblog.setTemplate

}
