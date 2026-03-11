# Kubernetes Deployment

This guide covers the development deployment path for AirOps360 on Kubernetes using the base manifests and the `dev` overlay.

## Prerequisites
- Kubernetes cluster with `kubectl` access
- Kustomize support through `kubectl`
- Container images published with the `dev` tag for each deployed service

## Render manifests

```bash
kubectl kustomize infrastructure/kubernetes/base
kubectl kustomize infrastructure/kubernetes/overlays/dev
```

## Deploy the development overlay

```bash
kubectl apply -k infrastructure/kubernetes/overlays/dev
```

## Verify rollout

```bash
kubectl get pods -n airops360
kubectl get svc -n airops360
```

## Local access points
- API gateway: `http://localhost:30080`
- Operations dashboard: `http://localhost:30081`

## Notes
- The overlay keeps all workloads in the `airops360` namespace.
- Supporting dependencies such as Kafka, PostgreSQL, Redis, and MongoDB can be supplied by separate manifests or an external environment.
- Later overlays can replace the `dev` image tags and NodePort exposure with ingress-based routing.
