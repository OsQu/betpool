REPO ?= osqu/betpool
IMAGE_TAG ?= $(shell git rev-parse HEAD)

.PHONY: release
release: compile build push

.PHONY: compile
compile:
	./gradlew shadowJar

.PHONY: build
build:
	docker build -t ${REPO}:latest .
	docker tag ${REPO}:latest ${REPO}:${IMAGE_TAG}

.PHONY: push
push:
	docker push ${REPO}:${IMAGE_TAG}
