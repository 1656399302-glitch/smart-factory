# 数据库表结构

## ar_application_configuration
- id int
- trigger_method varchar(255)
- display_method varchar(255)
- screen_resolution varchar(50)
- video_resolution varchar(50)
- privacy_settings varchar(50)
- app_id int
- created_at timestamp

## ar_applications
- ApplicationID int
- Name varchar(255)
- Description text
- Version varchar(50)
- ReleaseDate date

## ar_content
- ar_content_id int
- name varchar(255)
- category varchar(255)
- file_url varchar(500)
- description text
- usage_status varchar(50)
- qr_code_id int

## device
- device_number int
- name varchar(100)
- description varchar(255)
- status varchar(255)
- qr_code_number int
- factory_number int

## device_association
- qr_code_id int
- qr_code_name varchar(255)
- qr_code text
- usage_status varchar(50)

## energy_consumption
- id int
- energy_consumed decimal(10,2)
- unit varchar(50)
- type varchar(50)
- year int
- month tinyint
- factory_id varchar(50)

## equipment
- equipment_id int
- name varchar(255)
- description text
- equipment_status varchar(50)
- qr_code_id int
- factory_id int

## factory
- factory_id int
- name varchar(255)
- description text
- contact_person varchar(255)
- contact_phone varchar(20)
- address varchar(255)

## gen_table
- table_id bigint
- table_name varchar(200)
- table_comment varchar(500)
- sub_table_name varchar(64)
- sub_table_fk_name varchar(64)
- class_name varchar(100)
- tpl_category varchar(200)
- tpl_web_type varchar(30)
- package_name varchar(100)
- module_name varchar(30)
- ... (21 columns)

## gen_table_column
- column_id bigint
- table_id bigint
- column_name varchar(200)
- column_comment varchar(500)
- column_type varchar(100)
- java_type varchar(500)
- java_field varchar(200)
- is_pk char(1)
- is_increment char(1)
- is_required char(1)
- ... (22 columns)

## maintenance_record
- record_id int
- maintenance_log text
- date date
- maintainer_name varchar(255)
- equipment_id int

## material
- material_id int
- material_name varchar(255)
- material_model varchar(255)
- material_description text
- stock_amount int
- warehouse_zone_id int

## material_transaction
- transaction_id int
- transaction_type int
- operator_name varchar(255)
- warehouse_admin_name varchar(255)

## material_transaction_detail
- material_id int
- transaction_id int
- quantity int
- transaction_date date

## product
- product_id int
- product_name varchar(255)
- product_model varchar(255)
- product_description text
- stock_amount int
- warehouse_zone_id int

## product_transaction
- transaction_id int
- transaction_type int
- operator_name varchar(255)
- warehouse_admin_name varchar(255)

## product_transaction_detail
- product_id int
- transaction_id int
- quantity int
- transaction_date date

## production_batch
- batch_id int
- quantity int
- unit varchar(50)
- production_date date
- shelf_life varchar(50)
- product_id int

## qr_code
- qr_code_id int
- qr_code_name varchar(50)
- qr_code varchar(255)
- usage_status varchar(50)

## qrtz_blob_triggers
- sched_name varchar(120)
- trigger_name varchar(200)
- trigger_group varchar(200)
- blob_data blob

## qrtz_calendars
- sched_name varchar(120)
- calendar_name varchar(200)
- calendar blob

## qrtz_cron_triggers
- sched_name varchar(120)
- trigger_name varchar(200)
- trigger_group varchar(200)
- cron_expression varchar(200)
- time_zone_id varchar(80)

## qrtz_fired_triggers
- sched_name varchar(120)
- entry_id varchar(95)
- trigger_name varchar(200)
- trigger_group varchar(200)
- instance_name varchar(200)
- fired_time bigint
- sched_time bigint
- priority int
- state varchar(16)
- job_name varchar(200)
- ... (13 columns)

## qrtz_job_details
- sched_name varchar(120)
- job_name varchar(200)
- job_group varchar(200)
- description varchar(250)
- job_class_name varchar(250)
- is_durable varchar(1)
- is_nonconcurrent varchar(1)
- is_update_data varchar(1)
- requests_recovery varchar(1)
- job_data blob

## qrtz_locks
- sched_name varchar(120)
- lock_name varchar(40)

## qrtz_paused_trigger_grps
- sched_name varchar(120)
- trigger_group varchar(200)

## qrtz_scheduler_state
- sched_name varchar(120)
- instance_name varchar(200)
- last_checkin_time bigint
- checkin_interval bigint

## qrtz_simple_triggers
- sched_name varchar(120)
- trigger_name varchar(200)
- trigger_group varchar(200)
- repeat_count bigint
- repeat_interval bigint
- times_triggered bigint

## qrtz_simprop_triggers
- sched_name varchar(120)
- trigger_name varchar(200)
- trigger_group varchar(200)
- str_prop_1 varchar(512)
- str_prop_2 varchar(512)
- str_prop_3 varchar(512)
- int_prop_1 int
- int_prop_2 int
- long_prop_1 bigint
- long_prop_2 bigint
- ... (14 columns)

