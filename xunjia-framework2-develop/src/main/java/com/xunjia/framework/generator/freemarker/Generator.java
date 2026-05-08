package com.xunjia.framework.generator.freemarker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

import com.xunjia.framework.generator.bean.DataModel;
import com.xunjia.framework.generator.entity.CustomEntity;
import com.xunjia.framework.utils.ArrayUtils;
import com.xunjia.framework.utils.StringUtils;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

public class Generator {

	private Configuration configuration;
	
	private String outFileDir;

	private CustomEntity entity;

	private DataModel dataModel;
	
	public Generator(CustomEntity entity, String outFileDir) {
		this.entity = entity;
		this.dataModel = new DataModel(entity);
		this.outFileDir = outFileDir;
		this.configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		//设置默认生成文件编码
		this.configuration.setDefaultEncoding("utf-8");
		//设置模板路径
		this.configuration.setClassForTemplateLoading(this.getClass(), "/static/generatorTemplates");
	}
	
	public void generateEntity() throws TemplateNotFoundException,
		MalformedTemplateNameException, ParseException, IOException, TemplateException {
		String templateFile = "/entity.ftl";
		String outputDirPath = outFileDir + "/" + entity.getPackageName().replaceAll("\\.", "/") + "/entity";
		String outputFilePath = outputDirPath + "/" + entity.getEntityName() + ".java";
		
		this.generateFile(templateFile, outputDirPath, outputFilePath, dataModel);
	}
	
	public void generateRepository() throws TemplateNotFoundException,
		MalformedTemplateNameException, ParseException, IOException, TemplateException {
		String templateFile = "/repository.ftl";
		String outputDirPath = outFileDir + "/" + entity.getPackageName().replaceAll("\\.", "/") + "/repository";
		String outputFilePath = outputDirPath + "/I" + StringUtils.upperFirst(entity.getEntityName()) + "Repository.java";
		
		this.generateFile(templateFile, outputDirPath, outputFilePath, dataModel);
	}
	
	public void generateService() throws TemplateNotFoundException,
		MalformedTemplateNameException, ParseException, IOException, TemplateException {
		
		String[] operations = dataModel.getOperations();
		Map<String, String> methodCodeMap = dataModel.getMethodCode();
		
		if(!ArrayUtils.isArrayEmpty(operations)) {
			for (String operation : operations) {
				switch (operation) {
				case "添加":
					String saveMethodTemplate = "/serviceSaveMethod.ftl";
					String saveMethodCode = this.generateFileToString(saveMethodTemplate, dataModel);
					methodCodeMap.put("saveMethod", saveMethodCode);
					break;
				case "修改":
					String updateMethodTemplate = "/serviceUpdateMethod.ftl";
					String updateMethodCode = this.generateFileToString(updateMethodTemplate, dataModel);
					methodCodeMap.put("updateMethod", updateMethodCode);
					break;
				case "删除":
					String deleteMethodTemplate = "/serviceDeleteMethod.ftl";
					String deleteMethodCode = this.generateFileToString(deleteMethodTemplate, dataModel);
					methodCodeMap.put("deleteMethod", deleteMethodCode);
					break;
				case "启用":
				case "禁用":
					String stateMethodsTemplate = "/serviceStateMethods.ftl";
					String stateMethodsCode = this.generateFileToString(stateMethodsTemplate, dataModel);
					methodCodeMap.put("stateMethods", stateMethodsCode);
					break;
				case "导入":
					break;
				case "导出":
					break;
				}
			}
			
			String queryMethodTemplate = dataModel.getSearchFlag() == 0 ? "/serviceNonParmsQueryMethod.ftl" : "/serviceQueryMethod.ftl";
			String queryMethodCode = this.generateFileToString(queryMethodTemplate, dataModel);
			methodCodeMap.put("queryMethod", queryMethodCode);
		}
		
		String templateFile = "/service.ftl";
		String outputDirPath = outFileDir + "/" + entity.getPackageName().replaceAll("\\.", "/") + "/service";
		String outputFilePath = outputDirPath + "/" + StringUtils.upperFirst(entity.getEntityName()) + "Service.java";
		
		this.generateFile(templateFile, outputDirPath, outputFilePath, dataModel);
	}
	
