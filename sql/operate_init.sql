create schema nodeCollegeTest collate utf8_general_ci;

create table o_admin
(
	admin_id bigint auto_increment comment '主键'
		primary key,
	account varchar(64) charset utf8mb4 null comment '账号',
	telephone varchar(20) charset utf8mb4 null comment '电话',
	password varchar(64) null comment '密码',
	salt varchar(64) null comment '盐值',
	first_login tinyint null comment '是否首次登陆',
	show_all_org tinyint null comment '是否显示所有机构数据 0-否，1-是',
	show_all_role tinyint null comment '是否显示所有角色数据 0-否，1-是',
	default_org_code varchar(64) null comment '默认机构代码',
	default_role_code varchar(64) null comment '默认角色代码',
	state tinyint default 0 null comment '状态 -1-已删除，0-不可删除，1-正常，2-冻结',
	create_time datetime null comment '创建时间'
);

create table o_admin_org
(
	id bigint auto_increment comment '主键'
		primary key,
	admin_id bigint null comment '管理员id',
	org_code varchar(64) null comment '机构代码'
)
comment '管理员组织机构';

create table o_admin_org_role
(
	id bigint auto_increment comment '主键'
		primary key,
	admin_id bigint null comment '管理员id',
	org_code varchar(64) null comment '机构代码',
	role_code varchar(64) null comment '角色代码'
)
comment '管理员角色';

create table o_admin_password
(
	admin_pwd_id bigint auto_increment comment '主键id'
		primary key,
	admin_id bigint null comment '管理员id',
	password varchar(64) charset utf8mb4 null comment '密码',
	salt varchar(64) null comment '盐值',
	create_time datetime null comment '创建时间'
);

create table o_app_api
(
	api_id bigint auto_increment comment '接口id'
		primary key,
	application_name varchar(255) charset utf8mb4 null comment '接口应用名称',
	controller_name varchar(255) charset utf8mb4 null comment 'controller_name',
	method varchar(8) null comment '请求方式',
	api_url varchar(255) charset utf8mb4 null comment '接口地址',
	requestBody varchar(500) null comment '请求实体',
	responseBody varchar(500) null comment '返回实体',
	modular_name varchar(255) charset utf8mb4 null comment '模块名称',
	description varchar(255) charset utf8mb4 null comment '描述',
	access_auth varchar(255) null comment '访问授权，0-都可以访问，1-登录访问，2-授权访问',
	state tinyint null comment '状态 1-正常 2-该接口后期删除 -1-已删除'
);

create table o_config
(
	config_id bigint auto_increment comment '配置主键'
		primary key,
	config_usage tinyint null comment '配置用途
0-系统配置
1-管理员机构配置，2-管理员配置，3-管理员机构-管理员配置
4-用户机构配置，5-用户配置，6-用户机构-用户配置
7-租户配置，8-租户-机构配置，9-租户-机构-成员配置，10-租户-成员配置',
	config_group varchar(64) null comment '配置组',
	config_code varchar(255) not null comment '配置代码',
	config_name varchar(255) not null comment '配置名称',
	config_value varchar(255) not null comment '配置值',
	config_explain varchar(255) null comment '配置描述',
	config_type tinyint not null comment '配置类型
0-输入框
1-下拉单选
2-下拉多选',
	option_list varchar(255) null comment '选项列表 配置类型为2、3时有效',
	state int default 1 not null comment '配置状态 0-不可编辑删除 1-正常',
	admin_org_code varchar(64) null comment '管理员机构代码 配置类型为1、3必填',
	admin_account varchar(64) null comment '管理员账号 配置类型为2、3必填',
	user_org_code varchar(64) null comment '用户机构代码 配置类型为4、6必填',
	user_account varchar(64) null comment '用户账号 配置类型为5、6必填',
	tenant_code varchar(64) null comment '租户代码 配置类型为7、8、9、10是必填',
	tenant_org_code varchar(64) null comment '租户机构代码 配置类型为8、9必填',
	member_account varchar(64) null comment '租户成员账号 配置类型为9、10必填',
	create_user varchar(64) null comment '创建用户',
	create_time datetime null comment '创建时间',
	update_user varchar(64) null comment '更新用户',
	update_time datetime null comment '更新时间',
	pre_flag tinyint null comment '预制标志 0或空非预制配置，1-预制配置'
)
comment '配置表';

