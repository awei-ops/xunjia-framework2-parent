<!DOCTYPE html SYSTEM "http://www.thymeleaf.org/dtd/xhtml1-strict-thymeleaf-4.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:th="http://www.thymeleaf.org">
	

<div class="table-container">
	<form id="add-${entity.entityName?uncap_first}-form" method="post" enctype="${encType}">
		<table class="edit-table">
			<#list entity.properties as prop>
				<#if prop.propName != "id" && prop.enableFlag != 1>
					<tr>
						<th>${prop.propDescr}：</th>
						<td>
							<#switch prop.controlType>
								<#case "文本">
									<input class="easyui-textbox" id="${entity.entityName?uncap_first}-${prop.propName}" name="${prop.propName}" data-options="<#if prop.requiredFlag == 1>required:true,</#if>width:300" />
									<#break>
								<#case "多行文本框">
                                	<input class="easyui-textbox" id="${entity.entityName?uncap_first}-${prop.propName}" name="${prop.propName}" data-options="<#if prop.requiredFlag == 1>required:true,</#if>width:300,height:60,multiline:true" />
                                	<#break>
                                <#case "隐藏域">
                                    <input type="hidden" id="${entity.entityName?uncap_first}-${prop.propName}" name="${prop.propName}" />
                                    <#break>
								<#case "数字">
									<input class="easyui-numberbox" id="${entity.entityName?uncap_first}-${prop.propName}" name="${prop.propName}" data-options="
										<#if prop.requiredFlag == 1>required:true,</#if>
										<#if prop.type == "Integer" || prop.type == "Long">
											precision: 0,
										<#else>
											precision: 2,
										</#if>
										width:300" />
									<#break>
								<#case "日期">
									<input class="easyui-datebox" id="${entity.entityName?uncap_first}-${prop.propName}" name="${prop.propName}" data-options="<#if prop.requiredFlag == 1>required:true,</#if>editable:false,width:300" />
									<#break>
								<#case "下拉框">
									<select class="easyui-combobox" id="${entity.entityName?uncap_first}-${prop.propName}" name="${prop.propName}" data-options="
									    <#if prop.dicTypeCode??>
									        url: '/dicContent/findByTypeCode?typeCode=${prop.dicTypeCode}',
									    <#else>
									        url: '',
									    </#if>
										textField: 'name',
										valueField: 'name',
										<#if prop.requiredFlag == 1>required:true,</#if>
										editable: false,
										width: 300
									"></select>
									<#break>
								<#case "单选">
									<input type="radio" name="${prop.propName}" value="1" />
									<#break>
								<#case "复选">
									<input type="checkbox" name="${prop.propName}" value="1" />
									<#break>
								<#case "文件">
									<input class="easyui-filebox" data-options="buttonText:'选择文件', multiple:false,
										<#if prop.requiredFlag == 1>required:true,</#if>
									" />
									<#break>
								<#case "密码">
									<input class="easyui-passwordbox" id="${entity.entityName?uncap_first}-${prop.propName}" name="${prop.propName}" 
										data-options="
											<#if prop.requiredFlag == 1>required:true,</#if>
											width:300
									" />
									<#break>
								<#case "组织选择">
								    <input class="easyui-textbox" id="${entity.entityName?uncap_first}-${prop.propName}"
                                       	data-options="
                                       	    <#if prop.requiredFlag == 1>required:true,</#if>
                                       	    width:300,
                                      	    editable:false,
                                       	    icons:[{iconCls:'fa fa-search',handler:showSelectOrgDialog}]
                                       	" />
                                    <input type="hidden" id="${entity.entityName?uncap_first}-${prop.propName}-id" name="${prop.propName}.id" />
									<#break>
								<#case "用户选择">
								    <input class="easyui-textbox" id="${entity.entityName?uncap_first}-${prop.propName}"
                                    	data-options="
                                    	    <#if prop.requiredFlag == 1>required:true,</#if>
                                    	    width:300,
                                    	    editable:false,
                                    	    icons:[{iconCls:'fa fa-search',handler:showSelectUserDialog}]
                                    	" />
                                    <input type="hidden" id="${entity.entityName?uncap_first}-${prop.propName}-id" name="${prop.propName}.id" />
									<#break>
								<#case "下拉树">
									<#if prop.propName == "parent">
										<select class="easyui-combotree" id="${entity.entityName?uncap_first}-parent" name="parentId" data-options="
											width: 300,
											url: '/${entity.entityName?uncap_first}/get${entity.entityName?cap_first}Tree',
											textField: 'name',
											valueField: 'id'
										">
										</select>
									<#else>
										<select class="easyui-combotree" id="${entity.entityName?uncap_first}-${prop.propName}" name="${prop.propName}" data-options="
											width: 300,
											url: '',
											textField: 'name',
											valueField: 'id'
										">
										</select>
									</#if>
									<#break>
							</#switch>
						</td>
					</tr>
				</#if>
			</#list>
		</table>
	</form>
	<#if radioFlag == 1 || checkboxFlag == 1>
	<script th:inline="none">
        <#if radioFlag == 1>
            $(":radio").iCheck({
            	radioClass: 'iradio_square-red',
            	increaseArea: '20%'
            });

        <#elseif checkboxFlag == 1>
            $(":checkbox").iCheck({
                checkboxClass: 'icheckbox_square-red',
                increaseArea: '20%'
            });
        </#if>
	</script>
	</#if>
</div>
</html>