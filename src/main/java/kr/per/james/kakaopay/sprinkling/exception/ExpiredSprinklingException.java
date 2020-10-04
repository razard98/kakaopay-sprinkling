package kr.per.james.kakaopay.sprinkling.exception;

public class ExpiredSprinklingException extends RuntimeException {
    public ExpiredSprinklingException() {
        super("뿌린 건은 10분간만 유효합니다.");
    }
}
