CREATE DATABASE IF NOT EXISTS easystatisticsDB;

USE easystatisticsDB;
CREATE TABLE IF NOT EXISTS `easystatisticsDB`.`users` (
  `userid` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `loggedin` BOOL NOT NULL,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC));

CREATE TABLE IF NOT EXISTS `easystatisticsDB`.`projects` (
  `userid` INT NOT NULL,
  `projectname` VARCHAR(45) NOT NULL,
  `projectstream` TEXT NOT NULL,
  `shared` BOOL NOT NULL,
  FOREIGN KEY(`userid`) REFERENCES `easystatisticsDB`.`users`(`userid`),
  UNIQUE INDEX `userid_projectname_UNIQUE` (`userid`, `projectname`))

ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;