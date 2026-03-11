# ArgoCD

AirOps360 uses ArgoCD to apply Kubernetes manifests from this repository through a GitOps workflow.

## Current GitOps entrypoints
- `infrastructure/argocd/project.yaml` registers the `airops360` ArgoCD project
- `infrastructure/argocd/application-dev.yaml` points ArgoCD at `infrastructure/kubernetes/overlays/dev`

## Apply the ArgoCD definitions

```bash
kubectl apply -n argocd -f infrastructure/argocd/project.yaml
kubectl apply -n argocd -f infrastructure/argocd/application-dev.yaml
```

## Result
- ArgoCD watches the `main` branch
- The `airops360-dev` application syncs the development Kustomize overlay
- Namespace creation and automated reconciliation are enabled
