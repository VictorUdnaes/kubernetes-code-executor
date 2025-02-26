docker build -t vudnaes/ktor-server:latest .

docker push vudnaes/ktor-server:latest

# Apply the deployment
kubectl apply -f configuration.yaml

# Restart the deployment
kubectl rollout restart deployment/ktor-server-deployment

clear
