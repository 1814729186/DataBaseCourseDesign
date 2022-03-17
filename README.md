# DataBaseCourseDesign

### 超市收银系统

B/S模式超市收银系统

#### 一、需求调研分析

##### 1.基本元素

​	收银员操作端、顾客

​	顾客会员卡、商品list

​	交易信息

##### 2.功能需求与描述

###### （1）收银

​	收银员输入顾客的**会员卡卡号**（若有卡）、所购**商品的货号**等信息，系统根据这些信息获取相应的价格信息并计算应收取的**总金额**。完成收银后，记录**交易信息**，修改有关种类**商品的剩余**量以及该**持卡顾客的消费情况**。

###### （2）发卡

​	顾客可交纳一定的费用（如50元）办理一张会员卡，以后在该商场购物可凭卡享受**9折优惠**。如果一个未持卡顾客一次购物满1000元，可为其免费发放一张会员卡，每张卡的**优惠期为一年**，**一年内消费达到一定金额的可继续享受下一年的优惠**。

###### （3）款项盘存

​	收银员下班或交接班前对本收银台中本班次收取的款额进行盘存，明确责任。

**后台功能**：

###### （4）商品信息录入、修改、删除、查询

###### （5）收银员身份、口令管理

**其它要求**：

###### （6）包含事务（commit，rollback）存储过程/触发器，视图、函数。

###### （7）体现SQL和编程语言的结合

#### 二、总体设计

​	实现B/S、C/S系统形式与数据库系统进行基本连接。

1.控制台模式

2.浏览器界面设计与制作

3.客户端形式

#### 三、数据库设计

##### 1.数据库基本参数

##### 2.数据库表项与E-R图

​	① 收银员员工信息表

​		-- 员工编号 int 主码

​		-- 员工姓名 char(5)

​		-- 员工登录口令信息（加密存储） char(20)

​		-- 员工账号上一次登录情况 date_time

​		-- 员工类型：收银员/仓库管理员/检货员

​	②盘存信息表

​		--盘存单号 主码

​		--责任员工编号 外码

​		--收银总款项

​		--上班时间

​		--收班时间

​	③会员卡信息表

​		-- 会员卡卡号 （主码）

​		-- 持卡人姓名

​		-- 优惠期结束时间

​		-- 本年度消费总额

​	④商品信息表

​		-- 商品货号 （主码）

​		-- 商品名称

​		-- 商品单价

​		-- 计量单位 

​		-- 商品剩余数量

​	⑤收银信息表

​		-- 收银单编号（主码）

​		-- 收银员编号（外码）

​		-- 会员卡编号 （外码）

​		-- 出单时间

​		-- 总金额

​		-- 折后总金额

​	⑥销售商品信息表

​		-- 商品销售id号（主码）

​		-- 收银单编号（外码）

​		-- 商品编号（外码）

​		-- 商品数量

​	⑦会员卡配发表

​		-- 配发id

​		-- 处理员工id

​		-- 会员号

​		-- 配发日期

​		-- 配发方式

（触发器已设置，插入条目时自动分配id号，vip_id号，并将条目插入到会员卡表格中，之后的会员卡vip_name需要额外设置）

​	⑧员工申请表

​		-- 申请id号

​		-- 申请姓名

​		-- 密码

​		-- 申请类型

​		-- 申请时间

​	（触发器已添加，记录申请时间，填入申请编号）

​	⑨进货单

​		-- 进货单号

​		-- 进货商品编号，（当仓库中不存在相关商品时，需要指定商品名和计量单位）

​		-- 进货数量

​		-- 责任员工

​		-- 进货时间



**E-R图**

![E-R图](G:/PersonalFiles/2021_Grade3_LaterTerm/数据库实验/收银系统ER图.png)

#### 四、详细设计与实现

​	用户设计，每种员工使用一个用户，每个环节使用一个用户

店长用户：root：拥有全部权限



登录环节：LoginUser：拥有员工表stuff_info的查询和更新权限 密码：loginuser

申请环节：ApplyUser：拥有申请表application_form的插入权限 密码：applyuser



收银员：CashierUser：拥有表goods_info、goods_sold_info、sale_info、save_disk_info、vip_card_info、vipcard_distribute的查询和update权限

