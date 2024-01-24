-- Dump completed on 2023-12-29 10:19:14
-- MySQL dump 10.13  Distrib 8.0.32, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: chatgpt-assistant
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
-- Table structure for table `conversation`
--

DROP TABLE IF EXISTS `conversation`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conversation`
(
    `id`                bigint      NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`        datetime(6)            DEFAULT NULL COMMENT 'Created time',
    `last_modified_at`  datetime(6)            DEFAULT NULL COMMENT 'Last modified time',
    `choices`           int                    DEFAULT NULL COMMENT 'Chat completion choices to generate',
    `conversation_id`   varchar(64) NOT NULL COMMENT 'Conversation ID',
    `end_user`          varchar(255)           DEFAULT NULL COMMENT 'A unique identifier representing your end-user',
    `frequency_penalty` double                 DEFAULT NULL COMMENT 'Positive values penalize new tokens based on their existing frequency in the text so far',
    `logit_bias`        text COMMENT 'Modify the likelihood of specified tokens appearing in the completion.',
    `max_tokens`        int                    DEFAULT NULL COMMENT 'The maximum number of tokens to generate in the chat completion.',
    `model`             enum ('GPT3_5','GPT4') DEFAULT NULL COMMENT 'ID of the model to use.',
    `name`              varchar(255)           DEFAULT NULL COMMENT 'Conversation name',
    `owner_user_id`     varchar(64) NOT NULL COMMENT 'Owner User',
    `presence_penalty`  double                 DEFAULT NULL COMMENT 'Positive values penalize new tokens based on whether they appear in the text so far',
    `send_history`      bit(1)                 DEFAULT NULL COMMENT 'Whether to send the chat completion history API. (default true)',
    `stop`              text COMMENT 'Up to 4 sequences where the API will stop generating further tokens.',
    `system_message`    varchar(255)           DEFAULT NULL COMMENT 'System message of this conversation',
    `temperature`       double                 DEFAULT NULL COMMENT 'Sampling temperature to use',
    `topp`              double                 DEFAULT NULL COMMENT 'Alternative to sampling with temperature',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_conversation_id` (`conversation_id`),
    KEY `IDX_conversation_owner_user_id` (`owner_user_id`)
);
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `conversation_archive`
--

DROP TABLE IF EXISTS `conversation_archive`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conversation_archive`
(
    `id`                bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`        datetime(6)            DEFAULT NULL COMMENT 'Created time',
    `last_modified_at`  datetime(6)            DEFAULT NULL COMMENT 'Last modified time',
    `archive_id`        varchar(64)            DEFAULT NULL COMMENT 'Conversation Archive ID',
    `choices`           int                    DEFAULT NULL COMMENT 'Chat completion choices to generate',
    `conversation_id`   varchar(64)            DEFAULT NULL COMMENT 'Conversation ID',
    `end_user`          varchar(255)           DEFAULT NULL COMMENT 'A unique identifier representing your end-user',
    `frequency_penalty` double                 DEFAULT NULL COMMENT 'Positive values penalize new tokens based on their existing frequency in the text so far',
    `logit_bias`        text COMMENT 'Modify the likelihood of specified tokens appearing in the completion.',
    `max_tokens`        int                    DEFAULT NULL COMMENT 'The maximum number of tokens to generate in the chat completion.',
    `model`             enum ('GPT3_5','GPT4') DEFAULT NULL COMMENT 'ID of the model to use.',
    `name`              varchar(255)           DEFAULT NULL COMMENT 'Conversation name',
    `owner_user_id`     varchar(64)            DEFAULT NULL COMMENT 'Owner User',
    `presence_penalty`  double                 DEFAULT NULL COMMENT 'Positive values penalize new tokens based on whether they appear in the text so far',
    `send_history`      bit(1)                 DEFAULT NULL COMMENT 'Whether to send the chat completion history API. (default true)',
    `stop`              text COMMENT 'Up to 4 sequences where the API will stop generating further tokens.',
    `system_message`    varchar(255)           DEFAULT NULL COMMENT 'System message of this conversation',
    `temperature`       double                 DEFAULT NULL COMMENT 'Sampling temperature to use',
    `topp`              double                 DEFAULT NULL COMMENT 'Alternative to sampling with temperature',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_conversation_archive_id` (`archive_id`),
    KEY `IDX_conversation_archive_owner_user_id` (`owner_user_id`)
#     CONSTRAINT `conversation_archive_chk_1` CHECK (((`frequency_penalty` >= -(2)) and (`frequency_penalty` <= 2))),
#     CONSTRAINT `conversation_archive_chk_2` CHECK (((`presence_penalty` >= -(2)) and (`presence_penalty` <= 2))),
#     CONSTRAINT `conversation_archive_chk_3` CHECK (((`temperature` >= 0) and (`temperature` <= 2)))
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `conversation_message`
--

DROP TABLE IF EXISTS `conversation_message`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `conversation_message`
(
    `id`               bigint      NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`       datetime(6)                                      DEFAULT NULL COMMENT 'Created time',
    `last_modified_at` datetime(6)                                      DEFAULT NULL COMMENT 'Last modified time',
    `deleted`          bit(1)      NOT NULL,
    `context_size`     int                                              DEFAULT NULL COMMENT 'Context size(Sent history messages)',
    `conversation_id`  varchar(64)                                      DEFAULT NULL COMMENT 'Conversation ID',
    `error_info`       varchar(255)                                     DEFAULT NULL COMMENT 'Message sent error info',
    `message_content`  text COMMENT 'Message',
    `message_role`     enum ('SYSTEM','USER','ASSISTANT')               DEFAULT NULL COMMENT 'Message',
    `message_id`       varchar(64) NOT NULL COMMENT 'Message ID',
    `status`           enum ('Processing','Succeeded','Failed','Clear') DEFAULT NULL COMMENT 'Message status',
    `used_token`       int                                              DEFAULT NULL COMMENT 'Charged token',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_message_id` (`message_id`),
    KEY `IDX_conversation_id` (`conversation_id`)
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `token_account`
--

DROP TABLE IF EXISTS `token_account`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token_account`
(
    `id`                     bigint      NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`             datetime(6)            DEFAULT NULL COMMENT 'Created time',
    `last_modified_at`       datetime(6)            DEFAULT NULL COMMENT 'Last modified time',
    `account_id`             varchar(64) NOT NULL COMMENT 'Account ID',
    `day_token_usage`        bigint                 DEFAULT NULL COMMENT 'Today tokens usage',
    `hour_token_usage`       bigint                 DEFAULT NULL COMMENT 'Current hour tokens usage',
    `latest_token_usage`     bigint                 DEFAULT NULL COMMENT 'Latest token usage',
    `latest_token_used_time` datetime(6)            DEFAULT NULL COMMENT 'Latest token used time',
    `minute_token_usage`     bigint                 DEFAULT NULL COMMENT 'Current minute tokens usage',
    `model`                  enum ('GPT3_5','GPT4') DEFAULT NULL COMMENT 'Chat model',
    `month_token_usage`      bigint                 DEFAULT NULL COMMENT 'Current month tokens usage',
    `owner_user_id`          varchar(64) NOT NULL COMMENT 'Owner User',
    `paid_tokens`            bigint                 DEFAULT NULL COMMENT 'Total paid tokens',
    `total_tokens`           bigint                 DEFAULT NULL COMMENT 'Total tokens',
    `used_tokens`            bigint                 DEFAULT NULL COMMENT 'Total used tokens',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_account_id` (`account_id`),
    KEY `IDX_account_owner_user_id` (`owner_user_id`)
) DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `token_charge`
--

DROP TABLE IF EXISTS `token_charge`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `token_charge`
(
    `id`               bigint      NOT NULL AUTO_INCREMENT COMMENT 'Primary Key',
    `created_at`       datetime(6)            DEFAULT NULL COMMENT 'Created time',
    `last_modified_at` datetime(6)            DEFAULT NULL COMMENT 'Last modified time',
    `account_id`       varchar(64) NOT NULL COMMENT 'Account ID',
    `charge_id`        varchar(64) NOT NULL COMMENT 'Charge ID',
    `model`            enum ('GPT3_5','GPT4') DEFAULT NULL COMMENT 'Model',
    `paid`             bit(1)                 DEFAULT NULL COMMENT 'is tokens paid',
    `time`             datetime(6)            DEFAULT NULL COMMENT 'Charge Time',
    `tokens`           int                    DEFAULT NULL COMMENT 'Tokens',
    PRIMARY KEY (`id`),
    UNIQUE KEY `UK_token_charge_id` (`charge_id`),
    KEY `IDX_charge_account_id` (`account_id`),
    KEY `IDX_charge_time` (`time`)
) DEFAULT CHARSET = utf8mb4
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
