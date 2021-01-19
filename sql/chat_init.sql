create table chat_friend
(
	user_friend_id bigint auto_increment comment '用户朋友id'
		primary key,
	user_id bigint not null comment '用户id',
	friend_id bigint not null comment '朋友id',
	create_time datetime null comment '创建时间',
	state int default 0 not null comment '状态 -1-黑名单 0-等待我同意 发起方是朋友 1-等待朋友同意 发起方是我 2-已同意 3-已拒绝',
	nickname varchar(255) null comment '昵称',
	source int default 1 not null comment '来源 1-搜索加好友 2-群组加好友',
	friend_account varchar(255) null comment '好友账号'
)
comment '好友表';

create table chat_group
(
	group_id bigint auto_increment comment '群组id'
		primary key,
	group_no bigint default 0 not null comment '群编号',
	group_name varchar(64) not null comment '群组名称',
	notice varchar(64) null comment '群组公告',
	notice_setting int default 1 not null comment '群组公告设置 1-所有人都可以修改 2-管理员可以修改 3-只有群主可以修改',
	setting int default 1 not null comment '群组设置 1-成员随意发言 2-管理员可以发言 3-只有群主可以发言',
	group_type int default 1 not null comment '群组类型 1-公司全员群 2-公司部门群 3-公司内部群 4-公司外部群 5-辩论群',
	tenant_id bigint null comment '租户id',
	org_id bigint null comment '组织机构id',
	debate_id bigint null comment '辩论主键',
	create_time datetime not null comment '创建时间',
	state int default 1 not null comment '状态 1-正常 -1-已解散',
	update_time datetime null comment '更新时间'
)
comment '群组表';

create table chat_group_user
(
	group_user_id bigint auto_increment comment '群成员主键'
		primary key,
	group_id bigint not null comment '群组id',
	user_id bigint not null comment '用户id',
	nickname varchar(64) not null comment '用户昵称',
	user_type int not null comment '用户类型 1-群主 2-管理员 3-普通成员',
	member_id bigint null comment '成员id',
	state int default 1 not null comment '状态 -1-被移除 1-正常 2-被禁言',
	group_state int default 1 not null comment '群成员消息设置 1-正常 2-屏蔽群消息',
	record_read_time datetime null comment '消息读取时间'
)
comment '群成员表';

create table chat_record
(
	record_id bigint auto_increment comment '主键'
		primary key,
	send_user_id bigint not null comment '发送用户id',
	record_type int default 1 not null comment '消息类型 1-聊天群消息 2-好友聊天消息',
	content_type int default 1 not null comment '内容类型 1-文字内容 2-图片 3-语音 4-视频 5-url 6-表情 7-进入聊天界面类型',
	content varchar(8000) null comment '发送消息内容',
	group_id bigint null comment '群组id',
	receive_user_id bigint null comment '接收用户id',
	send_time datetime not null comment '发送时间',
	receive_state int null comment '接收状态 1-未读 2-已读',
	receive_time datetime null comment '接收时间',
	state int default 1 not null comment '状态 1-正常 2-撤回'
)
comment '群成员表';

