package my.framework.web;

/**
 * 用于保存在配置文件中配置的Controller信息
 */
public class ControllerMapping {

	private String className;
	private String methodName;
	
	public ControllerMapping() {}

	public ControllerMapping(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
}