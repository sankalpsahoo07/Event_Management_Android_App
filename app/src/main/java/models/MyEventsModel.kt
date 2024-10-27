package models

class MyEventsModel(
    private var _event_id: Int,
    private var _event_name: String,
    private var _event_type: String,
    private var _event_start: String,
    private var _event_finish: String,
    private var _event_info: String
) {
    fun setEventId(eventId: Int) {
        _event_id = eventId
    }

    fun setEventName(eventName: String) {
        _event_name = eventName
    }

    fun setEventType(eventType: String) {
        _event_type = eventType
    }

    fun setEventStartTime(eventStart: String) {
        _event_start = eventStart
    }

    fun setEventFinishTime(eventFinish: String) {
        _event_finish = eventFinish
    }

    fun setEventDescription(eventInfo: String) {
        _event_info = eventInfo
    }

    // Getters
    fun getEventId(): Int = _event_id
    fun getEventName(): String = _event_name
    fun getEventType(): String = _event_type
    fun getEventStartTime(): String = _event_start
    fun getEventFinishTime(): String = _event_finish
    fun getEventDescription(): String = _event_info
}
