package kr.per.james.kakaopay.sprinkling.exception;

public class NotBelongRoomException extends RuntimeException {
    public NotBelongRoomException() {
        super("뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만 받을 수 있습니다.");
    }
}
