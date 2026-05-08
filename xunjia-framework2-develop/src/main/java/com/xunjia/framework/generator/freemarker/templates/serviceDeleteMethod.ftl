
	/**
	 * 根据id批量删除${entity.entityDescr}信息
	 * @param ids
	 * @return
	 */
	public ResponseData<Boolean> deleteByIds(String[] ids){
		ResponseData<Boolean> resp = null;
		try {
			${entity.entityName?uncap_first}Repo.deleteByIds(ids);
			resp = ResponseData.getSuccess(ResponseMsg.DELETE_SUCCESS);
		} catch (Exception e) {
			LOGGER.error("${entity.entityName?uncap_first}Service.deleteByIds方法异常。", e);
			resp = ResponseData.getError(e);
		}
		return resp;
	}