# Distributed-Rate-Limiter


# Project Goal:

1. This project implements a distributed rate limiter using the Token Bucket algorithm.
2. The goal is to design backend system capable of enforcing request limit per user in a scalable environment.
3. The system demonstrate how rate limiting can work across multiple service instances using redis as shared state store.
4. This project focuses on backend architecture concept including:
    - distributed state management
    - request throttling
    - horizontal scalability
    - cloud-native deployment readiness

5. The system is design to evolve toward *Kubernetes* deployment and production-grade observability 

The non functional requirements include robustness, scalability etc

Architecture
Client
    |
Api-Gateway (later)
    |
Rate-limiter-service
    |
Redis (Shared state)


Rate Limiting Algorithm
Tech Stack
Java + Springboot
Redis
Docker
Kubernetes (later)


Future Enhancements
Additional of Api-Gateway