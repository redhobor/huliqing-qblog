
function replyReply(replyId) {
	// 回复目标
	var reply_replyBy = document.getElementById(replyId + ":replyBy");
	// 内容
	var content = document.getElementById("re_content");
	// 格式如下:   
	// @1234,胡李青
	// 每一个email回复信息占一行，由 "@" + replyId + 逗号 + replyBy 组成
	var target = "@" + replyId + "," + reply_replyBy.value;
	content.value = target + "\r\n" + content.value;
	content.focus();
}

function editReply(replyId) {
	// 准备被编辑的回复信息
	var edit_replyId = document.getElementById(replyId + ":replyId");
	var edit_title = document.getElementById(replyId + ":title");
	var edit_replyBy = document.getElementById(replyId + ":replyBy");
	var edit_email = document.getElementById(replyId + ":email");
	var edit_content = document.getElementById(replyId + ":content");
	
	// re_replyId, re_title, re_replyBy, re_email, re_content
	var reply = document.getElementById("re_replyId");
	var title = document.getElementById("re_title");
	var replyBy = document.getElementById("re_replyBy");
	var email = document.getElementById("re_email");
	var content = document.getElementById("re_content");
	
	// 将选中的reply赋值到隐藏控件中，这些控件主要是要被ajaxSupport引用，
	// 进行ajax编辑
	reply.value = edit_replyId.value;
	title.value = edit_title.value;
	replyBy.value = edit_replyBy.value;
	email.value = edit_email.value;
	content.value = edit_content.value;
	location.href = "#p_reply";
}

function replyOnStart(titleId, replyById, emailId, contentId) {
	var title = document.getElementById(titleId);
	var replyBy = document.getElementById(replyById);
	var email = document.getElementById(emailId);
	var content = document.getElementById(contentId);
	if (title.value == "") {
		alert("Please input \"Title\" ");
		title.focus();
		return false;
	}
	if (replyBy.value == "") {
		alert("Please input your \"Name\" ");
		replyBy.focus();
		return false;
	}
	if (email.value == "") {
		alert("Please input your \"Email\" ");
		email.focus();
		return false;
	}
	if (content.value == "") {
		alert("Please input \"Content\" ");
		content.focus();
		return false;
	}
	var isOK = true;
	var regex = new RegExp("^[_a-z0-9]+(\\.[_a-z0-9]+)*@([_a-z0-9]+\\.)+[a-z0-9]{2,4}$");
	if (!regex.test(email.value)) {
		isOK = confirm("您输入的Email地址可能有错误！要继续回复吗？");
	}
	Q.F.loading(isOK);
	return isOK;
}

function replyOnFinish(contentId, replyId) {
	var obj1 = document.getElementById(contentId);
	var obj2 = document.getElementById(replyId);
	obj1.value = "";
	obj2.value = "";
//	location.href = "#p_replies";
}

function deleteReply(replyId) {
	// 必须设置隐藏控件re_replyId的值,因为ajaxSupport必须引用到这个控件的值
	var replyIdObj = document.getElementById("re_replyId");
	replyIdObj.value = replyId
	// 寻找deleter(AjaxSupport控件),调用ajax进行delete
	Q.F.find("deleter").ajaxInvoke();
}
function deleteReplyOnFinish() {
	// 删除之后必须清除re_replyId控件的值。
	var replyIdObj = document.getElementById("re_replyId");
	replyIdObj.value = "";
}