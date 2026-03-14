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

# Architecture
Client
    |
Api-Gateway (later)
    |
Rate-limiter-service
    |
Redis (Shared state)

1. The system is designed as a stateless backend service responsible for validating whether request should be allow or reject based on rate limits. 
2. The service store rate limiting state in *Redis* to allow multiple service instance to share the same rate limit counters. 
3. This enables horizontal scaling of the system without losing correctness of rate limit enforcement. 

# Each component in detail

1. Rate Limiter Service
    - The core service responsible for validating incoming requests.
    - It calculates whether a request should be allowed based on the token bucket algorithm.

2. Redis
    - Redis acts as *distributed state* store. 
    - It allows multiple instances of the rate limiter service to *share token bucket state*.
    - It enables *consistent enforcement* of rate limits in a distributed system. 

3. Client
    -  Represent any external system making API calls that need rate limiting protection.




Rate Limiting Algorithm
Tech Stack
Java + Springboot
Redis
Docker
Kubernetes (later)


Future Enhancements
Additional of Api-Gateway