仓库管理员：StoreUser：拥有goods_info表的查询和更新权限

stuff_type:

​	0 -- 店长用户

​	1 -- 收银员

​	2 -- 仓库管理员

```sql
//用户添加和权限授予
create user 'sqluser' @ 'host' identfied by 'password';
//其中aqluser为用户名，host为指定主机（localhost表示本地机，？表示匹配所有）
//权限授予
grant *** on ** to sqluser$'%' identified by 'password' {with grant option};
//实例
CREATE USER 'LoginUser'@'%' IDENTIFIED BY 'loginuser';
CREATE USER 'ApplyUser'@'%' IDENTIFIED BY 'applyuser';
grant select,update on table staff_info to LoginUser;
grant update,insert on table application_form to ApplyUser;
create user 'CashierUser'@'%' identified by 'cashieruser';
create user 'StoreUser'@'%' identified by 'storeuser';
//权限授予
grant update,select on table goods_info to CashierUser;
grant update,select on table goods_sold_info to CashierUser;
grant update,select on table sale_info to CashierUser;
grant update,select on save_disk_info to CashierUser;
grant update,select on vip_card_info to CashierUser;

grant update,select on table goods_info to StoreUser;
-- 删除上述CashierUser，StoreUser用户

```

使用角色模式重构上述操作模式

```mysql
-- 需要先基于root用户grant的权限
-- 查看权限
select host,user,grant_priv,super_priv from mysql.user;
-- 查看已授予用户或者角色的权限
show grants for test；
-- 授予函数执行权限
grant execute on *.* to test;

-- 将新创建的用户的上述权限改为Y才能登录数据库
update mysql.user set Grant_priv='Y',Super_priv='Y' where user = 'test' and host = '%';
-- 修改后需要重启mysql服务
net stop mysql
net start mysql


-- 示例
create role read_SC;
grant select on lab2.SC to read_SC;
-- 给角色分配成员
grant xxx to zhengfan@'%';
-- 创建角色
-- 店长角色
create role Manager;

-- 收银员角色
create role CashierRole; 

-- 仓库管理员角色
create role StoreRole;

-- 删除角色
drop role xxx;

-- 查询数据库存在的角色
select [name] from sysusers where issqlrole=1;

```

角色授予相关：这是因为在向用户帐户授予角色时，当用户帐户连接到数据库服务器时，它不会自动使角色变为活动状态。

如果调用`CURRENT_ROLE()`函数：能够查看当前活跃的角色。

要在每次用户帐户连接到数据库服务器时指定哪些角色应该处于活动状态，请使用`SET DEFAULT ROLE`语句。

```mysql
-- 设置默认角色，使用该账号登录该数据库时，将默认激活该角色
set default role xxx to xxx;
-- 设置已有的所有角色为默认激活角色
SET DEFAULT ROLE ALL TO crm_read;
-- 以下语句将活动角色设置为NONE，表示没有活动角色。
SET ROLE NONE;
-- 要将活动角色设置为所有授予的角色，请使用：
SET ROLE ALL;
-- 要将活动角色设置为由SET DEFAULT ROLE语句设置的默认角色，请使用：
SET ROLE DEFAULT;
-- 要设置活动的命名角色，请使用：
SET ROLE granted_role_1, granted_role_2, ...


```

数据库角色设定

```mysql
-- 店长角色
create role ManagerRole;
grant select,update,insert,delete on * to ManagerRole;
grant execute on *.* to ManagerRole;
-- 收银员角色
create role CashierRole; 
-- 收银员执行函数权限
grant execute on online_sale_db.* to CashierRole;
-- 收银员goods_info查询权限
grant select on goods_info to CashierRole;
grant select on vip_card_info to CashierRole;
grant insert on goods_sold_info to CashierRole;
grant update on vip_card_info to CashierRole;
grant select on vip_card_info to CashierRole;
grant select on vipcard_distribute to CashierRole;
grant select on staff_info to CashierRole;
grant select on save_disk_info to CashierRole;
grant select on sale_info to CashierRole;

-- 仓库管理员角色
create role StoreRole;
grant execute on online_sale_db.* to StoreRole;
grant select on purchase_order to StoreRole;
grant select on staff_info to StoreRole;
grant select on goods_info to StoreRole;
grant select on save_disk_info to StoreRole;
grant insert on purchase_order to StoreRole;
grant update on goods_info to StoreRole;
grant delete on purchase_order to StoreRole;
grant update on purchase_order to StoreRole;
```



