
	/**
	 * 保存${entity.entityDescr}
	 * @param ${entity.entityName?uncap_first}
	 * @return
	 */
	public ResponseData<Boolean> save(${entity.entityName} ${entity.entityName?uncap_first}){
		ResponseData<Boolean> resp = null;
		try {
			<#list entity.properties as p>
				<#if p.enableFlag == 1>
					${entity.entityName?uncap_first}.set${p.propName?cap_first}(
					<#switch p.type>
						<#case "Integer">1<#break>
						<#case "Double">1<#break>
						<#case "String">"1"<#break>
						<#case "Boolean">true<#break>
					</#switch>
					);
				</#if>
				
				<#if p.uniqueFlag == 1 && p.pkFlag != 1>
					${entity.entityName} same${p.propName?cap_first + entity.entityName?cap_first} = ${entity.entityName?uncap_first}Repo.findBy${p.propName?cap_first}(${entity.entityName?uncap_first}.get${p.propName?cap_first}());
					if (same${p.propName?cap_first + entity.entityName?cap_first} != null){
						resp = ResponseData.getFail("保存失败，系统中已存在相同的${p.propDescr}。");
						return resp;
					}
				</#if>
			</#list>
		
			${entity.entityName?uncap_first}Repo.save(${entity.entityName?uncap_first});
			resp = ResponseData.getSuccess(ResponseMsg.SAVE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("${entity.entityName?uncap_first}Service.save方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}