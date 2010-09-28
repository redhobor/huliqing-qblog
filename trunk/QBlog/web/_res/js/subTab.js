
function loadTabEvent(tabPanel, currentSubTabName) {
    var panel = document.getElementById(tabPanel);
    var children;
    var active;
    if (panel.hasChildNodes()) {
        children = panel.childNodes;
        children = findDivs(children);
        for (var i = 0; i < children.length; i++) {
            var child = children[i];
            var subTabName = findSubTabName(child.id);
            active = (subTabName == currentSubTabName);
            tabEventMouseEvent(child, active);
        }
    }
    function tabEventMouseEvent(child, active) {
        child.style.cssText = "cursor:pointer;width:122px;height:35px;line-height:30px;margin:10px 0 0 0;text-align:center;float:right;border-right:3px solid #CAD1D9;" +
        "background:url(/_res/image/menu-off.png) no-repeat;font-weight:bold;";
        var _fun_over = function() {this.style.color = "orange";}
        var _fun_out = function() {this.style.color = "gray";}
        if (active) {
            child.style.color = "orange";
            child.style.background = "url(/_res/image/menu-on.png) no-repeat";
        } else {
            child.style.color = "gray";
            child.onmouseover = _fun_over;
            child.onmouseout = _fun_out;
        }
        child.onclick = function() {
            location.href = child.id;
        }
    }
    // find all sub div(tab header)
    function findDivs(nodes) {
        var _arr = new Array();
        for (var i = 0; i < nodes.length; i++) {
            var _currentNode = nodes.item(i);
            if (_currentNode.id != "clear" && _currentNode.tagName != null && 
                (_currentNode.tagName == "DIV" || _currentNode.tagName == "div")) {
                _arr.push(_currentNode);
            }
        }
        return _arr;
    }
    function findSubTabName(url) {
        // e.g. url = /admin/blog/articleAdd.faces?
        var subTabName;
        var index = url.indexOf("/admin/");
        if (index != -1) {
            var temp = url.substring(index + 7);
            var start = temp.indexOf("/");
            var end = temp.indexOf(".");
            subTabName = temp.substring(start + 1, end);
        }
        return subTabName;
    }
}
