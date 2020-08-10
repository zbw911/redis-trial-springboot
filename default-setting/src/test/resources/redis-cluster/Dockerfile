FROM redis

ENV REDIS_PORT 6379

EXPOSE $REDIS_PORT

RUN apt update &&\
    apt-get install net-tools && \
    apt-get install -y iputils-ping &&\
    apt-get install -y telnet

COPY entrypoint.sh /usr/local/bin/
COPY redis.conf /usr/local/etc/

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD ["redis-server", "/usr/local/etc/redis.conf"]