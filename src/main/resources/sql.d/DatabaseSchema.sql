/**
SQL conventions for this project:

Style conventions:
 - Use newlines liberally.
 - Commas go at the beginning of the line.
 - Indent with 4 spaces, or (2 spaces, a comma, and 1 more space)
 - Align types in table declarations.

Naming conventions:
 - table and view names are PascalCase
 - columns are camelCase
 - constraint names look like TableName_then_snake_case
 - trigger names should be descriptive snake_case
 - functions use Capital_Underscore_Case
   - This is to discourage functions.

Style convention for table creation:
  General patterns:
    - types are lowercase
    - statements are in ALL CAPS (e.g. CREATE TABLE)
    - primary keys come before other columns
    - lowercase patterns (see below) follow uppercase patterns
  Capitalized:
    - PRIMARY KEY
    - LIKE, REGEXP_LIKE
    - NOT NULL
  Lowercase:
    - constraint
    - foreign key ... references
    - check
    - in

Style convention for queries:
  - Always capitalize any words recognized by SQL.
*/

CREATE TABLE Nodes (
    nodeID integer PRIMARY KEY
  , nodeX  double precision
  , nodeY  double precision
);

CREATE TABLE Edges (
    node1 integer references Nodes(nodeID) NOT NULL ON DELETE CASCADE
  , node2 integer references Nodes(nodeID) NOT NULL ON DELETE CASCADE
);

CREATE TABLE Rooms (
    roomName        varchar(200) PRIMARY KEY
  , roomDescription varchar(1000)
  , nodeID          integer references Nodes(nodeID)
);

CREATE TABLE Employees (
    employeeID        integer PRIMARY KEY
  , employeeGivenName varchar(100)
  , employeeSurname   varchar(100)
  , employeeTitle     varchar(100)
);

CREATE TABLE EmployeeRooms (
    roomName   varchar(200) references Rooms(roomName)
  , employeeID integer references Employees(employeeID)
  , constraint EmployeeRooms_pk PRIMARY KEY (roomName, employeeID)
);
