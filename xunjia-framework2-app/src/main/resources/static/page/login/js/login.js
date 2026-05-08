addEventListener("load", function() {
	setTimeout(hideURLbar, 0);
}, false);

$(function(){
    if (verify){
        $("#drag").show();
    	$("#drag").drag();
    }

	$("#login-form").ajaxForm({
		url: '/doLogin',
		type: 'post',
		dataType: 'json',
		beforeSubmit: function(){
		    if (verify) {
		        if (!verifyResult){
		            $(".err_msg").text("请拖动滑块验证。");
                	$(".err_msg").show();
		        }
		        return verifyResult;;
		    }
		    return true;
		},
		success: function(data){
			if (data.result){
			    if (data.data == "0"){
				    document.location.href = data.url;
				} else if (data.data == "1") {
				    showChangePasswordDialog("修改初始密码");
				} else if (data.data == "2") {
				    $.messager.defaults = { ok: "是", cancel: "否" };
                    $.messager.confirm("密码即将过期", data.msg, function(r){
                        if (r){
                            showChangePasswordDialog("修改登录密码");
                        } else {
                            document.location.href = data.url;
                        }
                    });
				}
			} else {
				$(".err_msg").text(data.msg);
				$(".err_msg").show();
				if (data.data == "1"){
				    verify = true;
				    var btnHeight = $(".login").height();
				    $(".login").height(btnHeight + 1);
				    $("#drag").show();
				    $("#drag").resetDrag();
				    $(".login").height(btnHeight);
				} else if (data.data == "2"){
				    showChangePasswordDialog("修改登录密码");
				}
			}
		}
	});
});

function hideURLbar() {
	window.scrollTo(0, 1);
}

function showChangePasswordDialog(title){
    var addDialog = $("<div></div>").dialog({
    	title: title,
    	width: 530,
    	height: 290,
    	modal: true,
    	iconCls: "fa fa-pencil-square-o",
    	href: '/initUser',
    	onClose : function() {
            $(this).dialog('destroy');
        },
        onLoad: function(){
            $("#user-username").textbox('setValue', $("#username").val());
        },
        buttons: [
        	{
        		text: '保存',
        		iconCls: 'fa fa-save',
        		handler: function(){
            		submitOrgForm("edit-user-password-form", addDialog, "/submitInitUser");
        		}
        	},
        	{
        		text: '关闭',
        		iconCls: 'fa fa-close',
        		handler: function() {
        			addDialog.dialog('destroy');
        		}
        	}
        ]
    });
}

function submitOrgForm(formId, dialog, url){
	$("#" + formId).form('submit', {
		url: url,
		onSubmit: function(){
			return $(this).form("validate");
		},
		success: function(data){
			data = $.parseJSON(data);
			if (data.result){
				dialog.dialog("destroy");
				$.messager.alert("提示信息", data.msg, "info");
			} else {
				$.messager.alert("提示信息", data.msg, "error");
			}
		}
	});
}

function unionLogin(){
    window.location.href = ssoUrl + "oauth/authorize?client_id=" + ssoClientId
        + "&response_type=code"
        + "&state=dlogin";
}