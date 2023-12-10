package com.example.parking.Enum;

public enum NotifictionMsgType {

        General(0),
        Notice(1),
        Message(2),
        EndBookingTimeApproaching(3) ,
        FinishedBooking(4),
        EndTemporaryVirtualBooking(5);

        private Integer value;

        NotifictionMsgType(Integer value) {
                this.value = value;
        }

        public final Integer getValue() {
                return value;
        }
}
