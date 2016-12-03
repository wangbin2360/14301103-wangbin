package dev.edu.javaee.spring.autowird;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dev.edu.javaee.spring.bean.BeanDefinition;
import dev.edu.javaee.spring.bean.BeanUtil;
import dev.edu.javaee.spring.bean.PropertyValue;
import dev.edu.javaee.spring.factory.BeanFactory;
import dev.edu.javaee.spring.factory.XMLBeanFactory;
import dev.edu.javaee.spring.resource.LocalFileResource;


public class AutowireCreateBean {
	// �Զ�����ʣ�´�autowired
	public static List<BeanDefinition> CreateRemainedBean(List<BeanDefinition> leftBeanList,
			List<BeanDefinition> FinishedBeanList) throws IllegalArgumentException, IllegalAccessException,
			InstantiationException, NoSuchMethodException, SecurityException, InvocationTargetException {

		List<BeanDefinition> FinishedleftBeanList = new ArrayList<BeanDefinition>();
		for (int i = 0; i < leftBeanList.size(); i++) {
			Class<?> beanClass = null;
			beanClass = leftBeanList.get(i).getBeanClass();
			BeanDefinition beanDefinition = leftBeanList.get(i);
			Constructor con = beanClass.getConstructors()[0];// ѡ���һ�����췽����Ĭ�Ϲ��췽��

			Object bean = null;
			Class[] type = con.getParameterTypes();
			Object bean1 = null;

			Object obj[] = new Object[type.length];
			if (con.isAnnotationPresent(Autowired.class)) {
				Iterator it = FinishedBeanList.iterator();
				System.out.println("Autowiredͨ�����췽���Զ���������");
				// �������е�beandefinition,�ҵ��͹��췽��ƥ���bean;
				while (it.hasNext()) {
					BeanDefinition bean11 = (BeanDefinition) it.next();
					
					for (int i1 = 0; i1 < type.length; i1++) {
						if (bean11.getBean().getClass().equals(type[i1])) {
							obj[i1] = bean11.getBean();
							System.out.println(beanDefinition.getBeanClassName()+"ͨ��Autowired�����ʵ��: "+bean11.getBean().getClass());
							continue;
						}
					}
				}

				bean = con.newInstance(obj);

			}

			Field[] fs = bean.getClass().getDeclaredFields();
			for (int i1 = 0; i1 < fs.length; i1++) {
				fs[i1].setAccessible(true);
				if (fs[i1].isAnnotationPresent(Autowired.class)) {
					System.out.println("Autowiredͨ���ֶ��Զ���������");
					Autowired auto = fs[i1].getAnnotation(Autowired.class);
					Iterator it1 = FinishedBeanList.iterator();
					
					while (it1.hasNext()) {
						BeanDefinition bean12 = (BeanDefinition) it1.next();

						Class<?> cl = (Class<?>) fs[i1].getGenericType();

						if (bean12.getBean().getClass().isAssignableFrom(cl)) {
							fs[i1].set(auto, bean12.getBean());
							System.out.println(beanDefinition.getBeanClassName()+"ͨ��Autowired�����ʵ��: "+bean12.getBean().getClass());
						}
					}

				}
			}

			Method[] method = bean.getClass().getDeclaredMethods();
			for (int i1 = 0; i1 < method.length; i1++) {
				method[i1].setAccessible(true);
				if (method[i1].isAnnotationPresent(Autowired.class)) {
					Autowired auto = fs[i1].getAnnotation(Autowired.class);
					System.out.println("Autowiredͨ����ͨ�����Զ����������");
					Class<?>[] MethodTypeClass = method[i1].getParameterTypes();
					if (MethodTypeClass.length > 0) {
						Object obj1[] = new Object[MethodTypeClass.length];
						Iterator it2 = FinishedBeanList.iterator();
						while (it2.hasNext()) {
							BeanDefinition bean123 = (BeanDefinition) it2.next();
							for (int j = 0; j < MethodTypeClass.length; j++) {
								if (bean123.getBean().getClass().isAssignableFrom(MethodTypeClass[j])) {
									obj1[j] = bean123.getBean();
									System.out.println(beanDefinition.getBeanClassName()+"ͨ��Autowired�����ʵ����"+bean123.getBean().getClass());
								}
							}

						}
						method[i1].invoke(bean, obj1);

					}
				}
			}

			List<PropertyValue> fieldDefinitionList = beanDefinition.getPropertyValues().GetPropertyValues();
			for (PropertyValue propertyValue : fieldDefinitionList) {
				BeanUtil.invokeSetterMethod(bean, propertyValue.getName(), propertyValue.getValue());
			}
			beanDefinition.setBean(bean);
			FinishedleftBeanList.add(beanDefinition);
		}
		return FinishedleftBeanList;
	}
}
