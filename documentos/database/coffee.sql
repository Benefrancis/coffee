--------------------------------------------------------
--  Arquivo criado - sábado-setembro-24-2022   
--------------------------------------------------------
DROP TABLE "DDD_COFFEE";
--------------------------------------------------------
--  DDL for Table DDD_COFFEE
--------------------------------------------------------

  CREATE TABLE "DDD_COFFEE" 
   (	"NAME" VARCHAR2(40 BYTE), 
	"PRICE" NUMBER(19,2), 
	"ID" NUMBER(19,0)
   )  ;
REM INSERTING into DDD_COFFEE
SET DEFINE OFF;
Insert into DDD_COFFEE (NAME,PRICE,ID) values ('Coffee-A','2,75','1');
Insert into DDD_COFFEE (NAME,PRICE,ID) values ('Coffee-B','3,8','2');
Insert into DDD_COFFEE (NAME,PRICE,ID) values ('Coffee-C','3,25','3');
Insert into DDD_COFFEE (NAME,PRICE,ID) values ('Coffee-D','9,52','4');
