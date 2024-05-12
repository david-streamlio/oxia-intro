# Apache Oxia - A Horizontially Scalable Alternative to Zookeeper

This repo introduces Oxia, a subproject within the Apache Pulsar community aimed at providing a horizontally scalable 
alternative to the traditional Zookeer-based consensus architecture. The goal of Oxia is two-fold:

    1. Develop a consensus and coordination system that doesn’t suffer from Zookeeper’s horizontal scalability limitations.

    2. Create a compelling Zookeeper replacement that can be used across the entire Apache ecosystem.

## Getting Started
This repo walks you through the process of running Oxia, using the Oxia Java client, and running basic performance tests

## Running Oxia Locally

See the [infrastructure guide](docs%2Fguides%2FInfra-Guide.md) for more details on running Oxia locally using MiniKube.

## Using the Oxia Client

See the [Client Guide](docs%2Fguides%2Fclient-guide.md) for more details.

## Performance Testing

TBD

## References

- https://github.com/streamnative/oxia
- https://github.com/streamnative/oxia-java
- https://github.com/aahmed-se/oxia-java-client/blob/main/lib/src/main/java/oxia/java/client/OxiaKVClient.java
