//全局异步XHR权限验证
$.ajaxSetup({
    complete: function (xhr, status) {
    	if (xhr.status == 401) {
        	$.messager.alert("提示信息", "登录超时，点击确认按钮跳转至登录页。", "warning", function(){
        		window.top.location.href = "/";
        	});
        }
    }
});


//增加验证validatebox验证规则
$.extend($.fn.validatebox.defaults.rules, {
    equals: {
		validator: function(value,param){
			return value == $(param[0]).val();
		},
		message: '两次密码输入不一致，请重新输入。'
    }
});