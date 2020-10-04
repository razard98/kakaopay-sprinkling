package kr.per.james.kakaopay.sprinkling.exception;

public class SprinklingCompletedException extends RuntimeException {

    public SprinklingCompletedException() {
        super("뿌리기가 모두 완료 되어, 받기에 실패하였습니다.");
    }
}
