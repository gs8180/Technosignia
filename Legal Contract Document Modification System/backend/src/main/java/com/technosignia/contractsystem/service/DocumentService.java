package com.technosignia.contractsystem.service;

import com.technosignia.contractsystem.entity.Contract;
import com.technosignia.contractsystem.entity.Document;
import com.technosignia.contractsystem.entity.User;
import com.technosignia.contractsystem.repository.ContractRepository;
import com.technosignia.contractsystem.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuditService auditService;

    public List<Document> getDocumentsForContract(Long contractId) {
        return documentRepository.findByContractIdOrderByUploadedAtDesc(contractId);
    }

    public Document getDocumentById(Long documentId) {
        return documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + documentId));
    }

    @Transactional
    public Document uploadDocument(Long contractId, MultipartFile file) throws IOException {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found with id: " + contractId));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() != null ? file.getOriginalFilename() : "unnamed_document");
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }

        String storedFileName = UUID.randomUUID().toString() + extension;
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path targetLocation = uploadPath.resolve(storedFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        User currentUser = authService.getCurrentUser();

        Document doc = new Document(
                storedFileName,
                originalFileName,
                file.getContentType(),
                file.getSize(),
                targetLocation.toString(),
                contract,
                currentUser
        );

        Document saved = documentRepository.save(doc);

        auditService.log("DOCUMENT_UPLOADED", currentUser.getUsername(), "Contract", contract.getId(),
                "Uploaded document: " + originalFileName + " (" + (file.getSize() / 1024) + " KB) for contract " + contract.getContractNumber());

        return saved;
    }

    public Resource loadFileAsResource(Long documentId) {
        Document document = getDocumentById(documentId);
        try {
            Path filePath = Paths.get(document.getFilePath()).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found on disk: " + document.getOriginalFileName());
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found: " + document.getOriginalFileName(), ex);
        }
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        Document document = getDocumentById(documentId);
        Long contractId = document.getContract().getId();
        String originalName = document.getOriginalFileName();

        try {
            Path filePath = Paths.get(document.getFilePath()).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // Log disk deletion failure, proceed to delete DB record
        }

        documentRepository.delete(document);

        auditService.log("DOCUMENT_DELETED", null, "Contract", contractId,
                "Deleted document: " + originalName);
    }
}
