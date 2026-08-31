package manager.controller;

import lombok.RequiredArgsConstructor;
import manager.db.model.Chunk;
import manager.db.model.Document;
import manager.dto.chat.request.ChatRequest;
import manager.dto.chat.response.ChatResponse;
import manager.service.ChatService;
import manager.service.RagService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AiManagerController {

    private final ChatService chatService;
    private final RagService ragService;

    @PostMapping("/chat")
    //TODO: Убрать как натестируюсь
    @CrossOrigin(origins = "*")
    public Mono<ChatResponse> chat(@RequestBody Mono<ChatRequest> request) {
        return request.log().flatMap(chatService::answer);
    }

    @PostMapping("/upload-document")
    public Flux<Chunk> uploadDocument(@RequestBody Document doc) {
        return ragService.upload(doc).log();
    }
}