时间触发器

```mysql
SHOW VARIABLES LIKE 'event_scheduler'; -- 查看是否开启定时器
SET GLOBAL event_scheduler = 1; -- 开启定时器
-- 开启event_scheduler SQL指令
SET GLOBAL event_scheduler = ON;  
SET @@global.event_scheduler = ON;  
SET GLOBAL event_scheduler = 1;  
SET @@global.event_scheduler = 1; 
-- 定义储存过程
DELIMITER |  
DROP PROCEDURE IF EXISTS update_remind_status |  
CREATE PROCEDURE update_remind_status()  
BEGIN  
    IF exists (select id from remind where status='1' and SYSDATE()>=remind_time) THEN  
            update remind_receive set `status`='1'  
            where remind_id in (SELECT id from remind where `status`='1' and SYSDATE()>=remind_time) and `status`='3';  
    END IF;  
END   
 |  
DELIMITER; 
-- 自己的储存过程：该版本未通过
DELIMITER |  
DROP PROCEDURE IF EXISTS update_vip_card |  
CREATE PROCEDURE update_vip_card()  
BEGIN  
	declare curVipId int;
	declare amountYearly double;
	if exists (select * from vip_card_info where end_time <= now()) THEN
		set curVipId = (select vip_id from vip_card_info where end_time <= now() limit 1);
		set amountYearly = (select amount_yearly from vip_card_info where vip_id = curVipId);
		if(amountYearly >= 1000) then
			update vip_card_info set amount_yearly=0 and end_time=date_add(now(),interval 1 year) where vip_id=curVipId;
		else
			delete from vip_card_info where vip_id=curVipId;
		end if;
	end if;
END   
|  
DELIMITER ; 
-- 以下储存过程测试通过
DELIMITER |
DROP PROCEDURE IF EXISTS update_vip_card |  
CREATE PROCEDURE update_vip_card()  
BEGIN  
	declare curVipId int;
	declare amountYearly double;
	set curVipId = (select vip_id from vip_card_info where end_time < now() limit 1);
	set amountYearly = (select amount_yearly from vip_card_info where vip_id = curVipId);
	if(amountYearly>=1000) then
		update vip_card_info set end_time = date_add(now(),interval 1 year),amount_yearly=0.0 where vip_id=curVipId;
	else
		delete from vip_card_info where vip_id=curVipId;
	end if;
END
|

update vip_card_info set end_time = now(),amount_yearly = amountYearly+20 where vip_id=curVipId;
-- 创建定时器，每间隔一秒调用一次存储过程。
DELIMITER //  
drop event if exists event_vipcard_update//
CREATE EVENT  event_vipcard_update
ON SCHEDULE EVERY 1 second  do  
begin  
call update_vip_card();  
end //  
DELIMITER ;
-- 另一种创建方法

SET GLOBAL event_scheduler = 1;
CREATE EVENT IF NOT EXISTS event_vipcard_update
ON SCHEDULE EVERY 1 SECOND
ON COMPLETION PRESERVE  
DO CALL update_vip_card();
-- 启动定时器
ALTER EVENT event_vipcard_update ON    
COMPLETION PRESERVE ENABLE; 
```

函数添加

