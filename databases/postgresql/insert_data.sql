
INSERT INTO SUBSCRIPTION_INFO(ID, SUBSCRIPTION_ID, SERVICE_NAME, SECRET) VALUES (1, 'sub1', 'epay', 'abcdefghijklmnopqrstuvwxyz012345');
INSERT INTO SUBSCRIPTION_INFO(ID, SUBSCRIPTION_ID, SERVICE_NAME, SECRET) VALUES (2, 'sub2', 'dms', 'abcdefghijklmnopqrstuvwxyz012345');
INSERT INTO SUBSCRIPTION_INFO(ID, SUBSCRIPTION_ID, SERVICE_NAME, SECRET) VALUES (3, 'sub3', 'skeleton', 'abcdefghijklmnopqrstuvwxyz012345');

INSERT INTO SUBSCRIPTION_INFO_VALUES(SUBSCRIPTION_INFO_ID, KEY, VALUE) VALUES 
(1, 'merchantId', '300209473'),
(1, 'apiAccessPasscode', 'A97C9eA2ECfe4312aC222AB6CB563e02'),
(1, 'hashKey', '4F33FB6F6-6AF5-4BB6-AEC6-2F723700');
