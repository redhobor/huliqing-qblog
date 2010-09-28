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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import name.huliqing.qblog.processor.impl.AlbumProcessor;
import name.huliqing.qblog.processor.impl.ArticlesProcessor;
import name.huliqing.qblog.processor.impl.CalendarProcessor;
import name.huliqing.qblog.processor.impl.DefaultProcessor;
import name.huliqing.qblog.processor.impl.ModulePanelProcessor;
import name.huliqing.qblog.processor.impl.PlayerProcessor;
import name.huliqing.qblog.processor.impl.NavigationProcessor;
import name.huliqing.qblog.processor.impl.PhotoProcessor;
import name.huliqing.qblog.processor.impl.RSSProcessor;
import name.huliqing.qblog.processor.impl.RecentPostArticleProcessor;
import name.huliqing.qblog.processor.impl.RecentReplyProcessor;
import name.huliqing.qblog.processor.impl.TuDouFlashPlayerProcessor;

/**
 *
 * @author huliqing
 */
public class ProcessorFactory {
    private final static Logger logger = Logger.getLogger(ProcessorFactory.class.getName());

    private final static ProcessorMap ins = new ProcessorMap();

    static {
        add(new DefaultProcessor());
        add(new NavigationProcessor());
        add(new ArticlesProcessor());
        add(new RecentPostArticleProcessor());
        add(new RecentReplyProcessor());
        add(new RSSProcessor());
        add(new CalendarProcessor());
        add(new AlbumProcessor());
        add(new PhotoProcessor());
        add(new PlayerProcessor());
        add(new TuDouFlashPlayerProcessor());
        add(new ModulePanelProcessor());
    }

    private ProcessorFactory() {}

    /**
     * 添加Module Processor(处理器）
     * @param p
     */
    public final static void add(Processor p) {
        if (p == null)
            throw new NullPointerException("Processor couldn't be null!");
        if (p.getType() == null)
            throw new NullPointerException("Processor name not found by processor=" + p);
        if (ins.get(p.getType()) != null)
            return;
        
        ins.put(p.getType(), p);
    }

    /**
     * 通过名称查找Processor,如果找不到相应的Processor,则使用默认Processor
     * @param pName
     * @return
     */
    public final static Processor find(String pName) {
        if (pName == null)
            return null;
        Processor p = ins.get(pName);
        if (p == null) {
            p = ins.getAsList().get(0);
            logger.warning("Processor not found by name =" + pName +
                    ", use default processor now, processor=" + p);
        }
        return p;
    }

    public final static List<Processor> findAllProcessor() {
        return ins.getAsList();
    }

    /**
     * 以数组形式返回所有可用的Processor名称
     * @return
     */
    public final static String[] findAllProcessorName() {
        Collection<Processor> ps = ins.values();
        String[] asArr = new String[ps.size()];
        int i = 0;
        for (Processor p : ps) {
            asArr[i] = p.getType();
            i++;
        }
        return asArr;
    }

    private static class ProcessorMap extends HashMap<String, Processor> {
        private List<Processor> asList = new ArrayList<Processor>();
        
        @Override
        public Processor put(String key, Processor value) {
            if (this.containsKey(key))
                throw new RuntimeException("Processor exists, processor name=" + key);
            asList.add(value);
            return super.put(key, value);
        }

        @Override
        public void putAll(Map<? extends String, ? extends Processor> m) {
            throw new UnsupportedOperationException("Do not supported.");
        }

        public List<Processor> getAsList() {
            return asList;
        }
    }
}
