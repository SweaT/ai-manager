package manager.ssm;

public enum ChatState {
    NEW_MESSAGE_00,
    RETRIEVE_AUGMENT_01,
    LLM_GENERATION_02,
    NEEDS_CLARIFICATION_02,
    DONE_99,
    ERROR_99
}