create table o_data_power
(
	id bigint auto_increment comment '主键'
		primary key,
	data_power_usage int null comment '数据权限用途 0-运营/运维，1-2C，2-2B',
	data_power_code varchar(64) null comment '权限代码',
	data_power_name varchar(64) null comment '数据权限名称',
	data_power_type int null comment '数据权限类型 0-机构，1-机构用户',
	data_option varchar(5000) null comment '数据权限选项 json字符串'
)
comment '数据权限主表';

create table o_data_power_auth
(
	id bigint auto_increment
		primary key,
	data_power_code varchar(64) null comment '数据权限代码',
	user_id bigint null comment '用户id',
	org_code varchar(64) null comment '机构代码',
	all_data tinyint(1) null comment '所有数据',
	auth_list varchar(5000) null comment '授权权限列表',
	create_time datetime null comment '创建时间',
	state int null comment '状态 0-不允许修改'
)
comment '数据权限授权';

create table o_file
(
	file_id bigint auto_increment
		primary key,
	user_id bigint null,
	tenant_id bigint null,
	org_id bigint null,
	file_name varchar(255) null,
	file_path varchar(255) null,
	file_type int default 1 not null,
	file_validity int null,
	create_time datetime null,
	file_purpose int default 1 null
);

create table o_org
(
	id bigint auto_increment comment '主键'
		primary key,
	org_usage int null comment '机构用途 0-运维/运营，1-2C',
	org_code varchar(64) null comment '机构代码',
	org_name varchar(32) null comment '机构名称',
	parent_code varchar(64) null comment '上级机构代码',
	num int null comment '排序',
	create_time datetime null comment '创建时间'
)
comment '机构';

create table o_product
(
	id bigint auto_increment comment '主键'
		primary key,
	product_name varchar(64) null comment '产品名称',
	product_code varchar(64) null comment '产品代码',
	product_type tinyint null comment '产品类型：0-顶级产品，1-共存式产品，2-排斥式产品',
	product_usage tinyint null comment '产品用途 0-运营，1-2C，2-2B',
	parent_code varchar(64) null comment '父级产品代码',
	product_desc varchar(255) null comment '产品描述',
	product_icon varchar(32) null comment '产品图标',
	top_code varchar(64) null comment '顶级产品代码',
	belong_code varchar(64) null comment '归属代码'
)
comment '产品信息';

create table o_product_menu
(
	id bigint auto_increment comment '主键'
		primary key,
	product_code varchar(64) null comment '产品代码',
	menu_code varchar(64) null comment '菜单代码',
	menu_type tinyint null comment '菜单类型：0-分类导航，1-菜单页面',
	menu_name varchar(64) null comment '菜单名称',
	menu_icon varchar(32) null comment '菜单图标',
	parent_code varchar(64) null comment '父级代码',
	num int null comment '排序号'
)
comment '产品菜单';

create table o_product_menu_relation
(
	id bigint auto_increment comment '主键'
		primary key,
	product_code varchar(64) null comment '产品代码',
	menu_code varchar(64) null comment '菜单代码',
	belong_code varchar(64) null comment '所属产品代码'
)
comment '产品菜单关系';

create table o_role
(
	id bigint auto_increment comment '主键'
		primary key,
	product_code varchar(64) null comment '归属产品代码',
	role_name varchar(32) null comment '角色名称',
	role_code varchar(32) null comment '角色代码',
	role_desc varchar(64) null comment '角色描述',
	role_usage int null comment '角色用途 0-运维角色，1-2C角色，2-2B角色',
	role_type int null comment '角色类型2B用途有效  0-组织角色，1-组织成员角色',
	data_power tinyint null comment '数据权限 2B用途有效
0-所有机构
1-所属机构及下级机构
2-所属机构及
3-用户自己的数据',
	role_state int null comment '状态 -1-已删除，0-正常，1-禁用'
)
comment '角色信息';

create table o_role_menu
(
	id bigint auto_increment comment '主键'
		primary key,
	role_menu_usage int null comment '角色菜单用途 0-运营/运维，1-2C，2-2B',
	role_code varchar(64) null comment '角色代码',
	menu_code varchar(64) null comment '菜单代码'
)
comment '角色菜单';

