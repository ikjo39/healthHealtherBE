package com.health.healther.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.health.healther.constant.ConvenienceType;
import com.health.healther.constant.SpaceType;
import com.health.healther.domain.model.Convenience;
import com.health.healther.domain.model.Image;
import com.health.healther.domain.model.Space;
import com.health.healther.domain.model.SpaceKind;
import com.health.healther.domain.model.SpaceTime;
import com.health.healther.domain.repository.ConvenienceRepository;
import com.health.healther.domain.repository.ImageRepository;
import com.health.healther.domain.repository.SpaceKindRepository;
import com.health.healther.domain.repository.SpaceRepository;
import com.health.healther.domain.repository.SpaceTimeRepository;
import com.health.healther.dto.space.CreateSpaceRequestDto;
import com.health.healther.exception.space.NotMatchSpaceTypeException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpaceService {
	private final SpaceRepository spaceRepository;
	private final SpaceTimeRepository spaceTimeRepository;
	private final SpaceKindRepository spaceKindRepository;
	private final ConvenienceRepository convenienceRepository;
	private final ImageRepository imageRepository;

	@Transactional
	public void saveSpaceInfo(CreateSpaceRequestDto createSpaceRequestDto) {
		// 1. 공간 등록
		Space space = Space.builder()
				// .member()
				.title(createSpaceRequestDto.getTitle())
				.content(createSpaceRequestDto.getContent())
				.address(createSpaceRequestDto.getAddress())
				.addressDetail(createSpaceRequestDto.getAddressDetail())
				.notice(createSpaceRequestDto.getNotice())
				.rule(createSpaceRequestDto.getRule())
				.price(createSpaceRequestDto.getPrice())
				.build();

		spaceRepository.save(space);

		// 2. 예약 가능 시간 등록
		spaceTimeRepository.save(SpaceTime.of(space, createSpaceRequestDto));

		// 3. 공간 유형 등록
		spaceKindRepository.saveAll(
				createSpaceRequestDto.getSpaceTypes().stream()
						.map(spaceType -> SpaceKind.builder()
								.space(space)
								.spaceType(spaceType)
								.build())
						.collect(Collectors.toList())
		);

		// 4. 편의사항 등록
		convenienceRepository.saveAll(
				createSpaceRequestDto.getConvenienceTypes().stream()
						.map(convenienceType -> Convenience.builder()
								.space(space)
								.convenienceType(convenienceType)
								.build())
						.collect(Collectors.toList())
		);

		// 5. 이미지 등록

		imageRepository.saveAll(
				createSpaceRequestDto.getImages().stream()
						.map(url -> Image.builder()
								.space(space)
								.imageUrl(url)
								.build()
						).collect(Collectors.toList())
		);

	}

	public void createSpace(CreateSpaceRequestDto createSpaceRequestDto) {
		validationSpaceType(createSpaceRequestDto.getSpaceTypes());
		validationConvenienceType(createSpaceRequestDto.getConvenienceTypes());

		// TODO Find the member and set it up.
		saveSpaceInfo(createSpaceRequestDto);
	}

	private void validationConvenienceType(Set<ConvenienceType> convenienceTypes) {
		for (ConvenienceType convenienceType : convenienceTypes) {
			if (convenienceType == null)
				throw new NotMatchSpaceTypeException("일치하는 편의사항이 없습니다.");
		}
	}

	private void validationSpaceType(Set<SpaceType> spaceTypes) {
		for (SpaceType spaceType : spaceTypes) {
			if (spaceType == null)
				throw new NotMatchSpaceTypeException("일치하는 공간 유형이 없습니다.");
		}
	}
}
