package manager.ssm;

import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.StateDoActionPolicy;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
public class ChatStateMachineConfig extends EnumStateMachineConfigurerAdapter<ChatState, ChatEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<ChatState, ChatEvent> states) throws Exception {
        states.withStates()
                .initial(ChatState.NEW_MESSAGE_00)
                .states(EnumSet.allOf(ChatState.class))
                .state(ChatState.RETRIEVE_AUGMENT_01)
                .end(ChatState.NEEDS_CLARIFICATION_02)
                .end(ChatState.DONE_99)
                .end(ChatState.ERROR_99);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<ChatState, ChatEvent> config) throws Exception {
        config.withConfiguration()
                .autoStartup(true);
        super.configure(config);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ChatState, ChatEvent> transitions) throws Exception {
        transitions
                // NEW_MESSAGE_00
                .withExternal()
                .source(ChatState.NEW_MESSAGE_00).target(ChatState.RETRIEVE_AUGMENT_01)
                    .event(ChatEvent.SUCCESS)
                    .and()
                .withExternal()
                .source(ChatState.NEW_MESSAGE_00).target(ChatState.ERROR_99)
                .event(ChatEvent.ERROR)
                .and()

                // RETRIEVE_AUGMENT_01
                .withExternal()
                .source(ChatState.RETRIEVE_AUGMENT_01).target(ChatState.LLM_GENERATION_02)
                    .event(ChatEvent.SUCCESS)
                    .and()
                .withExternal()
                .source(ChatState.RETRIEVE_AUGMENT_01).target(ChatState.ERROR_99)
                .event(ChatEvent.ERROR)
                .and()

                // LLM_GENERATION_02
                .withExternal()
                .source(ChatState.LLM_GENERATION_02).target(ChatState.NEEDS_CLARIFICATION_02)
                    .event(ChatEvent.FAILED)
                    .and()
                .withExternal()
                .source(ChatState.LLM_GENERATION_02).target(ChatState.ERROR_99)
                .event(ChatEvent.ERROR)
                .and()
                .withExternal()
                .source(ChatState.LLM_GENERATION_02).target(ChatState.DONE_99)
                    .event(ChatEvent.SUCCESS);
    }
}
