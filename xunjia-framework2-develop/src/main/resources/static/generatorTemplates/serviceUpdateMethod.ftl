
	/**
	 * 更新${entity.entityDescr}信息
	 * @param ${entity.entityName?uncap_first}
	 * @return
	 */
	public ResponseData<Boolean> update(${entity.entityName} ${entity.entityName?uncap_first}){
		ResponseData<Boolean> resp = null;
		try {
			<#list entity.properties as p>
				<#if p.uniqueFlag == 1 && p.pkFlag == 0 && p.enableFlag == 0>
					${entity.entityName} same${p.propName?cap_first + entity.entityName?cap_first} = ${entity.entityName?uncap_first}Repo.findBy${p.propName?cap_first}(${entity.entityName?uncap_first}.get${p.propName?cap_first}());
					if (same${p.propName?cap_first + entity.entityName?cap_first} != null && !same${p.propName?cap_first + entity.entityName?cap_first}.getId().equals(${entity.entityName?uncap_first}.getId())){
						resp = ResponseData.getFail("保存失败，系统中已存在相同的${p.propDescr}。");
						return resp;
					}
				</#if>
			</#list>
			
			
			${entity.entityName?uncap_first}Repo.save(${entity.entityName?uncap_first});
			resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
		} catch (Exception e) {
			log.error("${entity.entityName?uncap_first}Service.update方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}