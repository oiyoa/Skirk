# syntax=docker/dockerfile:1.7

ARG GO_VERSION=1.25

FROM --platform=$BUILDPLATFORM golang:${GO_VERSION}-alpine AS builder

ARG TARGETOS
ARG TARGETARCH
ARG VERSION=dev
ARG COMMIT=unknown
ARG DATE=unknown
# Optional OAuth client to bake into the binary
ARG SKIRK_OAUTH_CLIENT_ID=""
ARG SKIRK_OAUTH_CLIENT_SECRET=""

WORKDIR /src

COPY go.mod go.sum ./
RUN --mount=type=cache,target=/go/pkg/mod \
    go mod download

COPY cmd ./cmd
COPY internal ./internal

RUN --mount=type=cache,target=/go/pkg/mod \
    --mount=type=cache,target=/root/.cache/go-build \
    set -eu; \
    ldflags="-s -w \
      -X main.version=${VERSION} \
      -X main.commit=${COMMIT} \
      -X main.date=${DATE}"; \
    if [ -n "${SKIRK_OAUTH_CLIENT_ID}" ] && [ -n "${SKIRK_OAUTH_CLIENT_SECRET}" ]; then \
        ldflags="${ldflags} \
          -X main.defaultOAuthClientID=${SKIRK_OAUTH_CLIENT_ID} \
          -X main.defaultOAuthClientSecret=${SKIRK_OAUTH_CLIENT_SECRET}"; \
    elif [ -n "${SKIRK_OAUTH_CLIENT_ID}${SKIRK_OAUTH_CLIENT_SECRET}" ]; then \
        echo "SKIRK_OAUTH_CLIENT_ID and SKIRK_OAUTH_CLIENT_SECRET must be set together" >&2; \
        exit 1; \
    fi; \
    CGO_ENABLED=0 GOOS=${TARGETOS} GOARCH=${TARGETARCH} \
        go build -trimpath -ldflags "${ldflags}" -o /out/skirk ./cmd/skirk

RUN mkdir -p /out/data && chown -R 65532:65532 /out/data

FROM gcr.io/distroless/static-debian12:nonroot

LABEL org.opencontainers.image.title="Skirk" \
      org.opencontainers.image.description="Skirk headless daemon (exit / client proxy)." \
      org.opencontainers.image.licenses="Apache-2.0"

COPY --from=builder --chown=65532:65532 /out/skirk /usr/local/bin/skirk
COPY --from=builder --chown=65532:65532 /out/data /data

USER 65532:65532
WORKDIR /data

VOLUME ["/data"]

ENTRYPOINT ["/usr/local/bin/skirk"]
CMD ["serve-exit", "--config", "/data/skirk-kit/exit.json"]
