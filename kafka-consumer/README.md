1.- Construye la imagen Docker dentro del entorno de Minikube
Minikube tiene su propio entorno de Docker, por lo que necesitas construir la imagen allí:

```
docker build -t localhost:5000/springboot-consumer:latest .
docker push localhost:5000/springboot-consumer:latest

```
2.- Crea un Deployment y Service en Kubernetes

Crea un archivo consumer-deployment.yaml:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: springboot-consumer
  namespace: kafka
spec:
  replicas: 1
  selector:
    matchLabels:
      app: springboot-consumer
  template:
    metadata:
      labels:
        app: springboot-consumer
    spec:
      containers:
      - name: springboot-consumer
        image: localhost:5000/springboot-consumer:latest
        imagePullPolicy: Always
        ports:
        - containerPort: 8080
---
# Opcional: Service si necesitas exponer algo
apiVersion: v1
kind: Service
metadata:
  name: springboot-consumer
  namespace: kafka
spec:
  selector:
    app: springboot-consumer
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
```
   
Nota: Si tu aplicación no expone un puerto HTTP, puedes omitir la parte del Service. 


3. Despliega en Minikube

```
kubectl apply -f consumer-deployment.yaml
```
4.- Verifica los pods:

```
kubectl get pods -n kafka
```
5.- Revisa los logs:

```
kubectl logs <nombre-del-pod> -n kafka
```

