package com.ssafy.token.member.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ssafy.token.member.model.MemberDto;
import com.ssafy.token.member.model.mapper.MemberMapper;

@Service
public class MemberServiceImpl implements MemberService{

	private final MemberMapper memberMapper;

	public MemberServiceImpl(MemberMapper memberMapper) {
		super();
		this.memberMapper = memberMapper;
	}

	@Override
	public void joinMember(MemberDto memberDto) throws Exception {
		// TODO Auto-generated method stub
		memberMapper.joinMember(memberDto);
	}

	@Override
	public MemberDto login(MemberDto memberDto) throws Exception {
		// TODO Auto-generated method stub
		return memberMapper.login(memberDto);
	}

	@Override
	public MemberDto userInfo(String userId) throws Exception {
		// TODO Auto-generated method stub
		return memberMapper.userInfo(userId);
	}

	@Override
	public void saveRefreshToken(String userId, String refreshToken) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", userId);
		map.put("token", refreshToken);
		memberMapper.saveRefreshToken(map);
	}

	@Override
	public Object getRefreshToken(String userId) throws Exception {
		return memberMapper.getRefreshToken(userId);
	}

	@Override
	public void deleRefreshToken(String userId) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		map.put("userId", userId);
		map.put("token", null);
		memberMapper.deleteRefreshToken(map);
	}
	

}
