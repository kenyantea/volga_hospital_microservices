package com.example.demoauth.configs.jwt;

import com.example.demoauth.models.ERole;
import com.example.demoauth.models.User;
import com.example.demoauth.repository.UserRepository;
import com.example.demoauth.service.UserDetailsImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationMs}")
	private int jwtExpirationMs;

	@Value("${jwt.refreshTokenSecret}")
	private String refreshTokenSecret;

	@Value("${jwt.refreshTokenExpirationMs}")
	private long refreshTokenExpirationMs;

	@Autowired
	UserRepository userRepository;

	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public String generateJwtToken(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}


	public String generateRefreshJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

		return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + refreshTokenExpirationMs))
				.signWith(SignatureAlgorithm.HS512, refreshTokenSecret).compact();
	}

	public String generateRefreshJwtToken(String username) {
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + refreshTokenExpirationMs))
				.signWith(SignatureAlgorithm.HS512, refreshTokenSecret)
				.compact();
	}



	public boolean validateJwtToken(String jwt) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt);
			return true;
		} catch (MalformedJwtException | IllegalArgumentException | SignatureException e) {
			System.err.println(e.getMessage());
		}
        return false;
	}

	public boolean validateRefreshToken(String jwt) {
		try {
			Jwts.parser().setSigningKey(refreshTokenSecret).parseClaimsJws(jwt);
			return true;
		} catch (MalformedJwtException | IllegalArgumentException | SignatureException e) {
			System.err.println(e.getMessage());
		}
		return false;
	}

	public String getUserNameFromJwtToken(String jwt) {
		return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().getSubject();
	}

	public ERole getRoleFromJwtToken(String jwt) {
		String username = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().getSubject();
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null) {
			return null;
		}
		return user.getRoles().stream()
				.findFirst()
				.orElseThrow(() -> new RuntimeException("User must have a role"))
				.getName();
	}




	public String getUserNameFromRefreshToken(String jwt) {
		return Jwts.parser().setSigningKey(refreshTokenSecret).parseClaimsJws(jwt).getBody().getSubject();
	}

}
