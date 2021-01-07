#!/bin/bash
# Start script
# =============== 
# This script will launch the services without building of metadata and shared module

docker-compose --env-file .env up $1
