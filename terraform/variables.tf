variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Name of the project"
  type        = string
  default     = "franchise-management"
}

variable "environment" {
  description = "Environment (dev, staging, prod)"
  type        = string
  default     = "prod"
}

variable "github_repository" {
  description = "GitHub repository name"
  type        = string
  default     = "ms-franchise-management"
}

variable "vpc_cidr" {
  description = "CIDR block for VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "app_cpu" {
  description = "Fargate instance CPU units to provision (1 vCPU = 1024 CPU units)"
  type        = number
  default     = 1024
}

variable "app_memory" {
  description = "Fargate instance memory to provision (in MiB)"
  type        = number
  default     = 2048
}

variable "mongodb_cpu" {
  description = "MongoDB Fargate instance CPU units to provision (1 vCPU = 1024 CPU units)"
  type        = number
  default     = 1024
}

variable "mongodb_memory" {
  description = "MongoDB Fargate instance memory to provision (in MiB)"
  type        = number
  default     = 2048
}

variable "app_count" {
  description = "Number of docker containers to run"
  type        = number
  default     = 1
}

variable "min_capacity" {
  description = "Minimum number of containers for auto-scaling"
  type        = number
  default     = 1
}

variable "max_capacity" {
  description = "Maximum number of containers for auto-scaling"
  type        = number
  default     = 3
}

variable "cpu_target_value" {
  description = "Target CPU utilization for auto-scaling"
  type        = number
  default     = 70.0
}

variable "memory_target_value" {
  description = "Target memory utilization for auto-scaling"
  type        = number
  default     = 80.0
}

variable "log_retention_days" {
  description = "CloudWatch log retention in days"
  type        = number
  default     = 7
}

variable "mongodb_username" {
  description = "MongoDB username"
  type        = string
  default     = "root"
  sensitive   = true
}

variable "mongodb_password" {
  description = "MongoDB password"
  type        = string
  default     = "franchise-secure-password-2024"
  sensitive   = true
}

variable "mongodb_database" {
  description = "MongoDB database name"
  type        = string
  default     = "franchise_db"
}