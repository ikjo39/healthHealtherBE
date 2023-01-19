package com.health.healther.dto.member;

import com.health.healther.domain.model.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberSearchResponse {
	private String name;

	private String nickName;

	private String phone;

	public static MemberSearchResponse from(Member member) {
		return MemberSearchResponse.builder()
			.name(member.getName())
			.nickName(member.getNickName())
			.phone(member.getPhone())
			.build();
	}
}
