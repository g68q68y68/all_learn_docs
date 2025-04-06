package com.bpm.example.demo2.javassist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.Modifier;
import lombok.Data;

import java.util.List;

@Data
public class FlowableSerializable {
    //需要动态序列化的类
    List<String> serializableFlowableClassList;

    public void execute() throws Exception {
        // 创建一个ClassPool对象，并指定Jar包的路径
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new LoaderClassPath(FlowableSerializable.class
                .getClassLoader()));
        for (String serializableFlowableClass : serializableFlowableClassList) {
            //获取需要动态修改的类
            CtClass ctClass = classPool.get(serializableFlowableClass);
            //动态添加实现Serializable
            ctClass.addInterface(classPool.get("java.io.Serializable"));
            //如果该类不是接口，则动态添加serialVersionUID属性
            if (!ctClass.isInterface()) {
                CtField field = CtField
                        .make("private static final long serialVersionUID = 1L;", ctClass);
                ctClass.addField(field);
            }
            //将CtClass实例转换为java类并实际加载到当前线程的ClassLoader中
            ctClass.toClass();
        }
        //引入CommandContextUtil类
        classPool.importPackage("org.flowable.engine.impl.util.CommandContextUtil");
        CtClass ctClass = classPool
                .get("org.flowable.common.engine.impl.el.JuelExpression");
        //在JuelExpression类的expressionManager属性前加上transient关键字
        ctClass.getField("expressionManager").setModifiers(Modifier.TRANSIENT);
        //在JuelExpression类的getValue、setValue中添加从流程引擎中获取expressionManager
        CtMethod[] methods = ctClass.getMethods();
        for (CtMethod method : methods) {
            if (method.getName().equals("getValue") || method.getName().equals("setValue")) {
                method.insertBefore("expressionManager = CommandContextUtil" +
                        ".getProcessEngineConfiguration().getExpressionManager();");
            }
        }
        ctClass.toClass();
    }
}
