


/* Inserting data of nodes into Table Nodes*/
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();
INSERT INTO Nodes VALUES();

/*
Data for above:

3600,700,1
3700,700,2
3800,800,3
3900,900,4
3675,1150,5
3700,1050,6
3650,1050,7
3400,1200,8
3650,1300,9
3650,1400,10
3650,1350,11
3600,1400,12
3500,1500,13
3400,1700,14
3500,1900,15
1700,2100,16
1900,2000,17
2300,2200,18
2400,2200,19
3700,1300,20
3900,2200,21
 */

/* Inserting data of edges into Table Edges*/
INSERT INTO Edges VALUES(1,9);
INSERT INTO Edges VALUES(9,1);
INSERT INTO Edges VALUES(8,5);
INSERT INTO Edges VALUES(5,8);
INSERT INTO Edges VALUES(7,12);
INSERT INTO Edges VALUES(12,7);
INSERT INTO Edges VALUES(16,3);
INSERT INTO Edges VALUES(3,16);
INSERT INTO Edges VALUES(20,2);
INSERT INTO Edges VALUES(2,20);
INSERT INTO Edges VALUES(13,21);
INSERT INTO Edges VALUES(21,13);

/* Inserting data of rooms into Table Rooms */
INSERT INTO Rooms VALUES("4A",1,"Storage of Patients");
INSERT INTO Rooms VALUES("4B",2,"Storage of Patients");
INSERT INTO Rooms VALUES("4C",3,"Storage of Patients");
INSERT INTO Rooms VALUES("4D",4,"Storage of Patients");
INSERT INTO Rooms VALUES("Atrium Ele",5,"Elevators Room");
INSERT INTO Rooms VALUES("4RestroomAUnisex",6,"A bathroom");
INSERT INTO Rooms VALUES("4RestroomBUnisex",7,"A bathroom");
INSERT INTO Rooms VALUES("4E",8,"Storage of Patients");
INSERT INTO Rooms VALUES("4L",9,"Storage of Patients");
INSERT INTO Rooms VALUES("4K",10,"Storage of Patients");
INSERT INTO Rooms VALUES("4G",11,"Storage of Patients");
INSERT INTO Rooms VALUES("4H",12,"Storage of Patients");
INSERT INTO Rooms VALUES("4I",13,"Storage of Patients");
INSERT INTO Rooms VALUES("4J",14,"Storage of Patients");
INSERT INTO Rooms VALUES("Hillside Ele",15,"Elevators Room");
INSERT INTO Rooms VALUES("TymanConfCent",16,"Tyman Conference Center");
INSERT INTO Rooms VALUES("4N",17,"Maintenance Closet");
INSERT INTO Rooms VALUES("4RestroomMale",18,"Male Bathroom");
INSERT INTO Rooms VALUES("4RestroomFemale",19,"Female Bathroom");
INSERT INTO Rooms VALUES("4F",20,"Storage of Patients");
INSERT INTO Rooms VALUES("4S",21,Storage of "Patients");

/* Inserting data of employee into Table Employee */
INSERT INTO Employees VALUES();
INSERT INTO Employees VALUES();
INSERT INTO Employees VALUES();
INSERT INTO Employees VALUES();
INSERT INTO Employees VALUES();
INSERT INTO Employees VALUES();
INSERT INTO Employees VALUES();
INSERT INTO Employees VALUES();
INSERT INTO Employees VALUES();
INSERT INTO Employees VALUES();
INSERT INTO Employees VALUES();
INSERT INTO Employees VALUES();

/*
Data for above:

Carla,Green,1,Dr.
Silas,Odoom,2,Dr.
Peter,Wong,3,Dr.
Willis,Smith,4,Dr.
Jess,Johnson,5,NURSE
Bill,Willis,6,Dr.
Vlad,Jones,7,Dr.
Oscar,Lee,8,Dr.
Edward,Wilson,9,NURSE
Louis,Moore,10,NURSE
Dean,Thomas,11,Dr.
Albert,Young,12,Dr.
 */

/* Inserting data of employeeRooms into table EmployeeRooms */
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();
INSERT INTO Employees Values();

/*
Data for above:

4A,1
4B,2
4C,3
4D,4
Atrium Ele,
4RestroomAUnisex,
4RestroomAUnisex,
4E,8
4L,9
4K,10
4G,11
4H,12
4I,5
4J,6
Hillside Ele,
TymanConfcent,5
4N,
4RestroomMale,
4RestroomFemale,
4F,7
4S,3
 */