package my.framework.web;

/**
 * 表示配置文件加载错误的异常
 */
public class ConfigLoadingException extends RuntimeException {
	private static final long serialVersionUID = 289349915756891942L;

	public ConfigLoadingException(Throwable cause) {
		super("Cannot load the config file.", cause);
	}
}