create table o_role_org
(
	id bigint auto_increment comment '主键'
		primary key,
	role_org_usage int null comment '用途 0-运营/运维，1-2C',
	role_code varchar(64) null comment '角色代码',
	org_code varchar(64) null comment '机构代码'
)
comment '组织机构角色信息';

create table o_route
(
	id bigint auto_increment
		primary key,
	route_id varchar(64) null comment '唯一id',
	route_name varchar(64) null comment '网关名称',
	route_type int null comment '转发类型 0-从注册中心获取地址，1-直接跳转网络地址',
	route_url varchar(64) null comment '路由地址',
	route_order int null comment '路由排序 越小越优先',
	route_state int null comment '路由状态 -1-异常配置，0-禁用，1-启用',
	predicate_json varchar(500) null comment '断言',
	filter_json varchar(500) null comment '过滤器'
)
comment '运营网关';

create table o_tenant
(
	tenant_id bigint auto_increment
		primary key,
	tenant_name varchar(255) default '' not null comment '租户名称',
	tenant_code varchar(255) default '' not null comment '租户代码',
	introduce varchar(255) default '' not null comment '描述',
	create_time datetime null comment '创建时间',
	create_user_id bigint not null comment '创建用户id',
	state tinyint default 1 not null comment '状态 -1-已删除，0-不可删除，1-正常，2-冻结，3-待审核'
);

create table o_tenant_apply
(
	id bigint auto_increment comment '主键'
		primary key,
	tenant_name varchar(64) null comment '租户名称',
	tenant_code varchar(64) null comment '租户代码',
	tenant_desc varchar(255) null comment '租户描述',
	apply_user_id bigint null comment '申请人用户id',
	apply_user_name varchar(64) null comment '申请人名称',
	apply_email varchar(64) null comment '申请人联系邮箱',
	remarks varchar(64) null comment '备注',
	modify_name varchar(64) null comment '修改人名称',
	modify_time datetime null comment '修改时间',
	state int null comment '状态 -1-拒绝，0-待审核，1-通过',
	create_time datetime null comment '申请时间'
)
comment '租户申请';

create table o_tenant_product
(
	id bigint auto_increment
		primary key,
	tenant_id bigint null comment '租户id',
	product_code varchar(64) null comment '产品代码',
	state tinyint null comment '状态 0-停用，1-启用',
	create_user varchar(64) null comment '创建用户',
	create_time datetime null comment '创建时间',
	update_user varchar(64) null comment '修改用户',
	update_time datetime null comment '修改时间'
)
comment '租户产品表';

create table o_ui
(
	ui_id bigint auto_increment comment '主键'
		primary key,
	ui_name varchar(64) null comment '前端名称',
	ui_code varchar(64) null comment '前端代码',
	ui_desc varchar(64) null comment '描述'
)
comment '前端信息';

create table o_ui_page
(
	ui_page_id bigint auto_increment comment '前端菜单页面主键'
		primary key,
	ui_code varchar(64) null comment '前端代码',
	page_name varchar(64) null comment '页面名称',
	page_code varchar(64) null comment '页面代码',
	page_path varchar(64) null comment '页面路径',
	page_icon varchar(32) null comment '页面图标',
	num int null
)
comment '前端菜单页面';

create table o_ui_page_button
(
	id bigint auto_increment comment '主键'
		primary key,
	page_code varchar(64) null comment '页面代码',
	button_name varchar(64) null comment '按钮名称',
	button_code varchar(64) null comment '按钮代码',
	button_type int null comment '按钮类型 2-查询类按钮，3-操作类按钮',
	button_icon varchar(32) null comment '图标',
	parent_code varchar(64) null comment '上级按钮代码',
	app_name varchar(64) null comment '接口微服务名称',
	api_url varchar(64) null comment '接口地址',
	num int null
)
comment '前端页面按钮';

