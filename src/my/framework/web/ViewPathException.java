package my.framework.web;

/**
 * 表示导航路径格式错误的异常
 */
public class ViewPathException extends IllegalArgumentException {
	private static final long serialVersionUID = 4245789971176109513L;

	public ViewPathException() {
		super("The view path does not start with a \"/\" character");
	}
}
