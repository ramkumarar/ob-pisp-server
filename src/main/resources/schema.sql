drop table merchant;
drop table payment;
CREATE TABLE merchant (
  merchantid VARCHAR(20) NOT NULL,
  sortcode VARCHAR(50) NOT NULL,
  accountnumber  VARCHAR(50) NOT NULL,
  name   VARCHAR(50) NOT NULL,
  PRIMARY KEY (merchantid)
  );