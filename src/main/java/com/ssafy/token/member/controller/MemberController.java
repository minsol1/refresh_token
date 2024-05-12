package com.ssafy.token.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.token.member.model.MemberDto;
import com.ssafy.token.member.model.service.MemberService;
import com.ssafy.token.util.JWTUtil;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class MemberController {

	private final MemberService memberService;
	private final JWTUtil jwtUtil;

	public MemberController(MemberService memberService, JWTUtil jwtUtil) {
		super();
		this.memberService = memberService;
		this.jwtUtil = jwtUtil;
	}

	@PostMapping("/login")
	public ResponseEntity<Map<String,Object>> login( @RequestBody MemberDto memberDto) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		HttpStatus status = HttpStatus.ACCEPTED;
		MemberDto loginUser = memberService.login(memberDto);
		if(loginUser != null) {
			String accessToken = jwtUtil.createAccessToken(loginUser.getId());
			String refreshToken = jwtUtil.createRefreshToken(loginUser.getId());
			
//			발급받은 refresh token 을 DB에 저장.
			memberService.saveRefreshToken(loginUser.getId(), refreshToken);
			
//			JSON 으로 token 전달.
			resultMap.put("access-token", accessToken);
			resultMap.put("refresh-token", refreshToken);
			
			status = HttpStatus.CREATED;
		} else {
			resultMap.put("message", "아이디 또는 패스워드를 확인해 주세요.");
			status = HttpStatus.UNAUTHORIZED;
		} 
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
	
	@GetMapping("/info/{userId}")
	public ResponseEntity<Map<String, Object>> getInfo( @PathVariable("userId") String userId, HttpServletRequest request) {
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		if (jwtUtil.checkToken(request.getHeader("Authorization"))) {
			try {
//				로그인 사용자 정보.
				MemberDto memberDto = memberService.userInfo(userId);
				resultMap.put("userInfo", memberDto);
				status = HttpStatus.OK;
			} catch (Exception e) {
				resultMap.put("message", e.getMessage());
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		} else {
			status = HttpStatus.UNAUTHORIZED;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}

	@GetMapping("/logout/{userId}")
	@Hidden
	public ResponseEntity<?> removeToken(@PathVariable ("userId") String userId) {
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		try {
			memberService.deleRefreshToken(userId);
			status = HttpStatus.OK;
		} catch (Exception e) {
			resultMap.put("message", e.getMessage());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}

	//토큰발급 
	@PostMapping("/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody MemberDto memberDto, HttpServletRequest request)
			throws Exception {
		Map<String, Object> resultMap = new HashMap<>();
		HttpStatus status = HttpStatus.ACCEPTED;
		String token = request.getHeader("refreshToken");
		if (jwtUtil.checkToken(token)) {
			if (token.equals(memberService.getRefreshToken(memberDto.getId()))) {
				String accessToken = jwtUtil.createAccessToken(memberDto.getId());
				resultMap.put("access-token", accessToken);
				status = HttpStatus.CREATED;
			}
		} else {
			status = HttpStatus.UNAUTHORIZED;
		}
		return new ResponseEntity<Map<String, Object>>(resultMap, status);
	}
}
