-- Dump completed on 2023-12-29 10:19:14
-- MySQL dump 10.13  Distrib 8.0.32, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: chatgpt-subscription
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
-- Table structure for table `subscription`
--

DROP TABLE IF EXISTS `subscription`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscription`
(
    `id`                     bigint                                     NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`             datetime(6)  DEFAULT NULL COMMENT 'Created time',
    `last_modified_at`       datetime(6)  DEFAULT NULL COMMENT 'Last modified time',
    `chat_models`            tinytext COMMENT 'Chat models could be used',
    `disabled`               bit(1)       DEFAULT NULL COMMENT 'Disabled',
    `level`                  enum ('ORDINARY','ADVANCED')               NOT NULL COMMENT 'Subscription level',
    `max_tokens_per_day`     bigint       DEFAULT NULL COMMENT 'Max tokens per day',
    `max_tokens_per_month`   bigint       DEFAULT NULL COMMENT 'Max tokens per month',
    `max_tokens_per_request` bigint       DEFAULT NULL COMMENT 'Max tokens per request',
    `name`                   varchar(255) DEFAULT NULL COMMENT 'Subscription name',
    `operator_user_id`       varchar(64)  DEFAULT NULL COMMENT 'Operator user',
    `operator_user_ip`       varchar(64)  DEFAULT NULL COMMENT 'Operator user',
    `operator_user_name`     varchar(128) DEFAULT NULL COMMENT 'Operator user',
    `subscription_id`        varchar(64)                                NOT NULL COMMENT 'Subscription ID',
    `type`                   enum ('DAILY','WEEKLY','MONTHLY','YEARLY') NOT NULL COMMENT 'Subscription type',
    PRIMARY KEY (`id`),
    KEY `UK_subscription_id` (`subscription_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user_equity`
--

DROP TABLE IF EXISTS `user_equity`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_equity`
(
    `id`                 bigint                NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`         datetime(6)    DEFAULT NULL COMMENT 'Created time',
    `last_modified_at`   datetime(6)    DEFAULT NULL COMMENT 'Last modified time',
    `effective_time`     datetime(6)    DEFAULT NULL COMMENT 'Effective time',
    `equity_id`          varchar(64)           NOT NULL COMMENT 'Equity ID',
    `equity_type`        enum ('Subscription') NOT NULL COMMENT 'Equity ID',
    `equity_name`        varchar(255)   DEFAULT NULL COMMENT 'Equity name',
    `expires_time`       datetime(6)    DEFAULT NULL COMMENT 'Expires time',
    `operator_user_id`   varchar(64)    DEFAULT NULL COMMENT 'Operator',
    `operator_user_ip`   varchar(64)    DEFAULT NULL COMMENT 'Operator',
    `operator_user_name` varchar(128)   DEFAULT NULL COMMENT 'Operator',
    `owner_user_id`      varchar(64)           NOT NULL COMMENT 'User ID',
    `quantity`           decimal(38, 2) DEFAULT NULL COMMENT 'Equity quantity',
    `unit`               varchar(255)   DEFAULT NULL COMMENT 'Equity uint',
    `user_equity_id`     varchar(64)           NOT NULL COMMENT 'User equity ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_user_equity_id` (`user_equity_id`),
    KEY `IDX_equity_owner_user_id` (`owner_user_id`),
    KEY `IDX_equity_id` (`equity_id`, `equity_type`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 2
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
