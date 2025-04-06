FROM nginx:1.22-alpine
MAINTAINER lifeilong@goldenit.com.cn

COPY config /app
WORKDIR app

ENV TZ="Asia/Shanghai" \
    uri="\$uri" \
    host="\$host" \
    http_host="\$http_host" \
    http_connection="\$http_connection" \
    remote_port="\$remote_port" \
    scheme="\$scheme" \
    proxy_add_x_forwarded_for="\$proxy_add_x_forwarded_for" \
    remote_addr="\$remote_addr" \
    http_upgrade="\$http_upgrade" \
    NGINX_PORT="80" \
    NGINX_HOST="localhost"

RUN mkdir /app/logs \
    && sed -i '2c user  root;' /etc/nginx/nginx.conf \
    && chmod +x docker-entrypoint.sh

ENTRYPOINT ["./docker-entrypoint.sh"]
