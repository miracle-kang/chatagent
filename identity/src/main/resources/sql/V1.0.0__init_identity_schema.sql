-- MySQL dump 10.13  Distrib 8.0.32, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: chatgpt-identity
-- ------------------------------------------------------
-- Server version	8.0.27

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `administrator`
--

DROP TABLE IF EXISTS `administrator`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrator`
(
    `id`                 bigint      NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`         datetime(6)  DEFAULT NULL COMMENT 'Created time',
    `last_modified_at`   datetime(6)  DEFAULT NULL COMMENT 'Last modified time',
    `administrator_id`   varchar(64) NOT NULL COMMENT 'Admin ID',
    `operator_user_id`   varchar(64)  DEFAULT NULL COMMENT 'Operator',
    `operator_user_ip`   varchar(64)  DEFAULT NULL COMMENT 'Operator',
    `operator_user_name` varchar(128) DEFAULT NULL COMMENT 'Operator',
    `user_id`            bigint       DEFAULT NULL COMMENT 'Owner User',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_administrator_id` (`administrator_id`),
    UNIQUE KEY `UK_jf8hwde5kvshk44s9pllcqid9` (`user_id`),
    CONSTRAINT `FK8pj42e16mnd26od64v1rvb9a4` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `administrator_role`
--

DROP TABLE IF EXISTS `administrator_role`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administrator_role`
(
    `administrator_id` bigint      NOT NULL,
    `role_id`          varchar(64) NOT NULL COMMENT 'Role ID',
    `role_name`        varchar(255) DEFAULT NULL COMMENT 'Role name',
    KEY `FKcqxqjeoiry5g99imt7nhno60v` (`administrator_id`),
    CONSTRAINT `FKcqxqjeoiry5g99imt7nhno60v` FOREIGN KEY (`administrator_id`) REFERENCES `administrator` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `customer`
--

DROP TABLE IF EXISTS `customer`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer`
(
    `id`                 bigint      NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`         datetime(6)  DEFAULT NULL COMMENT 'Created time',
    `last_modified_at`   datetime(6)  DEFAULT NULL COMMENT 'Last modified time',
    `customer_id`        varchar(64) NOT NULL COMMENT 'Customer ID',
    `operator_user_id`   varchar(64)  DEFAULT NULL COMMENT 'Operator',
    `operator_user_ip`   varchar(64)  DEFAULT NULL COMMENT 'Operator',
    `operator_user_name` varchar(128) DEFAULT NULL COMMENT 'Operator',
    `user_id`            bigint       DEFAULT NULL COMMENT 'Owner User',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_customer_id` (`customer_id`),
    UNIQUE KEY `UK_j7ja2xvrxudhvssosd4nu1o92` (`user_id`),
    CONSTRAINT `FK3qp2ocgwjbbsu3mbe43a4n9ro` FOREIGN KEY (`user_id`) REFERENCES `user_base` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `file`
--

DROP TABLE IF EXISTS `file`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file`
(
    `id`                 bigint      NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`         datetime(6)                                                              DEFAULT NULL COMMENT 'Created time',
    `last_modified_at`   datetime(6)                                                              DEFAULT NULL COMMENT 'Last modified time',
    `file_id`            varchar(64) NOT NULL COMMENT 'File ID',
    `md5`                varchar(255)                                                             DEFAULT NULL COMMENT 'File MD5',
    `mime_type`          varchar(255)                                                             DEFAULT NULL COMMENT 'File MIME Type',
    `name`               varchar(255)                                                             DEFAULT NULL COMMENT 'File Name',
    `operator_user_id`   varchar(64)                                                              DEFAULT NULL,
    `operator_user_ip`   varchar(64)                                                              DEFAULT NULL,
    `operator_user_name` varchar(128)                                                             DEFAULT NULL,
    `owner_id`           varchar(64)                                                              DEFAULT NULL COMMENT 'File Owner',
    `path`               varchar(255)                                                             DEFAULT NULL COMMENT 'File Path/Bucket',
    `reference_count`    int                                                                      DEFAULT NULL COMMENT 'File Reference Count',
    `scope`              enum ('PUBLIC','PRIVATE')                                                DEFAULT NULL COMMENT 'File Scope',
    `size`               bigint                                                                   DEFAULT NULL COMMENT 'File size',
    `status`             enum ('CREATED','UPLOADED','BOUNDED','RELEASED','DELETED','UNREACHABLE') DEFAULT NULL COMMENT 'File Status',
    `type`               enum ('AVATAR','IMAGE')                                                  DEFAULT NULL COMMENT 'File Type',
    PRIMARY KEY (`id`),
    UNIQUE KEY `IDX_file_id` (`file_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `invitation`
--

DROP TABLE IF EXISTS `invitation`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invitation`
(
    `id`                bigint      NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`        datetime(6)                             DEFAULT NULL COMMENT 'Created time',
    `last_modified_at`  datetime(6)                             DEFAULT NULL COMMENT 'Last modified time',
    `invitation_id`     varchar(64) NOT NULL COMMENT 'Invitation ID',
    `invitation_status` enum ('AVAILABLE','ACCEPTED','INVALID') DEFAULT NULL COMMENT 'Invitation Status',
    `invitation_type`   enum ('ONE_TIME','REUSABLE')            DEFAULT NULL COMMENT 'Invitation Type',
    `invite_code`       varchar(32)                             DEFAULT NULL COMMENT 'Invite code',
    `inviter_id`        varchar(64) NOT NULL COMMENT 'Inviter user ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_invitation_id` (`invitation_id`),
    UNIQUE KEY `IDX_invite_code` (`invite_code`),
    KEY `IDX_inviter_id` (`inviter_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role`
(
    `id`               bigint      NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`       datetime(6)  DEFAULT NULL COMMENT 'Created time',
    `last_modified_at` datetime(6)  DEFAULT NULL COMMENT 'Last modified time',
    `description`      varchar(255) DEFAULT NULL COMMENT 'Role description',
    `name`             varchar(255) DEFAULT NULL COMMENT 'Role name',
    `role_id`          varchar(64) NOT NULL COMMENT 'Role ID',
    PRIMARY KEY (`id`),
    KEY `UK_role_id` (`role_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role_permission`
(
    `role_id`          bigint                                   NOT NULL,
    `operation_action` enum ('CREATE','UPDATE','DELETE','READ') NOT NULL COMMENT 'Operation Action',
    `operation_key`    varchar(128)                             NOT NULL COMMENT 'Operation Key',
    PRIMARY KEY (`role_id`, `operation_action`, `operation_key`),
    CONSTRAINT `FKa6jx8n8xkesmjmv6jqug6bg68` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `system_profile`
--

DROP TABLE IF EXISTS `system_profile`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `system_profile`
(
    `id`                                bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`                        datetime(6)  DEFAULT NULL COMMENT 'Created time',
    `last_modified_at`                  datetime(6)  DEFAULT NULL COMMENT 'Last modified time',
    `announcement`                      text COMMENT 'announcement',
    `name`                              varchar(255) DEFAULT NULL COMMENT 'Name',
    `wechat_groupqrdark_image_file_id`  varchar(64)  DEFAULT NULL COMMENT 'Wechat Group QR dark image File',
    `wechat_groupqrlight_image_file_id` varchar(64)  DEFAULT NULL COMMENT 'Wechat Group QR light image File',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_base`
--

DROP TABLE IF EXISTS `user_base`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_base`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`       datetime(6)                       DEFAULT NULL COMMENT 'Created time',
    `last_modified_at` datetime(6)                       DEFAULT NULL COMMENT 'Last modified time',
    `disabled`         bit(1)                            DEFAULT NULL COMMENT 'User enabled',
    `email_address`    varchar(255)                      DEFAULT NULL COMMENT 'User Email',
    `invitation_id`    varchar(64)                       DEFAULT NULL COMMENT 'Invitation ID',
    `inviter_id`       varchar(64)                       DEFAULT NULL COMMENT 'Inviter user',
    `latest_login`     datetime(6)                       DEFAULT NULL COMMENT 'User latest login time',
    `password`         varchar(64)                       DEFAULT NULL COMMENT 'User password',
    `phone_area`       varchar(5)                        DEFAULT NULL COMMENT 'User Phone',
    `phone_number`     varchar(255)                      DEFAULT NULL COMMENT 'User Phone',
    `user_id`          varchar(255) NOT NULL COMMENT 'User ID',
    `user_type`        enum ('Administrator','Customer') DEFAULT NULL COMMENT 'User type',
    `username`         varchar(32)                       DEFAULT NULL COMMENT 'Username(uid)',
    `profile_id`       bigint                            DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_user_id` (`user_id`),
    UNIQUE KEY `UK_username` (`username`),
    UNIQUE KEY `UK_email_address` (`email_address`),
    UNIQUE KEY `UK_phone_number` (`phone_number`),
    UNIQUE KEY `UK_4t2s0udcoq9xpfr63k4com9it` (`profile_id`),
    KEY `IDX_invitation_id` (`invitation_id`),
    CONSTRAINT `FKbpjrrcslf6n2wawj545p0qkox` FOREIGN KEY (`profile_id`) REFERENCES `user_profile` (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_profile`
--

DROP TABLE IF EXISTS `user_profile`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_profile`
(
    `id`               bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`       datetime(6)            DEFAULT NULL COMMENT 'Created time',
    `last_modified_at` datetime(6)            DEFAULT NULL COMMENT 'Last modified time',
    `avatar_url`       varchar(512)           DEFAULT NULL COMMENT 'User avatar url',
    `description`      varchar(512)           DEFAULT NULL COMMENT 'User Description',
    `gender`           enum ('Male','Female') DEFAULT NULL COMMENT 'User Gender',
    `nickname`         varchar(255)           DEFAULT NULL COMMENT 'User Theme',
    `settings`         text COMMENT 'Custom Settings',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_registration`
--

DROP TABLE IF EXISTS `user_registration`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_registration`
(
    `id`                bigint       NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`        datetime(6)              DEFAULT NULL COMMENT 'Created time',
    `last_modified_at`  datetime(6)              DEFAULT NULL COMMENT 'Last modified time',
    `registration_id`   varchar(255) NOT NULL COMMENT 'User ID',
    `registration_type` enum ('Github','Google') DEFAULT NULL COMMENT 'User ID',
    `registration_info` text COMMENT 'Registration info',
    `user_id`           varchar(255)             DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_registration_id` (`registration_id`, `registration_type`),
    KEY `IDX_registration_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;