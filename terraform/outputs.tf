output "ecr_repository_url" {
  description = "URL of the ECR repository"
  value       = aws_ecr_repository.app.repository_url
}

output "load_balancer_dns" {
  description = "DNS name of the load balancer"
  value       = aws_lb.main.dns_name
}

output "application_url" {
  description = "Public URL of the application"
  value       = "http://${aws_lb.main.dns_name}"
}

output "api_base_url" {
  description = "Base URL for API endpoints"
  value       = "http://${aws_lb.main.dns_name}/api/v1"
}

output "health_check_url" {
  description = "Health check endpoint URL"
  value       = "http://${aws_lb.main.dns_name}/actuator/health"
}

output "ecs_cluster_name" {
  description = "Name of the ECS cluster"
  value       = aws_ecs_cluster.main.name
}

output "ecs_service_name" {
  description = "Name of the ECS app service"
  value       = aws_ecs_service.app.name
}

output "ecs_mongodb_service_name" {
  description = "Name of the ECS MongoDB service"
  value       = aws_ecs_service.mongodb.name
}

output "cloudwatch_log_group_app" {
  description = "CloudWatch log group name for application"
  value       = aws_cloudwatch_log_group.app.name
}

output "cloudwatch_log_group_mongodb" {
  description = "CloudWatch log group name for MongoDB"
  value       = aws_cloudwatch_log_group.mongodb.name
}

output "efs_file_system_id" {
  description = "ID of the EFS file system for MongoDB"
  value       = aws_efs_file_system.mongodb.id
}

output "vpc_id" {
  description = "ID of the VPC"
  value       = aws_vpc.main.id
}

output "public_subnet_ids" {
  description = "IDs of the public subnets"
  value       = aws_subnet.public[*].id
}

output "security_group_alb_id" {
  description = "ID of the ALB security group"
  value       = aws_security_group.alb.id
}

output "security_group_ecs_id" {
  description = "ID of the ECS security group"
  value       = aws_security_group.ecs.id
}