/*
 * Copyright (c) 2017. Chengdu Qianxing Technology Co.,LTD.
 * All Rights Reserved.
 */

package org.alan.mars.proxy;

import org.alan.mars.proxy.annotation.ProxyMapping;
import org.alan.mars.net.MarsSession;
import org.alan.mars.protobuf.PbMessage;
import org.alan.mars.protobuf.PbMessageDispatcher;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2017/4/19.
 * 脚本解析器
 *
 * @author Chow
 * @since 1.0
 */
public class ProxyResolver implements ApplicationListener<ContextRefreshedEvent> {

    Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private PbMessageDispatcher pb;

    /**
     * 扫描工程目录获取Script类
     *
     * @return 类信息对象
     */
    public List<ProxyClassInfo> scan(ApplicationContext context) {
        try {
            Map<String, Object> beans = context.getBeansWithAnnotation(ProxyMapping.class);
            ClassPool pool = ClassPool.getDefault();
            List<ProxyClassInfo> list = new ArrayList<>();
            for (Object o : beans.values()) {
                String mappingClass = o.getClass().getAnnotation(ProxyMapping.class).value().getName();
                CtClass ctClass = pool.getCtClass(o.getClass().getName());

                Map<String, Method> rightMethods = new HashMap<>();
                Method[] methods = o.getClass().getMethods();
                for (Method method : methods) {
                    if (method.getAnnotation(ProxyMapping.class) == null) {
                        continue;
                    }
                    Class methodCls = method.getAnnotation(ProxyMapping.class).value();
                    String annotationClassName = methodCls.getName();
                    if (rightMethods.containsKey(annotationClassName)) {
                        throw new RuntimeException("类 +(" + ctClass.getName() + ") ProxyMapping = " + annotationClassName + ",超过一个 ,请检查脚本代码的正确性");
                    }
                    rightMethods.put(annotationClassName, method);
                }
                list.add(new ProxyClassInfo(ctClass, mappingClass, rightMethods, o));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过脚本类创建解析后的类
     *
     * @param proxyClassInfo 脚本对象
     * @return 返回一个解析完成的类
     */
    public CtClass createClass(ClassPool pool, ProxyClassInfo proxyClassInfo) {
        try {
            CtClass clazz = pool.makeClass(proxyClassInfo.getScriptClass().getName() + "Proxy");
            CtClass templateInterface = pool.get(PbMessageHandlerProxy.class.getName());
            clazz.addInterface(templateInterface);
            CtField field = CtField.make("private " + proxyClassInfo.getScriptClass().getName() + " data = null;", clazz);
            clazz.addField(field);
            //创建实现方法
            CtMethod method = CtMethod.make(
                    "public int getMessageType(){" +
                            "return " + proxyClassInfo.getMappingClass() + ".getDefaultInstance().getMessageType();" +
                            "}",
                    clazz);
            clazz.addMethod(method);
            method = CtMethod.make(
                    "public void handle(" + MarsSession.class.getName() + " session, " + PbMessage.class.getName() + ".TXMessage msg){" +
                            "int cmd = msg.getCmd();" +
                            makeIf(proxyClassInfo) +
                            "}",
                    clazz);
            clazz.addMethod(method);
            method = CtMethod.make("public void setBean(Object b){" +
                    "this.data = (" + proxyClassInfo.getScriptClass().getName() + ")b;" +
                    "}", clazz);
            clazz.addMethod(method);
            return clazz;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过脚本代码生成IF语句
     *
     * @return if语句
     */
    private String makeIf(ProxyClassInfo proxyClassInfo) {
        final StringBuilder source = new StringBuilder();
        Map<String, Method> methods = proxyClassInfo.getScriptMethods();
        methods.forEach((str, method) -> {
            try {
//                MethodInfo methodInfo = method.getMethodInfo();
                String methodName = method.getName();
                StringBuilder params = new StringBuilder();
                boolean isFirst = true;
                //判断接受方法的参数 如果参数是非规定类型则会传空或者基本类型的默认值
                for (Class ctClass : method.getParameterTypes()) {
                    if (!isFirst) {
                        params.append(",");
                    }
                    if (ctClass.getName().equals(MarsSession.class.getName())) {
                        params.append("session");
                    } else if (ctClass.getName().equals(PbMessage.TXMessage.class.getName())) {
                        params.append("msg");
                    } else if (ctClass.getName().equals("com.tsixi.miner.data.role.RoleController")) {
                        params.append("(com.tsixi.miner.data.role.RoleController)session.getReference(com.tsixi.miner.data.role.RoleController.class)");
                    } else if (ctClass.getName().equals(str)) {
                        params.append(ctClass.getName() + ".parseFrom(msg.getDataMessage())");
                    } else {
                        log.warn(proxyClassInfo.getScriptClass().getName() + "方法" + methodName + "(" + ctClass.getName() + "):参数不合法 , 可能会导致错误!");
                        if (int.class.getName().equals(ctClass.getName())
                                || byte.class.getName().equals(ctClass.getName())
                                || short.class.getName().equals(ctClass.getName())
                                || long.class.getName().equals(ctClass.getName())
                                || float.class.getName().equals(ctClass.getName())
                                || double.class.getName().equals(ctClass.getName())
                                || char.class.getName().equals(ctClass.getName())) {
                            params.append("0");
                        } else if (boolean.class.getName().equals(ctClass.getName())) {
                            params.append("false");
                        } else {
                            params.append("null");
                        }
                    }
                    isFirst = false;
                }
                source.append("if(cmd == " + str + ".getDefaultInstance().getCmd()){" +
                        "this.data." + methodName + "(" + params + ");" +
                        "}").append('\n');
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return source.toString();
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            try {
                log.info("初始化消息解析代理");
                ClassPool pool = ClassPool.getDefault();
                List<ProxyClassInfo> proxyClassInfoList = scan(event.getApplicationContext());
                for (ProxyClassInfo proxyClassInfo : proxyClassInfoList) {
                    CtClass ctClass = createClass(pool, proxyClassInfo);
                    PbMessageHandlerProxy handler = (PbMessageHandlerProxy) ctClass.toClass().newInstance();
                    handler.setBean(proxyClassInfo.getBean());
                    log.info("消息代理:" + ctClass.getName() + "已创建!");
                    //添加到消息分发器中
                    pb.addMessageHandler(handler);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
