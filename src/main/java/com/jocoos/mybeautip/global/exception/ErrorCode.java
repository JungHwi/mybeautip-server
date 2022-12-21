package com.jocoos.mybeautip.global.exception;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements CodeValue {

    // COMMON
    INTERNAL_SERVER_ERROR("서버 오류"),
    BAD_REQUEST("잘못된 요청"),
    ACCESS_DENIED("권한 없음"),
    NOT_FOUND("정보 없음"),
    INVALID_DATE_FORMAT("날짜 포맷 오류"),
    ALREADY_USED("이미 사용중"),
    BANNED_WORD("금칙어 포함"),
    NOT_A_WRITER("작성자가 아님"),

    // LOGIN
    INVALID_TOKEN("잘못된 토큰"),

    // MEMBER
    MEMBER_NOT_FOUND("회원 정보 없음"),
    MEMBER_DUPLICATED("중복 회원"),
    ALREADY_REGISTRATION("이미 초대코드 등록함"),
    ALREADY_MEMBER("이미 가입된 회원"),
    DORMANT_MEMBER("휴면회원"),
    SUSPENDED_MEMBER("정지된 회원"),
    EXILED_MEMBER("추방된 회원"),
    NOT_YET_REJOIN("재가입 불가"),
    ROLE_NOT_FOUND("역할 정보 없음"),
    DUPLICATE_PHONE("전화번호 중복"),
    EMAIL_DUPLICATED("이메일 중복"),
    MY_TAG("초대코드가 내 코드"),
    INVALID_CHAR("잘못된 문자 포함"),
    INVALID_CHAR_ONLY_DIGIT("문자 미포함"),
    NO_PHONE("전화번호 정보 없음"),
    ADDRESS_TOO_MANY_ADDRESS("address.too_many_addresses"),
    NO_ADDRESS("주소 정보 없음"),
    RANDOM_USERNAME_NOT_FOUND("랜덤 닉네임 찾지 못함"),

    // MEMBER MEMO
    MEMBER_MEMO_NOT_FOUND("메모 정보 없음"),

    // EVENT
    NOT_STARTED_EVENT("아직 시작 안 한 이벤트"),
    CAN_NOT_JOIN_STATUS("참가 할 수 없는 이벤트"),
    ALREADY_ENDED_EVENT("이미 끝난 이벤트"),

    // FOLLOW
    CANNOT_FOLLOWED_MYSELF("나 자신을 follow 할수 없음"),
    ALREADY_FOLLOWED("이미 follow 중"),

    // COMMUNITY
    NOT_SUPPORTED_VOTE_NUM("요구되는 파일 수와 다름"),
    DUPLICATE_VOTE("투표는 한번만 가능함"),
    COMMUNITY_VOTE_NOT_MATCH("게시물에 등록된 파일이 아님"),
    TOO_MANY_FILE("파일 수가 너무 많음"),
    CATEGORY_NO_WRITABLE("작성이 가능한 카테고리가 아님"),
    FILE_NOT_EDITABLE("파일 수정이 불가함"),

    // VIDEO
    VIDEO_NOT_FOUND("비디오 정보 없음"),
    INVALID_VIDEO_TYPE("잘못된 비디오 타입"),
    ALREADY_LOCKED("이미 잠김"),
    ALREADY_UNLOCKED("이미 풀린 비디오"),

    // MOTD
    DUPLICATED_MOTDS("MOTD 정보 없음"),

    // PAYMENT
    INVALID_STATE("잘못된 상태"),
    PAYMENT_NOT_FOUND("결제 정보 없음"),
    REVENUE_PAYMENT_NOT_FOUND("수익 정보 없음"),

    // ORDER
    ORDER_NOT_FOUND("주문 정보 없음"),
    INVALID_ORDER_STATE("잘못된 주문 단계"),

    // DELIVERY
    ALREADY_DELIVERED("이미 배송"),

    // COUPON
    COUPON_NOT_FOUND("쿠폰 정보 없음"),

    // PURCHASE
    PURCHASE_NOT_FOUND("제품 정보 없음"),
    ALREADY_CONFIRMED("이미 확인됨"),
    REQUIRED_PURCHASE_STATUS("제품 상태 필요"),
    SOLD_OUT("품절"),


    // GOODS
    GOODS_NOT_FOUND("상품 정보 없음"),
    GOODSNO_NOT_FOUND("상품 정보 없음"),
    DUPLICATED_GOODS("중복된 상품"),

    // COMMENT
    COMMENT_NOT_FOUND("댓글 정보 없음"),
    INVALID_REQUEST_BODY("잘못된 요청"),

    // Fix
    NOT_SUPPORTED_FIX_STATUS("상단 고정이 가능한 상태가 아닙니다"),

    // THIRD PARTY
    S3_ERROR("S3 에러"),


    // Placard
    ONLY_ACTIVE_CAN_FIX("활성화된 플랜카드만 고정됨")
    ;

    private final String description;

    public String getKey() {
        return this.name().toLowerCase();
    }

    @Override
    public String getName() {
        return this.name();
    }
}
