package my.framework.web;

/**
 * 与Controller映射的路径错误
 */
public class PathException extends IllegalArgumentException {
	private static final long serialVersionUID = -5133000724710930489L;

	public PathException(String msg) {
		super(msg);
	}
}
