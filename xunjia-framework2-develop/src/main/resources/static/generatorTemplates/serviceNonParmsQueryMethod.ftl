
	/**
	 * 多参数查询分页信息
	 * @param pageIndex 页号
	 * @param rows 每页显示条数
	 * @return
	 */
	public Page<${entity.entityName}> find${entity.entityName?cap_first}s(int pageIndex, int rows){

		<#if orderExist == 1>
			List<Order> sortLists = new ArrayList<Order>();
			<#list entity.properties as p>
				<#if p.orderBy != "">
					sortLists.add(new Order(Direction.${p.orderBy}, "${p.propName}"));
				</#if>
			</#list>
			Sort sort = Sort.by(sortLists);
			Pageable pageable = PageRequest.of(pageIndex - 1, rows, sort);
		<#else>
			Pageable pageable = PageRequest.of(pageIndex - 1, rows);
		</#if>
		Page<${entity.entityName}> pageData = null;
		try {
			pageData = ${entity.entityName?uncap_first}Repo.findAll(pageable);
		} catch (Exception e) {
			log.error("${entity.entityName?uncap_first}Service.find${entity.entityName?cap_first}s方法异常。", e);
		}
		return pageData;
	}