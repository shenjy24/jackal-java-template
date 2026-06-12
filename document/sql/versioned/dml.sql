UPDATE `auth_perm` SET `component` = 'system/UserManageView' WHERE `code` = 'auth:user';
UPDATE `auth_perm` SET `component` = 'system/RoleManageView' WHERE `code` = 'auth:role';
UPDATE `auth_perm` SET `component` = 'system/MenuManageView' WHERE `code` = 'auth:perm';
