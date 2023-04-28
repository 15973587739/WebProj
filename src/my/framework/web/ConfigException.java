package my.framework.web;

/**
 * 表示配置文件内容错误的异常
 */
public class ConfigException extends RuntimeException {
	private static final long serialVersionUID = 6137431897374924960L;

	public ConfigException(String msg) {
		super(msg);
	}
}
