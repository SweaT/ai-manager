package manager.statemachine;

public enum ChatEvent {
    MESSAGE_RECEIVED,
    CONTEXT_FOUND,
    CONTEXT_MISSING,
    ANSWER_CREATED,
    FAILED
}
