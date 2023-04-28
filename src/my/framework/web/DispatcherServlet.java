package my.framework.web;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义Web框架的核心控制器类，web框架的入口
 */
public class DispatcherServlet extends HttpServlet {
	private static final long serialVersionUID = -3619491573769301637L;
	/**
	 * 默认的配置文件名，可以在web.xml中通过初始化参数configFileName修改
	 */
	private String configFileName = "myweb.xml";
	/**
	 * 配置信息初始化器，加载配置文件并保存
	 */
	private ControllerMappingManager ctrlMappingMgr;
	/**
	 * 存放Controller实例的HashMap，key为Controller的全类名，value为key所指的类的实例
	 */
	protected Map<String, Object> controllers = new HashMap<String, Object>();
	/**
	 * 为每个Controller实例存放其方法实例，key为方法映射的全路径，value为Method实例
	 */
	protected Map<String, Method> methods = new HashMap<String, Method>();

	public void init() throws ServletException {
		// 读取web.xml中提供的配置文件的位置信息
		String configFileName = this.getServletConfig().getInitParameter("configFileName");
		if (!(configFileName == null || (configFileName = configFileName.trim()).equals("")))
			this.configFileName = configFileName;

		ctrlMappingMgr = new ControllerMappingManager(this.configFileName);

		synchronized (controllers) {
			controllers.clear();
		}
		synchronized (methods) {
			methods.clear();
		}
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		synchronized (controllers) {
			controllers.clear();
		}
		synchronized (methods) {
			methods.clear();
		}
		this.ctrlMappingMgr.getControllerMappings().clear();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		execute(request, response);//通过取出请求的路径，反射调用控制器类的某个方法
	}

	/**
	 * 根据映射信息获取Controller的调用信息，包括类和方法两部分信息 映射信息形式为：/aaa/.../zzz
	 */
	protected void execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = null;
		// 先考虑按前缀映射，即Servlet采用 /xxx/* 的映射方式，取出Servlet访问路径中包含的映射信息
		// 取到的是 /xxx 后面的部分，带/开头
		// 先看一下是不是包含动作<jsp:include>发出的请求，如果是，则此项有值
		path = (String) request.getAttribute("javax.servlet.include.path_info");
		if (path == null) { // 不是包含动作，则正常取值
			path = request.getPathInfo();
			if (path == null || (path = path.trim()).isEmpty()) { // 依然为空说明不是按前缀映射
				// 再考虑按扩展名映射，即Servlet采用 *.xxx 的映射方式，取出Servlet访问路径中包含的映射信息
				// 先看一下是不是包含动作<jsp:include>发出的请求，如果是，则此项有值
				path = (String) request.getAttribute("javax.servlet.include.servlet_path");
				if (path == null) { // 不是包含动作，则正常取值
					path = request.getServletPath();
				}
				// 把扩展名截掉
				int slash = path.lastIndexOf("/");
				int period = path.lastIndexOf(".");
				if ((period >= 0) && (period > slash)) {
					path = path.substring(0, period);
				}
			}
		}
		System.out.println("=============================Path: " + path); // log

		// 如果找不到Controller的信息，抛出PathException异常
		if (!ctrlMappingMgr.containsKey(path))
			throw new PathException("No Controller & Method is mapped with this path : " + path);
		ControllerMapping controllerMapping = ctrlMappingMgr.getControllerMapping(path);
		try {
			Object instance = null;
			// 根据得到的全类名，创建该类的唯一实例
			synchronized (controllers) {
				// 从集合属性controllers中查找已创建的Controller实例
				instance = controllers.get(controllerMapping.getClassName());
				// 如未找到，则通过反射加载，并放入集合
				if (instance == null) {
					instance = Class.forName(controllerMapping.getClassName()).newInstance();
					controllers.put(controllerMapping.getClassName(), instance);
				}
			}

			// 调用指定方法
			Method method = null;
			synchronized (methods) {
				// 从集合属性methods中查找已创建的Method实例
				method = methods.get(path);
				// 如未找到，则通过反射加载，并放入集合
				if (method == null) {
					method = instance.getClass().getMethod(controllerMapping.getMethodName(), HttpServletRequest.class,
							HttpServletResponse.class);
					methods.put(path, method);
				}
			}
			//通过反射调用XxController中的xx方法
			Object result = method.invoke(instance, request, response);
			toView(result, request, response);
			
		} catch (InstantiationException e) {
			// 无法正确实例化
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			// 无法正确调用方法
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			// 找不到指定的类，一般是配置文件写错
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			// 找不到指定的方法，一般是配置文件写错
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}

	}
	//去页面
	protected void toView(Object result, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (result == null)
			return;
		if (result instanceof String) {
			boolean isRedirect = false;
			String url = (String) result;
			if (url.startsWith("redirect:")) {
				isRedirect = true;
				url = url.substring("redirect:".length());
			} else if (url.startsWith("forward:")) {
				url = url.substring("forward:".length());
			}
			if (!(url = url.trim()).startsWith("/"))
				throw new ViewPathException();
			if (isRedirect)
				response.sendRedirect(request.getContextPath() + url);//重定向去jsp页面
			else
				request.getRequestDispatcher(url).forward(request, response);//转发去jsp页面
		} else {
			PrintUtil.write(result, response); //输出json数据
		}
	}

}
