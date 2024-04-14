package ru.practicum.model.enums;

public enum StateActionEvent {
    PUBLISH_EVENT,
    REJECT_EVENT;

    public static StateActionEvent findByValue(String value) {
        StateActionEvent result = null;
        for (StateActionEvent status : values()) {
            if (status.name().equalsIgnoreCase(value)) {
                result = status;
                break;
            }
        }
        return result;
    }
}
