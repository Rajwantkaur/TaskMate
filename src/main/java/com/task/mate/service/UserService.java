
package com.task.mate.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.task.mate.auth.security.UserNotFoundException;
import com.task.mate.entity.Role;
import com.task.mate.entity.User;
import com.task.mate.repository.RoleRepository;
import com.task.mate.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.bytebuddy.utility.RandomString;

@Service
public class UserService {

//	Service class to save and update User details

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private RoleRepository roleRepo;

	@Autowired
	private JavaMailSender mailSender;

//	on sign up we are providing user role and saving to data base

	public User saveUserWithDefaultRole(User user) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);
		Role roleUser = roleRepo.findByName("User");
		user.addRole(roleUser);
		user.setEnabled(false);
		String randomCode = RandomString.make(64);
		user.setVerificationCode(randomCode);
		return userRepo.save(user);

	}

//	to send email to admin for enabling user to login

	public void sendVerificationEmail(User user, String siteURL)
			throws UnsupportedEncodingException, MessagingException {
		String subject = "Please Verify registration";
		String senderName = "Contexo";
		String mailContent = "<p>Dear Admin</p>";
		mailContent += "<p> Please click the link below to verify the registration of user " + user.getFirstName()
				+ " with Email " + user.getEmail() + ":</p>";
		String verifyURL = siteURL + "/verify?code=" + user.getVerificationCode();
		mailContent += "<h3><a href=\"" + verifyURL + "\">VERIFY</a></h3>";
		mailContent += "<p> Thank you <br> Contexo </p>";

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom("vikram@signitySolutions.com", senderName);
		helper.setTo("vsrattol@gmail.com");
		helper.setSubject(subject);
		helper.setText(mailContent, true);
		mailSender.send(message);
	}

	public List<User> listAll() {
		return userRepo.findAll();
	}

	public User get(Long id) {
		return userRepo.findById(id).get();
	}

	public List<Role> listRoles() {
		return roleRepo.findAll();
	}

//	to update user details like roles
	@Transactional
	public void save(User user) {
		User existingUser = userRepo.findById(user.getId()).get();
		if (user.getPassword() == null || user.getPassword().isEmpty()) {
			user.setPassword(existingUser.getPassword());
		}
		/*
		 * BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); String
		 * encodedPassword = passwordEncoder.encode(user.getPassword());
		 * user.setPassword(encodedPassword); // user.setEnabled(true);
		 */ userRepo.save(user);
	}

// on verification by admin enable is set true.
	@Transactional
	public boolean verify(String verificationCode) {
		User user = userRepo.findByVerificationCode(verificationCode);
		if (user == null || user.isEnabled()) {
			return false;
		} else {
			userRepo.enable(user.getId());
			return true;
		}
	}

	@Transactional
	public void saveResetPasswordToken(String email, String token) throws UserNotFoundException {
		User user = userRepo.findByEmail(email);

		if (user != null) {
			user.setResetPasswordToken(token);
			userRepo.save(user);
		} else {
			throw new UserNotFoundException("Could not find any user with email " + email);
		}

	}

	public User getByResetToken(String resetPasswordToken) {
		return userRepo.findByResetPasswordToken(resetPasswordToken);
	}

// To change the password
	@Transactional
	public void updatePassword(User user, String newPassword) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPassword = encoder.encode(newPassword);
		user.setPassword(encodedPassword);
		user.setResetPasswordToken(null);
		userRepo.save(user);
	}

}