create table o_user
(
	user_id bigint auto_increment comment '用户主键'
		primary key,
	account varchar(255) null comment '用户账号 全局唯一',
	nickname varchar(255) null comment '昵称',
	email varchar(255) null comment '邮箱',
	telephone varchar(255) null comment '电话',
	avatar varchar(255) null comment '用户头像',
	sex int null comment '性别 0-男，1-女',
	birthday datetime null comment '出生日期',
	tenant_id bigint null comment '默认登录租户id',
	tenant_code varchar(64) null comment '默认登录租户代码',
	first_login tinyint default 0 not null comment '首次登陆 0-首次登陆，1-非首次登陆',
	show_all_org tinyint null comment '显示所有机构数据',
	show_all_role tinyint null comment '显示所有角色数据',
	default_org_code varchar(64) null comment '默认机构代码 showAllOrg=false有效',
	default_role_code varchar(64) null comment '默认角色代码 showAllRole=false有效',
	password varchar(255) default '' not null comment '密码，密文',
	salt varchar(255) default '' not null comment '密码盐值',
	state int default 1 not null comment '状态 -1-已删除，0-不可删除，1-正常, 2-冻结',
	create_time datetime null comment '创建时间'
);

create table o_user_invite
(
	id bigint auto_increment
		primary key,
	telephone varchar(255) default '' not null comment '被邀请人电话',
	user_name varchar(255) default '' not null comment '被邀请用户姓名',
	tenant_id bigint null comment '邀请租户id',
	state tinyint null comment '邀请状态 0-邀请中，1-邀请成功',
	create_user varchar(64) null comment '发出邀请人姓名',
	create_time datetime null comment '发出邀请时间',
	update_time datetime null comment '更新时间'
);

create table o_user_org
(
	id bigint auto_increment comment '主键'
		primary key,
	user_id bigint null comment '用户id',
	org_code varchar(64) null comment '机构代码'
)
comment '用户-机构表';

create table o_user_org_role
(
	id bigint auto_increment
		primary key,
	user_id bigint null comment '用户id',
	org_code varchar(64) null comment '机构代码',
	role_code varchar(64) null comment '角色代码'
)
comment '用户机构角色';

create table o_user_password
(
	user_pwd_id bigint auto_increment
		primary key,
	password varchar(255) null,
	salt varchar(255) null,
	user_id bigint null,
	create_time datetime null
);

create table o_user_tenant
(
	id bigint auto_increment comment '主键'
		primary key,
	user_id bigint null comment '用户id',
	tenant_id bigint null comment '租户id',
	state tinyint null comment '状态 0-离职，1-正常',
	create_user varchar(64) null comment '创建人',
	create_time datetime null comment '创建时间',
	update_user varchar(64) null comment '更新用户',
	update_time datetime null comment '更新时间'
)
comment '用户归属租户信息';

create table sys_log
(
	id bigint auto_increment comment '主键'
		primary key,
	request_id varchar(32) null comment '请求id',
	call_ids varchar(32) null comment '线程ids',
	access_source varchar(32) null comment '访问来源',
	app_name varchar(32) null comment '服务名称',
	request_uri varchar(64) null comment '请求地址',
	referer varchar(255) null comment '请求页面',
	request_ip varchar(32) null comment '请求ip',
	in_param varchar(8192) null comment '入参',
	out_param varchar(8192) null comment '出参',
	success tinyint null comment '请求是否成功',
	error_msg varchar(500) null comment '错误信息',
	lost_time bigint null comment '耗时',
	admin_id bigint null comment '管理员id',
	admin_account varchar(64) null comment '管理员账号',
	user_id bigint null comment '用户id',
	user_account varchar(64) null comment '用户账号',
	member_id bigint null comment '成员id',
	member_account varchar(64) null comment '租户成员账号',
	tenant_id bigint null comment '租户id',
	tenant_code varchar(64) null comment '租户代码',
	create_time timestamp null comment '创建时间'
)
comment '系统日志';

create table sys_visit_log
(
	id bigint auto_increment comment 'id'
		primary key,
	visit_type tinyint null comment '访问类型 0-微服务接口访问量，1-微服务访问量, 2-ip接口访问量，3-ip访问量，4-文章访问量',
	time_dimension tinyint null comment '时间维度 0-分钟，1-小时，2-天',
	visit_day int null comment '访问天 yyyyMMdd',
	visit_hour varchar(2) null comment '访问小时 HH',
	visit_minute varchar(4) null comment '访问分钟 HHmm',
	visit_app_name varchar(64) null comment '访问微服务名称',
	visit_url varchar(255) null comment '访问地址',
	visit_ip varchar(32) null comment '访问ip',
	visit_count bigint null comment '访问总数',
	visit_ip_count bigint null comment '访问ip数',
	create_time datetime null comment '创建时间'
)
comment '系统访问日志';

