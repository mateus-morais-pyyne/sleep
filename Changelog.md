# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and this project adheres to [Semantic Versioning](https://semver.org/).

## [0.0.3]
### Changed
- Upgraded Spring Boot and related dependencies to version 3.2.7 for full Jakarta EE compatibility.
- Updated `build.gradle`
- Modified `Dockerfile` to support updated build and runtime environment.

### Added
- **Controllers:**
    - `SleepLogController.kt`: REST controller and DTO's for managing sleep logs.
    - `UserController.kt`: REST controller and DTO's for user-related operations.
- **Repositories:**
    - `SleepLogRepository.kt`: Spring Data JPA repository for sleep logs.
    - `UserRepository.kt`: Spring Data JPA repository for users.
- **Services:**
    - `SleepLogService.kt`: Service for managing sleep logs.
    - `UserService.kt`: Service for user-related operations.

## [0.0.2]
### Added
- Introduced `User` entity to manage user data.
- Introduced `SleepLog` class to manage sleep log data, including `bedTime`, `wakeUpTime`, and `morningFeeling`.
- Created migrations to update database with new entities.

## [0.0.1] - YYYY-MM-DD
### Added
- Initial release of the project.
- Basic structure and configurations set up (e.g., foundational files, dependencies).