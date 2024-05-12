# Apache Oxia - A Horizontially Scalable Alternative to Zookeeper

This repo introduces Oxia, a subproject within the Apache Pulsar community aimed at providing a horizontally scalable 
alternative to the traditional Zookeer-based consensus architecture. The goal of Oxia is two-fold:

    1. Develop a consensus and coordination system that doesn’t suffer from Zookeeper’s horizontal scalability limitations.

    2. Create a compelling Zookeeper replacement that can be used across the entire Apache ecosystem.

## Getting Started
This repo walks you through the process of running Oxia on a single machine.

### 1️⃣ Start Minikube

[Minikube](https://minikube.sigs.k8s.io/docs/) is a tool that enables us to run a local, single-node Kubernetes cluster on our machine, which is ideal for 
testing and development. It requires the following resources in order to run:

* 2 CPUs or more
* 2GB of free memory
* 20GB of free disk space
* Internet connection
* Container or virtual machine manager, such as: [Docker](https://minikube.sigs.k8s.io/docs/drivers/docker/),
[QEMU](https://minikube.sigs.k8s.io/docs/drivers/qemu/),
[Hyperkit](https://minikube.sigs.k8s.io/docs/drivers/hyperkit/), 
[Hyper-V](https://minikube.sigs.k8s.io/docs/drivers/hyperv/), 
[KVM](https://minikube.sigs.k8s.io/docs/drivers/kvm2/), 
[Parallels](https://minikube.sigs.k8s.io/docs/drivers/parallels/), 
[Podman](https://minikube.sigs.k8s.io/docs/drivers/podman/), 
[VirtualBox](https://minikube.sigs.k8s.io/docs/drivers/virtualbox/), or [VMware Fusion/Workstation](https://minikube.sigs.k8s.io/docs/drivers/vmware/)

If you plan on running MiniKube on a Macbook with a M1 chip, then you must use the command below to download and install
it.

```bash
curl -LO https://storage.googleapis.com/minikube/releases/latest/minikube-darwin-arm64

sudo install minikube-darwin-arm64 /usr/local/bin/minikube
```


For this guide, we will be using Docker, as it one of the most widely adopted container technologies. After you have 
installed minikube and Docker, you can start Minikube using the following command: 

```bash
minikube start --driver=docker --alsologtostderr &
```

### 2️⃣ Deploy Oxia using the Helm chart

Use the following commands to deploy Oxia using Helm:

```bash
$ kubectl create namespace oxia

$ git clone https://github.com/streamnative/oxia.git

$ cd oxia

$ helm upgrade --install oxia \
  --namespace oxia \
  --set image.repository=streamnative/oxia \
  --set image.tag=main \
  --set image.pullPolicy=IfNotPresent \
  deploy/charts/oxia-cluster
```

After the installation is complete, you can validate that Oxia is running by running the following command:

```bash
kubectl -n oxia get all

NAME                                    READY   STATUS    RESTARTS      AGE
pod/oxia-0                              1/1     Running   0             114s
pod/oxia-1                              1/1     Running   0             114s
pod/oxia-2                              1/1     Running   0             114s
pod/oxia-coordinator-7476d758b5-6bqxv   1/1     Running   1 (72s ago)   114s

NAME                       TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)                      AGE
service/oxia               ClusterIP   10.108.210.245   <none>        6649/TCP,8080/TCP,6648/TCP   114s
service/oxia-coordinator   ClusterIP   10.97.145.18     <none>        6649/TCP,8080/TCP            114s
service/oxia-svc           ClusterIP   None             <none>        6649/TCP,8080/TCP,6648/TCP   114s

NAME                               READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/oxia-coordinator   1/1     1            1           114s

NAME                                          DESIRED   CURRENT   READY   AGE
replicaset.apps/oxia-coordinator-7476d758b5   1         1         1       114s

NAME                    READY   AGE
statefulset.apps/oxia   3/3     114s
```


### 4️⃣ Deploy Prometheus for Monitoring Oxia

Oxia supports monitoring through exposing a `ServiceMonitor` profile. If you have already a Prometheus deployment
int your Kubernetes cluster, you won't need any extra steps. It would directly start collecting monitoring data
from Oxia clusters.

The Helm Chart for the Oxia cluster uses a `monitoringEnabled` flag to decide whether to
install the service monitor. If you don't have Prometheus installed and don't want to install it, you can set
`monitoringEnabled: false` to skip this part.

Grafana's dashboards are available at [deploy/dashboards](/deploy/dashboards).
These can just be imported in your existing Grafana instance.

If you don't have already a Prometheus deployment, you can easily add it with Helm. You should first create the K8s 
namespace and install the Helm chart using the commands shown here:

```shell
$ kubectl create namespace monitoring

$ helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
$ helm repo update
```

Next, you can install the standard Prometheus & Grafana stack from Helm with the following command:

```shell
$ helm install monitoring prometheus-community/kube-prometheus-stack \
          --namespace monitoring \
          --set prometheus.prometheusSpec.serviceMonitorSelectorNilUsesHelmValues=false
```

You can validate that the pods are running with the following command:

```shell
kubectl --namespace monitoring get pods -l "release=monitoring"
NAME                                                   READY   STATUS    RESTARTS   AGE
monitoring-kube-prometheus-operator-665fbfc776-lqvg6   1/1     Running   0          71s
monitoring-kube-state-metrics-79ffbdd844-j2gtj         1/1     Running   0          71s
monitoring-prometheus-node-exporter-x48m5              1/1     Running   0          71s
```

Lastly, we will manually import the Oxia dashboards using the following sequence of commands:

```shell
$ helm pull prometheus-community/kube-prometheus-stack
$ tar -xf kube-prometheus-stack-*.tgz
$ cp deploy/dashboards/*.json kube-prometheus-stack/charts/grafana/dashboards

$ helm upgrade --install monitoring kube-prometheus-stack \
  --namespace monitoring \
  --values deploy/dashboards/values-kube-prometheus-stack.yaml

$ rm -rf kube-prometheus-stack*
```

***Note***: The unpack method has to be used as the grafana chart utilises Helm's `.Files.Get` to load custom dashboards
which cannot reach files outside the chart directory.

***Note***:  The default login credentials for grafana are admin/prom-operator.


### 5️⃣ Access the monitoring Dashboards

To access the Graphana dashboard you will need to run the following command to expose the port to your local machine:

```shell
$ kubectl port-forward svc/prometheus-operated 9090:9090 -n monitoring
```

![Graphana.png](docs%2Fimages%2FGraphana.png)


To access the Prometheus dashboard you will need to run the following command to expose the port to your local machine:

```shell
$ kubectl port-forward service/monitoring-grafana 3000:80 -n monitoring
```

![Prometheus.png](docs%2Fimages%2FPrometheus.png)


## References

- https://github.com/streamnative/oxia
- https://github.com/streamnative/oxia-java
- https://github.com/aahmed-se/oxia-java-client/blob/main/lib/src/main/java/oxia/java/client/OxiaKVClient.java
