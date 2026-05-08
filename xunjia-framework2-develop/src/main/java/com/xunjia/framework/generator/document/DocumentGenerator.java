package com.xunjia.framework.generator.document;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

//@Component
//@Slf4j
public class DocumentGenerator {

    /*@Value("${com.xunjia.framework.baseDownloadFolder}")
    private String downloadFolder;

    @Autowired
    private TextTemplate textTemplate;


    public String generateUseDoc(String projectName, String version, List<CustomEntity> entities, InputStream templateStream){

        String saveFolder = downloadFolder + "用户使用手册";
        File manualFolderFile = new File(saveFolder);
        if (!manualFolderFile.exists() || !manualFolderFile.isDirectory()){
            manualFolderFile.mkdirs();
        }

        FileUtils.copyFile(saveFolder, projectName + ".docx", templateStream);
        File destFile = new File(saveFolder + File.separator + projectName + ".docx");

        Map<String, String> replaceMap = new HashMap<>();
        replaceMap.put("{{projectName}}", projectName);
        replaceMap.put("{{version}}", version);

        XWPFDocument document;
        try (InputStream is = new FileInputStream(destFile)) {
            document = new XWPFDocument(is);
        } catch (Exception e){
            log.error(e.getMessage(), e);
            return null;
        }


        StringBuilder modulesBuilder = new StringBuilder();
        if (!ListUtils.isListEmpty(entities)){
            for(int i = 0; i < entities.size(); i++){
                CustomEntity entity = entities.get(i);
                modulesBuilder.append(entity.getEntityDescr()).append("管理、");

                XWPFParagraph titleParagraph = document.createParagraph();
                XWPFRun titleRun = titleParagraph.createRun();
                titleRun.setText((i + 2) + ". " + entity.getEntityDescr() + "管理");
                titleRun.setBold(true);
                titleRun.setFontSize(16);

                XWPFParagraph moduleDescriptionPara = document.createParagraph();
                XWPFRun moduleDescriptionRun = moduleDescriptionPara.createRun();
                String entityDescription = entity.getInstruction();
                if (!entityDescription.endsWith("。")){
                    entityDescription += "。";
                }
                String moduleDescription = textTemplate.getDescription().replace("{{moduleDescription}}", entityDescription);
                moduleDescriptionRun.setText(moduleDescription);
                moduleDescriptionPara.setIndentationFirstLine(450);

                String operation = entity.getOperations();
                if (StringUtils.isNotEmpty(operation)){
                    String[] operations = operation.split(",");
                    for (int k = 0; k < operations.length; k++){
                        XWPFParagraph operationTitlePara = document.createParagraph();
                        operationTitlePara.createRun().setText("（" + (k + 1) + "）" + operations[k]);
                        operationTitlePara.setFirstLineIndent(450);

                        XWPFParagraph operationPara = document.createParagraph();
                        operationPara.setFirstLineIndent(450);

                        String operationTextTemplate = null;
                        switch(operations[k]){
                            case "添加":
                                operationTextTemplate = textTemplate.getAdd();
                                List<CustomEntityProperty> props = entity.getProperties();
                                StringBuilder propBuilder = new StringBuilder();
                                for (CustomEntityProperty prop : props){
                                    if (prop.getPkFlag() == 0 && prop.getEnableFlag() == 0){
                                        propBuilder.append(prop.getPropDescr()).append("、");
                                    }
                                }
                                String propStr = propBuilder.substring(0, propBuilder.length() - 1);
                                operationTextTemplate = operationTextTemplate.replace("{{propNames}}", propStr);
                                break;
                            case "修改":
                                operationTextTemplate = textTemplate.getUpdate();
                                break;
                            case "删除":
                                operationTextTemplate = textTemplate.getDelete();
                                break;
                            case "启用":
                                operationTextTemplate = textTemplate.getEnable();
                                break;
                            case "禁用":
                                operationTextTemplate = textTemplate.getDisable();
                                break;
                            case "打印":
                                operationTextTemplate = textTemplate.getPrint();
                                break;
                            case "导入":
                                operationTextTemplate = textTemplate.getImportData();
                                break;
                            case "导出":
                                operationTextTemplate = textTemplate.getExportData();
                                break;
                        }
                        operationTextTemplate = operationTextTemplate.replace("{{entityDescription}}", entity.getEntityDescr());
                        operationPara.createRun().setText(operationTextTemplate);
                    }
                }
            }
        }

        String loginText = "本款"+ projectName +"除包含系统必要的用户及权限设置功能外，还包含"+ modulesBuilder.substring(0, modulesBuilder.length() - 1) +"等功能。";
        replaceMap.put("{{loginModuleDescription}}", loginText);

        List<XWPFParagraph> paragraphs = document.getParagraphs();
        List<XWPFHeader> headers = document.getHeaderList();
        this.replaceText(paragraphs, headers, replaceMap);


        try (OutputStream os = new FileOutputStream(destFile)) {
            document.write(os);
        } catch (Exception e){
            log.error(e.getMessage(), e);
        }

        return destFile.getAbsolutePath();
    }

    private void replaceText(List<XWPFParagraph> paragraphs, List<XWPFHeader> headers, Map<String, String> replaceMap){
        Set<String> keySet = replaceMap.keySet();
        //正文段落
        for (XWPFParagraph p : paragraphs){
            List<XWPFRun> runs = p.getRuns();
            for (XWPFRun run : runs){
                String line = run.getText(run.getTextPosition());
                for (String key : keySet){
                    if (line != null && line.contains(key)){
                        line = line.replace(key, replaceMap.get(key));
                        run.setText(line, 0);
                    }
                }
            }
        }
        //页眉
        if (!ListUtils.isListEmpty(headers)){
            for (XWPFHeader header : headers){
                List<XWPFParagraph> headerParagraphs = header.getParagraphs();
                for (XWPFParagraph p : headerParagraphs){
                    for (XWPFRun r : p.getRuns()){
                        String line = r.getText(r.getTextPosition());
                        if (StringUtils.isNotEmpty(line)){
                            for (String key : keySet){
                                if (line.contains(key)){
                                    line = line.replace(key, replaceMap.get(key));
                                    r.setText(line, 0);
                                }
                            }
                        }
                    }
                }

            }
        }
    }*/
}
