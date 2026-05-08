package com.xunjia.framework.common.customerLabel.dialect;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import com.xunjia.framework.common.customerLabel.element.ButtonLabelProcessor;


/**
 * 框架thymeleaf自定义标签方言
 * 2020/5/8
 * @author 姜浩
 */
@Component
public class FrameworkLabelDialect extends AbstractProcessorDialect implements IProcessorDialect {

	/** 方言名称 */
	private static final String DIALECT_NAME = "Framework Dialect";
	
	/** 标签前缀 */
	private static final String PREFIX="xunjia";
	
	public FrameworkLabelDialect() {
		super(DIALECT_NAME, PREFIX, StandardDialect.PROCESSOR_PRECEDENCE);
	}

	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		Set<IProcessor> processors = new HashSet<IProcessor>();
		processors.add(new ButtonLabelProcessor(PREFIX));
		return processors;
	}
}
