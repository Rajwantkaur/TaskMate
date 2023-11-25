package com.task.mate.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.task.mate.entity.User;
import com.task.mate.service.UserService;
import com.task.mate.utilities.Utility;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class UserController {

	@Autowired
	private UserService userService;

	private Utility utility;

	@GetMapping("/login")
	public String viewLoginPage() {
		return "login";
	}

	@GetMapping("/hello")
	@ResponseBody
	public String hello() {
		return "Helloo";
	}

	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());

		return "signup_form";
	}

	@GetMapping("/errorpage")
	public String viewStartPage() {
		return "errorpage";
	}

	@PostMapping("/user_register")
	public String processRegister(User user, HttpServletRequest request) {
		userService.saveUserWithDefaultRole(user);
		String siteURL = utility.getSiteURL(request);
		try {
			userService.sendVerificationEmail(user, siteURL);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return "register_success";
	}

	@GetMapping("/verify")
	public String verifyAccount(@Param("code") String code, Model model) {
		boolean verified = userService.verify(code);
		String pageTitle = verified ? "Verification Succedeed!" : "Verification Failed";
		model.addAttribute("pageTitle", pageTitle);
		return (verified ? "verify_success" : "verify_fail");
	}

}
