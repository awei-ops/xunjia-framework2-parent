$(function() {
	//初始化默认首页
	var defaultMenuUrl = $("#default-menu-url").val();
	if (defaultMenuUrl != ""){
		addTab($("#default-menu-name").val(), defaultMenuUrl, $("#default-menu-icon").val(), $("#default-menu-integrate-type").val());
	}
	
	//初始化修改密码对话框
	$("#edit-user-password-dialog").dialog({
		title: '修改密码',
		width: 630,
		height: 294,
		closed: true,
		cache: false,
		modal: true,
		iconCls: "fa fa-pencil-square-o",
		buttons: [
			{ text: '保存', iconCls: 'fa fa-save', handler: submitEditPasswordForm },
			{ text: '关闭', iconCls: 'fa fa-close', handler: function(){ $("#edit-user-password-dialog").dialog('close'); } }
		]
	});
	
	//初始化首页菜单树
	$.ajax({
		url: '/desktop/getMenuTrees',
		type: 'get',
		dataType: 'json',
		success: function(data){
			$.each(data, function(i, n){
				$("#menu_tree_" + i).tree({
					data: n,
					animate: true,
					onClick: function(node){
						var attributes = node.attributes;
						var url = attributes.url;
						var menuId = attributes.menuId;
						var integrateType = attributes.integrateType;
						if (url != null && url != ''){
							if (url.indexOf("?") == -1){
								url += "?menuId=" + menuId;
							} else {
								url += "&menuId=" + menuId;
							}
							addTab(node.text, url, node.iconCls, integrateType);
						} else {
							$(this).tree("toggle", node.target);
						}
					}
				});
			});
		}
	});

	// 退出系统
	$("#logout").on('click', function() {
		$.messager.confirm('注销确认', '您是否确认要退出当前系统？', function(r){
			if (r){
				window.location.href = "/logout";
			}
		});
	});
	
	// 设置按钮的下拉菜单
	$('.super-setting-icon').on('click', function() {
		$('#mm').menu('show', {
			top: 50,
			left: document.body.scrollWidth - 160
		});
	});
	
	// 修改主题
	$('#themeSetting').on('click', function() {
		var themeWin = $('#win').dialog({
			width: 460,
			height: 260,
			modal: true,
			title: '主题设置',
			buttons: [{
				text: '保存',
				id: 'btn-sure',
				handler: function() {
					themeWin.panel('close');
					// css
					var themeName = $(".themeItem ul li.themeActive>div").attr('class');
					initTheme(themeName);
					saveTheme(themeName);
				}
			}, {
				text: '关闭',
				handler: function() {
					themeWin.panel('close');
				}
			}],
			onOpen: function() {
				$(".themeItem").show();
			}
		});
	});
	
	// 初始化主题
	var initTheme = function(themeName) {
		if(themeName == null) {
			themeName = $('#themeCss').attr('href').split('/').pop().split('.css')[0];
			// 添加勾选状态
			$(".themeItem ul li").removeClass('themeActive');
			$('.themeItem ul li .' + themeName).parent().addClass('themeActive');
			return;
		}
		var themeUrl = $('#themeCss').attr('href').split('/');
		themeUrl.pop();
		$('#themeCss').after('<link rel="stylesheet" href="' + themeUrl.join('/') + '/' + themeName + '.css" id="themeCss">');
		$('#themeCss').remove();

		// 添加勾选状态
		$(".themeItem ul li").removeClass('themeActive');
		$('.themeItem ul li .' + themeName).parent().addClass('themeActive');
	}

	//勾选主题
	$(".themeItem ul li").on('click', function() {
		$(".themeItem ul li").removeClass('themeActive');
		$(this).addClass('themeActive');
	});

	$("#index-left-accordion").accordion("select", 0);
});

//添加标签页
function addTab(title, url, icon, integrateType){
	if (integrateType == "_blank"){
		window.open(url,  "黑河市疫情流调大数据可视化平台");
		return;
	}

	if (!$("#content-tabs").tabs("exists", title)){
		if (integrateType == "iframe"){
			if (url.indexOf("?") == -1){
				url += "?theme=" + $("#theme-name").val();
			} else {
				url += "&theme=" + $("#theme-name").val();
			}
			url += "&t=" + new Date().getTime();
			var content = "<iframe src='"+ url +"' width='100%' height='100%' frameborder='0' scrolling='auto'></iframe>";
			$("#content-tabs").tabs("add", {
				title: title,
				content: content,
				cache: true,
				iconCls: icon,
				closable: true
			});
		} else {
			$("#content-tabs").tabs("add", {
				title: title,
				href: url,
				cache: true,
				iconCls: icon,
				closable: true
			});
		}
	} else {
		$("#content-tabs").tabs("select", title);
	}
}

var tabContextMenuClickedTargetName;
function tabContextMenuClicked(e, title, index){
	tabContextMenuClickedTargetName = e.target.innerText;
	e.preventDefault();
	$("#tabs-context-menu").menu('show', {
		left: e.pageX,
		top: e.pageY
	});
}

function tabContextMenuItemClicked(item){
	if (item.text == '刷新'){
		refreshTab();
	} else if (item.text == '关闭'){
		closeTab();
	} else if (item.text == '关闭活动页'){
		closeSelectedTab();
	} else if (item.text == '关闭其他页'){
		closeOtherTabs();
	} else if (item.text == '关闭所有页'){
		closeAllTabs();
	}
}

function refreshTab(){
	var tab = $("#content-tabs").tabs("getSelected");
	var url = $(tab.panel('options')).attr('href');
	if (url == null){
		url = $(tab.panel('options').content).attr('src');
		var content = "<iframe src='"+ url +"' width='100%' height='100%' frameborder='0' scrolling='auto'></iframe>";
		$('#content-tabs').tabs('update', {
			tab: tab,
			options: {
				content: content
			}
		});
	} else {
		tab.panel('refresh', url);
	}
}

