package com.example.management.infrastructure.adapters.out.persistence;

import com.example.management.application.ports.out.OrderRepository;
import com.example.management.application.ports.out.OrderRepositoryContractTest;
import org.junit.jupiter.api.BeforeEach;

class InMemoryOrderRepositoryContractTest extends OrderRepositoryContractTest {

    private InMemoryOrderRepository repository;

    @Override
    @BeforeEach
    protected void setUp() {
        repository = new InMemoryOrderRepository();
        super.setUp();
    }

    @Override
    protected void clearRepository() {
        // In-memory: new instance each test is enough; no shared state
    }

    @Override
    protected OrderRepository repository() {
        return repository;
    }
}
