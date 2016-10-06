#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'buildAndDeployAutomation' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    mvn deploy -P ossrh --settings deploy/mvnsettings.xml
fi