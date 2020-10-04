package kr.per.james.kakaopay.sprinkling.exception;

public class InquiryForbiddenException extends RuntimeException {
    public InquiryForbiddenException() {
        super("뿌린 사람 자신만 조회를 할 수 있습니다.");
    }
}
