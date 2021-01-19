create table t_member
(
	member_id bigint auto_increment comment '成员主键'
		primary key,
	tenant_id bigint null comment '租户id',
	user_id bigint null comment '用户id',
	account varchar(64) null comment '账号',
	password varchar(64) null comment '密码',
	salt varchar(64) null comment '密码盐值',
	nickname varchar(64) null comment '昵称',
	email varchar(32) null comment '邮箱',
	telephone varchar(32) null comment '电话',
	avatar varchar(64) null comment '成员头像',
	avatar_thumb varchar(64) null comment '用户头像缩略图',
	sex int null comment '性别 0-男，1-女',
	first_login int null comment '首次登陆 0-首次登陆，1-非首次登陆',
	show_all_org tinyint(1) null comment '显示所有机构数据',
	show_all_role tinyint(1) null comment '显示所有角色数据',
	default_org_code varchar(64) null comment '默认机构代码 showAllOrg=false有效',
	default_role_code varchar(64) null comment '默认角色代码 showAllRole=false有效',
	state int null comment '状态 -1-已删除，0-不可删除，1-可删除, 2-冻结',
	create_time datetime null comment '创建时间'
)
comment '成员表';

create table t_member_org
(
	id bigint auto_increment comment '主键'
		primary key,
	tenant_id bigint null comment '租户id',
	member_id bigint null comment '成员id',
	org_code varchar(64) null comment '机构代码'
)
comment '成员-机构表';

create table t_member_org_role
(
	id bigint auto_increment comment '主键'
		primary key,
	tenant_id bigint null comment '租户id',
	member_id bigint null comment '成员id',
	org_code varchar(64) null comment '机构代码',
	role_code varchar(64) null comment '角色代码',
	role_source tinyint null comment '角色来源 0-预制角色，1-自定义角色'
)
comment '成员-机构-角色表';

create table t_org
(
	id bigint auto_increment comment '主键'
		primary key,
	tenant_id bigint null comment '租户id',
	org_code varchar(64) null comment '机构代码',
	org_name varchar(32) null comment '机构名称',
	parent_code varchar(64) null comment '上级机构代码',
	num int null comment '排序',
	create_user varchar(64) null comment '创建人',
	create_time datetime null comment '创建时间',
	update_user varchar(64) null comment '修改人',
	update_time datetime null comment '修改时间'
)
comment '租户机构';

create table t_role
(
	id bigint auto_increment comment '主键'
		primary key,
	tenant_id bigint null comment '租户id',
	role_name varchar(32) null comment '角色名称',
	role_code varchar(32) null comment '角色代码',
	role_desc varchar(64) null comment '角色描述',
	role_type int null comment '角色类型2B用途有效  0-组织角色，1-组织成员角色',
	data_power tinyint null comment '数据权限 0-所有数据，1-所属及下级机构数据，2-所属机构数据，3-用户自己的数据',
	role_state int null comment '状态 -1-已删除，0-正常，1-禁用',
	create_user varchar(64) null comment '创建人',
	create_time datetime null comment '创建时间',
	update_user varchar(64) null comment '修改人',
	update_time datetime null comment '修改时间'
)
comment '租户角色信息';

create table t_role_menu
(
	id int auto_increment comment '主键'
		primary key,
	tenant_id bigint null comment '租户id',
	role_code varchar(64) null comment '角色代码',
	menu_code varchar(64) null comment '菜单代码'
)
comment '角色菜单';

create table t_role_org
(
	id bigint auto_increment comment '主键'
		primary key,
	tenant_id bigint null comment '租户id',
	role_code varchar(64) null comment '角色代码',
	org_code varchar(64) null comment '机构代码',
	role_source tinyint null comment '角色来源 0-预制角色，1-自定义角色'
)
comment '角色-机构表';

