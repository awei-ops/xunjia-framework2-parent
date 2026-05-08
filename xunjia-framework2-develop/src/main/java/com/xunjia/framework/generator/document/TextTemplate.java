package com.xunjia.framework.generator.document;

/*@Configuration
@ConfigurationProperties(prefix = "manual.module", ignoreUnknownFields = false)
@PropertySource(value = "classpath:document-text-template.properties")
@Data
@Component*/
public class TextTemplate {

    private String description;

    private String add;

    private String update;

    private String delete;

    private String enable;

    private String disable;

    private String importData;

    private String exportData;

    private String print;
}
