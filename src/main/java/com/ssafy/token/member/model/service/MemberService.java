package com.ssafy.token.member.model.service;
import com.ssafy.token.member.model.MemberDto;

public interface MemberService {
	
	// 회원가입 
	void joinMember(MemberDto memberDto) throws Exception;
	//로그인 
	MemberDto login(MemberDto memberDto) throws Exception;
	//회원정보 
	MemberDto userInfo(String userId) throws Exception;
	//refresh token
	void saveRefreshToken(String userId, String refreshToken) throws Exception;
	Object getRefreshToken(String userId) throws Exception;
	void deleRefreshToken(String userId) throws Exception;
}
