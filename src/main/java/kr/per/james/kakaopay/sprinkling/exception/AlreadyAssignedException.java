package kr.per.james.kakaopay.sprinkling.exception;

public class AlreadyAssignedException extends RuntimeException {
    public AlreadyAssignedException() {
        super("뿌리기 당 한번만 받을 수 있습니다.");
    }
}
