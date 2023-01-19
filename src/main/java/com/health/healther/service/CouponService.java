package com.health.healther.service;

import static com.health.healther.exception.coupon.CouponErrorCode.*;

import java.time.LocalDate;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.health.healther.domain.model.Coupon;
import com.health.healther.domain.model.Space;
import com.health.healther.domain.repository.CouponRepository;
import com.health.healther.domain.repository.SpaceRepository;
import com.health.healther.dto.coupon.CouponCreateRequestDto;
import com.health.healther.dto.coupon.CouponResponseDto;
import com.health.healther.dto.coupon.CouponUpdateRequestDto;

import com.health.healther.exception.coupon.CouponCustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponService {

	private final CouponRepository couponRepository;

	private final SpaceRepository spaceRepository;

	public void addCoupon(CouponCreateRequestDto createDto) {

		Space space = spaceRepository.findById(createDto.getSpaceId())
			.orElseThrow(() -> new CouponCustomException(NOT_FOUND_SPACE));

		Coupon coupon = Coupon.builder()
			.space(space)
			.discountAmount(createDto.getDiscountAmount())
			.openDate(createDto.getOpenDate())
			.expiredDate(createDto.getExpiredDate())
			.couponNumber(UUID.randomUUID().toString())
			.amount(createDto.getAmount())
			.isUsed(false)
			.build();

		couponRepository.save(coupon);
	}

	@Transactional
	public void deleteCoupon(Long couponId) {

		Coupon coupon = couponRepository.findById(couponId)
			.orElseThrow(() -> new CouponCustomException(NOT_FOUND_COUPON));

		couponRepository.delete(coupon);

	}

	@Transactional(readOnly = true)
	public CouponResponseDto getCoupon(Long spaceId) {

		LocalDate now = LocalDate.now().minusDays(1);

		Optional<Coupon> optionalCoupon = couponRepository.findBySpace_IdAndExpiredDateIsAfterAndIsUsed(spaceId, now,
			false);

		if (!optionalCoupon.isPresent()) {
			throw new CouponCustomException(NOT_FOUND_COUPON);
		}
		
		Coupon coupon = optionalCoupon.get();

		return CouponResponseDto.builder()
			.couponId(coupon.getId())
			.memberName(coupon.getMember().getName())
			.discountAmount(coupon.getDiscountAmount())
			.openDate(coupon.getOpenDate())
			.expiredDate(coupon.getExpiredDate())
			.couponNumber(coupon.getCouponNumber())
			.amount(coupon.getAmount())
			.isUsed(coupon.isUsed())
			.build();
	}

	@Transactional
	public void updateCoupon(Long couponId, CouponUpdateRequestDto couponUpdateRequestDto) {

		Coupon coupon = couponRepository.findById(couponId)
			.orElseThrow(() -> new CouponCustomException(NOT_FOUND_COUPON));

		coupon.updateCoupon(couponUpdateRequestDto.getDiscountAmount(),
			couponUpdateRequestDto.getOpenDate(),
			couponUpdateRequestDto.getExpiredDate());
	}

}