	public void generateController() throws TemplateNotFoundException,
		MalformedTemplateNameException, ParseException, IOException, TemplateException {
		String templateFile = "/controller.ftl";
		String outputDirPath = outFileDir + "/" + entity.getPackageName().replaceAll("\\.", "/") + "/controller";
		String outputFilePath = outputDirPath + "/" + StringUtils.upperFirst(entity.getEntityName()) + "Controller.java";
		
		this.generateFile(templateFile, outputDirPath, outputFilePath, dataModel);
	}
	
	public void generateListHtml() throws TemplateNotFoundException,
	MalformedTemplateNameException, ParseException, IOException, TemplateException {
		String templateFile = "/html/list.ftl";
		String outputDirPath = outFileDir + "/" + entity.getPackageName().replaceAll("\\.", "/") + "/templates/" + StringUtils.lowerFirst(entity.getEntityName());
		String outputFilePath = outputDirPath + "/list.html";
		
		this.generateFile(templateFile, outputDirPath, outputFilePath, dataModel);
	}
	
	public void generateTreeListHtml() throws TemplateNotFoundException,
	MalformedTemplateNameException, ParseException, IOException, TemplateException {
		String templateFile = "/html/tree_list.ftl";
		String outputDirPath = outFileDir + "/" + entity.getPackageName().replaceAll("\\.", "/") + "/templates/" + StringUtils.lowerFirst(entity.getEntityName());
		String outputFilePath = outputDirPath + "/list.html";
		
		this.generateFile(templateFile, outputDirPath, outputFilePath, dataModel);
	}
	
	public void generateAddHtml() throws TemplateNotFoundException,
	MalformedTemplateNameException, ParseException, IOException, TemplateException {
		String templateFile = "/html/add.ftl";
		String outputDirPath = outFileDir + "/" + entity.getPackageName().replaceAll("\\.", "/") + "/templates/" + StringUtils.lowerFirst(entity.getEntityName());
		String outputFilePath = outputDirPath + "/add.html";
		
		this.generateFile(templateFile, outputDirPath, outputFilePath, dataModel);
	}
	
	public void generateEditHtml() throws TemplateNotFoundException,
	MalformedTemplateNameException, ParseException, IOException, TemplateException {
		String templateFile = "/html/edit.ftl";
		String outputDirPath = outFileDir + "/" + entity.getPackageName().replaceAll("\\.", "/") + "/templates/" + StringUtils.lowerFirst(entity.getEntityName());
		String outputFilePath = outputDirPath + "/edit.html";
		
		this.generateFile(templateFile, outputDirPath, outputFilePath, dataModel);
	}
	
	private void generateFile(String templateFile, String outputDirPath, String outputFilePath, Object dataModel) 
			throws TemplateNotFoundException, MalformedTemplateNameException, 
			ParseException, IOException, TemplateException {
		
		File outputDirFile = new File(outputDirPath);
		if (!outputDirFile.exists()) {
			outputDirFile.mkdirs();
		}
		
		File outputFile = new File(outputFilePath);
		if (outputDirFile.exists()) {
			outputFile.delete();
		}
		
		//获取模板
		Template template = configuration.getTemplate(templateFile);
		//创建输出对象,将文件输出到D盘根目录下
		FileWriter fileWriter = new FileWriter(outputFile);
		//渲染模板和数据
		template.process(dataModel, fileWriter);
		//关闭输出
		fileWriter.close();
	}
	
	private String generateFileToString(String templateFile, Object dataModel) throws TemplateNotFoundException, 
		MalformedTemplateNameException, ParseException, IOException, TemplateException {
		//获取模板
		Template template = configuration.getTemplate(templateFile);
		
		StringWriter writer = new StringWriter();
		template.process(dataModel, writer);
		String result = writer.toString();
		writer.close();
		return result;
	}
}
