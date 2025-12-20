#!/bin/bash

cd /Users/georgi/Downloads/bank

echo "Killing existing processes on ports 8080 and 9000..."
pkill -f "authorization-service:bootRun" || true
pkill -f "frontend-service:bootRun" || true
sleep 2

echo "Starting Authorization Server..."
./gradlew authorization-service:bootRun > /tmp/auth.log 2>&1 &
AUTH_PID=$!

echo "Starting Frontend Service..."
./gradlew frontend-service:bootRun > /tmp/frontend.log 2>&1 &
FRONTEND_PID=$!

echo "Waiting 25 seconds for services to start..."
sleep 25

echo ""
echo "=== SERVICE STATUS ==="
AUTH_UP=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:9000/.well-known/openid-configuration)
FRONTEND_UP=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/)

echo "Auth Server: HTTP $AUTH_UP"
echo "Frontend: HTTP $FRONTEND_UP"

if [ "$AUTH_UP" = "200" ] && [ "$FRONTEND_UP" = "200" ]; then
  echo ""
  echo "✓ Both services are running!"
  echo "Run: bash /Users/georgi/Downloads/bank/run_oauth2_curl_test.sh"
else
  echo "Logs:"
  echo "Auth: $(tail -5 /tmp/auth.log)"
  echo "Frontend: $(tail -5 /tmp/frontend.log)"
fi
