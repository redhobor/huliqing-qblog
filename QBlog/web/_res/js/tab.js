
function loadTabEvent(tabPanel, currentTabName) {
    var panel = document.getElementById(tabPanel);
    var children;
    var active;
    if (panel.hasChildNodes()) {
        children = panel.childNodes;
        children = findDivs(children);
        for (var i = 0; i < children.length; i++) {
            var child = children[i];
            var tabName = findTabName(child.id);
            active = (tabName == currentTabName);
            tabEventMouseEvent(child, active);
        }
    }
    function tabEventMouseEvent(child, active) {
        child.style.cssText = "cursor:pointer;float:left;text-align:center;background:url(/_res/image/tab-off.png) no-repeat;width:150px;height:30px;line-height:35px;margin-bottom:0;margin-left:0;";
        var _fun_over = function() {
            this.style.color = "orange";
            this.style.fontWeight = "bold";
        }
        var _fun_out = function() {
            this.style.color = "";
            this.style.fontWeight = "";
        }
        if (active) {
            child.style.color = "orange";
            child.style.fontWeight = "bold";
            child.style.background = "url(/_res/image/tab-on.png) no-repeat";
        } else {
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
    function findTabName(url) {
        // e.g. url = /admin/blog/articleAdd.faces?
        var tabName;
        var index = url.indexOf("/admin/");
        if (index != -1) {
            var temp = url.substring(index + 7);
            tabName = temp.substring(0, temp.indexOf("/"));
        }
        return tabName;
    }
}
