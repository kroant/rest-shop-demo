INSERT INTO order_ (id, state, created_on) VALUES
('0d3ea9d7-9c23-46c7-9c76-861586a4eeb1', 'NEW', TIMESTAMPADD(MINUTE, -10, CURRENT_TIMESTAMP)),
('122c1845-1647-47ba-8254-7cfde64261bd', 'NEW', TIMESTAMPADD(MINUTE, -240, CURRENT_TIMESTAMP)),
('8b731ad0-35f8-4af1-b8a2-b4d874bf1dc8', 'NEW', TIMESTAMPADD(MINUTE, -240, CURRENT_TIMESTAMP)),
('959000c0-0fe5-478e-8ccf-bb47c4c07c6c', 'PAID', TIMESTAMPADD(MINUTE, -240, CURRENT_TIMESTAMP)),
('4774fbe9-a259-4ac7-a6df-a160038c077d', 'CANCELLED', TIMESTAMPADD(MINUTE, -240, CURRENT_TIMESTAMP));