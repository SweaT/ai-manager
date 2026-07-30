package manager.rag;

import manager.config.RagProperties;
import manager.ollama.OllamaClient;
import manager.rag.model.KnowledgeChunk;
import manager.rag.repository.KnowledgeChunkRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final OllamaClient ollamaClient;
    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final RagProperties ragProperties;

    public RagService(
            OllamaClient ollamaClient,
            KnowledgeChunkRepository knowledgeChunkRepository,
            RagProperties ragProperties
    ) {
        this.ollamaClient = ollamaClient;
        this.knowledgeChunkRepository = knowledgeChunkRepository;
        this.ragProperties = ragProperties;
    }

    public Flux<KnowledgeChunk> retrieve(String question) {
        return ollamaClient.embed(question)
                .flatMapMany(embedding -> knowledgeChunkRepository.findSimilar(embedding, ragProperties.topK()));
    }

    public String buildPrompt(String question, List<KnowledgeChunk> chunks) {
        String context = chunks.stream()
                .map(chunk -> "Title: " + chunk.title() + "\nContent: " + chunk.content())
                .collect(Collectors.joining("\n\n"));

        return """
                You are a chatbot for a beauty studio website.
                Answer only from the provided studio context.
                If the context is not enough, say that you do not have enough information and ask a clarifying question.

                Studio context:
                %s

                User question:
                %s
                """.formatted(context, question);
    }
}
