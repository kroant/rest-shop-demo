INSERT INTO product (id, name, unit, price, stock, deleted) VALUES
('3e752234-0a19-49c0-ba18-cfebf0bb7772', 'Milk 1 l', 'PIECE', 18.50, 50, false),
('10b10895-cce9-48c6-bc8c-7025d0a7fe57', 'Milk 500 ml', 'PIECE', 12, 30, false),
('a3c64d30-cb49-4279-9a83-282a7d0c7669', 'Cashew Nuts', 'GRAM', 0.30, 100000, false),
('92ebda36-bfe1-4dc2-b7d6-84576e58a621', 'Sugar 1 kg', 'PIECE', 11, 0, true);

INSERT INTO order_ (id, state, created_on) VALUES
('27408323-1031-4658-8995-7ecff8f2b26f', 'CANCELLED', '2022-01-15T06:29:59Z'),
('fa254654-bdbc-431b-8b9e-f6bf34540ee9', 'NEW', '2022-01-05T14:23:08Z'),
('b3a48eee-65a4-431b-a11a-e770a7f0ba8b', 'NEW', '2022-01-05T16:58:46Z'),
('e2a878e6-72c6-49f5-b391-cb60fbca944e', 'PAID', '2022-01-10T09:43:00Z');

INSERT INTO order_item (order_id, product_id, amount) VALUES
('fa254654-bdbc-431b-8b9e-f6bf34540ee9', '10b10895-cce9-48c6-bc8c-7025d0a7fe57', 1),
('fa254654-bdbc-431b-8b9e-f6bf34540ee9', 'a3c64d30-cb49-4279-9a83-282a7d0c7669', 500),

('b3a48eee-65a4-431b-a11a-e770a7f0ba8b', '3e752234-0a19-49c0-ba18-cfebf0bb7772', 2),
('b3a48eee-65a4-431b-a11a-e770a7f0ba8b', 'a3c64d30-cb49-4279-9a83-282a7d0c7669', 2500),

('e2a878e6-72c6-49f5-b391-cb60fbca944e', '3e752234-0a19-49c0-ba18-cfebf0bb7772', 1),
('e2a878e6-72c6-49f5-b391-cb60fbca944e', '10b10895-cce9-48c6-bc8c-7025d0a7fe57', 1),
('e2a878e6-72c6-49f5-b391-cb60fbca944e', 'a3c64d30-cb49-4279-9a83-282a7d0c7669', 300);