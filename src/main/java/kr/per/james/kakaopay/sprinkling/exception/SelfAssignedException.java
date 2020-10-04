package kr.per.james.kakaopay.sprinkling.exception;

public class SelfAssignedException extends RuntimeException {
    public SelfAssignedException() {
        super("자신이 뿌리기한 건은 자신이 받을 수 없습니다.");
    }
}
