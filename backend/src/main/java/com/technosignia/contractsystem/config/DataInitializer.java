package com.technosignia.contractsystem.config;

import com.technosignia.contractsystem.entity.*;
import com.technosignia.contractsystem.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ClauseRepository clauseRepository;

    @Autowired
    private ModificationRequestRepository modificationRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            logger.info("Seeding initial users...");

            String defaultHash = passwordEncoder.encode("password123");

            User admin = userRepository.save(new User("admin", "admin@technosignia.com", defaultHash, "System Administrator", Role.ROLE_ADMIN));
            User manager = userRepository.save(new User("manager", "manager@technosignia.com", defaultHash, "Sarah Jenkins (Contract Manager)", Role.ROLE_CONTRACT_MANAGER));
            User approver = userRepository.save(new User("approver", "approver@technosignia.com", defaultHash, "David Vance (Legal Approver)", Role.ROLE_APPROVER));
            User clientUser = userRepository.save(new User("user", "user@technosignia.com", defaultHash, "Alex Mercer (Client Representative)", Role.ROLE_USER));

            logger.info("Seeding sample legal contracts & clauses...");

            // Contract 1
            Contract c1 = new Contract(
                    "CNT-2026-001",
                    "Software Development & SLA Master Agreement",
                    "Comprehensive master services agreement outlining engineering deliverables, cloud hosting terms, and enterprise SLA commitments.",
                    ContractStatus.ACTIVE,
                    manager
            );
            c1 = contractRepository.save(c1);

            Clause c1_1 = clauseRepository.save(new Clause("Payment Terms", "Payment must be completed within 30 days of verified invoice receipt via direct wire transfer.", 1, c1));
            Clause c1_2 = clauseRepository.save(new Clause("Confidentiality & Non-Disclosure", "Both parties agree to treat all exchanged proprietary algorithms, source code, and business intelligence with strict confidentiality for a duration of 5 years.", 2, c1));
            Clause c1_3 = clauseRepository.save(new Clause("Intellectual Property Assignment", "All custom deliverables and source code developed under this agreement become exclusive intellectual property of the Client upon full financial settlement.", 3, c1));
            Clause c1_4 = clauseRepository.save(new Clause("Termination & Exit Strategy", "Either party may terminate this master agreement by providing a minimum 60-day advance written notice without financial penalty.", 4, c1));

            // Contract 2
            Contract c2 = new Contract(
                    "CNT-2026-002",
                    "Commercial Technology Facility Lease",
                    "Lease agreement covering corporate headquarters floor 4 & 5, including server room power and network connectivity facilities.",
                    ContractStatus.DRAFT,
                    manager
            );
            c2 = contractRepository.save(c2);

            clauseRepository.save(new Clause("Premises & Monthly Rent", "The monthly rent shall be $8,500 net, payable promptly on the first calendar day of each month.", 1, c2));
            clauseRepository.save(new Clause("Security Deposit", "A refundable security deposit equal to 2 months' lease amount ($17,000) shall be deposited in escrow prior to occupancy.", 2, c2));
            clauseRepository.save(new Clause("Maintenance & Utilities", "Lessor shall be liable for HVAC and electrical infrastructure maintenance with 99.9% uptime for data center loads.", 3, c2));

            // Seed a sample pending Modification Request for demonstration
            ModificationRequest pendingMod = new ModificationRequest();
            pendingMod.setContract(c1);
            pendingMod.setClause(c1_1);
            pendingMod.setRequestType(ModificationType.CLAUSE);
            pendingMod.setOriginalValue(c1_1.getContent());
            pendingMod.setProposedValue("Payment must be completed within 45 days of verified invoice receipt via direct wire transfer.");
            pendingMod.setReason("Extended billing cycles requested by enterprise client accounts payable processing schedules.");
            pendingMod.setStatus(ModificationStatus.PENDING);
            pendingMod.setRequestedBy(clientUser);
            modificationRepository.save(pendingMod);

            // Audit Logs
            auditLogRepository.save(new AuditLog("SYSTEM_INITIALIZED", "SYSTEM", "System", 1L, "System initialized with sample contract data and default roles."));
            auditLogRepository.save(new AuditLog("CONTRACT_CREATED", "manager", "Contract", c1.getId(), "Created master agreement CNT-2026-001"));
            auditLogRepository.save(new AuditLog("MODIFICATION_REQUESTED", "user", "Modification", pendingMod.getId(), "Submitted pending change request for payment terms"));

            logger.info("Data initialization complete.");
        }
    }
}
