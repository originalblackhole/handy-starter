package com.github.handy.jdbc.mybatis;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * <p></p>
 *
 * @author rui.zhou
 * @date 2018/12/4 18:10
 */
@Slf4j
@Data
public class GenericXmlMapperBuilder {

    //mapperLocations
    private Resource[] mapperLocations;

    //mybatis-config path
    private String configPath;


    private String javaPackage;


    private HashMap<String, String> typeAliasesHashMap = new HashMap<String, String>();

    //xml中结果集映射片段id
    private String baseResultMap;

    //xml中配置的表名片段id
    private String baseTableName;

    //xml中配置的所有字段片段id
    private String baseColumns;

    //id生成sql
    private String idGenerationSql;
    /**
     * 数据库Key生成方式
     */
    private KeyGenerationMode keyGenerationMode = KeyGenerationMode.IDENTITY;

    public GenericXmlMapperBuilder() throws Exception {
        log.info("GenericXmlMapperBuilder | {}", "new");
    }

    public Resource[] builderGenericXmlMapper(Resource[] mapperLocations) throws Exception {
        parserConfig();
        Resource[] genericMapperResources = new Resource[mapperLocations.length];

        for (int i = 0; i < mapperLocations.length; i++) {
            genericMapperResources[i] = parse(mapperLocations[i]);

        }

        return genericMapperResources;
    }

    /**
     * 支持Mybatis配置文件
     *
     * @throws Exception
     */
    private void parserConfig() throws Exception {
        if (StringUtils.isEmpty(configPath)) {
            return;
        }

        XPath xpath = XPathFactory.newInstance().newXPath();
        Node rootNode = (Node) xpath.evaluate("/configuration", new InputSource(configPath), XPathConstants.NODE);
        Node packageNode = (Node) xpath.evaluate("package", rootNode, XPathConstants.NODE);
        if (packageNode != null) {
            javaPackage = getAttribute(packageNode, "name", true);
        }

        //处理别名
        NodeList typeAliaseNodeList = (NodeList) xpath.evaluate("typeAliases", rootNode, XPathConstants.NODESET);
        for (int i = 0; i < typeAliaseNodeList.getLength(); i++) {
            Node typeAliaseNode = typeAliaseNodeList.item(i);
            typeAliasesHashMap.put(getAttribute(typeAliaseNode, "alias", false), getAttribute(typeAliaseNode, "type", false));
        }
    }


    //处理mapper文件，以此处理每个node 构建TableDefine对象
    private Resource parse(Resource resource) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        builder.setEntityResolver(new XMLMapperEntityResolver());
        Document document = builder.parse(resource.getInputStream());
        Node rootNode = (Node) xpath.evaluate("/mapper", document, XPathConstants.NODE);
        if (rootNode == null) {
            log.info("Invalid XML Map file, ignore xml file {}", resource.getFile().getAbsolutePath());
            return null;
        }

        //查找表名Sql块，这里其实可以通过JPA注解拿到实体里注解的表名的，但对实体类有侵入性，先这样。
        Node tableNameNode = (Node) xpath.evaluate("sql[@id='" + baseTableName + "'][1]", rootNode, XPathConstants.NODE);

        if (tableNameNode == null) {
            log.info("Can't found {} sql segment, ignore map {}", baseTableName, resource.getFile().getAbsolutePath());
            return null;
        }

        String classType = getFullClassPath(tableNameNode.getTextContent().trim());
        TableDefine table = new TableDefine();
        table.setTable(classType);
        table.setNamespace(getAttribute(rootNode, "namespace", true));


        //查找自定义的主键生成Sql块
        Node generationTypeNode = (Node) xpath.evaluate("sql[@id='" + idGenerationSql + "'][1]", rootNode, XPathConstants.NODE);
        if (generationTypeNode != null) {
            String generationTypeValue = generationTypeNode.getTextContent().trim();
            if (generationTypeValue.length() > 0) {
                int pos = generationTypeValue.indexOf("(");

                String code = generationTypeValue.substring(0, pos).trim();
                KeyGenerationMode privateKeyGenerationMode = KeyGenerationMode.parse(code);
                if (privateKeyGenerationMode != null) {
                    String value = generationTypeValue.substring(pos + 1, generationTypeValue.length() - 1).trim();
                    privateKeyGenerationMode.setValue(value);

                    table.setKeyGenerationMode(privateKeyGenerationMode);
                }
            }
        }

        //查询字段列表Sql块
        table.setBaseColumnsId(baseColumns);
        Node baseColumnsNode = (Node) xpath.evaluate("sql[@id='" + baseColumns + "'][1]", rootNode, XPathConstants.NODE);
        if (baseColumnsNode != null) {
            table.setBaseColumns(baseColumnsNode.getTextContent().trim());
        }

        //查找表字段块
        Node resultMapNode = (Node) xpath.evaluate("resultMap[@id='" + baseResultMap + "'][1]", rootNode, XPathConstants.NODE);

        if (resultMapNode == null) {
            log.info("Can't found {} resultMap, ignore map {}", baseTableName, resource.getFile().getAbsolutePath());
            return null;
        }

