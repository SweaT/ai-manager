package manager.ssm.action;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manager.ssm.ChatEvent;
import manager.ssm.ChatState;
import manager.ssm.SsmAttribute;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.action.ReactiveAction;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import static manager.ssm.SsmAttribute.ERROR_MSG;
import static manager.ssm.utils.SsmUtils.getSsmAttribute;
import static manager.utils.ReactiveUtils.fromBlocking;

@Component
@Slf4j
public class OnErrorAction implements ReactiveAction<ChatState, ChatEvent> {

    @Override
    public Mono<Void> apply(StateContext<ChatState, ChatEvent> chatStateChatEventStateContext) {
        return Mono.fromRunnable(() -> handle(chatStateChatEventStateContext));
    }

    private void handle(StateContext<ChatState, ChatEvent> context) {
        String errorMsg = getSsmAttribute(context, ERROR_MSG);
        log.error(errorMsg);
    }

}
