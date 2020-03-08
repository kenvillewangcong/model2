package com.lagou.edu.factory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工厂类，生产对象（反射实现）
 */
public class BeanFactory {

    // 存储实例化对象
    private static Map<String, Object> map = new HashMap<>();

    // 1.读取解析XML文件，通过反射技术实例化对象并存储待用（存储在Map集合）
    static {
        // 加载XML,转为字节流
        InputStream resourceAsStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        // 解析XML
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            List<Element> beanList = rootElement.selectNodes("//bean");
            for (int i = 0; i < beanList.size(); i++) {
                Element element = beanList.get(i);
                // 获取每个bean元素的ID和class属性
                String id = element.attributeValue("id");
                String clazz = element.attributeValue("class");

                // 反射技术实例化对象、
                Class<?> aClass = Class.forName(clazz);
                Object o = aClass.newInstance();

                // 将实例化的对象放入Map
                map.put(id,o);
            }

            // 实例化完成之后维护对象的依赖关系，根据XML文件中配置的property标签注入实例
            List<Element> propertyList = rootElement.selectNodes("//property");

            // 解析property获取依赖实例
            for (int i = 0; i < propertyList.size(); i++) {
                Element element = propertyList.get(i);
                String name = element.attributeValue("name");
                String ref = element.attributeValue("ref");

                // 找到需要注入实例的对象
                Element parent = element.getParent();

                // 获取ID
                String parentId = parent.attributeValue("id");
                Object parentObject = map.get(parentId);

                // 遍历父对象中的set方法注入实例
                Method[] methods = parentObject.getClass().getMethods();
                for (int j = 0; j < methods.length; j++) {
                    Method method = methods[j];

                    // 遍历到的当前方法为需要注入的Set方法
                    if(method.getName().equals("set" + name)){
                        method.invoke(parentObject, map.get(ref));
                    }
                }

                // 将map中的对象刷新
                map.put(parentId, parentObject);
            }

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    // 2.对外提供获取实例对象的接口（根据ID获取）
    public static Object getBean(String id){
        return map.get(id);
    }

}
