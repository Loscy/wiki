# drop table if exists `ebook`;
# create table `ebook` (
#     `id` bigint not null comment 'id',
#     `name` varchar(50) comment '名称',
#     `category1_id` bigint comment '分类1',
#     `category2_id` bigint comment '分类2',
#     `description` varchar(200) comment '描述',
#     `cover` varchar(200) comment '封面',
#     `doc_count` int comment '文档数',
#     `view_count` int comment '阅读数',
#     `vote_count` int comment '点赞数',
#     primary key(`id`)
# )engine = innodb default charset=utf8mb4 comment='电子书';
#
# insert into `ebook` (id, name, description) values (1, 'Spring Boot 入门教程', '零基础入门 Java 开发，企业级应用开发最佳首选框架');
# insert into `ebook` (id, name, description) values (2, 'Vue 入门教程', '零基础入门 Vue 开发，企业级应用开发最佳首选框架');
# insert into `ebook` (id, name, description) values (3, 'Python 入门教程', '零基础入门 Python 开发，企业级应用开发最佳首选框架');
# insert into `ebook` (id, name, description) values (4, 'Mysql 入门教程', '零基础入门 Mysql 开发，企业级应用开发最佳首选框架');
# insert into `ebook` (id, name, description) values (5, 'Oracle 入门教程', '零基础入门 Oracle 开发，企业级应用开发最佳首选框架');
#
# -- 文档表
# drop table if exists `doc`;
# create table `doc` (
#     `id` bigint not null default 0 comment 'id',
#     `ebook_id` bigint not null default 0 comment '电子书id',
#     `parent` bigint not null default 0 comment '父id',
#     `name` varchar(50) not null comment '名称',
#     `sort` int comment '顺序',
#     `view_count` int default 0 comment '阅读数',
#     `vote_count` int default 0 comment '点赞数',
#     primary key (`id`)
# ) engine=innodb default charset =utf8mb4 comment ='文档';
#
# insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count) values (1, 1, 0, '文档1' , 1, 0 ,0);
# insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count) values (2, 1, 1, '文档1.1' , 1, 0 ,0);
# insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count) values (3, 1, 0, '文档2' , 2, 0 ,0);
# insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count) values (4, 1, 3, '文档2.1' , 1, 0 ,0);
# insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count) values (5, 1, 3, '文档2.2' , 2, 0 ,0);
# insert into `doc` (id, ebook_id, parent, name, sort, view_count, vote_count) values (6, 1, 5, '文档2.2.5' , 1, 0 ,0);

-- 文档内容
drop table if exists `content`;
create table `content` (
    `id` bigint not null comment '文档id',
    `content` mediumtext not null comment '内容',
    primary key (`id`)
) engine=innodb default  charset = utf8mb4 comment ='文档内容';