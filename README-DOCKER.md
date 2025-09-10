# Despliegue Automatizado con GitHub Actions y Terraform

Este documento describe cómo configurar el despliegue automatizado de la aplicación de gestión de franquicias en AWS usando GitHub Actions y Terraform.

## 🚀 Configuración Inicial (Una sola vez)

### 1. Crear Backend de Terraform

Primero necesitas crear el bucket S3 y la tabla DynamoDB para el estado de Terraform:

```bash
cd terraform
terraform init
terraform apply -var="project_name=franchise-management"
```

Este comando creará:
- ✅ Bucket S3 para el estado de Terraform
- ✅ Tabla DynamoDB para bloqueo de estado
- ✅ Rol IAM para GitHub Actions
- ✅ Proveedor OIDC para autenticación sin credenciales

### 2. Configurar Secrets en GitHub

Ve a tu repositorio en GitHub: **Settings → Secrets and variables → Actions**

Agrega estos secrets:

| Secret Name | Value | Descripción |
|-------------|--------|-------------|
| `AWS_ROLE_ARN` | `arn:aws:iam::ACCOUNT:role/franchise-management-github-actions-role` | Rol IAM para GitHub Actions |
| `TERRAFORM_STATE_BUCKET` | `franchise-management-terraform-state-XXXX` | Bucket S3 para estado |
| `TERRAFORM_LOCK_TABLE` | `franchise-management-terraform-locks` | Tabla DynamoDB para bloqueos |
| `MONGODB_PASSWORD` | `tu-password-seguro` | Contraseña segura para MongoDB |

## 🔄 Despliegue Automático

### Trigger del Pipeline

El despliegue se ejecuta automáticamente cuando:
- Haces `push` a la rama `master`
- Se ejecutan los tests primero
- Si los tests pasan, se despliega en AWS

```bash
git add .
git commit -m "Deploy to production"
git push origin master
```

### Pipeline de GitHub Actions

El workflow incluye:

1. **🧪 Tests**
   - Tests unitarios con Gradle
   - Validación de arquitectura
   - Reportes de cobertura

2. **🏗️ Build & Deploy**
   - Construcción de la aplicación
   - Creación de infraestructura con Terraform
   - Build y push de imagen Docker a ECR
   - Despliegue en ECS Fargate

3. **✅ Verificación**
   - Health check automático
   - Validación de endpoints
   - Notificación de éxito/fallo

## 🐳 Ejecución Local con Docker

### Comandos de Docker

```bash
# Construir y ejecutar localmente
docker-compose up --build

# Ejecutar en background
docker-compose up -d

# Ver logs
docker-compose logs -f

# Parar servicios
docker-compose down
```

La aplicación estará disponible en:
- **API**: http://localhost:8080
- **Health Check**: http://localhost:8080/actuator/health
- **MongoDB**: localhost:27017

## 🔧 Configuración

### Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring activo | `docker` |
| `SPRING_DATA_MONGODB_URI` | URI de conexión a MongoDB | `mongodb://root:password@mongodb:27017/franchise_db?authSource=admin` |
| `SERVER_PORT` | Puerto del servidor | `8080` |
| `MONGO_PASSWORD` | Contraseña de MongoDB | `password` |

### Profiles de Spring

- **default**: Configuración local
- **docker**: Configuración para contenedores
- **aws**: Configuración específica para AWS (opcional)

## 📊 Monitoreo

### Health Checks
- **Aplicación**: `GET /actuator/health`
- **MongoDB**: `mongosh --eval "db.adminCommand('ping')"`

### Logs en AWS
Los logs se envían automáticamente a CloudWatch:
- Log Group: `/ecs/franchise-management`
- Streams: `mongodb` y `app`

### Métricas
- CPU y memoria en CloudWatch
- Métricas de contenedor en ECS
- Logs de aplicación con niveles configurables

## 🔒 Seguridad

### Consideraciones de Seguridad
1. **Credenciales**: Usar AWS Secrets Manager para credenciales sensibles
2. **Red**: Configurar Security Groups restrictivos
3. **CORS**: Configurado para permitir todos los orígenes (`*`)
4. **MongoDB**: Autenticación habilitada por defecto

### Security Groups Recomendados
```bash
# Security Group para ECS
aws ec2 create-security-group \
  --group-name franchise-ecs-sg \
  --description "Security group for franchise ECS tasks"

# Permitir tráfico HTTP en puerto 8080
aws ec2 authorize-security-group-ingress \
  --group-id sg-xxxxx \
  --protocol tcp \
  --port 8080 \
  --cidr 0.0.0.0/0
```

## 🚀 Endpoints de la API

Base URL: `http://[ALB-DNS-NAME]` o `http://[ECS-PUBLIC-IP]:8080`

### Franquicias
- `POST /api/v1/franchise` - Crear franquicia
- `PUT /api/v1/franchise/{id}` - Actualizar nombre de franquicia

### Sucursales
- `POST /api/v1/franchise/{franchiseId}/branch` - Agregar sucursal
- `PUT /api/v1/franchise/{franchiseId}/branch/{branchId}` - Actualizar sucursal

### Productos
- `POST /api/v1/franchise/{franchiseId}/branch/{branchId}/product` - Agregar producto
- `PUT /api/v1/franchise/{franchiseId}/branch/{branchId}/product/{productId}` - Actualizar producto
- `DELETE /api/v1/franchise/{franchiseId}/branch/{branchId}/product/{productId}` - Eliminar producto
- `GET /api/v1/franchise/{franchiseId}` - Obtener stock máximo por sucursal

## 🛠️ Troubleshooting

### Problemas Comunes

1. **Error de conexión a MongoDB**
   ```bash
   # Verificar logs
   docker-compose logs mongodb
   
   # Verificar conectividad
   docker exec -it franchise-management-app ping mongodb
   ```

2. **Imagen no se encuentra en ECR**
   ```bash
   # Verificar imagen en ECR
   aws ecr describe-images --repository-name franchise-management
   
   # Re-build y push
   ./deploy-aws.sh
   ```

3. **Task no se inicia en ECS**
   ```bash
   # Verificar task definition
   aws ecs describe-task-definition --task-definition franchise-management-task
   
   # Verificar logs
   aws logs tail /ecs/franchise-management --follow
   ```

### Comandos Útiles

```bash
# Ver estado del servicio ECS
aws ecs describe-services --cluster franchise-cluster --services franchise-service

# Ver tasks en ejecución
aws ecs list-tasks --cluster franchise-cluster --service-name franchise-service

# Escalar servicio
aws ecs update-service --cluster franchise-cluster --service franchise-service --desired-count 2

# Ver logs en tiempo real
aws logs tail /ecs/franchise-management --follow --region us-east-1
```

## 💰 Consideraciones de Costos

### Estimación AWS (us-east-1)
- **ECS Fargate**: ~$15-30/mes (1 tarea, 0.5 vCPU, 1GB RAM)
- **ECR**: ~$1/mes (almacenamiento de imágenes)
- **CloudWatch**: ~$1-5/mes (logs y métricas)
- **ALB**: ~$20/mes (opcional)

### Optimizaciones
- Usar instancias Spot para desarrollo
- Configurar auto-scaling basado en CPU/memoria
- Implementar lifecycle policies en ECR para limpiar imágenes antiguas

## 📞 Soporte

Para problemas o dudas:
1. Revisar logs de CloudWatch
2. Verificar configuración de Security Groups
3. Validar task definition de ECS
4. Comprobar conectividad de red VPC