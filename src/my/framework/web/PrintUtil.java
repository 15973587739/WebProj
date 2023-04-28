package my.framework.web;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

public class PrintUtil {

	public static void write(Object obj, HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		String json = JSONObject.toJSONString(obj);
		out.print(json);
		out.flush();
		out.close();
	}
}
