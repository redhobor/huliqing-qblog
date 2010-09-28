AjaxModule = {
    clientId : "AjaxModule",
    componentClass : "name.huliqing.qblog.component.AjaxModule",
    ajaxModulePanel : null, // Include the modulePanel

    isChrome : function() { 
        return (navigator.userAgent.toLowerCase().indexOf("chrome") != -1);
    },
    isIE : function() {
        return (navigator.userAgent.toLowerCase().indexOf("msie") != -1);
    },
    isSafari : function() {
        return (navigator.userAgent.toLowerCase().indexOf("safari") != -1);
    },
    createModule : function(moduleId) {
        if (!this.ajaxModulePanel)
            this.ajaxModulePanel = Q.get("ajaxModulePanel");
        this.ajaxModulePanel.innerHTML = "";
        Q.F.loading();
        var _self = this;
        var ajax = Q.F.Ajax.build(this.clientId);
        ajax.put("componentClass", this.componentClass);
        ajax.put("moduleId", moduleId);
        var processWhenOK = function() {
            Q.F.loading(false);
            var result = ajax.request.responseText;
            _self.initAjaxModule(result, moduleId);
        }
        ajax.setProcess(processWhenOK);
        ajax.get();
    },
    initAjaxModule : function(moduleContent, moduleId) {
        var chrome = this.isChrome();
        var safari = this.isSafari();
        var module = null;
        var iframe = document.createElement("iframe");

        if (chrome || safari) {
            module = new AjaxModule.Module(iframe, moduleId);
            this.ajaxModulePanel.appendChild(module.getPanel());
        } else {
            var modulePanel = document.createElement("div");
            module = new AjaxModule.Module(modulePanel, moduleId);
            this.ajaxModulePanel.appendChild(iframe);
            this.ajaxModulePanel.appendChild(module.getPanel());
        }

        iframe.setAttribute("frameborder", "0", 0);
        iframe.setAttribute("frameSpacing", "0");
        iframe.setAttribute("marginWidth", "0");
        iframe.setAttribute("marginHeight", "0");
        iframe.setAttribute("scrolling", "no");
        iframe.setAttribute("width", "100%");
        iframe.setAttribute("border", "0");

        var doc = iframe.contentWindow.document;
        doc.open(); // 必要的，否则在FF下可能一直显示loading动画
        doc.write(moduleContent);
        doc.close();
        module.updateState(doc.body.innerHTML);
        module.enableHeader(true);
        module.enablePanel(true);

        if (chrome || safari) {
            this.setWinHeight(iframe);
        } else {
            this.ajaxModulePanel.removeChild(iframe);
        }
    },
    setWinHeight : function (obj) {
        var win=obj;
        if (document.getElementById) {
            if (win && !window.opera) {
                if (win.contentDocument && win.contentDocument.body.offsetHeight) {
                    win.height = win.contentDocument.body.offsetHeight + 20;
                } else if(win.Document && win.Document.body.scrollHeight) {
                    win.height = win.Document.body.scrollHeight + 20;
                }
            }
        } 
    }
}
AjaxModule.Manager = {
    header : null,
    top : null,
    navigation : null,
    menu : null,
    content : null,
    footer : null,

    // 在页面装载后，载入ModuleGroup,并载入各Group已经存在的Module
    initAllGroup : function() {
        this.header = new AjaxModule.ModuleGroup("HEADER");
        this.top = new AjaxModule.ModuleGroup("TOP");
        this.navigation = new AjaxModule.ModuleGroup("NAVIGATION");
        this.menu = new AjaxModule.ModuleGroup("MENU");
        this.content = new AjaxModule.ModuleGroup("CONTENT");
        this.footer = new AjaxModule.ModuleGroup("FOOTER");
    },
    enableAllGroup : function() {
        this.header.setEnabled(true);
        this.top.setEnabled(true);
        this.navigation.setEnabled(true);
        this.menu.setEnabled(true);
        this.content.setEnabled(true);
        this.footer.setEnabled(true);
    },
    disableAllGroup : function() {
        this.header.setEnabled(false);
        this.top.setEnabled(false);
        this.navigation.setEnabled(false);
        this.menu.setEnabled(false);
        this.content.setEnabled(false);
        this.footer.setEnabled(false); 
    },
    // 通过鼠标位置，查询当前被激活的Group
    findActiveGroup : function(ex, ey) {
        if (ex == null || ey == null) {
            var e = Q.U.getMousePosition();
            ex = e.x;
            ey = e.y;
        }
        var g = null;
        if (this.header.isMouseover(ex, ey)) {
            g = this.header;
        } else if (this.top.isMouseover(ex, ey)) {
            g = this.top;
        } else if (this.navigation.isMouseover(ex, ey)) {
            g = this.navigation;
        } else if (this.menu.isMouseover(ex, ey)) {
            g = this.menu;
        } else if (this.content.isMouseover(ex, ey)) {
            g = this.content;
        } else if (this.footer.isMouseover(ex, ey)) {
            g = this.footer;
        }
        return g;
    },
    // ex, ey鼠标位置,根据鼠标位置激活某个Group
    findAndActiveGroup : function(ex, ey) {
        this.header.setActived(false);
        this.top.setActived(false);
        this.navigation.setActived(false);
        this.menu.setActived(false);
        this.content.setActived(false);
        this.footer.setActived(false);
        var group = this.findActiveGroup(ex, ey);
        if (group != null)
            group.setActived(true);
    },
    // 添加Module到Group中, Module = AjaxModule.Module
    toAttach : function(module) {
        if (module == null) return;
        var group = this.findActiveGroup(); 
        if (group != null)
            group.attach(module);
    },
    toDetach : function(module) {
        if (module == null) return;
        this.enableAllGroup();
        module.enablePanel(true);
        if (module.getGroup() != null)
            module.getGroup().detach(module);
    },
    toRemove : function(module) {
        if (module == null) return;
        if (module.getGroup() != null)
            module.getGroup().detach(module);
        // 在从Group中detach之后，再从document.body中移除,为了确保body中存在ModulePanel
        // 应该先append.因为：1.ModulePanel可能仍存在于Group中，2.也可能已经detach到document.body中
        document.body.appendChild(module.getPanel());
        document.body.removeChild(module.getPanel());
    },
    // Collect all configs as string
    // Format = groupA,moduleId,moduleId;groupB,moduleId,moduleId;groupC,moduleId,moduleId,moduleId
    // 以分号(；)分格各group,以逗号分隔各module,第一个逗号前为group name.
    collectGroupConfigAll : function() {
        var config = this.collectGroupConfig(this.header);
        config += ";" + this.collectGroupConfig(this.top);
        config += ";" + this.collectGroupConfig(this.navigation);
        config += ";" + this.collectGroupConfig(this.menu);
        config += ";" + this.collectGroupConfig(this.content);
        config += ";" + this.collectGroupConfig(this.footer);
        Q.debug("config=" + config);
        return config;
    },
    // group -> AjaxModule.ModuleGroup
    collectGroupConfig : function(group) {
        var gc = group.name + ",";
        var module = group.getFirstModule();
        while (module != null) {
            gc += module.getModuleId() + ",";
            module = module.getNext();
        }
        gc = gc.substring(0, gc.length - 1);
        return gc;
    }
}
AjaxModule.ModuleGroup = function(name) {
    // Group name
    this.name = name;

    // 第一个Module
    this.firstModule = null;

    // Group 窗口
    this.panel = Q.get(name);
    // Panel 的原始样式
    this.panelOldStyle = this.panel.style.cssText;
    // Title 窗口,这是关于当前Group的title信息，ID，在组件UIModuleGroup中指定,需对应
    this.title = Q.get(name + ":title");

    // 是否状态为开启的，可能显示边框
    this.enabled = false;
    // 是否为激活的，即当前选中的Group
    this.actived = false;
    // 是否显示Group标题
    this.displayTitle = false;

    this.init = function() {
        Q.debug("load module group, name=" + this.name);
        if (this.panel == null)
            alert("Module group panel not found! group name=" + this.name);
        else
            this.loadModules();

        if (this.title != null) {
            this.title.style.color = "gray";
        }
    }
    this.loadModules = function() {
        // 找出所有Module
        var elements = new Array();
        var ids = new Array();
        var children = this.panel.childNodes;
        for (var i = 0; i < children.length; i++) {
            var child = children.item(i);
            if (child.id != null && child.id.indexOf("module:") != -1) {
                // 不要在这里生成AjaxModule.Module,会破坏Group的结构
                var id = child.id;
                id = id.substring(id.indexOf("module:") + 7);
                id = id.substring(0, id.indexOf(":"));
                elements.push(child);
                ids.push(id);
                Q.debug("Find element id=" + child.id + ", module id=" + id);
            }
        }
        // 生成Group下的所有Module
        var modules = new Array();
        for (var k = 0; k < elements.length; k++) {
            var module = new AjaxModule.Module(elements[k], ids[k]);
            modules.push(module);
            Q.debug("module.id=" + module.contentPanel.id);
        }
        // 添加所有Modules
        if (modules.length > 0) {
            this.clearAndInitFirstModule(modules[0]);
            var pre = null;
            for (var n = 0; n < modules.length; n++) {
                pre = modules[n];
                if (modules.length >= n) {
                    pre.attach(modules[n + 1]);
                }
            }
        }
    }
    this.setEnabled = function(enabled) {
        if (enabled) {
            this.enabled = true;
            this.panel.style.cssText = this.panelOldStyle + ";margin:3px;border:1px solid GRAY";
            if (this.title != null) Q.U.open(this.title);
        } else {
            this.enabled = false;
            this.panel.style.cssText = this.panelOldStyle;
            if (this.title != null) Q.U.close(this.title);
        }
    }
    this.setActived = function(actived) {
        if (actived) {
            this.actived = true;
            this.panel.style.cssText = this.panelOldStyle + ";margin:3px;border:1px solid blue";
        } else {
            actived = false;
            // 复原激活状态
            this.setEnabled(this.enabled);
        }
    }
    this.setDisplayTitle = function(displayTitle) {
        this.displayTitle = displayTitle;
    }
    this.isMouseover = function(ex, ey) {
        var p = Q.U.getPosition(this.panel);
        var x1 = p.x;
        var y1 = p.y;
        var x2 = x1 + this.panel.offsetWidth;
        var y2 = y1 + this.panel.offsetHeight;
        return (ex >= x1 && ex <= x2 && ey >= y1 && ey <= y2);
    }
    this.isMouseInTitle = function(ex, ey) {
        if (!this.title) return false;
        var p = Q.U.getPosition(this.title);
        var x1 = p.x;
        var y1 = p.y;
        var x2 = x1 + this.title.offsetWidth;
        var y2 = y1 + this.title.offsetHeight;
        var result = (ex >= x1 && ex <= x2 && ey >= y1 && ey <= y2);
        Q.debug("Mouse in title=" + result);
        return result;
    }
    this.clearAndInitFirstModule = function(module) {
        this.setFirstModule(module);
        if (module != null) {
            this.panel.appendChild(module.getPanel());
            module.getPanel().style.position = "";
            module.updateState();
        }
    }
    this.setFirstModule = function(module) {
        if (module != null) {
            module.setGroup(this);
        }
        this.firstModule = module;
    }
    this.getFirstModule = function() {
        return this.firstModule;
    }
    this.attach = function(module) {
        var _self = this;
        // 找出indexM, 这个module将append到 indexM后面.
        // 如果indexM找不到(means:未初始化firstModule)，则初始化当前group
        var e = Q.U.getMousePosition();
        var mit = this.isMouseInTitle(e.x, e.y);
        var indexM = null;
        // 如果mouse in title,则attach到group第一个位置,否则需要找到indexM,
        // indexM即鼠标所在Group中的某个Module,如果找到indexM,则attach到indexM后面
        if (!mit) {
            var currM = this.firstModule;
            while (currM != null) {
                if (currM.isMouseover(e.x, e.y)) {
                    indexM = currM;
                    break;
                }
                currM = currM.getNext();
            }
        }
        // 回调
        var callback = function() {
            if (_self.firstModule == null) {
                Q.debug("init firstModule.");
                _self.clearAndInitFirstModule(module);
            } else {
                Q.debug("mit=" + mit);
                if (mit) {
                    Q.debug("Attach to first ");
                    _self.getFirstModule().attachBefore(module);
                } else {
                    if (indexM != null) {
                        Q.debug("Attach to after index module.")
                        indexM.attach(module);
                    } else {
                        Q.debug("Where to attach???")
                    }
                }
            }
            module.enableHeader(false);
            module.enablePanel(false);
            AjaxModule.Manager.disableAllGroup();
        }
        // Move dyn.
        var srcPanel = module.getPanel();
        var targetPanel = indexM != null ? indexM.getPanel() : this.panel;
        // Start position
        var s = Q.U.getPosition(srcPanel);
        var t = Q.U.getPosition(targetPanel);
        if (indexM != null) {
            t.y += Q.U.getElementSize(targetPanel)[1];
        }
        // 必须的，否则在某些浏览器下(e.g.Chrome,IE)会变成modulePanel相对于父窗口的定位
        document.body.appendChild(srcPanel);
        // 移动mPanel到当前group的panel位置
        Q.U.move(srcPanel, t.x, t.y, 200, callback, s.x, s.y);
    }
    // 剥离出当前Group之后再添加到document中，以再次重新attach到其它Group中
    this.detach = function(module) {
        // 移除
        module.detach();
        // 重新添加到body中
        module.updateState();
    }
    this.getPanel = function() {
        return this.panel;
    }
    this.init();
}
AjaxModule.Module = function(contentPanel, moduleId) {
    // Module所在的Group, type = AjaxModule.ModuleGroup,这标识了当前Module所在的Group,
    // 当进行Attach时应该正确更新parent
    this.group = null;
    // Module id
    this.moduleId = moduleId;
    // Previous and next module
    this._previous = null;
    this._next = null;
    this.panel = null;
    this.header = null;
    this.contentPanel = contentPanel;
    this.value = null;
    // Drag button
    this.drag = null;
    // Undeploy button
    this.undeploy = null;

    this.init = function() {
        this.panel = document.createElement("div");
        this.header = this.createHeader();
        this.panel.appendChild(this.header);
        this.panel.appendChild(this.contentPanel);

        // css
        this.panel.style.padding = this.panel.style.margin = 0;

        var _self = this;
        var fun_attach = function() {
            AjaxModule.Manager.toAttach(_self);
        }
        var fun_detach = function() {
            AjaxModule.Manager.toDetach(_self);
        }
        this.undeploy.onclick = function() {
            AjaxModule.Manager.toRemove(_self);
        }
        this.panel.onmouseover = function() {
            _self.enableHeader(true);
            _self.enablePanel(true);
        }
        this.panel.onmouseout = function() {
            _self.enableHeader(false);
            _self.enablePanel(false);
        }
        Q.U.attachEvent(this.drag, "onmousedown", fun_detach);
        Q.U.attachEvent(this.drag, "onmouseup", fun_attach);
        this.toEnableDrag(this.drag, this.panel);
        this.enableHeader(false);
        this.enablePanel(false);
    } 
    this.createHeader = function() {
        var h = document.createElement("table");
        var t = Q.U.createTable(h, 1, 2)
        this.drag = document.createElement("div");
        this.drag.innerHTML = "拖动并部署";
        this.drag.style.background = "#F0F0F0";
        this.undeploy = document.createElement("img");
        this.undeploy.src = "/_res/image/button-delete.gif";
        this.undeploy.alt = "";
        this.undeploy.title = "取消部署";
        this.undeploy.style.verticalAlign = "middle";
        this.undeploy.style.cursor = "pointer";
        t[0][0].appendChild(this.drag);
        t[0][1].appendChild(this.undeploy);
        t[0][0].style.cssText = "height:20px;line-height:20px;";
        h.style.zIndex = 100000;
        h.style.border = "2px dotted gray";
        h.style.position = "absolute";
        h.style.background = "white";
        h.cellSpacing = h.cellPadding = 0;
        h.style.padding = h.style.margin = 0; 
        h.setAttribute("cellspacing", "0");
        h.setAttribute("cellpadding", "0");
        return h;
    }
    this.enableHeader = function(enabled) {
        enabled ? Q.U.open(this.header) : Q.U.close(this.header);
    }
    this.enablePanel = function(enabled) {
        if (enabled) {
            this.panel.style.border = "1px solid blue";
            this.panel.style.background = "white";
        } else {
            this.panel.style.border = "";
            this.panel.style.background = "";
        }
    }
    this.getPanel = function() {
        return this.panel;
    }
    this.getGroup = function() {
        return this.group;
    }
    this.setGroup = function(group) {
        this.group = group;
    }
    this.updateState = function(value) {
        if (value != null)
            this.value = value;
        if (this.isIframe()) {
            var doc = this.contentPanel.contentWindow.document;
            doc.open(); 
            doc.write(this.value);
            // 必要的，否则在FF下可能一直显示loading动画
            doc.close();
        } else {
            if (this.value != null && this.value != "") {
                try {
                    this.contentPanel.innerHTML = this.value;
                } catch (e) {
                    var notice = "<div style=\"margin-top:22px;border:2px solid gray;\" >";
                    notice += "<div style=\"color:red;font-weight:bold;\" >抱歉：这个模块在生成预览过程中遇到异常</div>";
                    notice += "<p>可以拖动这个模块继续进行部署";
                    notice += "<p>但是您可能需要在部署或保存配置后才能看到完整效果";
                    notice += "<p>如果仍然遇到问题，请偿试用其它浏览器来进行配置";
                    notice += "</div>";
                    this.contentPanel.innerHTML = notice;
                }
            }
        }
    }
    this.isIframe = function() {
        return (this.contentPanel.tagName == "iframe" || this.contentPanel.tagName == "IFRAME");
    }
    this.isMouseover = function(ex, ey) {
        var p = Q.U.getPosition(this.panel);
        var x1 = p.x;
        var y1 = p.y;
        var x2 = x1 + this.panel.offsetWidth;
        var y2 = y1 + this.panel.offsetHeight;
//        Q.debug("x1=" + x1 + ", x2=" + x2 + ", y1=" + y1 + ", y2=" + y2 + ",group name=" + this.name);
        return (ex >= x1 && ex <= x2 && ey >= y1 && ey <= y2);
    }
    // Test
    this.toEnableDrag = function(id, parentId) {
        var dragObj = Q.get(id);
        var moveObj = Q.get(parentId != null ? parentId : id);
        dragObj = Q.get(id);
        dragObj.style.cursor = "move";
        moveObj.style.zIndex = Q.U.zIndex++;
        moveObj.offsetX = moveObj.offsetY = 0;
        event_onmouseup = function() { 
            document.onmousemove = "";
            Q.U.detachEvent(null, "onmouseup", event_onmouseup);
        }
        dragObj.onmousedown = function() {
            var e = Q.U.getMousePosition();
            var p = Q.U.getPosition(moveObj);
            moveObj.offsetX = e.x - p.x;
            moveObj.offsetY = e.y - p.y;
            Q.debug("p.x=" + p.x + ", p.y=" + p.y);
            Q.debug("e.x=" + e.x + ", e.y=" + e.y);
            Q.debug("offsetX=" + moveObj.offsetX + ", offsetY=" + moveObj.offsetY);
            // 必须append到body中，否则可能在IE,FF,Chrome各不同浏览器中偏移计算存在差异。
            document.body.appendChild(moveObj);
            moveObj.style.position = "absolute";
            moveObj.style.left = (e.x - moveObj.offsetX) + "px";
            // 在IE,FF,Chrome中的行为存在较大差异，所以直接使用一个相对于mouse位置的偏移
            moveObj.style.top  = (e.y - 15) + "px";

            document.onmousemove = function() {
                window.getSelection ? window.getSelection().removeAllRanges() : document.selection.empty();
                var e = Q.U.getMousePosition();
                moveObj.style.left = (e.x - moveObj.offsetX) + "px";
                moveObj.style.top  = (e.y - 15) + "px";
                Q.debug("set to left=" + moveObj.style.left + ", top=" + moveObj.style.top);
                AjaxModule.Manager.findAndActiveGroup(e.x, e.y);
            }
            Q.U.attachEvent(null, "onmouseup", event_onmouseup);
        }
        Q.U.attachEvent(moveObj, "onmousedown", function(){moveObj.style.zIndex = Q.U.zIndex++;});
    }
    this.getPrevious = function() {
        return this._previous;
    }
    this.getNext = function() {
        return this._next;
    }
    this.attachBefore = function(module) {
        if (module == null)
            return;
        var oldPrevious = this._previous;
        this._previous = module;
        module.setGroup(this.getGroup());
        module._previous = oldPrevious;
        module._next = this;
        if (oldPrevious != null) {
            oldPrevious._next = module;
        } else { // 可能insert到Group的第一个位置
            this.getGroup().setFirstModule(module);
        }
        this.getGroup().getPanel().insertBefore(module.getPanel(), this.getPanel());
        module.getPanel().style.position = "";
        // Update state(maybe module panel is a iframe)
        module.updateState();
    }
    // Attach 到当前Module的后面
    this.attach = function(module) {
        if (module == null)
            return;
        var oldNext = this._next;
        this._next = module;
        module.setGroup(this.getGroup());
        module._previous = this;
        module._next = oldNext;
        if (oldNext != null) {
            oldNext._previous = module;
            this.getGroup().getPanel().insertBefore(module.getPanel(), oldNext.getPanel());
        } else {
            this.getGroup().getPanel().appendChild(module.getPanel());
        }
        module.getPanel().style.position = "";
        // Update state(maybe module panel is a iframe)
        module.updateState();
    }
    this.detach = function() {
        // 这可能当前Module尚未添加到任何Group中
        if (this.getGroup() == null) {
            return;
        }
        // 重新连接好Previous及Next,重要
        var p = this._previous;
        var n = this._next;
        if (p != null)
            p._next = n;
        if (n != null) 
            n._previous = p;
        // 或许detach的是Group中的第一个,则将下一个设为Group中的第一个,
        // 注：n若为null,则表示当前Group中已经不存在任何Module,所以不用担心 setFirstModule(null)
        if (p == null) {
            this.getGroup().setFirstModule(n);
        }
        // 将当前module断开,重要的。
        this._previous = null;
        this._next = null;

        // 不需要从当前group中remove出element,由document.body.appendChild搞定
        // 这在toEnableDrag中可见到
//        this.getGroup().getPanel().removeChild(this.getPanel());

        // 除去Group属性
        this.setGroup(null);
    }
    this.getModuleId = function() {
        return this.moduleId;
    }
    // ---- init
    this.init();
}
// End
