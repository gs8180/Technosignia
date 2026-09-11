package com.technosignia.contractsystem.controller;

import com.technosignia.contractsystem.entity.Document;
import com.technosignia.contractsystem.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contracts/{contractId}/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping
    public ResponseEntity<List<Document>> getDocuments(@PathVariable Long contractId) {
        return ResponseEntity.ok(documentService.getDocumentsForContract(contractId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Document> uploadDocument(
            @PathVariable Long contractId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(documentService.uploadDocument(contractId, file));
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long contractId,
            @PathVariable Long documentId) {
        Document document = documentService.getDocumentById(documentId);
        Resource resource = documentService.loadFileAsResource(documentId);

        String contentType = document.getFileType() != null ? document.getFileType() : "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getOriginalFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Map<String, String>> deleteDocument(
            @PathVariable Long contractId,
            @PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok(Map.of("message", "Document deleted successfully"));
    }
}
