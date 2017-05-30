#!/bin/bash
echo "JAVA_HOME=$JAVA_HOME"

./start

echo "Karaf container starting"
sleep 5

TIMEOUT=5
DELAY=1
T=0

RESULT=0

until [ $T -gt $TIMEOUT ]
do
  if ./client info; then
    echo "Server is reachable."


    echo "Updating Config"
    ./client -f update-config.cli
    if [ $? -ne 0 ]; then
      echo "Call update-config.cli failed!";
      RESULT=1;
    else
      ./client config:list | grep org.ops4j.pax.url.mvn.
    fi

    echo "Installing features."
    ./client -f install-features.cli
    if [ $? -ne 0 ]; then
      echo "Call install-features.cli failed!";
      RESULT=1;
    fi
    exit $RESULT
  else
    echo "Server is not reachable. Waiting."
    sleep $DELAY
    let T=$T+$DELAY
  fi
done
