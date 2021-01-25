-- operate库
alter table o_product_menu
    add nav_platform int default 1 null comment '导航平台 0-不生成导航，1-pc端导航，2-移动端导航' after product_code;
alter table o_product_menu_relation
    add nav_platform int default 1 null comment '导航平台 0-不生成导航，1-pc端导航，2-移动端导航' after product_code;
alter table o_role_menu
    add nav_platform int default 1 null comment '导航平台 0-不生成导航，1-pc端导航，2-移动端导航';

-- tenant库
alter table t_role_menu
    add nav_platform int default 1 null comment '导航平台 0-不生成导航，1-pc端导航，2-移动端导航';
	
	
-- operate库
-- 日志出参格式修改
ALTER TABLE sys_log modify out_param varchar(8192) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '出参';