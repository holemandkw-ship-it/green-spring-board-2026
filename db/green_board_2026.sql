-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: green_board_2026
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `green_board_2026`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `green_board_2026` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_520_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `green_board_2026`;

--
-- Table structure for table `boards`
--

DROP TABLE IF EXISTS `boards`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `boards` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_520_ci DEFAULT NULL,
  `content` text COLLATE utf8mb4_unicode_520_ci,
  `hits` int NOT NULL DEFAULT '0',
  `updated_datetime` datetime DEFAULT NULL,
  `created_datetime` datetime DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_author_1` (`user_id`),
  CONSTRAINT `fk_author_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `boards`
--

LOCK TABLES `boards` WRITE;
/*!40000 ALTER TABLE `boards` DISABLE KEYS */;
INSERT INTO `boards` VALUES (1,'제목1','내용1',6,'2026-10-02 17:08:40','2026-10-02 15:48:58',NULL),(2,'asdfdd','asdfdd',1,'2026-10-02 17:08:40','2026-10-02 15:48:58',NULL),(3,'제목3','내용3',0,'2026-10-02 17:08:40','2026-10-02 15:48:58',NULL),(11,'금요일','Fridat Night',0,'2026-10-02 17:08:40','2026-10-02 16:28:08',NULL),(12,'작성자 테스트','로그인 유저 연결 테스트',0,NULL,NULL,7);
/*!40000 ALTER TABLE `boards` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `nickname` varchar(50) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `password` varchar(200) COLLATE utf8mb4_unicode_520_ci NOT NULL,
  `created_datetime` datetime DEFAULT NULL,
  `updated_datetime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_520_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'조그린','hello@green.com','hello1234','2026-10-02 15:41:47','2026-10-02 15:48:58'),(2,'조그린','green1@test.com','$2a$10$.ziLf5OCxB4DY5gIeYe9f.dBM7GJMhTHzhbK7NApbVF2rg8O6.f.K','2026-10-02 15:41:47','2026-10-02 15:48:58'),(3,'그그그','green2@test.com','$2a$10$FGxOAZUGEw5s66HfXHn51es7indZD2kmnhSq9acVnfqekxhQBXuXm','2026-10-02 15:41:47','2026-10-02 15:48:58'),(5,'수정후','hello1@green.com','$2a$10$ui2OTrBMEUC7pLILJjrxBOvgmhjeue5.mH8V8oPJceRC2RObK1bPW','2026-10-02 15:41:47','2026-10-02 15:48:58'),(6,'시간테스트111','1234@test.com','$2a$10$j5pPk8hTyfKpvb084wuoaeOWn3/ocRHG6bCzF0hIiGZY5bi9wOPUa','2026-10-02 16:32:48','2026-10-02 16:33:27'),(7,'테스트','test@test.com','$2a$10$Z.4in2a4keWLGEw1RxEOk.H/BRM4PjVyCprrmOwVe.3PcZGDz.R.6','2026-10-06 10:22:55','2026-10-06 10:22:55');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-06 10:32:11
