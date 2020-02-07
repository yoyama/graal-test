#!/bin/bash

. /root/.sdkman/bin/sdkman-init.sh

echo install JDK
sdk install java 19.3.1.r11-grl
sdk install java 19.3.1.r8-grl
sdk install java 11.0.6.hs-adpt
sdk install java 8.0.242.hs-adpt
sdk install gradle 6.0.1
rm  /root/.sdkman/archives/*.zip






