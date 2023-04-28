package cn.houserent.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserAction {
	public String login(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		System.out.println("access to UserAction.login");
		String name = request.getParameter("name");
		String password = request.getParameter("password");
		
		if ("admin".equals(name) && "123456".equals(password)) {
			request.getSession().setAttribute("login", name);
			return "/success.jsp";
		} else {
			return "redirect: /input.jsp?message=fail";
		}

	}
	
	public String register(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		System.out.println("access to UserAction.register");
		if("admin".equals(request.getParameter("name"))){
			request.setAttribute("message", "用户名存在");
			return "forward: /page/register.jsp";
		} else {
			//用户名不存在
			String password = request.getParameter("password");
			if(password != null && !password.isEmpty()){
				request.getSession().setAttribute("message", "注册成功");
				return "/page/success.jsp";
			}else{
				request.setAttribute("message", "注册失败");
				return "/page/register.jsp";
			}
		}		
	}
	
	public User findById(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = new User();
		return user;
	}
}

class User {
	private Long id = 1L;
	private String username = "test";
	private String password = "123456";
	private String address = "somewhere";
	private String tel = "13412341234";
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	
}