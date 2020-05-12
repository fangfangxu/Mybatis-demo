package com.mybatis.builder;

import com.mybatis.config.Configuration;
import com.mybatis.io.Resources;
import com.mybatis.utils.DocumentUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.Document;
import org.dom4j.Element;


import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * 主要用来解析全局配置文件的
 */
public class XMLConfigBuilder {
private Configuration configuration;


    public XMLConfigBuilder(){
        configuration=new Configuration();
    }

    public Configuration parse(Element element) throws Exception {
        Element envirsElement = element.element("environments");
        parseEnvironments(envirsElement);
        Element mappersElement = element.element("mappers");
        parseMappers(mappersElement);
        return configuration;
    }

    private void parseEnvironments(Element envirsElement) {
        String defaultId = envirsElement.attributeValue("default");
        List<Element> environments = envirsElement.elements("environment");
        for (Element element : environments) {
            if (element.attributeValue("id").equals(defaultId)) {
                parseDataSource(element);
                break;

            }
        }
    }

    /**
     * 解析数据源、放入Configuration中
     *
     * @param element
     */
    private void parseDataSource(Element element) {
        Element dataSourceElement = element.element("dataSource");
        String type = dataSourceElement.attributeValue("type");
        if (type.equals("DBCP")) {
            BasicDataSource dataSource = new BasicDataSource();
            Properties properties = parseProperties(dataSourceElement);

            dataSource.setDriverClassName(properties.getProperty("driver"));
            dataSource.setUrl(properties.getProperty("url"));
            dataSource.setUsername(properties.getProperty("username"));
            dataSource.setPassword(properties.getProperty("password"));
            configuration.setDataSource(dataSource);
        }
    }


    private Properties parseProperties(Element dataSourceElement) {
        Properties properties = new Properties();
        List<Element> propertys = dataSourceElement.elements("property");
        for (Element element : propertys) {
            String name = element.attributeValue("name");
            String value = element.attributeValue("value");
            properties.put(name, value);
        }
        return properties;
    }


    private void parseMappers(Element mappersElement) throws Exception {
        List<Element> mappers = mappersElement.elements("mapper");
        for (Element mapperElement : mappers) {
            String resource = mapperElement.attributeValue("resource");
            /**
             * ==============提取成工具类==============
             */
//            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(resource);
           InputStream resourceAsStream= Resources.getResource(resource);
//            Document document = createDocument(resourceAsStream);
            Document document = DocumentUtils.createDocument(resourceAsStream);
            //按照映射文件的语义进行解析
            /**
             * 映射文件也专人专搞
             */
            XMLMapperBuilder mapperBuilder=new XMLMapperBuilder(configuration);
            mapperBuilder.parse(document.getRootElement());
        }
    }

}
