CREATE TABLE product (
	id      UUID NOT NULL DEFAULT random_uuid() PRIMARY KEY,
	name    VARCHAR(100) NOT NULL,
	unit    ENUM ('PIECE', 'GRAM', 'LITER', 'METER') NOT NULL,
	price   NUMERIC(19,2) NOT NULL,
	stock   NUMERIC(19,3) NOT NULL DEFAULT 0,
	deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE order_ (
	id         UUID NOT NULL DEFAULT random_uuid() PRIMARY KEY,
	state      ENUM ('NEW', 'PAID', 'CANCELLED') NOT NULL DEFAULT 'NEW',
	price      NUMERIC(19,2) NOT NULL DEFAULT 0,
	created_on TIMESTAMP(0) WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_item (
	order_id   UUID NOT NULL,
	product_id UUID NOT NULL,
	amount     NUMERIC(19,3) NOT NULL,

	PRIMARY KEY (order_id, product_id),
	FOREIGN KEY (order_id)   REFERENCES order_ (id),
	FOREIGN KEY (product_id) REFERENCES product (id)
);
