FROM ubuntu:18.04

COPY install_jdk.sh ./

ENV DEBIAN_FRONTEND=noninteractive
RUN apt-get update && \
    apt-get install -y --no-install-recommends apt-utils && \
    apt-get install -y curl unzip zip git && \
    apt-get install -y tzdata && \
    curl -s "https://get.sdkman.io" | bash && \
    echo "sdkman_auto_answer=true" > $SDKMAN_DIR/etc/config && \
    echo "sdkman_auto_selfupdate=false" >> $SDKMAN_DIR/etc/config && \
    echo "sdkman_insecure_ssl=true" >> $SDKMAN_DIR/etc/config   && \
    ./install_jdk.sh
    
ENTRYPOINT /bin/bash

