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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import name.huliqing.qblog.daocache.ConfigCache;
import name.huliqing.qblog.entity.ConfigEn;
import name.huliqing.qblog.enums.Config;
import name.huliqing.qfaces.QFaces;

public class ConfigManager {
    private final static ConfigManager ins = new ConfigManager();
    private ConfigManager() {}
    public final static ConfigManager getInstance() {
        return ins;
    }

    private List<ConfigEn> configs = new ArrayList<ConfigEn>();
    private Map<Config, ConfigEn> configMap = new HashMap<Config, ConfigEn>();
    private boolean needToReloadConfigs = true;

    /**
     * 获取配置信息,该方法首先从已经设置的系统参数中获取指定的配置信息，如果配置信息
     * 为null或空，则使用默认配置 c 的值, 结果以字符串的方式返回,该方法不会返回null值
     * @throws NullPointerException 如果配置不存在或尚未载入
     * @param c
     * @return
     */
    public final String getAsString(Config c) {
        ConfigEn ce = findConfigMap().get(c);
        if (ce == null)
            throw new NullPointerException("Config not found, config=" + c);
        String value = ce.getValue();
        if (value != null && !"".equals(value)) {
            return value;
        }
        return c.getValue();
    }

    /**
     * 以Boolean方式获取配置信息。
     * @param c
     * @return
     * @see #getAsString(name.huliqing.qblog.enums.Config) 
     */
    public final Boolean getAsBoolean(Config c) {
        ConfigEn ce = findConfigMap().get(c);
        if (ce == null)
            throw new NullPointerException("Config not found, config=" + c);
        Boolean value = QFaces.convertToBoolean(ce.getValue());
        if (value == null)
            value = QFaces.convertToBoolean(c.getValue());

        if (value == null)
            throw new RuntimeException("目标不能转换为Boolean, config=" + c);

        return value;
    }

    /**
     * 以Integer方式获取配置信息
     * @param c
     * @return
     * @see #getAsString(name.huliqing.qblog.enums.Config) 
     */
    public final Integer getAsInteger(Config c) {
        ConfigEn ce = findConfigMap().get(c);
        if (ce == null)
            throw new NullPointerException("Config not found, config=" + c);
        Integer value = QFaces.convertToInteger(ce.getValue());
        if (value == null)
            value = QFaces.convertToInteger(c.getValue());

        if (value == null)
            throw new RuntimeException("目标不能转换为Integer, config=" + c);

        return value;
    }

    /**
     * 查找配置
     * @param c
     * @return
     */
    public final ConfigEn findConfig(Config c) {
        return findConfigMap().get(c);
    }

    public final List<ConfigEn> findConfigs() {
        reloadConfigs();
        return configs;
    }

    public final Map<Config, ConfigEn> findConfigMap() {
        reloadConfigs();
        return this.configMap;
    }

    public final Boolean updateConfig(ConfigEn c) {
        needToReloadConfigs = true;
        return ConfigCache.getInstance().update(c);
    }

    /**
     * 更新或保存新的Config,如果已经存在与ce相同名称的config,
     * 则更新该config,否则插入新数据config.
     * @param ce
     */
    public final void saveOrUpdate(ConfigEn ce) {
        needToReloadConfigs = true;
        ConfigEn old = ConfigCache.getInstance().find(ce.getName());
        if (old != null) {
            old.setValue(ce.getValue());
            ConfigCache.getInstance().update(old);
        } else {
            ConfigCache.getInstance().save(ce);
        }
    }

    /**
     * 获取所有配置信息,如果不存在，则创建并载入默认的配置信息
     * @return
     */
    private final void reloadConfigs() {
        if (needToReloadConfigs) {
            needToReloadConfigs = false;
            configMap.clear();
            configs.clear();
            Config[] cs = Config.values();
            for (Config c : cs) {
                ConfigEn ce = ConfigCache.getInstance().find(c.name());
                if (ce == null) {
                    ce = new ConfigEn();
                    ce.setName(c.name());
                    ce.setValue(c.getValue());
                    ConfigCache.getInstance().save(ce);
                }
                configMap.put(c, ce);
                configs.add(ce);
            }
        }
    }

    /**
     * 将所有的配置信息恢复到系统的默认值。
     */
    public final void resetAllConfig() {
        needToReloadConfigs = true;
        Config[] cs = Config.values();
        for (Config c : cs) {
            ConfigEn ce = ConfigCache.getInstance().find(c.name());
            if (ce != null) {
                ce.setValue(c.getValue());
                ConfigCache.getInstance().update(ce);
            } else {
                ce = new ConfigEn();
                ce.setName(c.name());
                ce.setValue(c.getValue());
                ConfigCache.getInstance().save(ce);
            }
        }
    }

}
 