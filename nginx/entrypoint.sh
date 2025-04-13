#!/bin/sh

# Replace placeholders with actual values
export DOLLAR="$"
envsubst < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf

# Start NGINX
nginx -g 'daemon off;'