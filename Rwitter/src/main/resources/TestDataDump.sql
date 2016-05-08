-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: rwitter
-- ------------------------------------------------------
-- Server version	5.7.12-log

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
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message` (
  `message_id` bigint(32) NOT NULL AUTO_INCREMENT,
  `content` varchar(512) NOT NULL,
  `creator_id` varchar(45) NOT NULL,
  `parent_id` bigint(32) DEFAULT NULL,
  PRIMARY KEY (`message_id`),
  KEY `mf1_idx` (`creator_id`),
  KEY `mf2_idx` (`parent_id`),
  CONSTRAINT `mf1` FOREIGN KEY (`creator_id`) REFERENCES `user` (`user_tag`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `mf2` FOREIGN KEY (`parent_id`) REFERENCES `message` (`message_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (1,'What a lovely day! @Lovely @Day','Bhel',NULL),(2,'What a lovely cake! @Lovely @Cake','Bhel',NULL),(3,'Cake is a Lie! @Cake @Lie','Alix',2),(4,'Hello World! @Hello @World','Chor',NULL),(5,'Hi There!','Chor',NULL);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message_keyword`
--

DROP TABLE IF EXISTS `message_keyword`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `message_keyword` (
  `message_id` bigint(32) NOT NULL,
  `keyword` varchar(32) NOT NULL,
  PRIMARY KEY (`message_id`,`keyword`),
  CONSTRAINT `mk1` FOREIGN KEY (`message_id`) REFERENCES `message` (`message_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message_keyword`
--

LOCK TABLES `message_keyword` WRITE;
/*!40000 ALTER TABLE `message_keyword` DISABLE KEYS */;
INSERT INTO `message_keyword` VALUES (1,'@Day'),(1,'@Lovely'),(2,'@Cake'),(2,'@Lovely'),(3,'@Cake'),(3,'@Lie'),(4,'@Hello'),(4,'@World');
/*!40000 ALTER TABLE `message_keyword` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_tag` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `password` varchar(512) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `age` int(32) NOT NULL,
  `email` varchar(45) NOT NULL,
  PRIMARY KEY (`user_tag`),
  UNIQUE KEY `userTag_UNIQUE` (`user_tag`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('Alix','Alice','Alice','A',32,'A'),('Bhel','Bob','Bob','B',25,'B'),('Chor','Claire','Claire','C',28,'C');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_followed`
--

DROP TABLE IF EXISTS `user_followed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_followed` (
  `follower_id` varchar(45) NOT NULL,
  `followed_id` varchar(45) NOT NULL,
  PRIMARY KEY (`follower_id`,`followed_id`),
  KEY `uf2_idx` (`followed_id`),
  CONSTRAINT `uf1` FOREIGN KEY (`follower_id`) REFERENCES `user` (`user_tag`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `uf2` FOREIGN KEY (`followed_id`) REFERENCES `user` (`user_tag`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_followed`
--

LOCK TABLES `user_followed` WRITE;
/*!40000 ALTER TABLE `user_followed` DISABLE KEYS */;
INSERT INTO `user_followed` VALUES ('Bhel','Alix'),('Alix','Bhel'),('Bhel','Chor');
/*!40000 ALTER TABLE `user_followed` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-05-08 21:48:10
