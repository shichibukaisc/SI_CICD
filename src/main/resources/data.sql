
INSERT INTO SUBSCRIPTION_INFO(ID, CLIENT_ID, AUDIENCE, SECRET, ROLES) VALUES
(1, 'client-id-test', 'sbras-lcl', 'abcdefghijklmnopqrstuvwxyz012345', 'SBRAS_ADMIN'),
(2, 'client-id-test', 'sbras-dev', 'abcdefghijklmnopqrstuvwxyz012345', 'SBRAS_USER'),
(3, 'client-id-test', 'sbras-uat', 'abcdefghijklmnopqrstuvwxyz012345', 'SBRAS_USER'),
(4, 'client-id-test', 'sbras-prod', 'abcdefghijklmnopqrstuvwxyz012345', 'SBRAS_USER');

INSERT INTO SUBSCRIPTION_INFO_VALUES(SUBSCRIPTION_INFO_ID, KEY, VALUE) VALUES 
(1, 'someId', '300209473'),
(1, 'someApiAccessPasscode', 'A97C9eA2ECfe4312aC222AB6CB563e02'),
(1, 'someGUID', '4F33FB6F6-6AF5-4BB6-AEC6-2F723700');
