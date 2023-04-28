package my.framework.web;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 用于加载Controller映射信息
 *
 */
public class ControllerMappingManager {

	private Map<String, ControllerMapping> controllerMappings = new HashMap<String, ControllerMapping>();

	/**
	 * 构造方法
	 * 
	 * @param configFileName
	 *            配置文件名
	 */
	public ControllerMappingManager(String configFileName) {
		init(configFileName);
	}

	/**
	 * init方法用来加载Controller配置文件。
	 * 
	 * @param configFileName
	 *            配置文件名
	 */
	public void init(String configFileName) {
		InputStream is = this.getClass().getResourceAsStream("/" + configFileName);
		Document doc;
		try {
			doc = new SAXReader().read(is);
		} catch (DocumentException e) {
			throw new ConfigLoadingException(e);
		}
		Element root = doc.getRootElement();
		Iterator<Element> controllersIt = root.elements("controllers").iterator();
		Element controllersEl = controllersIt.next();
		for (Iterator<Element> controllerIt = controllersEl.elementIterator("controller"); controllerIt.hasNext();) {
			Element controller = controllerIt.next();
			String firstPath = controller.attributeValue("path");
			String className = controller.attributeValue("class");
			if (className.isEmpty())
				throw new ConfigException("Controller的映射信息不能为空！");
			ControllerMapping mapping = null;
			for (Iterator<Element> methodIt = controller.elementIterator("method"); methodIt.hasNext();) {
				Element method = methodIt.next();
				String secondPath = method.attributeValue("path");
				String methodName = method.getText();
				if (methodName.isEmpty())
					throw new ConfigException("方法的映射信息不能为空！");
				String fullPath = (firstPath + secondPath).replaceAll("//", "/");
				if (controllerMappings.containsKey(fullPath)) {
					mapping = controllerMappings.get(fullPath);
					throw new ConfigException("不能将路径 " + fullPath + " 映射到 " + className + "." + methodName + "()\n路径 "
							+ fullPath + " 已被 " + mapping.getClassName() + "." + mapping.getMethodName() + "() 映射");
				}
				mapping = new ControllerMapping(className, methodName);
				controllerMappings.put(fullPath, mapping);
				System.out.println("================================map " + fullPath + " to " + className + "."
						+ methodName + "()"); // log
			}
		}
	}

	/**
	 * 根据映射信息查询对应的ControllerMapping实例。
	 * 
	 * @param path
	 * @return ControllerMapping
	 */
	public ControllerMapping getControllerMapping(String path) {
		return this.controllerMappings.get(path);
	}
	
	/**
	 * 判断是否存在对应的映射信息。
	 * 
	 * @param path
	 * @return
	 */
	public boolean containsKey(String path) {
		return this.controllerMappings.containsKey(path);
	}

	/**
	 * @return 返回所有Controller映射信息
	 */
	public Map<String, ControllerMapping> getControllerMappings() {
		return this.controllerMappings;
	}
}