<!DOCTYPE html SYSTEM "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-4.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:th="http://www.thymeleaf.org"
	xmlns:xunjia="http://www.thymeleaf.org">
	
<body>
	<div class="easyui-layout" fit="true">
		<div data-options="region:'west',width:210">
			<div id="${entity.entityName?uncap_first}-tree" class="easyui-tree" data-options="
				url: '/${entity.entityName?uncap_first}/get${entity.entityName?cap_first}Tree',
				animate: true,
				onClick: ${entity.entityName?uncap_first}Clicked
			"></div>
		</div>
		<div data-options="region:'north',height:53" style="padding:10px 8px 10px 8px;overflow:hidden;">
			<#list entity.properties as prop>
				<#if prop.searchFlag == 1 && prop.searchCond??>
					<#if prop.searchCond == "between">
						<#if prop.type == "Long" || prop.type == "Integer" || prop.type == "Double">
							<input id="${entity.entityName?uncap_first}-${prop.propName}-start-key" class="easyui-numberbox" data-options="width:240,label:'${prop.propDescr}起始：' ,labelAlign:'right',labelWidth:80" />
							<input id="${entity.entityName?uncap_first}-${prop.propName}-end-key" class="easyui-numberbox" data-options="width:240,label:'${prop.propDescr}截止：' ,labelAlign:'right',labelWidth:80" />
						<#elseif prop.type == "Date">
							<input id="${entity.entityName?uncap_first}-${prop.propName}-start-key" class="easyui-datebox" data-options="width:240,label:'起始日期：' ,labelAlign:'right',labelWidth:80" />
							<input id="${entity.entityName?uncap_first}-${prop.propName}-end-key" class="easyui-datebox" data-options="width:240,label:'截止日期：' ,labelAlign:'right',labelWidth:80" />
						<#else>
							<#if prop.dicFlag == 1>
								<select id="${entity.entityName?uncap_first}-${prop.propName}-key" class="easyui-combobox" data-options="
									url: '/dicContent/findByTypeCodeWithBlank?typeCode=${prop.dicTypeCode}',
									width:240,
									editable:false,
									textField: 'name',
									valueField: 'name',
									label:'${prop.propDescr}：',
									labelAlign:'right',
									labelWidth:80">
								</select>
							<#else>
								<input id="${entity.entityName?uncap_first}-${prop.propName}-key" class="easyui-textbox" data-options="width:240,label:'${prop.propDescr}：' ,labelAlign:'right',labelWidth:80" />
							</#if>
						</#if>
					<#else>
						<#if prop.type == "Long" || prop.type == "Integer" || prop.type == "Double">
							<input id="${entity.entityName?uncap_first}-${prop.propName}-key" class="easyui-numberbox" data-options="width:240,label:'${prop.propDescr}：' ,labelAlign:'right',labelWidth:80" />
						<#elseif prop.type == "Date">
							<input id="${entity.entityName?uncap_first}-${prop.propName}-key" class="easyui-datebox" data-options="width:240,label:'${prop.propDescr}：' ,labelAlign:'right',labelWidth:80" />
						<#else>
							<#if prop.dicFlag == 1>
								<select id="${entity.entityName?uncap_first}-${prop.propName}-key" class="easyui-combobox" data-options="
									url: '/dicContent/findByTypeCodeWithBlank?typeCode=${prop.dicTypeCode}',
									width:240,
									editable:false,
									textField: 'name',
									valueField: 'name',
									label:'${prop.propDescr}：',
									labelAlign:'right',
									labelWidth:80">
								</select>
							<#else>
								<input id="${entity.entityName?uncap_first}-${prop.propName}-key" class="easyui-textbox" data-options="width:240,label:'${prop.propDescr}：' ,labelAlign:'right',labelWidth:80" />
							</#if>
						</#if>
					</#if>
				</#if>
			</#list>
			<a href="javascript:" onclick="search${entity.entityName?cap_first}();" class="easyui-linkbutton" iconCls="fa fa-search">查询</a>
		</div>
		<div data-options="region:'center'">
			<table id="${entity.entityName?uncap_first}-table"></table>
			<div id="${entity.entityName?uncap_first}-datagrid-toolbar" class="datagrid-toolbar">
				<#list operations as operation>
					<#switch operation>
						<#case "添加">
							<xunjia:button th:menu-id="<#noparse>${menuId}</#noparse>" perm-code="${entity.entityName?uncap_first}:save" />
							<#break>
						<#case "修改">
							<xunjia:button th:menu-id="<#noparse>${menuId}</#noparse>" perm-code="${entity.entityName?uncap_first}:update" />
							<#break>
						<#case "删除">
							<xunjia:button th:menu-id="<#noparse>${menuId}</#noparse>" perm-code="${entity.entityName?uncap_first}:delete" />
							<#break>
						<#case "启用">
							<xunjia:button th:menu-id="<#noparse>${menuId}</#noparse>" perm-code="${entity.entityName?uncap_first}:enable" />
							<#break>
						<#case "禁用">
							<xunjia:button th:menu-id="<#noparse>${menuId}</#noparse>" perm-code="${entity.entityName?uncap_first}:disable" />
							<#break>
						<#case "导入">
							<xunjia:button th:menu-id="<#noparse>${menuId}</#noparse>" perm-code="${entity.entityName?uncap_first}:import" />
							<#break>
						<#case "导出">
							<xunjia:button th:menu-id="<#noparse>${menuId}</#noparse>" perm-code="${entity.entityName?uncap_first}:export" />
							<#break>
					</#switch>
				</#list>
			</div>
		</div>
	</div>
	
	<script th:inline="none">
	$(function(){
		$("#${entity.entityName?uncap_first}-table").datagrid({
			url: '/${entity.entityName?uncap_first}/find${entity.entityName?cap_first}s',
			idField: 'id',
			fit: true,
			fitColumns: true,
			striped: true,
			loadMsg: '数据加载中，请稍候...',
			pagination: true,
			pageSize: 20,
			rownumbers: true,
			toolbar: "#${entity.entityName?uncap_first}-datagrid-toolbar",
			columns: [[
				{
					field: 'id',
					checkbox: true
				}
				<#list entity.properties as prop>
					<#if prop.tableColumnFlag == 1>
						, {
							field: '${prop.propName}',
							title: '${prop.propDescr}',
							width: 80
						}
					</#if>
				</#list>
			]]
		});
	});
	
	<#list operations as operation>
		<#switch operation>
			<#case "添加">
				function add${entity.entityName?cap_first}(){
					var addDialog = $("<div></div>").dialog({
						title: '添加${entity.entityDescr}',
						width: 630,
						height: 550,
						modal: true,
						iconCls: "fa fa-pencil-square-o",
						href: '/${entity.entityName?uncap_first}/toAdd',
						onClose : function() {
			                $(this).dialog('destroy');
			            },
						buttons: [
							{ 
								text: '保存', 
								iconCls: 'fa fa-save', 
								handler: function(){
									submit${entity.entityName?cap_first}Form("add-${entity.entityName?uncap_first}-form", addDialog, "/${entity.entityName?uncap_first}/save");
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
				<#break>
			<#case "修改">
				function edit${entity.entityName?cap_first}(){
					var selectedRows = $("#${entity.entityName?uncap_first}-table").datagrid("getSelections");
					if (selectedRows == null || selectedRows.length == 0){
						$.messager.alert("提示信息", "您未选择要编辑的记录。", "warning");
					} else if (selectedRows.length > 1){
						$.messager.alert("提示信息", "您只能选择一条记录进行操作。", "warning");
					} else {
						var editDialog = $("<div></div>").dialog({
							title: '修改${entity.entityDescr}',
							width: 630,
							height: 550,
							modal: true,
							iconCls: "fa fa-pencil-square-o",
							href: '/${entity.entityName?uncap_first}/toEdit',
							onClose : function() {
				                $(this).dialog('destroy');
				            },
							onLoad: function(){
								$.ajax({
									url: '/${entity.entityName?uncap_first}/findById',
									data: { id: selectedRows[0].id },
									type: 'get',
									dataType: 'json',
									success: function(data){
										$("#${entity.entityName?uncap_first}-id").val(data.id);
										<#if enableProp??>
											$("#${entity.entityName?uncap_first}-${enableProp.propName}").val(data.${enableProp.propName});
										</#if>
										<#list entity.properties as prop>
											<#if prop.propName == "id">
												$("#${entity.entityName?uncap_first}-id").val(data.id);
											<#else>
												<#if prop.type == "Integer" || prop.type == "Double" || prop.type == "Long">
													$("#${entity.entityName?uncap_first}-${prop.propName}").numberbox('setValue', data.${prop.propName});
												<#elseif prop.type == "Date">
													$("#${entity.entityName?uncap_first}-${prop.propName}").datebox('setValue', data.${prop.propName});
												<#else>
													<#if prop.propName == "parent">
														if (data.parent != null){
															$("#${entity.entityName?uncap_first}-parent").combotree('setValue', data.parent.id);
															$("#${entity.entityName?uncap_first}-parent").combotree('setText', data.parent.name);
														}
													<#else>
														<#if prop.dicFlag == 1>
															$("#${entity.entityName?uncap_first}-${prop.propName}").combobox('setValue', data.${prop.propName});
														<#else>
															$("#${entity.entityName?uncap_first}-${prop.propName}").textbox('setValue', data.${prop.propName});
														</#if>
													</#if>
												</#if>
											</#if>
										</#list>
									}
								});	
							},
							buttons: [
								{ 
									text: '保存', 
									iconCls: 'fa fa-save', 
									handler: function(){
										submit${entity.entityName?cap_first}Form("edit-${entity.entityName?uncap_first}-form", editDialog, "/${entity.entityName?uncap_first}/update");
									} 
								},
								{ 
									text: '关闭', 
									iconCls: 'fa fa-close', 
									handler: function(){ 
										$("#${entity.entityName?uncap_first}-table").datagrid("reload");
										editDialog.dialog('destroy'); 
									} 
								}
							]
						});
					}
				}
				<#break>
			<#case "删除">
				function dataRemove${entity.entityName?cap_first}(){
					var selectedRows = $("#${entity.entityName?uncap_first}-table").datagrid("getSelections");
					if (selectedRows == null || selectedRows.length == 0){
						$.messager.alert("提示信息", "您未选择要编辑的记录。", "warning");
					} else {
						$.messager.confirm('删除确认', '您是否确认要删除选中的记录？', function(r){
							if (r){
								var ids = new Array();
								$.each(selectedRows, function(i, n){
									ids[i] = n.id;
								});
								
								$.ajax({
									url: '/${entity.entityName?uncap_first}/delete',
									data: { "ids[]": ids },
									type: 'post',
									dataType: 'json',
									success: function(data){
										if (data.result){
											$.messager.show({ title:'提示信息', msg: data.msg, timeout:5000, showType: 'show' });
											$("#${entity.entityName?uncap_first}-table").datagrid("reload");
											$("#${entity.entityName?uncap_first}-table").datagrid("clearSelections");
											$("#${entity.entityName?uncap_first}-tree").tree("reload");
										} else {
											$.messager.alert("提示信息", data.msg, "error");
										}
									}
								});	
							}
						});
					}
				}
				<#break>
			<#case "启用">
				function setEnable${entity.entityName?cap_first}(){
					changeEnableState${entity.entityName?cap_first}(1);
				}
				<#break>
			<#case "禁用">
				function setDisable${entity.entityName?cap_first}(){
					changeEnableState${entity.entityName?cap_first}(0);
				}
				<#break>
			<#case "导入">
				<#break>
			<#case "导出">
				<#break>
		</#switch>
	</#list>
	
	<#if enableFlag == 1>
		function changeEnableState${entity.entityName?cap_first}(state){
			var stateStr = state == 0 ? "禁用" : "启用";
			var selectedRows = $("#${entity.entityName?uncap_first}-table").datagrid("getSelections");
			if (selectedRows == null || selectedRows.length == 0){
				$.messager.alert("提示信息", "您未选择要"+ stateStr +"的${entity.entityDescr}。", "warning");
			} else {
				$.messager.confirm('操作确认', '您是否确认要'+ stateStr +'选中的${entity.entityDescr}？', function(r){
					if (r){
						var ids = new Array();
						$.each(selectedRows, function(i, n){
							ids[i] = n.id;
						});
						
						$.ajax({
							url: '/${entity.entityName?uncap_first}/updateEnableState',
							data: { "enable": state, "ids[]": ids },
							type: 'post',
							dataType: 'json',
							success: function(data){
								if (data.result){
									$.messager.show({ title:'提示信息', msg: data.msg, timeout:5000, showType: 'show' });
									$("#${entity.entityName?uncap_first}-table").datagrid("reload");
									$("#${entity.entityName?uncap_first}-table").datagrid("clearSelections");
								} else {
									$.messager.alert("提示信息", data.msg, "error");
								}
							}
						});	
					}
				});
			}
		}
	</#if>
	<#if submitFlag == 1>
		function submit${entity.entityName?cap_first}Form(formId, dialog, url){
			$("#" + formId).form('submit', {
				url: url,
				onSubmit: function(){
					return $(this).form("validate");
				},
				success: function(data){
					data = $.parseJSON(data);
					if (data.result){
						dialog.dialog("destroy");
						$.messager.show({ title:'提示信息', msg: data.msg, timeout:5000, showType: 'show' });
						$("#${entity.entityName?uncap_first}-table").datagrid("reload");
						$("#${entity.entityName?uncap_first}-table").datagrid("clearSelections");
						$("#${entity.entityName?uncap_first}-tree").tree("reload");
					} else {
						$.messager.alert("提示信息", data.msg, "error");
					}
				}
			});
		}
	</#if>
	
	
	function ${entity.entityName?uncap_first}Clicked(node){
		$("#${entity.entityName?uncap_first}-tree").tree("toggle", node.target);
		$("#${entity.entityName?uncap_first}-table").datagrid("clearSelections");
		search${entity.entityName?cap_first}();
	}
	
	function search${entity.entityName?cap_first}(){
		var selectedNode = $("#${entity.entityName?uncap_first}-tree").tree("getSelected"); 
		$("#${entity.entityName?uncap_first}-table").datagrid('load', {
			<#list entity.properties as prop>
				<#if prop.searchFlag == 1 && prop.searchCond??>
					<#if prop.searchCond == "between">
						<#if prop.type == "Long" || prop.type == "Integer" || prop.type == "Double">
							'${prop.propName}Start': $("#${entity.entityName?uncap_first}-${prop.propName}-start-key").numberbox('getValue'),
							'${prop.propName}End': $("#${entity.entityName?uncap_first}-${prop.propName}-end-key").numberbox('getValue'),
						<#elseif prop.type == "Date">
							'${prop.propName}Start': $("#${entity.entityName?uncap_first}-${prop.propName}-start-key").datebox('getValue'),
							'${prop.propName}End': $("#${entity.entityName?uncap_first}-${prop.propName}-end-key").datebox('getValue'),
						<#else>
							<#if prop.dicFlag == 1>
								'${prop.propName}': $("#${entity.entityName?uncap_first}-${prop.propName}-key").combobox('getValue'),
							<#else>
								'${prop.propName}': $("#${entity.entityName?uncap_first}-${prop.propName}-key").textbox('getValue'),
							</#if>
						</#if>
					<#else>
						<#if prop.type == "Long" || prop.type == "Integer" || prop.type == "Double">
							'${prop.propName}': $("#${entity.entityName?uncap_first}-${prop.propName}-key").numberbox('getValue'),
						<#elseif prop.type == "Date">
							'${prop.propName}': $("#${entity.entityName?uncap_first}-${prop.propName}-key").datebox('getValue'),
						<#else>
							<#if prop.dicFlag == 1>
								'${prop.propName}': $("#${entity.entityName?uncap_first}-${prop.propName}-key").combobox('getValue'),
							<#else>
								'${prop.propName}': $("#${entity.entityName?uncap_first}-${prop.propName}-key").textbox('getValue'),
							</#if>
						</#if>
					</#if>
				</#if>
			</#list>
			'parentId': selectedNode == null ? '' : selectedNode.id
		});
	}
	
	</script>
</body>
</html>