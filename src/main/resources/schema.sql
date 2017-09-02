CREATE TABLE receipts (
  id INT UNSIGNED AUTO_INCREMENT,
  uploaded TIME DEFAULT CURRENT_TIME(),
  merchant VARCHAR(255),
  amount DECIMAL(12,2),
  receipt_type INT UNSIGNED,

  PRIMARY KEY (id)
);

CREATE TABLE tags (
  id INT UNSIGNED AUTO_INCREMENT,
  name VARCHAR(255),

  PRIMARY KEY (id)
);

CREATE TABLE receipts_tags (
  receipt_id INT UNSIGNED,
  tag_id INT UNSIGNED,

  CONSTRAINT receipt_tag_pk PRIMARY KEY (receipt_id, tag_id),
  CONSTRAINT fk_receipt FOREIGN KEY (receipt_id) REFERENCES receipts (id),
  CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tags (id)
);

select * from receipts;
select * from tags;