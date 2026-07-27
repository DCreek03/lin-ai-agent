package com.aloha.linaiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import javax.print.Doc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * LoveAppDocumentLoader class.
 *
 * @author linfeng
 * @date 2026/7/26
 */
@Component
@Slf4j
public class LoveAppDocumentLoader {
    private final ResourcePatternResolver resourcePatternResolver;

    LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    public List<Document> loadMarkdowns() {
        List<Document> documents = new ArrayList<>();
        try {
            Resource[] resources = this.resourcePatternResolver.getResources("classpath:document/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(true)
                        .withIncludeBlockquote(true)
                        .withAdditionalMetadata("filename", filename)
                        .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                documents.addAll(reader.read());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return documents;
    }
}
