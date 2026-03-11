# kubernetes

Kubernetes manifests and deployment overlays for AirOps360.

## Structure
- `base/` reusable namespace, config, Deployments, and internal Services
- `overlays/dev/` local-development overlay with dev image tags and NodePort exposure for the gateway and dashboard

## Render manifests

```bash
kubectl kustomize infrastructure/kubernetes/base
kubectl kustomize infrastructure/kubernetes/overlays/dev
```

## Apply the development overlay

```bash
kubectl apply -k infrastructure/kubernetes/overlays/dev
```

## Access points after deployment
- API gateway: `http://localhost:30080`
- Operations dashboard: `http://localhost:30081`

## Notes
- The dev overlay expects images tagged as `airops360/<service>:dev`.
- Backing infrastructure like Kafka, Redis, MongoDB, and PostgreSQL can be layered in later or provided externally.
- Detailed deployment steps are documented in `docs/deployment/kubernetes-deployment.md`.
