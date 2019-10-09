# Contents

This directory contains files needed to create runnable docker image with application.

## Provision module

Module used to gather all necessary bundles from application.

## conf.default and conf directories

Folder containing configuration files for Configuration Admin service. Since the application
is running in docker, only inside-the-container configuration should be passed (file storage, log levels, ...)
but not outside-the-container configuration such as database connection details.

If the `conf` directory doesn't exist, be sure to create it. You can just copy the `conf.default`.

## Dockerfile

File used to create docker image.

## felix-configuration directory

Felix framework configuration files.