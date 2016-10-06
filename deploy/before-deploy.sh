#!/usr/bin/env bash

if [ "$TRAVIS_BRANCH" = 'buildAndDeployAutomation' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_89dd7f8061de_key -iv $encrypted_89dd7f8061de_iv -in codesigning.asc.enc -out codesigning.asc -d
    gpg --fast-import deploy/signingkey.asc
fi
