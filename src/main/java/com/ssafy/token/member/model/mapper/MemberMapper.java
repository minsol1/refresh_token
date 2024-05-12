package com.ssafy.token.member.model.mapper;

import java.sql.SQLException;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.ssafy.token.member.model.MemberDto;

@Mapper
public interface MemberMapper {

	void joinMember(MemberDto memberDto) throws SQLException;
	MemberDto login(MemberDto memberDto) throws SQLException;
	MemberDto userInfo(String userId) throws SQLException;
	void saveRefreshToken(Map<String, String> map) throws SQLException;
	Object getRefreshToken(String userid) throws SQLException;
	void deleteRefreshToken(Map<String, String> map) throws SQLException;
}
