/*
Navicat MySQL Data Transfer

Source Server         : yy_local
Source Server Version : 50720
Source Host           : localhost:3306
Source Database       : excledata

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2019-06-10 18:12:30
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('1', '东东', '151212144');
INSERT INTO `user` VALUES ('2', '滔滔', '444');
INSERT INTO `user` VALUES ('3', '妹妹', '44');
INSERT INTO `user` VALUES ('4', '南岸', '555');
INSERT INTO `user` VALUES ('5', '北玻', '666');
INSERT INTO `user` VALUES ('6', '小明', '555555');
INSERT INTO `user` VALUES ('7', '小美', '666666');
INSERT INTO `user` VALUES ('8', '小P', '666666');
