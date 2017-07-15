package com.smart.web;

import com.smart.domain.User;
import com.smart.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;


/*
	登录请求Controller
 */
@RestController
public class LoginController{
	private UserService userService;

	/*
		这里把截获的/和/index.html请求,返回了login视图,框架解析根据application.properties设置视的view的路径和后缀找到对应的jsp文件login.jsp
		login.jsp把请求转发给了loginCheck.html,会被下面的requestMapping截获
	 */
	@RequestMapping(value = {"/","/index.html"})
	public ModelAndView loginPage(){
		return new ModelAndView("login");
	}

	/*
		这里截获了/loginCheck.html请求,通过调用UserService的方法来返回不同的视图
	 */
	@RequestMapping(value = "/loginCheck.html")
	public ModelAndView loginCheck(HttpServletRequest request,LoginCommand loginCommand){
		boolean isValidUser =  userService.hasMatchUser(loginCommand.getUserName(),
					                    loginCommand.getPassword());
		if (!isValidUser) {
			return new ModelAndView("login", "error", "用户名或密码错误。");
		} else {
			User user = userService.findUserByUserName(loginCommand
					.getUserName());
			user.setLastIp(request.getLocalAddr());
			user.setLastVisit(new Date());
			userService.loginSuccess(user);
			request.getSession().setAttribute("user", user);
			return new ModelAndView("main");
		}
	}

	@Autowired
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
}
