CREATE TYPE sex_t AS ENUM (
  'PREFER_NOT_SAY',
  'MALE',
  'FEMALE'
);

--;;
CREATE TABLE IF NOT EXISTS patients (
  id serial NOT NULL PRIMARY KEY,
  first_name varchar(255) NOT NULL,
  last_name varchar(255) NOT NULL,
  middle_name varchar(255) NOT NULL,
  sex sex_t NOT NULL,
  date_of_birth date NOT NULL,
  address varchar(255) NOT NULL,
  oms_policy_number varchar(255) NOT NULL
);