        //后面这里其实可以通过数据库连接拿到表的元数据。进行拼接
        //组装表的映射类
        table.setClassType(getAttribute(resultMapNode, "type", true));

        //组装主键
        Node idNode = (Node) xpath.evaluate("id[1]", resultMapNode, XPathConstants.NODE);
        table.setIdColumn(createTableColumn(idNode));
        table.setBaseResultMap(baseResultMap);

        //组装非主键字段
        ArrayList<TableColumnDefine> columnList = new ArrayList<TableColumnDefine>();
        NodeList resultNodeList = (NodeList) xpath.evaluate("result", resultMapNode, XPathConstants.NODESET);
        for (int i = 0; i < resultNodeList.getLength(); i++) {
            Node resultNode = resultNodeList.item(i);
            columnList.add(createTableColumn(resultNode));
        }
        table.setColumnList(columnList);
        //获取Mapper里所有的select/insert/update/delete
        HashSet<String> existMethodSet = findExistMethodInMapper(xpath, rootNode);

        return rewriteXmlMapper(resource, table, existMethodSet);
    }


    /**
     * 获取Mapper里所有的select/insert/update/delete
     * 用于判断是否要由框架生成
     *
     * @param xpath
     * @param rootNode
     * @return
     * @throws XPathExpressionException
     */
    private HashSet<String> findExistMethodInMapper(XPath xpath, Node rootNode) throws XPathExpressionException {
        HashSet<String> existMethodSet = new HashSet<String>();
        NodeList sqlNodeList = (NodeList) xpath.evaluate("select|insert|update|delete", rootNode, XPathConstants.NODESET);
        for (int i = 0; i < sqlNodeList.getLength(); i++) {
            Node sqlNode = sqlNodeList.item(i);
            existMethodSet.add(getAttribute(sqlNode, "id", false));
        }
        return existMethodSet;
    }

    /**
     * @param resource       原XML Mapper
     * @param table          Xml Mapper分析出来的表元数据
     * @param existMethodSet XML Mapper 已经存在的SQL 操作方法
     * @return
     * @throws IOException
     * @throws UnsupportedEncodingException
     */
    private Resource rewriteXmlMapper(Resource resource, TableDefine table, HashSet<String> existMethodSet)
            throws IOException, UnsupportedEncodingException {
        String idPropertyType = getIdPropertyType(table.getClassType(), table.getIdColumn().getProperty());

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(bos);
        //这里生成公用方法
        GenericMethodXmlGenerator.process(writer, table, idPropertyType, existMethodSet, keyGenerationMode);
        writer.flush();

        //获取原始mapper
        String originalXmlMapperStr = "";
        String line = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), "utf-8"));
        while ((line = br.readLine()) != null) {
            originalXmlMapperStr = originalXmlMapperStr + line + "\n";
        }
        //构建完整体mapper文件
        int index = originalXmlMapperStr.lastIndexOf("</mapper>");
        String xmlContext = originalXmlMapperStr.substring(0, index) + new String(bos.toByteArray(), "utf-8") + originalXmlMapperStr.substring(index);

        log.debug("rewite xml mapper content：\n{}", xmlContext);

        return new ByteArrayResource(xmlContext.getBytes("utf-8"), resource.getFilename());
    }

    /**
     * 获取主键的Class类型
     *
     * @param classType 表的类的类型
     * @param id        表主键属性
     * @return
     */
    private String getIdPropertyType(String classType, String id) {
        try {
            Class<?> idClass = Class.forName(classType);

            Field idField = null;
            for (; idClass != Object.class; idClass = idClass.getSuperclass()) {
                try {
                    idField = idClass.getDeclaredField(id);
                    break;
                } catch (Exception e) {
                }
            }
            return idField.getType().getName();
        } catch (Exception e) {
            throw new InvalidXmlException(String.format("Can't parse id property %s.%s in result map, exception:%s", classType, id, e.getMessage()));
        }
    }

    /**
     * 获取Class全路径
     *
     * @param name 类名
     * @return
     */
    private String getFullClassPath(String name) {
        String type = typeAliasesHashMap.get(name);

        if (StringUtils.isEmpty(type) && !StringUtils.isEmpty(javaPackage)) {
            return javaPackage + "." + name;
        }

        return name;
    }

    /**
     * @param idNode
     */
    private TableColumnDefine createTableColumn(Node idNode) {
        TableColumnDefine column = new TableColumnDefine();
        column.setColumn(getAttribute(idNode, "column", true));
        column.setProperty(getAttribute(idNode, "property", true));
        column.setJdbcType(getAttribute(idNode, "jdbcType", false));

        return column;
    }

    /**
     * 获取节点属性
     *
     * @param node 节点
     * @param name 属性名称
     * @return
     */
    private String getAttribute(Node node, String name, boolean necessary) {
        Node attr = node.getAttributes().getNamedItem(name);
        String value = null;

        if (attr != null) {
            value = attr.getNodeValue();
        }

        if (necessary && StringUtils.isEmpty(value)) {
            throw new InvalidXmlException("Can't find " + name + " attribute in result map");
        }

        return value;
    }
}
