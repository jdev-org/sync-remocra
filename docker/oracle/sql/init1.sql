-- sqlplus / as sysdba@//localhost:1521/EDPSIGDEV

create tablespace edp_tabspace 
datafile 'edp_tabspace.dat' 
size 10M autoextend on;

create temporary tablespace edp_tabspace_temp
 tempfile 'edp_tabspace_temp.dat'
 size 5M autoextend on;

create user EDP_REMOCRA
 identified by 1234
 default tablespace edp_tabspace
 temporary tablespace edp_tabspace_temp;

grant create session to EDP_REMOCRA;
grant create table to EDP_REMOCRA;
grant unlimited tablespace to EDP_REMOCRA;