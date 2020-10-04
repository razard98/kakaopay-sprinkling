package kr.per.james.kakaopay.sprinkling.exception;

public class ExpiredInquiryException extends RuntimeException {
    public ExpiredInquiryException() {
        super("뿌린 건에 대한 조회는 7일 동안 할 수 있습니다.");
    }
}