```mysql
-- 打开变量权限
SET @@global.log_bin_trust_function_creators='ON';
-- 查看权限
SHOW VARIABLES LIKE 'log_bin_trust_function_creators';
-- 函数添加：staff_info表修改函数
-- 函数功能：检查修改后的账号名是否与已有账号重名，若存在，则不能修改，返回false；若能修改，完成修改后返回true
DELIMITER //
create function updateInfo_staff_info(staffId int,staffName VARCHAR(45),staffType int) returns boolean as
begin
	if (exists(select * from staff_info where staff_id != staffId and staffName=staff_name)) then
    	return false;    
    else
        update staff_info set staff_name=StaffName,staff_type=staffType where staff_id=staffId;
        return true;
    end if
end //
DELIMITER ;
-- 添加成功
-- ------
-- 函数添加：接收申请信息，删除:application_form删除表项信息，根据参数决定是否插入到staff_info表
create function apply_application_form(applyId int,isApplied boolean) returns boolean
begin
	declare name VARCHAR(20);
	declare pass VARCHAR(20);
	declare type int;
	if(isApplied)then
		set name = (select apply_name from application_form where apply_id=applyId);
		set pass = (select apply_password from application_form where apply_id=applyId);
		set type = (select apply_type from application_form where apply_id=applyId);
		-- 查询是否存在相同账号信息，存在则拒绝插入
		if (exists(select * from staff_info where name=staff_name)) then
			return false;
		end if;
		-- 插入到员工表
		insert into staff_info values(1,name,pass,NULL,type,0);
	end if;
	-- 删除application_form中的表项
	delete from application_form where apply_id = applyId;
	return true;
end;

-- 获得用户名
CREATE DEFINER=`root`@`%` FUNCTION `getUser`() RETURNS char(20) CHARSET utf8mb4
BEGIN
	return left(user(),position('@' in user())-1);
END

-- 修改当前用户的登录状态为在线，修改成功返回1，失败返回0
CREATE DEFINER=`root`@`%` FUNCTION `staff_login_in`() RETURNS tinyint(1)
BEGIN
	if(exists(select * from staff_info where staff_name=getUser())) then
		update staff_info set staff_state=1 where staff_name=getUser();
		return true;
	else return false;
    end if;
END
-- 修改当前用户登录状态为离线
CREATE DEFINER=`root`@`%` FUNCTION `staff_login_off`() RETURNS tinyint(1)
BEGIN
	if(exists(select * from staff_info where staff_name=getUser())) then
		update staff_info set staff_state=0 where staff_name=getUser();
		return true;
	else return false;
    end if;
END

-- 检测vip信息是否存在
CREATE FUNCTION `check_vip_card` (vip_id int)
RETURNS boolean
BEGIN
	if(exists (select * from vip_card_info where vip_id = vip_id)) then
		return true;
	else 
		return false;
    end if;
END

-- 插入收银单，返回收银单号
CREATE DEFINER=`root`@`%` FUNCTION `insert_sale_order`(vip_id int,amount double,amount_after_discount double) RETURNS int
BEGIN
	declare sale_id int;
    declare staff_id int;
    -- 访问数据库获得员工id号
    set staff_id = (select staff_id from staff_info where staff_name = getUser());
    -- 生成售货单号
    set sale_id = ifnull((select sale_id from sale_info order by sale_id desc limit 1),0) + 1;
    -- 插入信息
    if(vip_id=-1) then
		insert into sale_info values(sale_id,staff_id,null,amount,amount);
	else 
		insert into sale_info values(sale_id,staff_id,vip_id,amount,amount_after_discount);
    end if;
    -- 返回订单编号
    return sale_id;
END

--  
CREATE DEFINER=`root`@`%` FUNCTION `get_sale_amount_month`(aimtime datetime) RETURNS double
BEGIN
	declare amountofmonth double;
    set amountofmonth = (select ifnull(sum(amount_after_discount),0.0) from sale_info where month(sale_info.time)=month(aimtime) and year(sale_info.time)=year(aimtime));
    return amountofmonth;
END
--
CREATE DEFINER=`root`@`%` FUNCTION `get_sale_amount_day`(aimTime datetime) RETURNS double
BEGIN
	-- 返回相关信息
    declare amountofday double;
    set amountofday = ((select ifnull(sum(amount_after_discount),0.0) from sale_info where date(sale_info.time) = date(aimTime)));
    return amountofday;
END
-- 
CREATE FUNCTION `get_sale_amount_year` (aimtime datetime)
RETURNS DOUBLE
BEGIN
	declare amountofyear DOUBLE;
    set amountofyear = (select sum(amount_after_discount) from sale_info where year(sale_info.time) = year(aimtime));
    return amountofyear;
END
```





#### 五、测试环节



```mysql
-- This function has none of DETERMINISTIC, NO SQL, or READS SQL DATA in its declaration and binary logging is enabled (you *might* want to use the less safe log_bin_trust_function_creators variable)
-- 解决方法 在my.cnf配置文件里面设置 log-bin-trust-function-creators=1
-- 解决方法 2 重启会失效
SET GLOBAL log_bin_trust_function_creators = 1;
```

