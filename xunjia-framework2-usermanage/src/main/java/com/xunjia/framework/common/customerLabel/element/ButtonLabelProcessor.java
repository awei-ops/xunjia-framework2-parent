package com.xunjia.framework.common.customerLabel.element;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.xunjia.framework.usermanage.entity.Resource;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.Expression;
import org.thymeleaf.standard.expression.StandardExpressionParser;
import org.thymeleaf.templatemode.TemplateMode;

import com.xunjia.framework.common.Context;
import com.xunjia.framework.resource.html.ButtonHtmlGen;
import com.xunjia.framework.utils.ListUtils;
import com.xunjia.framework.utils.StringUtils;

/**
 * Easyui LinkButton部分有权按钮标签处理器
 * 2020/5/8
 * @author 姜浩
 */
public class ButtonLabelProcessor extends AbstractElementTagProcessor {

	/** 优先级 */
	private static final int PRECEDENCE = 10000;
	
	/** 标签名称 */
	private static final String TAG_NAME = "button";

	public ButtonLabelProcessor(String dialectPrefix) {
		super(TemplateMode.HTML, 
				dialectPrefix, 
				TAG_NAME, 
				true, 
				null, 
				false, 
				PRECEDENCE);
	}

	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag,
			IElementTagStructureHandler structureHandler) {
		
		IAttribute permCodeAttr = tag.getAttribute("perm-code");
		IAttribute resCodeAttr = tag.getAttribute("res-code");
		IAttribute menuIdAttr = tag.getAttribute("th:menu-id");		//menuId使用表达式传入，必须th:开头
		if (permCodeAttr != null && menuIdAttr != null) {
			String permCode = permCodeAttr.getValue();
			String menuIdStr = menuIdAttr.getValue();
			String resCode = resCodeAttr == null ? "" : resCodeAttr.getValue();
			if (!StringUtils.isEmpty(permCode) && !StringUtils.isEmpty(menuIdStr)) {
				Object executeExpression = null;
				if (!StringUtils.isEmpty(menuIdStr)) {
					executeExpression = executeExpression(menuIdStr, context);// 执行表达式
				}
				String menuId = executeExpression.toString();
				
				List<Resource> authorizedResources = Context.getAuthorizedResources();
				if (!ListUtils.isListEmpty(authorizedResources)) {
					Stream<Resource> stream = authorizedResources.stream()
					.filter(c -> c.getType().equals("按钮")
							&& c.getEnable() == 1
							&& c.getParent() != null
							&& c.getParent().getId().equals(menuId)
							&& c.getPermissionCode().equals(permCode));
					
					Optional<Resource> resourceOptional = null;
					if (permCode.equals("authc")) {
						resourceOptional = stream.filter(c -> resCode.equals(c.getCode())).findFirst();
					} else {
						resourceOptional = stream.findFirst();
					}
					if (resourceOptional.isPresent()) {
						//生成html标签
						String buttonHtml = ButtonHtmlGen.generateButtonHtml(resourceOptional.get());
						structureHandler.setBody(buttonHtml, true);
					}
				}
			}
		}
	}
	
	/**
	 * 执行自定义标签中的表达式
	 * @param value 表达式
	 * @param context 上下文
	 * @return 表达式计算结果
	 */
	private Object executeExpression(String value, ITemplateContext context) {
		StandardExpressionParser parser = new StandardExpressionParser();
		Expression parseExpression = parser.parseExpression(context, value);
		Object execute = parseExpression.execute(context);
		return execute;
	}
}
