package manager.statemachine;

public enum ChatState {
    NEW_MESSAGE,
    RETRIEVING_CONTEXT,
    ANSWERING,
    NEEDS_CLARIFICATION,
    DONE,
    ERROR
}