function closeTab(){
	var tab = $("#content-tabs").tabs("getTab", tabContextMenuClickedTargetName);
	var tabClosable = tab.panel("options").closable;
	if (tabClosable){
		var tabIndex = $("#content-tabs").tabs("getTabIndex", tab);
		if (tabIndex > 0){
			$("#content-tabs").tabs("close", tabIndex);
		} else {
			$.messager.show({ title:'提示信息', msg: '系统首页不可关闭。', timeout:5000, showType: 'show' });
		}
	} else {
		$.messager.show({ title:'提示信息', msg: '该标签页不可关闭。', timeout:5000, showType: 'show' });
	}
}

function closeSelectedTab(){
	var tab = $("#content-tabs").tabs("getSelected");
	var tabClosable = tab.panel("options").closable;
	if (tabClosable){
		var tabIndex = $("#content-tabs").tabs("getTabIndex", tab);
		if (tabIndex > 0){
			$("#content-tabs").tabs("close", tabIndex);
		} else {
			$.messager.show({ title:'提示信息', msg: '系统首页不可关闭。', timeout:5000, showType: 'show' });
		}
	} else {
		$.messager.show({ title:'提示信息', msg: '该标签页不可关闭。', timeout:5000, showType: 'show' });
	}
}

function closeOtherTabs(){
	var tab = $("#content-tabs").tabs("getTab", tabContextMenuClickedTargetName);
	var tabIndex = $("#content-tabs").tabs("getTabIndex", tab);
	
	var tabs = $("#content-tabs").tabs("tabs");
	for (var i = tabs.length - 1; i > 0; i--){
		if (i == tabIndex)
			continue;
		
		if(tabs[i].panel("options").closable){
			$("#content-tabs").tabs("close", i);
		}
	}
	$("#content-tabs").tabs("select", tabIndex);
}

function closeAllTabs(){
	var tabs = $("#content-tabs").tabs("tabs");
	for (var i = tabs.length - 1; i > 0; i--){
		if(tabs[i].panel("options").closable){
			$("#content-tabs").tabs("close", i);
		}
	}
	$("#content-tabs").tabs("select", 0);
}

//初始化修改个人信息对话框
function editProfile(){
	var editDialog = $("<div></div>").dialog({
		title: '修改个人信息',
		width: 900,
		height: 455,
		modal: true,
		iconCls: "fa fa-pencil-square-o",
		href: '/desktop/toEditProfile',
		onClose : function() {
            $(this).dialog('destroy');
        },
		onLoad: function(){
			$.ajax({
				url: '/desktop/getLoginUser',
				type: 'get',
				dataType: 'json',
				success: function(data){
					if (data.headImage != null && data.headImage != ""){
						$("#profile-head-image").attr("src", data.headImage);
					} else {
						$("#profile-head-image").attr("src", "/global/img/default_user_icon.png");
					}
					if (data.signImage != null && data.signImage != ""){
						$("#profile-sign-image").attr("src", data.signImage);
					} else {
						$("#profile-sign-image").remove();
					}
					$("#profile-email").textbox('setValue', data.email);
					$("#profile-phone").textbox('setValue', data.phone);
					$("#profile-idCard").textbox('setValue', data.idCard);
					$("#profile-address").textbox('setValue', data.address);
					if (data.userSettings != null && data.userSettings.defaultMenu != null){
						$("#profile-defaultMenu").combotree('setValue', data.userSettings.defaultMenu.id);
						$("#profile-defaultMenu").combotree('setText', data.userSettings.defaultMenu.name);
					}
				}
			});	
		},
		buttons: [
			{ 
				text: '保存', 
				iconCls: 'fa fa-save', 
				handler: function(){
					$("#edit-user-profile-form").form('submit', {
						url: "/desktop/updateProfile",
						onSubmit: function(){
							return $(this).form("validate");
						},
						success: function(data){
							data = $.parseJSON(data);
							if (data.result){
								editDialog.dialog("destroy");
								$.messager.alert("提示信息", data.msg, "info");
								updateUserHeadImage();
							} else {
								$.messager.alert("提示信息", data.msg, "error");
							}
						}
					});
				} 
			}, { 
				text: '关闭', 
				iconCls: 'fa fa-close', 
				handler: function(){ 
					editDialog.dialog('destroy'); 
				} 
			}
		]
	});
}

//打开修改密码对话框
function openEditPasswordDialog(){
	$("#edit-user-password-dialog").dialog("open");
	$.parser.parse("#edit-user-password-dialog");
}

//提交修改密码表单
function submitEditPasswordForm(){
	$("#edit-user-password-form").form('submit', {
		url: "/desktop/updatePassword",
		onSubmit: function(){
			return $(this).form("validate");
		},
		success: function(data){
			data = $.parseJSON(data);
			if (data.result){
				$("#edit-user-password-form").form("clear");
				$("#edit-user-password-dialog").dialog("close");
				$.messager.alert("提示信息", data.msg, "info");
			} else {
				$.messager.alert("提示信息", data.msg, "error");
			}
		}
	});
}

function updateUserHeadImage(){
	$.ajax({
		url: '/desktop/getLoginUser',
		type: 'get',
		dataType: 'json',
		success: function(data){
			if (data.headImage != null && data.headImage != ""){
				$("#index-user-head-image").attr("src", "data:image/jpeg;base64," + data.headImage);
			} else {
				$("#index-user-head-image").attr("src", "/global/img/default_user_icon.png");
			}
		}
	});	
}

function saveTheme(themeName){
	$.post("/user/saveCustomTheme", { "theme": themeName });
}