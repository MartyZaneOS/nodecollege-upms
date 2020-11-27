package com.nodecollege.cloud.plugins;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * mybatis 插件：注解生成器
 *
 * @author LC
 * @date 17:52 2019/11/2
 **/
public class CommentPlugin extends PluginAdapter {

    /**
     * 接口注释生成
     *
     * @param anInterface Interface
     * @param explain     String
     */
    static void interfaceAnnotation(Interface anInterface, String explain) {
        // 生成注释
        anInterface.addJavaDocLine("/**");
        anInterface.addJavaDocLine(" * 版权：节点学院");
        anInterface.addJavaDocLine(" * <p>");
        anInterface.addJavaDocLine(" * " + anInterface.getType().getShortName());
        anInterface.addJavaDocLine(" *");
        anInterface.addJavaDocLine(" * @author " + System.getProperties().getProperty("user.name"));
        anInterface.addJavaDocLine(" * @date " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        anInterface.addJavaDocLine(" */");
        // 生成注释结束
    }

    /**
     * 方法注释生成
     *
     * @param method  Method
     * @param explain String
     */
    static void methodAnnotation(Method method, String explain) {
        // 生成注释
        StringBuilder sb = new StringBuilder();
        method.addJavaDocLine("/**");
        sb.append(" * ");
        sb.append(explain);
        method.addJavaDocLine(sb.toString());
        List<Parameter> parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            method.addJavaDocLine(" * @param " + parameter.getName() + " " + parameter.getType().getShortName());
        }

        // 添加return注释
        FullyQualifiedJavaType returnType = method.getReturnType();
        method.addJavaDocLine(" * @return " + returnType.getShortName());

        method.addJavaDocLine(" */");
        // 生成注释结束
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    /**
     * 实体类，javabean对象
     *
     * @param topLevelClass     TopLevelClass
     * @param introspectedTable IntrospectedTable
     * @return boolean
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {

        topLevelClass.addImportedType("lombok.Data");

        topLevelClass.addAnnotation("@Data");

        topLevelClass.getJavaDocLines().clear();
        topLevelClass.addJavaDocLine("/**");
        topLevelClass.addJavaDocLine(" * Table: " + introspectedTable.getFullyQualifiedTable());
        topLevelClass.addJavaDocLine(" * 版权：节点学院");
        topLevelClass.addJavaDocLine(" *");
        topLevelClass.addJavaDocLine(" * @author " + System.getProperties().getProperty("user.name"));
        topLevelClass.addJavaDocLine(" * @date " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        topLevelClass.addJavaDocLine(" */");
        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    @Override
    public boolean modelFieldGenerated(Field field, TopLevelClass topLevelClass,
                                       IntrospectedColumn introspectedColumn,
                                       IntrospectedTable introspectedTable,
                                       ModelClassType modelClassType) {
        this.comment(field, introspectedTable, introspectedColumn);
        return true;
    }

    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return false;
    }

