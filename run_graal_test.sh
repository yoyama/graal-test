#!/bin/bash

export TZ="Asia/Tokyo"

source ~/.sdkman/bin/sdkman-init.sh

JDKs=(8.0.242.hs-adpt 11.0.6.hs-adpt 19.3.1.r11-grl 19.3.1.r8-grl)
ymd=`date +"%Y%m%d_%H%M%S"`

result_dir=results/$ymd
mkdir -p $result_dir

function run_test () {
    jdk=$1
    ./gradlew clean shadowJar
    java -jar build/libs/graal-test.jar -bm all -f 1 2>&1 |tee ${result_dir}/result_${jdk}.log
}

for jdk in ${JDKs[@]}
do
    echo $jdk
    sdk use java $jdk
    java -version
    run_test $jdk
done




