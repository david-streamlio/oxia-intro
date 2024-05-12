# Apache Oxia - Java Client Guide.

This guide walks you through the process of using the Java client for Oxia.

## Prerequisites

- An Oxia cluster that is accessible from the local machine.

### Run the test client

You must expose the Oxia port running inside MiniKube to localhost by running the following command:

```shell
$ kubectl -n oxia port-forward service/oxia 6648:6648
```