    @Override
    public boolean modelSetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn,
                                              IntrospectedTable introspectedTable,
                                              ModelClassType modelClassType) {
        return false;
    }

    /**
     * java bean中，字段注释核心
     *
     * @param element            JavaElement
     * @param introspectedTable  IntrospectedTable
     * @param introspectedColumn IntrospectedColumn
     */
    private void comment(JavaElement element, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        element.getJavaDocLines().clear();
        element.addJavaDocLine("/**");
        String remark = introspectedColumn.getRemarks();
        if (remark != null && remark.length() > 1) {
            element.addJavaDocLine(" * " + remark);
            element.addJavaDocLine(" *");
        }

        // element.addJavaDocLine(" * Table:     " + introspectedTable.getFullyQualifiedTable());
        element.addJavaDocLine(" * " + introspectedColumn.getActualColumnName());
        // element.addJavaDocLine(" * Nullable:  " + introspectedColumn.isNullable());
        element.addJavaDocLine(" */");
    }

    @Override
    public boolean sqlMapResultMapWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        this.commentResultMap(element, introspectedTable);
        return true;
    }

    @Override
    public boolean sqlMapResultMapWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        this.commentResultMap(element, introspectedTable);
        return true;
    }

    void commentResultMap(XmlElement element, IntrospectedTable introspectedTable) {
        List<Element> es = element.getElements();
        if (!es.isEmpty()) {
            String alias = introspectedTable.getTableConfiguration().getAlias();
            int aliasLen = -1;
            if (alias != null) {
                aliasLen = alias.length() + 1;
            }

            Iterator<Element> it = es.iterator();
            HashMap map = new HashMap(es.size());

            while (true) {
                while (it.hasNext()) {
                    Element e = (Element) it.next();
                    if (e instanceof TextElement) {
                        it.remove();
                    } else {
                        XmlElement el = (XmlElement) e;
                        List<Attribute> as = el.getAttributes();
                        if (!as.isEmpty()) {
                            String col = null;
                            Iterator i$ = as.iterator();

                            while (i$.hasNext()) {
                                Attribute a = (Attribute) i$.next();
                                if ("column".equalsIgnoreCase(a.getName())) {
                                    col = a.getValue();
                                    break;
                                }
                            }

                            if (col != null) {
                                if (aliasLen > 0) {
                                    col = col.substring(aliasLen);
                                }

                                IntrospectedColumn ic = introspectedTable.getColumn(col);
                                if (ic != null) {
                                    StringBuilder sb = new StringBuilder();
                                    if (ic.getRemarks() != null && ic.getRemarks().length() > 1) {
                                        sb.append("<!-- ");
                                        sb.append(ic.getRemarks());
                                        sb.append(" -->");
                                        map.put(el, new TextElement(sb.toString()));
                                    }
                                }
                            }
                        }
                    }
                }

                if (map.isEmpty()) {
                    return;
                }

                Set<Element> set = map.keySet();
                Iterator i$ = set.iterator();

                while (i$.hasNext()) {
                    Element e = (Element) i$.next();
                    int id = es.indexOf(e);
                    es.add(id, (Element) map.get(e));
                    es.add(id, new TextElement(""));
                }

                return;
            }
        }
    }

    @Override
    public boolean sqlMapInsertElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.getAttributes().add(new Attribute("useGeneratedKeys", "true"));
        element.getAttributes().add(new Attribute("keyProperty", introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty()));
        return true;
    }

    @Override
    public boolean sqlMapInsertSelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        element.getAttributes().add(new Attribute("useGeneratedKeys", "true"));
        element.getAttributes().add(new Attribute("keyProperty", introspectedTable.getPrimaryKeyColumns().get(0).getJavaProperty()));
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeySelectiveElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        this.removeAttribute(element.getAttributes(), "parameterType");
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        this.removeAttribute(element.getAttributes(), "parameterType");
        return true;
    }

    @Override
    public boolean sqlMapUpdateByPrimaryKeyWithoutBLOBsElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
        this.removeAttribute(element.getAttributes(), "parameterType");
        return true;
    }

    private void removeAttribute(List<Attribute> as, String name) {
        if (!as.isEmpty()) {
            Iterator it = as.iterator();

            Attribute attr;
            do {
                if (!it.hasNext()) {
                    return;
                }

                attr = (Attribute) it.next();
            } while (!attr.getName().equalsIgnoreCase(name));

            it.remove();
        }
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
        document.getRootElement().addElement(new TextElement(""));
        document.getRootElement().addElement(new TextElement("<!-- ### 以上代码由MBG + CommentPlugin自动生成, 生成时间: " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()) + " ### -->\n\n\n"));
        document.getRootElement().addElement(new TextElement("<!-- Your codes goes here!!! -->"));
        document.getRootElement().addElement(new TextElement(""));
        return true;
    }

    /**
     * mapper 接口文件注释处理
     *
     * @param anInterface       Interface
     * @param topLevelClass     TopLevelClass
     * @param introspectedTable IntrospectedTable
     * @return boolean
     */
    @Override
    public boolean clientGenerated(Interface anInterface, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaceAnnotation(anInterface, null);
        // import支持
        Set<FullyQualifiedJavaType> set = new HashSet<>();
        set.add(new FullyQualifiedJavaType(MyBatisAnnotationEnum.Mapper.getClazz()));
        set.add(new FullyQualifiedJavaType(MyBatisAnnotationEnum.Component.getClazz()));
        anInterface.addImportedTypes(set);

        // 增加注解
        anInterface.addAnnotation(MyBatisAnnotationEnum.Mapper.getAnnotation());
        anInterface.addAnnotation(MyBatisAnnotationEnum.Component.getAnnotation());
        return super.clientGenerated(anInterface, topLevelClass, introspectedTable);

    }

    /**
     * mapper接口方法 - DeleteByPrimaryKey方法
     *
     * @param method            Method
     * @param anInterface       Interface
     * @param introspectedTable IntrospectedTable
     * @return boolean
     */
    @Override
    public boolean clientDeleteByPrimaryKeyMethodGenerated(Method method, Interface anInterface, IntrospectedTable introspectedTable) {
        methodAnnotation(method, "根据主键删除数据");
        return super.clientDeleteByPrimaryKeyMethodGenerated(method, anInterface, introspectedTable);
    }

    /**
     * mapper接口方法 - Insert方法
     *
     * @param method            Method
     * @param anInterface       Interface
     * @param introspectedTable IntrospectedTable
     * @return boolean
     */
    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface anInterface, IntrospectedTable introspectedTable) {
        methodAnnotation(method, "插入数据库记录（不建议使用）");
        return true;
    }

    /**
     * mapper接口方法 - InsertSelective方法
     *
     * @param method            Method
     * @param anInterface       Interface
     * @param introspectedTable IntrospectedTable
     * @return boolean
     */
    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface anInterface, IntrospectedTable introspectedTable) {
        methodAnnotation(method, "插入数据库记录（建议使用）");
        return super.clientInsertSelectiveMethodGenerated(method, anInterface, introspectedTable);
    }

    /**
     * mapper接口方法 - SelectByPrimaryKey方法
     *
     * @param method            Method
     * @param anInterface       Interface
     * @param introspectedTable IntrospectedTable
     * @return boolean
     */
    @Override
    public boolean clientSelectByPrimaryKeyMethodGenerated(Method method, Interface anInterface, IntrospectedTable introspectedTable) {
        methodAnnotation(method, "根据主键id查询");
        return super.clientSelectByPrimaryKeyMethodGenerated(method, anInterface, introspectedTable);
    }

    /**
     * mapper接口方法 -  UpdateByPrimaryKeySelective方法
     *
     * @param method            Method
     * @param anInterface       Interface
     * @param introspectedTable IntrospectedTable
     * @return boolean
     */
    @Override
    public boolean clientUpdateByPrimaryKeySelectiveMethodGenerated(Method method, Interface anInterface, IntrospectedTable introspectedTable) {
        methodAnnotation(method, "修改数据(推荐使用)");
        return super.clientUpdateByPrimaryKeySelectiveMethodGenerated(method, anInterface, introspectedTable);
    }

    /**
     * mapper接口方法 - UpdateByPrimaryKey方法
     *
     * @param method            Method
     * @param anInterface       Interface
     * @param introspectedTable IntrospectedTable
     * @return boolean
     */
    @Override
    public boolean clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(Method method, Interface anInterface, IntrospectedTable introspectedTable) {
        methodAnnotation(method, "修改数据");
        return super.clientUpdateByPrimaryKeyWithoutBLOBsMethodGenerated(method, anInterface, introspectedTable);
    }
}