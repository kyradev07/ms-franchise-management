# 🚀 Guía de Despliegue Completa - Franchise Management

Esta guía te llevará paso a paso para configurar el despliegue automatizado en AWS usando GitHub Actions y Terraform.

## 📋 Prerrequisitos

- ✅ Cuenta de AWS con permisos administrativos
- ✅ Repositorio en GitHub
- ✅ AWS CLI instalado y configurado
- ✅ Terraform instalado (v1.6+)
- ✅ Docker instalado (para desarrollo local)

## 🏗️ Paso 1: Configuración Inicial en AWS

### 1.1 Crear Backend de Terraform

```bash
# Clonar el repositorio
git clone <tu-repositorio>
cd ms-franchise-management

# Navegar a la carpeta terraform
cd terraform

# Inicializar Terraform (usando backend-setup.tf)
terraform init

# Aplicar configuración inicial
terraform apply -var="project_name=franchise-management"
```

**¿Qué hace esto?**
- Crea bucket S3 para almacenar el estado de Terraform
- Crea tabla DynamoDB para bloqueo de estado
- Configura rol IAM para GitHub Actions
- Configura proveedor OIDC para autenticación segura

### 1.2 Guardar los Outputs

Después del `terraform apply`, guarda estos valores:

```
Outputs:

github_actions_role_arn = "arn:aws:iam::123456789012:role/franchise-management-github-actions-role"
terraform_lock_table = "franchise-management-terraform-locks"
terraform_state_bucket = "franchise-management-terraform-state-a1b2c3d4"
```

## 🔐 Paso 2: Configurar GitHub Secrets

Ve a tu repositorio en GitHub: **Settings → Secrets and variables → Actions → New repository secret**

Agrega estos 4 secrets:

| Secret Name | Value | Ejemplo |
|-------------|--------|---------|
| `AWS_ROLE_ARN` | `arn:aws:iam::TU-ACCOUNT:role/franchise-management-github-actions-role` | `arn:aws:iam::123456789012:role/franchise-management-github-actions-role` |
| `TERRAFORM_STATE_BUCKET` | `franchise-management-terraform-state-XXXX` | `franchise-management-terraform-state-a1b2c3d4` |
| `TERRAFORM_LOCK_TABLE` | `franchise-management-terraform-locks` | `franchise-management-terraform-locks` |
| `MONGODB_PASSWORD` | `tu-password-super-seguro` | `MySecurePassword123!` |

## 🔄 Paso 3: Configurar Variables del Proyecto

### 3.1 Actualizar variables de Terraform (opcional)

Si quieres cambiar configuraciones, edita `terraform/variables.tf`:

```hcl
variable "project_name" {
  default = "tu-nombre-proyecto"  # Cambia si quieres
}

variable "aws_region" {
  default = "us-east-1"  # Cambia si prefieres otra región
}

variable "app_count" {
  default = 1  # Número inicial de contenedores
}
```

### 3.2 Verificar configuración de GitHub Actions

El archivo `.github/workflows/deploy.yml` está configurado para:
- ✅ Ejecutarse en push a `master`
- ✅ Correr tests primero
- ✅ Desplegar solo si los tests pasan
- ✅ Crear infraestructura con Terraform
- ✅ Construir y subir imagen Docker
- ✅ Actualizar servicio ECS

## 🚀 Paso 4: ¡Desplegar!

### 4.1 Primer Despliegue

```bash
# Asegúrate de estar en la rama master
git checkout master

# Hacer commit de todos los cambios
git add .
git commit -m "Initial deployment setup"

# Hacer push para disparar el despliegue
git push origin master
```

### 4.2 Monitorear el Despliegue

1. Ve a tu repositorio en GitHub
2. Click en la pestaña **Actions**
3. Verás el workflow "Deploy to AWS" ejecutándose
4. Click en el workflow para ver logs detallados

**El proceso toma aproximadamente 8-12 minutos:**
- 2-3 min: Tests
- 3-4 min: Terraform (crear infraestructura)
- 2-3 min: Docker build y push
- 2-3 min: Deploy ECS y verificación

### 4.3 Obtener URL de la Aplicación

Una vez completado, verás en el summary del GitHub Action:

