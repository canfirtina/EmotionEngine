-- MySQL dump 10.13  Distrib 5.6.23, for Win64 (x86_64)
--
-- Host: 104.131.97.74    Database: emotion_db
-- ------------------------------------------------------
-- Server version	5.5.43-0ubuntu0.14.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `app_state`
--

DROP TABLE IF EXISTS `app_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app_state` (
  `state_name` varchar(20) NOT NULL,
  `state_value` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`state_name`),
  UNIQUE KEY `UQ_state_name_state_value` (`state_name`,`state_value`),
  KEY `fk_current_user_idx` (`state_value`),
  CONSTRAINT `fk_current_user` FOREIGN KEY (`state_value`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_state`
--

LOCK TABLES `app_state` WRITE;
/*!40000 ALTER TABLE `app_state` DISABLE KEYS */;
INSERT INTO `app_state` VALUES ('current_user','can@can.com');
/*!40000 ALTER TABLE `app_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `enabled_sensors`
--

DROP TABLE IF EXISTS `enabled_sensors`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `enabled_sensors` (
  `email` varchar(100) DEFAULT NULL,
  `sensor` varchar(25) DEFAULT NULL,
  `is_enabled` tinyint(1) DEFAULT NULL,
  UNIQUE KEY `UQ_email_sensor` (`email`,`sensor`),
  KEY `enabled_sensors_fk0` (`email`),
  CONSTRAINT `enabled_sensors_fk0` FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `enabled_sensors`
--

LOCK TABLES `enabled_sensors` WRITE;
/*!40000 ALTER TABLE `enabled_sensors` DISABLE KEYS */;
/*!40000 ALTER TABLE `enabled_sensors` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `games_played`
--

DROP TABLE IF EXISTS `games_played`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `games_played` (
  `email` varchar(100) DEFAULT NULL,
  `game` varchar(50) DEFAULT NULL,
  `time` int(11) DEFAULT NULL,
  UNIQUE KEY `UQ_email_game` (`email`,`game`),
  KEY `games_played_fk0` (`email`),
  CONSTRAINT `games_played_fk0` FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `games_played`
--

LOCK TABLES `games_played` WRITE;
/*!40000 ALTER TABLE `games_played` DISABLE KEYS */;
/*!40000 ALTER TABLE `games_played` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `play_count`
--

DROP TABLE IF EXISTS `play_count`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `play_count` (
  `email` varchar(100) DEFAULT NULL,
  `sensor` varchar(25) DEFAULT NULL,
  `tutorial` varchar(25) DEFAULT NULL,
  `count` int(11) DEFAULT NULL,
  UNIQUE KEY `UQ_email_sensor_tutorial` (`email`,`sensor`,`tutorial`),
  KEY `play_count_fk0` (`email`),
  CONSTRAINT `play_count_fk0` FOREIGN KEY (`email`) REFERENCES `users` (`email`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `play_count`
--

LOCK TABLES `play_count` WRITE;
/*!40000 ALTER TABLE `play_count` DISABLE KEYS */;
/*!40000 ALTER TABLE `play_count` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `email` varchar(100) NOT NULL,
  `password` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('ali@ali.com','f5bbc8de146c67b44babbf4e6584cc0'),('can@can.com','b4bed6bc05b52277ef1341dfebca4b');
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

-- Dump completed on 2015-05-01 21:17:02
