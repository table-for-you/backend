package com.project.tableforyou.common.fcm.util;

public interface FcmProperties {

    String RESERVATION_TITLE = "가게 예약이 완료 되었습니다.";
    String QUEUE_RESERVATION_CONTENT = "가게에 대한 예약이 완료 되었습니다.";
    String TIME_RESERVATION_CONTENT = "가게에 대한 예약이 완료 되었습니다. 시간: ";

    String CANCEL_RESERVATION_TITLE = "가게 예약을 취소하였습니다.";
    String CANCEL_RESERVATION_CONTENT = "가게에 대한 예약을 취소하였습니다.";

    String RESTAURANT_REJECT_TITLE = "가게 승인이 거절되었습니다.";
    String RESTAURANT_REJECT_CONTENT = "가게 승인이 거절되었습니다. 알림함에서 확인해주세요.";

    String RESTAURANT_APPROVED_TITLE = "가게 승인이 되었습니다.";
    String RESTAURANT_APPROVED_CONTENT = "가게 승인이 되었습니다. 알림함에서 확인해주세요.";

    String RESTAURANT_ENTER_TITLE = "가게에 입장해주세요.";
    String RESTAURANT_ENTER_CONTENT = "가게 예약에 대한 차례가 되었습니다. 가게에 입장해주세요.";

    String RESTAURANT_WAIT_TITLE = "가게 입장을 대기해주세요.";
    String RESTAURANT_WAIT_CONTENT = "가게 예약 번호가 5번입니다. 입장에 대기해주세요.";
}
