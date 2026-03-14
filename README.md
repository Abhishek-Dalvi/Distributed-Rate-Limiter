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

5. The system is design to evolve toward *Kubernetes* deployment and production-grade observability.

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

# Token Algorithm Explanation
1. Each user is associated with a token bucket.
2. The bucket contains a fixed number of tokens representing the number of allowed requests.
3. Each incoming request consumes one token from the bucket. 
4. Tokens are refilled at a *fixed rate* over a time.
5. If the bucket contains no tokens when a request arrives, the request is rejected.

# Why Token Bucket?
- Token bucket allows short burts of traffic while maintaining a defined long-term request rate
- This behaviour is commonly used in production systems such as API gateways and traffic management layes.

# Tech Stack
1. Java + Springboot- Used to implement the rate limiter service with a REST API interface.
2. Redis- Used as a distributed data store for maintaining token bucket state.
3. Docker (future phase)- Used to containerize the service for consistent runtime environments. 
4. Kubernetes (future phase)-  Used to deploy multiple service instances and test distributed rate limiting behavior. 



# Future Enhancements
Planned enhancements for the system include:
- Integration with Api-Gateway to enforce rate limits at the edge.
- Kubernetes deployment for horizontal scaling experiments
- Redis Lua scripts for *atomic rate* limit operations
- Load testing to validate system behavior under high request volumes 
- Monitoring and metrics using Prometheus and Grafana

# Example Request
POST /request
{
    "userId": "123"
}

{
    "allowed": true
}