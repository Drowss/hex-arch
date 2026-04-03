# 🧐 Project Overview

This project implements a **multi-module structure** using **Gradle**, enabling organized separation of dependencies.

### 🏗 Architecture & Best Practices
- **Hexagonal Architecture** for clean separation of concerns.
- Development principles followed: **KISS**, **YAGNI**, **DRY**.
- Concepts of **Clean Architecture** applied throughout the codebase.

### ☁️ Infrastructure
- Deployed using **Kubernetes**.
- Only the **main application** is exposed externally.
- The **database** remains accessible internally within the cluster to ensure **security** and **isolation**.

### ⚡ Key Highlights
- Modular design for maintainability and scalability.
- Strong adherence to development best practices.
- Secure and isolated deployment within a Kubernetes cluster.

## 🖥️ Run Locally

To run this project on your local machine, follow these steps:

### 1️⃣ Prerequisites
- Make sure **Docker Desktop** is running.
- Ensure **Kubernetes** is enabled in Docker Desktop.

### 2️⃣ Deploy the Infrastructure
Run the provided script to create all necessary resources:

```bash
./createInfra.sh
```

This will deploy the application and required services inside your local Kubernetes cluster.

### 3️⃣ Clean Up

To remove all deployed resources, run:
```bash
./deleteInfra.sh
```

This will clean up the Kubernetes cluster and remove all application resources.

| Endpoint             | Method | Body                                                                                                |
|----------------------|--------|-----------------------------------------------------------------------------------------------------|
| `/api/v1/user`       | POST   | `{ "name": "developer", "password": "1234", "metadata": { "email": "test@gmail.com", "age": 18 } }` |
| `/api/v1/user/login` | POST   | `{ "name": "developer", "password": "1234" }`                                                       |
| `/api/v1/user`       | GET    | *No body required, but login is required*                                                           |
