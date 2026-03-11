# argocd

ArgoCD application definitions for GitOps-based deployments.

## Commit 22 scope
This folder now contains the GitOps entrypoints for the current AirOps360 development deployment:
- `project.yaml` defines the `airops360` ArgoCD project
- `application-dev.yaml` syncs the Kubernetes development overlay from `infrastructure/kubernetes/overlays/dev`

## Apply to an ArgoCD control plane

```bash
kubectl apply -n argocd -f infrastructure/argocd/project.yaml
kubectl apply -n argocd -f infrastructure/argocd/application-dev.yaml
```

## Current behavior
- Tracks the `main` branch of this repository
- Syncs the `infrastructure/kubernetes/overlays/dev` Kustomize overlay
- Creates the `airops360` namespace if it does not exist
- Enables automated sync, pruning, and self-healing
