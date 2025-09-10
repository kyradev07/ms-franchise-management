# Backend configuration
# This file configures the Terraform backend to store state in S3
# The backend configuration will be provided via GitHub Actions

terraform {
  backend "s3" {
    # These values will be provided via terraform init -backend-config
    # bucket         = "your-terraform-state-bucket"
    # key            = "franchise-management/terraform.tfstate"
    # region         = "us-east-1"
    # encrypt        = true
    # dynamodb_table = "terraform-state-lock"
  }
}