if (jQuery.fn.pagination){
	jQuery.fn.pagination.defaults.beforePageText = '第';
	jQuery.fn.pagination.defaults.afterPageText = '共{pages}页';
	jQuery.fn.pagination.defaults.displayMsg = '显示{from}到{to},共{total}记录';
}
if (jQuery.fn.datagrid){
	jQuery.fn.datagrid.defaults.loadMsg = '正在处理，请稍待。。。';
}
if (jQuery.fn.treegrid && jQuery.fn.datagrid){
	jQuery.fn.treegrid.defaults.loadMsg = jQuery.fn.datagrid.defaults.loadMsg;
}
if (jQuery.messager){
	jQuery.messager.defaults.ok = '确定';
	jQuery.messager.defaults.cancel = '取消';
}
jQuery.map(['validatebox','textbox','passwordbox','filebox','searchbox',
		'combo','combobox','combogrid','combotree',
		'datebox','datetimebox','numberbox',
		'spinner','numberspinner','timespinner','datetimespinner'], function(plugin){
	if (jQuery.fn[plugin]){
		jQuery.fn[plugin].defaults.missingMessage = '该输入项为必输项';
	}
});
if (jQuery.fn.validatebox){
	jQuery.fn.validatebox.defaults.rules.email.message = '请输入有效的电子邮件地址';
	jQuery.fn.validatebox.defaults.rules.url.message = '请输入有效的URL地址';
	jQuery.fn.validatebox.defaults.rules.length.message = '输入内容长度必须介于{0}和{1}之间';
	jQuery.fn.validatebox.defaults.rules.remote.message = '请修正该字段';
}
if (jQuery.fn.calendar){
	jQuery.fn.calendar.defaults.weeks = ['日','一','二','三','四','五','六'];
	jQuery.fn.calendar.defaults.months = ['一月','二月','三月','四月','五月','六月','七月','八月','九月','十月','十一月','十二月'];
}
if (jQuery.fn.datebox){
	jQuery.fn.datebox.defaults.currentText = '今天';
	jQuery.fn.datebox.defaults.closeText = '关闭';
	jQuery.fn.datebox.defaults.okText = '确定';
	jQuery.fn.datebox.defaults.formatter = function(date){
		var y = date.getFullYear();
		var m = date.getMonth()+1;
		var d = date.getDate();
		return y+'-'+(m<10?('0'+m):m)+'-'+(d<10?('0'+d):d);
	};
	jQuery.fn.datebox.defaults.parser = function(s){
		if (!s) return new Date();
		var ss = s.split('-');
		var y = parseInt(ss[0],10);
		var m = parseInt(ss[1],10);
		var d = parseInt(ss[2],10);
		if (!isNaN(y) && !isNaN(m) && !isNaN(d)){
			return new Date(y,m-1,d);
		} else {
			return new Date();
		}
	};
}
if (jQuery.fn.datetimebox && jQuery.fn.datebox){
	jQuery.extend(jQuery.fn.datetimebox.defaults,{
		currentText: jQuery.fn.datebox.defaults.currentText,
		closeText: jQuery.fn.datebox.defaults.closeText,
		okText: jQuery.fn.datebox.defaults.okText
	});
}
if (jQuery.fn.datetimespinner){
	jQuery.fn.datetimespinner.defaults.selections = [[0,4],[5,7],[8,10],[11,13],[14,16],[17,19]]
}
