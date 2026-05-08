
	/**
	 * 多参数查询分页信息
	<#list entity.properties as p>
		<#if p.searchFlag == 1>
			<#if p.searchCond == "between">
            	<#if p.type == "Date">
            		* @param ${p.propName}Start 起始${p.propDescr}
            		* @param ${p.propName}End 截止${p.propDescr}
            	<#else>
            		* @param ${p.propName}Start 起始值
                    * @param ${p.propName}End 截止值
            	</#if>
            <#else>
            	* @param ${p.propName} ${p.propDescr}
            </#if>
		</#if>
	</#list>
	 * @param pageIndex 页号
	 * @param rows 每页显示条数
	 * @return
	 */
	public Page<${entity.entityName}> find${entity.entityName?cap_first}s(
		<#list entity.properties as p>
			<#if p.enableFlag == 0 && p.searchFlag == 1>
				<#if p.searchCond == "between">
					<#if p.type == "Date">
						String ${p.propName}Start, String ${p.propName}End,
					<#else>
						${p.type} ${p.propName}Start, ${p.type} ${p.propName}End,
					</#if>
				<#else>
					<#if p.type == "Date">
						String ${p.propName},
					<#else>
						${p.type} ${p.propName},
					</#if>
				</#if>
			<#elseif p.enableFlag == 1 && p.searchFlag == 1>
			    Integer enableState,
			</#if>
		</#list>
		<#if entity.treeStructure == 1>
			String parentId,
		</#if>
		int pageIndex, int rows){
		Specification<${entity.entityName}> spec = new Specification<${entity.entityName}>() {
			public Predicate toPredicate(Root<${entity.entityName}> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				
				List<Predicate> predicates = new ArrayList<Predicate>();
				
				<#list entity.properties as p>
					<#if p.enableFlag == 0 && p.searchFlag == 1>
						<#switch p.type>
							<#case "String">
								if (!StringUtils.isEmpty(${p.propName})) {
									<#switch p.searchCond>
										<#case "=">
											Predicate predicate = cb.equal(root.get("${p.propName}").as(String.class), ${p.propName});
											<#break>
										<#case "!=">
											Predicate predicate = cb.notEqual(root.get("${p.propName}").as(String.class), ${p.propName});
											<#break>
										<#case "like">
											Predicate predicate = cb.like(root.get("${p.propName}").as(String.class), "%" + ${p.propName} + "%");
											<#break>
										<#case "左like">
											Predicate predicate = cb.like(root.get("${p.propName}").as(String.class), "%" + ${p.propName});
											<#break>
										<#case "右like">
											Predicate predicate = cb.like(root.get("${p.propName}").as(String.class), ${p.propName} + "%");
											<#break>
									</#switch>
									predicates.add(predicate);
								}
								<#break>
							<#case "Integer">
								if (${p.propName} != null && ${p.propName} != -1) {
									<#switch p.searchCond>
										<#case "=">
											Predicate predicate = cb.equal(root.get("${p.propName}").as(Integer.class), ${p.propName});
											<#break>
										<#case "!=">
											Predicate predicate = cb.notEqual(root.get("${p.propName}").as(Integer.class), ${p.propName});
											<#break>
										<#case "\g">
											Predicate predicate = cb.greaterThan(root.get("${p.propName}").as(Integer.class), ${p.propName});
											<#break>
										<#case "\l">
											Predicate predicate = cb.lessThan(root.get("${p.propName}").as(Integer.class), ${p.propName});
											<#break>
										<#case "\g=">
											Predicate predicate = cb.greaterThanOrEqualTo(root.get("${p.propName}").as(Integer.class), ${p.propName});
											<#break>
										<#case "\l=">
											Predicate predicate = cb.lessThanOrEqualTo(root.get("${p.propName}").as(Integer.class), ${p.propName});
											<#break>
										<#case "between">
											Predicate predicate = cb.between(root.get("${p.propName}").as(Integer.class), ${p.propName}Start, ${p.propName}End);
											<#break>
									</#switch>
									predicates.add(predicate);
								}
								<#break>
							<#case "Long">
								if (${p.propName} != null && ${p.propName} != -1l) {
									<#switch p.searchCond>
										<#case "=">
											Predicate predicate = cb.equal(root.get("${p.propName}").as(Long.class), ${p.propName});
											<#break>
										<#case "!=">
											Predicate predicate = cb.notEqual(root.get("${p.propName}").as(Long.class), ${p.propName});
											<#break>
										<#case "\g">
											Predicate predicate = cb.greaterThan(root.get("${p.propName}").as(Long.class), ${p.propName});
											<#break>
										<#case "\l">
											Predicate predicate = cb.lessThan(root.get("${p.propName}").as(Long.class), ${p.propName});
											<#break>
										<#case "\g=">
											Predicate predicate = cb.greaterThanOrEqualTo(root.get("${p.propName}").as(Long.class), ${p.propName});
											<#break>
										<#case "\l=">
											Predicate predicate = cb.lessThanOrEqualTo(root.get("${p.propName}").as(Long.class), ${p.propName});
											<#break>
										<#case "between">
											Predicate predicate = cb.between(root.get("${p.propName}").as(Long.class), ${p.propName}Start, ${p.propName}End);
											<#break>
									</#switch>
									predicates.add(predicate);
								}
								<#break>
							<#case "Double">
								if (${p.propName} != null && ${p.propName} != -1d) {
									<#switch p.searchCond>
										<#case "=">
											Predicate predicate = cb.equal(root.get("${p.propName}").as(Double.class), ${p.propName});
											<#break>
										<#case "!=">
											Predicate predicate = cb.notEqual(root.get("${p.propName}").as(Double.class), ${p.propName});
											<#break>
										<#case "\g">
											Predicate predicate = cb.greaterThan(root.get("${p.propName}").as(Double.class), ${p.propName});
											<#break>
										<#case "\l">
											Predicate predicate = cb.lessThan(root.get("${p.propName}").as(Double.class), ${p.propName});
											<#break>
										<#case "\g=">
											Predicate predicate = cb.greaterThanOrEqualTo(root.get("${p.propName}").as(Double.class), ${p.propName});
											<#break>
										<#case "\l=">
											Predicate predicate = cb.lessThanOrEqualTo(root.get("${p.propName}").as(Double.class), ${p.propName});
											<#break>
										<#case "between">
											Predicate predicate = cb.between(root.get("${p.propName}").as(Double.class), ${p.propName}Start, ${p.propName}End);
											<#break>
									</#switch>
									predicates.add(predicate);
								}
								<#break>
							<#case "Boolean">
								<#switch p.searchCond>
									<#case "=">
										Predicate predicate = cb.equal(root.get("${p.propName}").as(Boolean.class), ${p.propName});
										<#break>
									<#case "!=">
										Predicate predicate = cb.notEqual(root.get("${p.propName}").as(Boolean.class), ${p.propName});
										<#break>
								</#switch>
								predicates.add(predicate);
								<#break>
							<#case "Date">
								<#if p.searchCond == "between">
									if (!StringUtils.isEmpty(${p.propName}Start)) {
										try {
											Date startDate = DateUtils.parse(${p.propName}Start, "yyyy-MM-dd");
											Predicate predicate = cb.greaterThanOrEqualTo(root.get("${p.propName}").as(Date.class), startDate);
											predicates.add(predicate);
										} catch (java.text.ParseException e) {
											e.printStackTrace();
										}
									}
									if (!StringUtils.isEmpty(${p.propName}End)) {
										try {
											Date endDate = DateUtils.parse(${p.propName}End + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
											Predicate predicate = cb.lessThanOrEqualTo(root.get("${p.propName}").as(Date.class), endDate);
											predicates.add(predicate);
										} catch (java.text.ParseException e) {
											e.printStackTrace();
										}
									}
								<#else>
									if (!StringUtils.isEmpty(${p.propName})) {
										Date date = null;
										try {
											date = DateUtils.parse(${p.propName}, "yyyy-MM-dd");
										} catch (ParseException e) {
											e.printStackTrace();
										}
										<#switch p.searchCond>
											<#case "=">
												Predicate predicate = cb.equal(root.get("${p.propName}").as(Date.class), date);
												<#break>
											<#case "!=">
												Predicate predicate = cb.notEqual(root.get("${p.propName}").as(Date.class), date);
												<#break>
											<#case "\g">
												Predicate predicate = cb.greaterThan(root.get("${p.propName}").as(Date.class), date);
												<#break>
											<#case "\l">
												Predicate predicate = cb.lessThan(root.get("${p.propName}").as(Date.class), date);
												<#break>
											<#case "\g=">
												Predicate predicate = cb.greaterThanOrEqualTo(root.get("${p.propName}").as(Date.class), date);
												<#break>
											<#case "\l=">
												Predicate predicate = cb.lessThanOrEqualTo(root.get("${p.propName}").as(Date.class), date);
												<#break>
										</#switch>
										predicates.add(predicate);
									}
								</#if>
								<#break>
							<#default>
								if (${p.propName} != null) {
									<#switch p.searchCond>
										<#case "=">
											Predicate predicate = cb.equal(root.get("${p.propName}").get("id").as(String.class), ${p.propName});
											<#break>
										<#case "!=">
											Predicate predicate = cb.notEqual(root.get("${p.propName}").get("id").as(String.class), ${p.propName});
											<#break>
									</#switch>
									predicates.add(predicate);
								}
						</#switch>
					<#elseif p.enableFlag == 1 && p.searchFlag == 1 >
					    if (enableState != null && enableState != -1){
                            Predicate predicate = cb.equal(root.get("enableState").as(Integer.class), enableState);
                        	predicates.add(predicate);
                        }
					</#if>
				</#list>
				
				<#if entity.treeStructure == 1>
					if (!StringUtils.isEmpty(parentId)){
						Predicate predicate = cb.equal(root.get("parent").get("id").as(String.class), parentId);
						predicates.add(predicate);
					}
				</#if>
				return cb.and(predicates.toArray(new Predicate[0]));
			}
		};
		
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
			pageData = ${entity.entityName?uncap_first}Repo.findAll(spec, pageable);
		} catch (Exception e) {
			log.error("${entity.entityName?uncap_first}Service.find${entity.entityName?cap_first}s方法异常。", e);
		}
		return pageData;
	}