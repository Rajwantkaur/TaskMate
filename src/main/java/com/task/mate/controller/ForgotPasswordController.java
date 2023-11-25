package com.task.mate.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.task.mate.auth.security.UserNotFoundException;
import com.task.mate.entity.User;
import com.task.mate.repository.UserRepository;
import com.task.mate.service.UserService;
import com.task.mate.utilities.Utility;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import net.bytebuddy.utility.RandomString;

@Controller
public class ForgotPasswordController {

	@Autowired
	private UserRepository userRepo;

	private Utility utility;

	@Autowired
	private UserService userService;

	@Autowired
	private JavaMailSender mailSender;

	@GetMapping("/forgot_password")
	public String viewForgotPasswordForm(Model model) {
		model.addAttribute("pageTitle", "Forgot Password");
		return "forgot_password_form";

	}

	@PostMapping("/forgot_password")
	public String processForgetPasswordRequest(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		User user = userRepo.findByEmail(email);

		String token = RandomString.make(45);
		try {
			userService.saveResetPasswordToken(email, token);

//			resetPasswordlink to be sent to user through email
			String resetPasswordLink = utility.getSiteURL(request) + "/reset_password?token=" + token;

			sendResetPasswordEmail(email, resetPasswordLink);
			model.addAttribute("message", "We have mailed you a reset password link.Please check.");
		} catch (UserNotFoundException e) {
			model.addAttribute("error", e.getMessage());
		} catch (UnsupportedEncodingException | MessagingException e) {

			model.addAttribute("error", "Error while Sending email");
		}
		return "message";

	}

	private void sendResetPasswordEmail(String email, String resetPasswordLink)
			throws UnsupportedEncodingException, MessagingException {
		String subject = " Link to Reset Password";
		String senderName = "Contexo";
		String mailContent = "<p>Hello</p>" + "<p> You have requested to reset your password on Contexo.</p>"
				+ "<p> Please click the link below to reset your password:</p>" + "<p><b><a href=\"" + resetPasswordLink
				+ "\">RESET PASSWORD</a><b></p>" + "<p>Ignore this if you have not requested to change the mail</p>";
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom("vikram@signitysolutions.com", senderName);
		helper.setTo(email);
		helper.setSubject(subject);
		helper.setText(mailContent, true);
		mailSender.send(message);

	}

	@GetMapping("/reset_password")
	public String showResetPasswordForm(@Param(value = "token") String token, Model model) {

		User user = userService.getByResetToken(token);
		if (user == null) {
			model.addAttribute("title", "Reset Your Password");
			model.addAttribute("message", "Invalid Token");
			return "message";
		}
		model.addAttribute("pageTitle", "Reset Your Password");
		model.addAttribute("token", token);
		return "reset_Password_Form";

	}

	@PostMapping("/reset_password")
	public String processResetPasswordRequest(HttpServletRequest request, Model model) {
		String token = request.getParameter("token");
		String password = request.getParameter("password");

		User user = userService.getByResetToken(token);
		if (user == null) {
			model.addAttribute("title", "Reset Your Password");
			model.addAttribute("message", "Invalid Token");
			return "message";
		} else {
			userService.updatePassword(user, password);
			model.addAttribute("title", "Password Reset");
			model.addAttribute("message", "You have successfully changed your password");
			return "message";
		}
	}

}