## qrtz_triggers
- sched_name varchar(120)
- trigger_name varchar(200)
- trigger_group varchar(200)
- job_name varchar(200)
- job_group varchar(200)
- description varchar(250)
- next_fire_time bigint
- prev_fire_time bigint
- priority int
- trigger_state varchar(16)
- ... (16 columns)

## sand_association
- qr_code_id int
- qr_code_name varchar(255)
- qr_code text
- usage_status varchar(50)

## sandbox_zone
- zone_id int
- name varchar(255)
- description text
- qr_code_id int
- factory_id int

## school_applications
- app_id int
- app_name varchar(255)
- description text
- version varchar(50)
- release_date date

## supply_batch
- batch_id int
- quantity int
- unit varchar(50)
- production_date date
- shelf_life varchar(50)
- manufacturer varchar(255)
- material_id int

## sys_config
- config_id int
- config_name varchar(100)
- config_key varchar(100)
- config_value varchar(500)
- config_type char(1)
- create_by varchar(64)
- create_time datetime
- update_by varchar(64)
- update_time datetime
- remark varchar(500)

## sys_dept
- dept_id bigint
- parent_id bigint
- ancestors varchar(50)
- dept_name varchar(30)
- order_num int
- leader varchar(20)
- phone varchar(11)
- email varchar(50)
- status char(1)
- del_flag char(1)
- ... (14 columns)

## sys_dict_data
- dict_code bigint
- dict_sort int
- dict_label varchar(100)
- dict_value varchar(100)
- dict_type varchar(100)
- css_class varchar(100)
- list_class varchar(100)
- is_default char(1)
- status char(1)
- create_by varchar(64)
- ... (14 columns)

## sys_dict_type
- dict_id bigint
- dict_name varchar(100)
- dict_type varchar(100)
- status char(1)
- create_by varchar(64)
- create_time datetime
- update_by varchar(64)
- update_time datetime
- remark varchar(500)

## sys_job
- job_id bigint
- job_name varchar(64)
- job_group varchar(64)
- invoke_target varchar(500)
- cron_expression varchar(255)
- misfire_policy varchar(20)
- concurrent char(1)
- status char(1)
- create_by varchar(64)
- create_time datetime
- ... (13 columns)

## sys_job_log
- job_log_id bigint
- job_name varchar(64)
- job_group varchar(64)
- invoke_target varchar(500)
- job_message varchar(500)
- status char(1)
- exception_info varchar(2000)
- create_time datetime

## sys_logininfor
- info_id bigint
- user_name varchar(50)
- ipaddr varchar(128)
- login_location varchar(255)
- browser varchar(50)
- os varchar(50)
- status char(1)
- msg varchar(255)
- login_time datetime

## sys_menu
- menu_id bigint
- menu_name varchar(50)
- parent_id bigint
- order_num int
- path varchar(200)
- component varchar(255)
- query varchar(255)
- route_name varchar(50)
- is_frame int
- is_cache int
- ... (20 columns)

## sys_notice
- notice_id int
- notice_title varchar(50)
- notice_type char(1)
- notice_content longblob
- status char(1)
- create_by varchar(64)
- create_time datetime
- update_by varchar(64)
- update_time datetime
- remark varchar(255)

## sys_oper_log
- oper_id bigint
- title varchar(50)
- business_type int
- method varchar(200)
- request_method varchar(10)
- operator_type int
- oper_name varchar(50)
- dept_name varchar(50)
- oper_url varchar(255)
- oper_ip varchar(128)
- ... (17 columns)

## sys_post
- post_id bigint
- post_code varchar(64)
- post_name varchar(50)
- post_sort int
- status char(1)
- create_by varchar(64)
- create_time datetime
- update_by varchar(64)
- update_time datetime
- remark varchar(500)

## sys_role
- role_id bigint
- role_name varchar(30)
- role_key varchar(100)
- role_sort int
- data_scope char(1)
- menu_check_strictly tinyint(1)
- dept_check_strictly tinyint(1)
- status char(1)
- del_flag char(1)
- create_by varchar(64)
- ... (14 columns)

## sys_role_dept
- role_id bigint
- dept_id bigint

## sys_role_menu
- role_id bigint
- menu_id bigint

## sys_user
- user_id bigint
- dept_id bigint
- user_name varchar(30)
- nick_name varchar(30)
- user_type varchar(2)
- email varchar(50)
- phonenumber varchar(11)
- sex char(1)
- avatar varchar(100)
- password varchar(100)
- ... (19 columns)

## sys_user_post
- user_id bigint
- post_id bigint

## sys_user_role
- user_id bigint
- role_id bigint

## tb_course
- id bigint
- code varchar(32)
- subject varchar(32)
- name varchar(64)
- price int
- applicable_person varchar(32)
- info varchar(255)
- create_time timestamp
- update_time timestamp

## warehouse_zone
- zone_id int
- name varchar(255)
- description text
- factory_id int

## work
- work_id int
- title varchar(255)
- authors text
- instructor varchar(255)
- award_info text
- description text
