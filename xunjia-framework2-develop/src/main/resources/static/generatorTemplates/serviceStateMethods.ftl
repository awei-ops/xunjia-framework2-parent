
	/**
	 * 批量更新${entity.entityDescr}可用状态
	 * @param ${enableProp.propName} 状态
	 * @param ids 主键数组
	 * @return
	 */
	public ResponseData<Boolean> updateEnableState(${enableProp.type} ${enableProp.propName}, String[] ids){
		ResponseData<Boolean> resp = null;
		try {
			${entity.entityName?uncap_first}Repo.updateEnableState(${enableProp.propName}, ids);
			resp = ResponseData.getSuccess(ResponseMsg.UPDATE_SUCCESS);
		} catch (Exception e) {
			log.error("${entity.entityName?uncap_first}Service.updateEnableState方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}
	
	/**
	 * 查询可用${entity.entityDescr}信息
	 * @return
	 */
	public List<${entity.entityName}> findEnable${entity.entityName?cap_first}s(){
		return ${entity.entityName?uncap_first}Repo.findByEnableState(1);
	}