package com.bpm.example.demo2.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import org.apache.commons.io.FileUtils;
import org.flowable.common.engine.impl.util.ReflectUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FlowableAdaptDmDatabaseUtils {
    //获取resources目录路径
    String resourcesPath = ReflectUtil.class.getClassLoader().getResource("").getPath();

    public void adaptDmDatabase() throws Exception {
        injectDmToProcessEngineConfiguration();
        copySqlFiles();
        copyPaginationProperties();
        copyLiquibaseFile();
    }

    //动态修改AbstractEngineConfiguration注入达梦数据库配置
    public void injectDmToProcessEngineConfiguration() {
        try {
            //创建一个ClassPool对象，并指定Jar包的路径
            ClassPool classPool = ClassPool.getDefault();
            classPool.insertClassPath(new LoaderClassPath(FlowableAdaptDmDatabaseUtils.class
                    .getClassLoader()));

            //获取需要动态修改的类
            CtClass ctClass = classPool
                    .get("org.flowable.common.engine.impl.AbstractEngineConfiguration");
            //增加代表达梦数据库的成员变量
            CtField field = CtField
                    .make("public static final String DATABASE_TYPE_DM = \"dm\";", ctClass);
            ctClass.addField(field);

            //在getDefaultDatabaseTypeMappings()方法中加入对达梦数据库的适配
            CtMethod method = ctClass.getDeclaredMethod("getDefaultDatabaseTypeMappings");
            method.insertAfter("$_.setProperty(\"DM DBMS\", \"dm\");");
            //将CtClass实例转换为java类并实际加载到当前线程的ClassLoader中
            ctClass.toClass();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //拷贝并修改sql脚本文件
    public void copySqlFiles() throws Exception {
        String prefix = "org/flowable/";
        String suffix = ".sql";
        List<String> sqls = new ArrayList() {{
            add("common/db/create/flowable.oracle.create.common");
            add("common/db/drop/flowable.oracle.drop.common");
            add("identitylink/service/db/create/flowable.oracle.create.identitylink");
            add("identitylink/service/db/create/flowable.oracle.create.identitylink.history");
            add("identitylink/service/db/drop/flowable.oracle.drop.identitylink");
            add("identitylink/service/db/drop/flowable.oracle.drop.identitylink.history");
            add("entitylink/service/db/create/flowable.oracle.create.entitylink");
            add("entitylink/service/db/create/flowable.oracle.create.entitylink.history");
            add("entitylink/service/db/drop/flowable.oracle.drop.entitylink");
            add("entitylink/service/db/drop/flowable.oracle.drop.entitylink.history");
            add("eventsubscription/service/db/create/flowable.oracle.create" +
                    ".eventsubscription");
            add("eventsubscription/service/db/drop/flowable.oracle.drop.eventsubscription");
            add("task/service/db/create/flowable.oracle.create.task");
            add("task/service/db/create/flowable.oracle.create.task.history");
            add("task/service/db/drop/flowable.oracle.drop.task");
            add("task/service/db/drop/flowable.oracle.drop.task.history");
            add("variable/service/db/create/flowable.oracle.create.variable");
            add("variable/service/db/create/flowable.oracle.create.variable.history");
            add("variable/service/db/drop/flowable.oracle.drop.variable");
            add("variable/service/db/drop/flowable.oracle.drop.variable.history");
            add("job/service/db/create/flowable.oracle.create.job");
            add("job/service/db/drop/flowable.oracle.drop.job");
            add("batch/service/db/create/flowable.oracle.create.batch");
            add("batch/service/db/drop/flowable.oracle.drop.batch");
            add("db/create/flowable.oracle.create.engine");
            add("db/create/flowable.oracle.create.history");
            add("db/drop/flowable.oracle.drop.engine");
            add("db/drop/flowable.oracle.drop.history");
            add("idm/db/create/flowable.oracle.create.identity");
            add("idm/db/drop/flowable.oracle.drop.identity");
        }};
        for (String sqlFile : sqls) {
            InputStream input = ReflectUtil.getResourceAsStream(prefix + sqlFile + suffix);
            File targetFile = new File(resourcesPath + prefix + sqlFile.replace("oracle",
                    "dm") + suffix);
            FileUtils.copyInputStreamToFile(input, targetFile);
        }
    }

    //拷贝并修改数据库分页语法配置资源文件
    public void copyPaginationProperties() throws Exception {
        String path = "org/flowable/common/db/properties/oracle.properties";
        InputStream input = ReflectUtil.getResourceAsStream(path);
        File targetFile = new File(resourcesPath + path.replace("oracle", "dm"));
        FileUtils.copyInputStreamToFile(input, targetFile);
    }

    //拷贝并修改Java SPI配置文件
    public void copyLiquibaseFile() throws Exception {
        String liquibaseFilePath = "META-INF/services/liquibase.database.Database";
        InputStream input = ReflectUtil.getResourceAsStream(liquibaseFilePath);
        File targetFile = new File(resourcesPath + liquibaseFilePath);
        FileUtils.copyInputStreamToFile(input, targetFile);

        FileWriter fw = new FileWriter(targetFile, true);
        fw.write("liquibase.database.core.DmDatabase");
        fw.flush();
        fw.close();
    }

}
