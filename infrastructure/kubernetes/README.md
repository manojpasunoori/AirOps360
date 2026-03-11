# kubernetes

Kubernetes manifests and deployment overlays for AirOps360.

## Commit 19 scope

This folder now includes a base Kubernetes manifest set for the current AirOps360 application tier:
- namespace definition
- shared application ConfigMap
- Deployments and Services for flight, cargo, warehouse, inventory, baggage, simulator, gateway, and dashboard components
- `kustomization.yaml` for assembling the base resources

## Structure
- `base/namespace.yaml`
- `base/app-config.yaml`
- `base/*.yaml` service manifests
- `base/kustomization.yaml`

## Notes
- These are base manifests only and intentionally use placeholder images such as `airops360/<service>:latest`.
- Environment-specific deployment settings, ingress, and infra dependencies can be layered on in the next commit.
