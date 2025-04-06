#!/usr/bin/env sh

#envsubst "`printf '${%s} ' $(bash -c "compgen -A variable")`" </app/default.temp> /etc/nginx/conf.d/default.conf
envsubst  </app/default.temp> /etc/nginx/conf.d/default.conf
nginx -g 'daemon off;'
