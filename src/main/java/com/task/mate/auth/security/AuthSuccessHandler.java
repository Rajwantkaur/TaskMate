
package com.task.mate.auth.security;

import java.io.IOException;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		HttpSession session = request.getSession();

		/* Set some session variables */
		Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
		String username = authUser.getName();
		session.setAttribute("authorities", authentication.getAuthorities());

		/* Set target URL to redirect */
		String targetUrl = determineTargetUrl(authentication);
		redirectStrategy.sendRedirect(request, response, targetUrl);
	}

	private String determineTargetUrl(Authentication authentication) {

		Set<String> authorities = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
		if (authorities.contains("MANAGER") || authorities.contains("CLIENT") || authorities.contains("USER")) {
			return "/fbg/MasterFeedOrderList";
		}

		else {
			throw new IllegalStateException();
		}
	}

	public RedirectStrategy getRedirectStrategy() {
		return redirectStrategy;
	}

	public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
		this.redirectStrategy = redirectStrategy;
	}
}
/*
 * @RequiredArgsConstructor
 * 
 * @Component public class AuthSuccessHandler implements
 * AuthenticationSuccessHandler {
 * 
 * private final RedirectStrategy redirectStrategy;
 * 
 * @Override public void onAuthenticationSuccess(HttpServletRequest
 * httpServletRequest, HttpServletResponse httpServletResponse, Authentication
 * authentication) throws IOException, ServletException {
 * 
 * Collection<? extends GrantedAuthority> authorities =
 * authentication.getAuthorities(); authorities.forEach(authority -> { if
 * (authority.getAuthority().equals("ROLE_CLIENT")) { try {
 * redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse,
 * "/fbg/MasterFeedOrderList"); } catch (IOException ignore) { } } else if
 * (authority.getAuthority().equals("ROLE_MANAGER")) { try {
 * redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse,
 * "/fbg/MasterFeedOrderList"); } catch (IOException ignore) { } } else { throw
 * new IllegalStateException(); } }); } }
 */
