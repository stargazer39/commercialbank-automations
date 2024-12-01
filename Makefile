DOCKER_USERNAME ?= stargazerdocker
APPLICATION_NAME ?= combank-automation
GIT_HASH ?= $(shell git log --format="%h" -n 1)

release-be:
	cd cmb-backend; \
		docker build --tag ${DOCKER_USERNAME}/${APPLICATION_NAME}:${GIT_HASH} . --no-cache; \
		docker tag  ${DOCKER_USERNAME}/${APPLICATION_NAME}:${GIT_HASH} ${DOCKER_USERNAME}/${APPLICATION_NAME}:latest; \
		docker push ${DOCKER_USERNAME}/${APPLICATION_NAME}:latest;

build-be:
	cd cmb-backend; \
		docker build --tag ${DOCKER_USERNAME}/${APPLICATION_NAME}:${GIT_HASH} .;

pull-be:
	docker pull ${DOCKER_USERNAME}/${APPLICATION_NAME}:latest

release-fe:
	cd cmb-frontend; \
		docker build --tag ${DOCKER_USERNAME}/${APPLICATION_NAME}-fe:${GIT_HASH} . --no-cache; \
		docker tag  ${DOCKER_USERNAME}/${APPLICATION_NAME}-fe:${GIT_HASH} ${DOCKER_USERNAME}/${APPLICATION_NAME}-fe:latest; \
		docker push ${DOCKER_USERNAME}/${APPLICATION_NAME}-fe:latest;

build-fe:
	cd cmb-frontend; \
		docker build --tag ${DOCKER_USERNAME}/${APPLICATION_NAME}-fe:${GIT_HASH} .;

pull-fe:
	docker pull ${DOCKER_USERNAME}/${APPLICATION_NAME}-fe:latest
