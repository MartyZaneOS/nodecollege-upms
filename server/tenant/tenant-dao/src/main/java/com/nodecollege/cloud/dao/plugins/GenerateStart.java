package com.nodecollege.cloud.dao.plugins;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 注意：根据个人pc不同，要调整generatorConfig.xml中的对应文件的路径
 * <p>
 * 1. 配置generatorConfig.xml
 * 2. 运行该类，生成对应的文件
 *
 * @author LC
 * @date 17:54 2019/11/2
 **/
public class GenerateStart {
    public static void main(String[] args) {
        try {
            System.out.println("--------------------start generator-------------------");
            List<String> warnings = new ArrayList<>();
            boolean overWrite = true;
            ConfigurationParser cp = new ConfigurationParser(warnings);
            InputStream resourceAsStream = GenerateStart.class.getClassLoader().getResourceAsStream("generatorConfig.xml");
            Configuration config = cp.parseConfiguration(resourceAsStream);
            DefaultShellCallback callback = new DefaultShellCallback(overWrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            myBatisGenerator.generate(null);
            System.out.println("--------------------end generator-------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
