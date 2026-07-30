package manager.statemachine;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class ChatStateMachineConfig extends StateMachineConfigurerAdapter<ChatState, ChatEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<ChatState, ChatEvent> states) throws Exception {
        states.withStates()
                .initial(ChatState.NEW_MESSAGE)
                .states(EnumSet.allOf(ChatState.class))
                .end(ChatState.DONE)
                .end(ChatState.ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ChatState, ChatEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(ChatState.NEW_MESSAGE)
                    .target(ChatState.RETRIEVING_CONTEXT)
                    .event(ChatEvent.MESSAGE_RECEIVED)
                    .and()
                .withExternal()
                .source(ChatState.RETRIEVING_CONTEXT)
                    .target(ChatState.ANSWERING)
                    .event(ChatEvent.CONTEXT_FOUND)
                    .and()
                .withExternal()
                .source(ChatState.RETRIEVING_CONTEXT)
                    .target(ChatState.NEEDS_CLARIFICATION)
                    .event(ChatEvent.CONTEXT_MISSING)
                    .and()
                .withExternal()
                .source(ChatState.ANSWERING)
                    .target(ChatState.DONE)
                    .event(ChatEvent.ANSWER_CREATED)
                    .and()
                .withExternal()
                .source(ChatState.NEW_MESSAGE)
                    .target(ChatState.ERROR)
                    .event(ChatEvent.FAILED);
    }
}
