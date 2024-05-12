package com.ssafy.token.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {
	@Value("${jwt.salt}")
	private String salt;

	@Value("${jwt.access-token.expiretime}")
	private long accessTokenExpireTime;

	@Value("${jwt.refresh-token.expiretime}")
	private long refreshTokenExpireTime;

	public String createAccessToken(String userId) throws InvalidKeyException, UnsupportedEncodingException {
		return create(userId, "access-token", accessTokenExpireTime);
	}

//	AccessToken에 비해 유효기간을 길게 설정.
	public String createRefreshToken(String userId) throws InvalidKeyException, UnsupportedEncodingException {
		return create(userId, "refresh-token", refreshTokenExpireTime);
	}

//	Token 발급
//	key : Claim에 셋팅될 key 값
//	value : Claim에 셋팅 될 data 값
//	subject : payload에 sub의 value로 들어갈 subject값
//	expire : 토큰 유효기간 설정을 위한 값
//	jwt 토큰의 구성 : header + payload + signature
	private String create(String userId, String subject, long expireTime) throws InvalidKeyException, UnsupportedEncodingException {
//	Payload 설정 : 생성일 (IssuedAt), 유효기간 (Expiration), 
//	토큰 제목 (Subject), 데이터 (Claim) 등 정보 세팅.
	Claims claims = Jwts.claims()
			.setSubject(subject) // 토큰 제목 설정 ex) access-token, refresh-token
			.setIssuedAt(new Date()) // 생성일 설정
//			만료일 설정 (유효기간)
			.setExpiration(new Date(System.currentTimeMillis() + expireTime));

//	저장할 data의 key, value
	claims.put("userId", userId);

	String jwt = Jwts.builder()
//		Header 설정 : 토큰의 타입, 해쉬 알고리즘 정보 세팅.
		.setHeaderParam("typ", "JWT").setClaims(claims)
//		Signature 설정 : secret key를 활용한 암호화.
		.signWith(this.generateKey())
		.compact(); // 직렬화 처리.

	return jwt;
}

	private SecretKey generateKey() throws UnsupportedEncodingException {
		byte[] key = Decoders.BASE64.decode(salt);
		
		return Keys.hmacShaKeyFor(key);
	}

//	전달 받은 토큰이 제대로 생성된 것인지 확인 하고 문제가 있다면 UnauthorizedException 발생.
	public boolean checkToken(String token) {
		try {
//			Json Web Signature? 서버에서 인증을 근거로 인증 정보를 서버의 private key 서명 한것을 토큰화 한것
//			setSigningKey : JWS 서명 검증을 위한  secret key 세팅
//			parseClaimsJws : 파싱하여 원본 jws 만들기
			@SuppressWarnings("deprecation")
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(this.generateKey()).build().parseClaimsJws(token);

//			Claims 는 Map 구현체 형태
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public String getUserId(String authorization) {
		Jws<Claims> claims = null;
		try {
			claims = Jwts.parserBuilder().setSigningKey(this.generateKey()).build().parseClaimsJws(authorization);
		} catch (Exception e) {
		}
		Map<String, Object> value = claims.getBody();
		return (String) value.get("userId");
	}

	
}
