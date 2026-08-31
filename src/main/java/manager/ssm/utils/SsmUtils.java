package manager.ssm.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import manager.ssm.ChatEvent;
import manager.ssm.ChatState;
import manager.ssm.SsmAttribute;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;
import reactor.core.publisher.Mono;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SsmUtils {

    public static void setSsmAttribute(StateContext<?, ?> context, SsmAttribute ssmAttribute, String value) {
        context.getExtendedState().getVariables().put(ssmAttribute, value);
    }

    public static String getSsmAttribute(StateContext<?, ?> context, SsmAttribute ssmAttribute) {
        return context.getExtendedState().get(ssmAttribute, String.class);
    }

    public static void sendEvent(StateContext<?, ?> context, ChatEvent event) {
        context.getStateMachine().sendEvent(Mono.just(new Message() {
            @Override
            public Object getPayload() {
                return event;
            }

            @Override
            public MessageHeaders getHeaders() {
                return null;
            }
        })).subscribe();
    }

}
