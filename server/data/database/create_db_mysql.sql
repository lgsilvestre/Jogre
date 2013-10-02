-- ===============================================================
-- 
-- Script file for creating a jogre database for storing server 
-- persistent data for HSQLDB database.
-- 
-- Author: Bob Marks
-- Date:   2nd March 2006
-- 
-- NOTE: This is different from hsqldb as we drop the whole 
-- database.
--
-- ===============================================================

-- 1) drop database (if they exist)

drop database if exists jogre_mysql;

-- 2) create database

create database jogre_mysql;

-- 3) Select this as default database

use jogre_mysql;

-- 4) Create tables

create table game_info (id mediumint not null auto_increment, game_key char(20), players char (255), results char (50), start_time datetime, end_time datetime, history char (255), score char (255), primary key (id));
create table game_summary (game_key char(20), username char (20), rating integer, wins integer, loses integer, draws integer, streak integer, primary key (game_key, username));
create table snap_shot (game_key char(20), num_of_users integer, num_of_tables integer, primary key (game_key));
create table user (username char(20), password char (20), security_question integer, security_answer char (50), year_of_birth char (4), email char (100), receive_newsletter char (1), primary key (username));


-- 5) Insert dummey data into tables to get started

insert into user (username, password, security_question, security_answer, year_of_birth, email, receive_newsletter) values ('bob', 'bob123', 0, '', 1999, '', 'n'); 
insert into user (username, password, security_question, security_answer, year_of_birth, email, receive_newsletter) values ('dave', 'dave123', 0, '', 1999, '', 'n'); 
insert into user (username, password, security_question, security_answer, year_of_birth, email, receive_newsletter) values ('john', 'john123', 0, '', 1999, '', 'n');
insert into user (username, password, security_question, security_answer, year_of_birth, email, receive_newsletter) values ('sharon', 'sharon123', 9, '', 1999, '', 'n');
-- 6) Add MySQL priviledges

grant all privileges on jogre_mysql.* to admin@localhost identified BY 'admin'