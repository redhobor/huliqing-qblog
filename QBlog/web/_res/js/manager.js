function changeLanguage(obj) {
    var href = window.location.href;
    if (href.indexOf("?") != -1) {
        href = href.substring(href.indexOf("?"));
        if (href.indexOf("language=") != -1) {
            href = href.substring(0, href.indexOf("language=") + 9);
            href += obj.value;
        } else {
            href += "&amp;language=" + obj.value;
        }
    } else {
        href += "?language=" + obj.value;
    }
    window.location.href = href;
}