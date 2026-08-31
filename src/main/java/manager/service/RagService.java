package manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import manager.config.RagProperties;
import manager.db.model.Chunk;
import manager.db.model.Document;
import manager.db.repository.KnowledgeChunkRepository;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static manager.utils.ReactiveUtils.fromBlocking;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final OllamaService ollamaService;
    private final KnowledgeChunkRepository knowledgeChunkRepository;
    private final RagProperties ragProperties;

    public Flux<Chunk> retrieve(String question) {
        return ollamaService.embed(question)
                .flatMapMany(embedding -> knowledgeChunkRepository.findSimilar(embedding, ragProperties.topK()));
    }

    public Flux<Chunk> upload(Document document) {
        return readFileContent(document.sourcePath())// Шаг 1
                .flatMap(content -> saveDocument(document.title(), content)// Шаг 2
                        .map(doc -> splitToChunks(content, doc.id())) // Шаг 3-4
                ).flatMapIterable(list -> list)  // Раскрываем Mono<List<Chunk>> в Flux<Chunk>
                .flatMap(this::saveChunk);
    }

    public String buildPrompt(String question, List<Chunk> chunks) {
        String context = chunks.stream()
                .map(chunk -> "\nContent: " + chunk.getContent())
                .collect(Collectors.joining("\n\n"));

        return """
                You are a chatbot for a beauty studio website.
                Answer only from the provided studio context.
                If the context is not enough, say that you do not have enough information and ask a clarifying question.
                You MUST answer ONLY on Russian language.

                Studio context:
                %s

                User question:
                %s
                """.formatted(context, question);
    }

    // Шаг 1: Читаем файл NIO и получаем String
    private Mono<String> readFileContent(String filePath) {
        return fromBlocking(Schedulers.boundedElastic(),
                () -> Files.readString(Paths.get(filePath))); // Выносим блокирующий I/O
    }

    // Шаг 2: Сохраняем документ в БД и получаем его ID
    private Mono<Document> saveDocument(String title, String content) {
        // R2DBC репозиторий возвращает Mono<Document> с заполненным ID
        return knowledgeChunkRepository.uploadDocument(title, content)
                .doOnNext(doc -> log.info("saved document {}", doc));
    }

    // Шаги 3-4: Разбиваем String на List<String> и маппим в Chunk
    private List<Chunk> splitToChunks(String content, Long documentId) {
        //TODO: Переделать спилттер на сплиттер с overlap (перекрытием)
        return TokenTextSplitter.builder()
                .withChunkSize(400)
                .withMinChunkSizeChars(100)
                .withMinChunkLengthToEmbed(10)
                .build()
                .apply(List.of(org.springframework.ai.document.Document.builder()
                        .text(content)
                        .build()
                )).stream()
                .map(doc -> Chunk.builder().content(content).documentId(documentId).build())
                .toList();
    }

    // Шаг 5: Сохраняем чанк в БД
    private Mono<Chunk> saveChunk(Chunk chunk) {
        log.info("Trying to save chunk {}", chunk);
        return ollamaService.embed(chunk.getContent())
                .flatMap(embending -> knowledgeChunkRepository.uploadChunk(chunk, embending))
                .doOnNext(doc -> log.info("saved chunk {}", doc));
    }

}