```
🚀 Deployment Successful!

🌐 Application URLs
- Application: http://franchise-alb-123456789.us-east-1.elb.amazonaws.com
- API Base: http://franchise-alb-123456789.us-east-1.elb.amazonaws.com/api/v1
- Health Check: http://franchise-alb-123456789.us-east-1.elb.amazonaws.com/actuator/health
```

## 🧪 Paso 5: Probar la Aplicación

### 5.1 Health Check

```bash
curl http://TU-URL/actuator/health
```

### 5.2 Probar API

```bash
# Crear franquicia
curl -X POST http://TU-URL/api/v1/franchise \
  -H "Content-Type: application/json" \
  -d '{"name":"Mi Franquicia Test","id":""}'

# Usar el script de pruebas
chmod +x test-endpoints.sh
./test-endpoints.sh http://TU-URL/api/v1
```

## 🔧 Gestión y Mantenimiento

### Deployments Futuros

Cada vez que hagas push a `master`, se ejecutará automáticamente:

```bash
git add .
git commit -m "New feature: add product validation"
git push origin master
```

### Ver Logs en AWS

```bash
# Ver logs de la aplicación
aws logs tail /ecs/franchise-management --follow --region us-east-1

# Ver estado del servicio
aws ecs describe-services \
  --cluster franchise-management-cluster \
  --services franchise-management-service \
  --region us-east-1
```

### Escalar la Aplicación

```bash
# Aumentar número de contenedores
aws ecs update-service \
  --cluster franchise-management-cluster \
  --service franchise-management-service \
  --desired-count 3 \
  --region us-east-1
```

### Destruir Infraestructura

Si necesitas eliminar todo:

```bash
cd terraform
terraform destroy -auto-approve
```

## 🛟 Troubleshooting

### Errores Comunes

**1. "Access Denied" en GitHub Actions**
- Verificar que `AWS_ROLE_ARN` sea correcto
- Confirmar que el rol tiene permisos necesarios

**2. "Bucket does not exist"**
- Verificar `TERRAFORM_STATE_BUCKET` en secrets
- Asegurar que el bucket fue creado en el paso 1

**3. "Task failed to start"**
- Revisar logs en CloudWatch
- Verificar que la imagen Docker se construyó correctamente

**4. "Service unhealthy"**
- Verificar que el puerto 8080 esté expuesto
- Confirmar que `/actuator/health` responde

### Logs y Debugging

```bash
# Ver logs de GitHub Actions
# - Ve a Actions tab en GitHub
# - Click en el workflow fallido
# - Expandir secciones para ver detalles

# Ver logs de AWS
aws logs describe-log-groups --region us-east-1
aws logs tail /ecs/franchise-management --region us-east-1

# Ver estado de recursos Terraform
cd terraform
terraform show
terraform state list
```

## 📊 Métricas y Monitoreo

### CloudWatch Dashboards

Los logs se almacenan automáticamente en:
- **Log Group**: `/ecs/franchise-management`
- **Streams**: `mongodb` y `app`

### Auto Scaling

La aplicación incluye auto-scaling basado en:
- **CPU**: Escala cuando CPU > 70%
- **Memory**: Escala cuando memoria > 80%
- **Min containers**: 1
- **Max containers**: 3

## 💰 Costos Estimados

**Infraestructura AWS (us-east-1):**
- ECS Fargate (1 tarea): ~$25-35/mes
- Application Load Balancer: ~$20/mes
- ECR: ~$1-2/mes
- CloudWatch Logs: ~$2-5/mes
- **Total**: ~$48-62/mes

**Para reducir costos en desarrollo:**
- Usar `desired_count = 0` cuando no uses la app
- Implementar schedule para auto-stop en horas no laborales

---

## 🎉 ¡Listo!

Tu aplicación ahora está completamente automatizada:

✅ **CI/CD**: Push → Test → Deploy automático  
✅ **Infrastructure as Code**: Todo definido en Terraform  
✅ **Escalable**: Auto-scaling configurado  
✅ **Monitoreo**: Logs en CloudWatch  
✅ **Seguro**: Autenticación OIDC, sin credenciales hardcodeadas  
✅ **URL Pública**: Accesible desde cualquier parte del mundo  

**URL de tu aplicación**: Aparecerá en GitHub Actions después del primer despliegue exitoso!