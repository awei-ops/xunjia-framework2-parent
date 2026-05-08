package ${entity.packageName}.entity;

<#list classes as c>
import ${c};
</#list>

/**
 * ${entity.entityDescr}
 * ${entity.createDate?string('yyyy年MM月dd日')}
 * @author ${entity.author}
 */
<#if entity.tableName??>
	@Table
<#else>
	@Table(name="${entity.tableName}")
</#if>
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ${entity.entityName} implements Serializable {

	<#list entity.properties as p>
	
		<#if p.pkFlag == 1>
			@Id
			@Column(name = "${p.columnName}", unique = true, nullable = false)
			@GeneratedValue(generator="system-uuid")
			@GenericGenerator(name="system-uuid",strategy="uuid")
			private String ${p.propName};
		<#elseif p.type?contains("List")>
			<#if p.fetchType != "">
			@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.${p.fetchType}, mappedBy = "${p.mappedProp}")
			<#else>
			@OneToMany(cascade = CascadeType.REFRESH, fetch = FetchType.LAZY, mappedBy = "${p.mappedProp}")
			</#if>
			private List<CustomEntityProperty> properties;
		<#elseif p.type?contains(".")>
			<#if p.fetchType != "">
			@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.${p.fetchType})
			<#else>
			@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
			</#if>
    		@JoinColumn(name = "${p.columnName}")
			private ${p.type?substring(0, p.type?last_index_of(".") - 1)} ${p.propName};
		<#elseif p.type == "Date">
			@Column
			@DateTimeFormat(pattern = "yyyy-MM-dd")
			private Date ${p.propName};
		<#else>
			<#if p.type == entity.entityName>
				@ManyToOne(cascade = {CascadeType.REFRESH},fetch = FetchType.EAGER)
			    @JoinColumn(name = "parentId")
			<#else>
				@Column
			</#if>
			private ${p.type} ${p.propName};
		</#if>
		
	
	</#list>
}