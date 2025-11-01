import pymysql
import random
from datetime import datetime, timedelta

from config import DB_NAME, DB_HOST, DB_USER, DB_CHARSET, DB_PASSWORD, DB_PORT

# 数据库配置
DB_CONFIG = {
    'host': DB_HOST,
    'user': DB_USER,
    'password': DB_PASSWORD,
    'database': DB_NAME,
    'charset': DB_CHARSET,
    'port': int(DB_PORT)
}

# 固定密码哈希值
FIXED_PASSWORD = '$2a$10$oyHLAD7nI08sO9lCh0RdJe/OkdCNY/gWz3nrngKqWlwXa28b1u2Rm'

# 车队数据 (添加teamId=0，表示"未加入车队"，以及10个非热门车队)
TEAMS = [
    (0, 'none', 'none'),  # 添加teamId=0，表示"未加入车队"
    (1, 'Thunder Racing', '/logos/thunder.png'),
    (2, 'Azusa Racing', '/logos/ar.png'),
    (3, 'What Can I Say Team', '/logos/wciST.png'),
    (4, 'Iron Girls Team', '/logos/IGR.png'),
    (5, 'BRM MotoSport', '/logos/BRM_M.png'),
    (6, 'Aurora Team', '/logos/aurora.png'),
    (7, '843 Racing', '/logos/843R.png'),
    (8, 'Astuko Racing', '/logos/AsR.png'),
    (9, 'FunRace Team', '/logos/frt.png'),
    (10, 'SSR MotoSport', '/logos/ssrM.png')
]

# 管理员用户数据 (userId=1)
ADMIN_USER = {
    'userId': 1,
    'userName': 'YZBen',
    'avatar': '/avatars/admin.jpg',
    'email': 'lec16@foxmail.com',
    'password': FIXED_PASSWORD,
    'realName': 'YZB',
    'identify': 'ID12345678',
    'rating': 0,
    'firstRace': '1970-01-01 00:00:00',
    'firstPodium': '1970-01-01 00:00:00',
    'firstPole': '1970-01-01 00:00:00',
    'firstWin': '1970-01-01 00:00:00',
    'totalRace': 0,
    'totalPodium': 0,
    'totalPole': 0,
    'totalWin': 0,
    'registerTime': '2025-10-27 16:49:07',
    'lastConnected': '2025-10-31 12:00:20',
    'type': 'ADMIN'
}

# 游戏风格用户名组件
USERNAME_PREFIXES = [
    'Shadow', 'Dark', 'Light', 'Epic', 'Mighty', 'Swift', 'Steel', 'Iron',
    'Golden', 'Silver', 'Fire', 'Ice', 'Thunder', 'Storm', 'Night', 'Day',
    'Ghost', 'Phantom', 'Mystic', 'Cosmic', 'Galactic', 'Space', 'Time',
    'Dragon', 'Wolf', 'Eagle', 'Lion', 'Tiger', 'Falcon', 'Hawk',
    'Ninja', 'Samurai', 'Knight', 'Warrior', 'Mage', 'Wizard', 'Sage',
    'Alpha', 'Beta', 'Omega', 'Neo', 'Cyber', 'Digital', 'Virtual',
    'Red', 'Blue', 'Green', 'Black', 'White', 'Purple', 'Pink'
]

USERNAME_SUFFIXES = [
    'Runner', 'Racer', 'Driver', 'Master', 'Lord', 'King', 'Queen', 'Prince',
    'Slayer', 'Hunter', 'Seeker', 'Finder', 'Maker', 'Breaker', 'Killer',
    'Blade', 'Sword', 'Axe', 'Bow', 'Arrow', 'Shield', 'Hammer',
    'Storm', 'Wind', 'Flame', 'Frost', 'Earth', 'Water', 'Lightning',
    'Walker', 'Rider', 'Flyer', 'Jumper', 'Diver', 'Surfer', 'Skater',
    'X', 'Z', 'Pro', 'Elite', 'Prime', 'Ultra', 'Mega', 'Giga',
    '01', '07', '13', '21', '42', '99', '00', '23'
]

# 真实姓名（中文和英文）
FIRST_NAMES_CN = ['张', '王', '李', '赵', '刘', '陈', '杨', '黄', '周', '吴',
                  '徐', '孙', '胡', '朱', '高', '林', '何', '郭', '马', '罗']

LAST_NAMES_CN = ['伟', '芳', '娜', '秀英', '敏', '静', '丽', '强', '磊', '军',
                 '洋', '勇', '艳', '杰', '娟', '涛', '明', '超', '秀兰', '霞',
                 '平', '刚', '鹏', '建', '亮', '健', '博', '鑫', '婷婷', '雪']

FIRST_NAMES_EN = ['James', 'John', 'Robert', 'Michael', 'William', 'David', 'Richard', 'Joseph', 'Thomas', 'Charles',
                  'Mary', 'Patricia', 'Jennifer', 'Linda', 'Elizabeth', 'Barbara', 'Susan', 'Jessica', 'Sarah', 'Karen',
                  'Alex', 'Chris', 'Dylan', 'Jordan', 'Taylor', 'Morgan', 'Casey', 'Jamie', 'Riley', 'Avery']

LAST_NAMES_EN = ['Smith', 'Johnson', 'Williams', 'Brown', 'Jones', 'Garcia', 'Miller', 'Davis', 'Rodriguez', 'Martinez',
                 'Wilson', 'Anderson', 'Taylor', 'Thomas', 'Hernandez', 'Moore', 'Martin', 'Jackson', 'Thompson',
                 'White']

EMAIL_DOMAINS = ['gmail.com', 'yahoo.com', 'hotmail.com', 'outlook.com',
                 'qq.com', '163.com', '126.com', 'sina.com', 'sohu.com']

# 积分规则: 名次 -> 积分
POINTS_SYSTEM = {
    1: 25, 2: 18, 3: 15, 4: 12, 5: 10, 6: 8, 7: 6, 8: 5, 9: 4, 10: 3, 11: 2, 12: 1
}

# 分站ID范围 (根据第二张图，sub_eventId可选1~8)
SUB_EVENT_IDS = list(range(1, 9))


def generate_username():
    """生成游戏风格的英文用户名"""
    # 80%概率使用前缀+后缀组合，20%概率使用其他格式
    if random.random() < 0.8:
        prefix = random.choice(USERNAME_PREFIXES)
        suffix = random.choice(USERNAME_SUFFIXES)

        # 30%概率在中间添加数字
        if random.random() < 0.3:
            num = str(random.randint(1, 999))
            return prefix + num + suffix
        else:
            return prefix + suffix
    else:
        # 其他格式：单个单词+数字
        word = random.choice(USERNAME_PREFIXES + USERNAME_SUFFIXES)
        num = str(random.randint(1, 9999))
        return word + num


def generate_real_name():
    """生成真实姓名（中文或英文）"""
    if random.random() < 0.7:  # 70%概率中文名
        first_name = random.choice(FIRST_NAMES_CN)
        last_name = random.choice(LAST_NAMES_CN)
        return first_name + last_name
    else:  # 30%概率英文名
        first_name = random.choice(FIRST_NAMES_EN)
        last_name = random.choice(LAST_NAMES_EN)
        return f"{first_name} {last_name}"


def connect_db():
    """连接数据库"""
    try:
        conn = pymysql.connect(**DB_CONFIG)
        return conn
    except Exception as e:
        print(f"数据库连接失败: {e}")
        return None


def clear_referencing_tables(conn):
    """清除引用teams和users表的数据（但保留userId=1的用户）"""
    try:
        cursor = conn.cursor()

        # 禁用外键约束检查
        cursor.execute("SET FOREIGN_KEY_CHECKS = 0")

        # 获取所有引用teams表的表
        cursor.execute("""
            SELECT TABLE_NAME, COLUMN_NAME 
            FROM information_schema.KEY_COLUMN_USAGE 
            WHERE REFERENCED_TABLE_NAME = 'teams' AND TABLE_SCHEMA = %s
        """, (DB_NAME,))
        teams_references = cursor.fetchall()

        # 获取所有引用users表的表
        cursor.execute("""
            SELECT TABLE_NAME, COLUMN_NAME 
            FROM information_schema.KEY_COLUMN_USAGE 
            WHERE REFERENCED_TABLE_NAME = 'users' AND TABLE_SCHEMA = %s
        """, (DB_NAME,))
        users_references = cursor.fetchall()

        # 清除引用teams表的数据
        for table_info in teams_references:
            table_name = table_info[0]
            column_name = table_info[1]
            try:
                cursor.execute(f"DELETE FROM {table_name} WHERE {column_name} IS NOT NULL")
                print(f"已清除 {table_name} 表中引用了车队的数据")
            except Exception as e:
                print(f"清除 {table_name} 表数据时出错: {e}")

        # 清除引用users表的数据（但保留userId=1的数据）
        for table_info in users_references:
            table_name = table_info[0]
            column_name = table_info[1]
            try:
                # 保留userId=1的数据
                cursor.execute(f"DELETE FROM {table_name} WHERE {column_name} IS NOT NULL AND {column_name} != 1")
                print(f"已清除 {table_name} 表中引用了用户的数据（保留userId=1）")
            except Exception as e:
                print(f"清除 {table_name} 表数据时出错: {e}")

        # 重新启用外键约束检查
        cursor.execute("SET FOREIGN_KEY_CHECKS = 1")

        conn.commit()
        return True
    except Exception as e:
        print(f"清除引用表数据失败: {e}")
        conn.rollback()
        return False


def generate_teams(conn):
    """生成车队数据"""
    try:
        cursor = conn.cursor()

        # 先清除引用teams表的数据
        if not clear_referencing_tables(conn):
            return False

        # 清空现有车队数据
        cursor.execute("DELETE FROM teams")

        # 插入车队数据
        sql = "INSERT INTO teams (teamId, teamName, teamLogo) VALUES (%s, %s, %s)"
        cursor.executemany(sql, TEAMS)

        conn.commit()
        print(f"成功插入 {len(TEAMS)} 条车队数据")
        return True
    except Exception as e:
        print(f"生成车队数据失败: {e}")
        conn.rollback()
        return False


def generate_users(conn, count=50000):
    """生成用户数据（保留userId=1的管理员用户）"""
    try:
        cursor = conn.cursor()

        # 先清除引用users表的数据（但保留userId=1）
        if not clear_referencing_tables(conn):
            return False

        # 检查userId=1的用户是否存在
        cursor.execute("SELECT COUNT(*) FROM users WHERE userId = 1")
        user1_exists = cursor.fetchone()[0] > 0

        # 如果userId=1不存在，则插入管理员用户
        if not user1_exists:
            sql = """
            INSERT INTO users (userId, userName, avatar, email, password, realName, identify, 
                              rating, firstRace, firstPodium, firstPole, firstWin, 
                              totalRace, totalPodium, totalPole, totalWin, 
                              registerTime, lastConnected, type) 
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            cursor.execute(sql, (
                ADMIN_USER['userId'],
                ADMIN_USER['userName'],
                ADMIN_USER['avatar'],
                ADMIN_USER['email'],
                ADMIN_USER['password'],
                ADMIN_USER['realName'],
                ADMIN_USER['identify'],
                ADMIN_USER['rating'],
                ADMIN_USER['firstRace'],
                ADMIN_USER['firstPodium'],
                ADMIN_USER['firstPole'],
                ADMIN_USER['firstWin'],
                ADMIN_USER['totalRace'],
                ADMIN_USER['totalPodium'],
                ADMIN_USER['totalPole'],
                ADMIN_USER['totalWin'],
                ADMIN_USER['registerTime'],
                ADMIN_USER['lastConnected'],
                ADMIN_USER['type']
            ))
            print("已插入管理员用户 (userId=1)")
        else:
            # 更新userId=1的用户为管理员
            sql = """
            UPDATE users SET 
                userName = %s, avatar = %s, email = %s, password = %s, realName = %s, identify = %s,
                rating = %s, firstRace = %s, firstPodium = %s, firstPole = %s, firstWin = %s,
                totalRace = %s, totalPodium = %s, totalPole = %s, totalWin = %s,
                registerTime = %s, lastConnected = %s, type = %s
            WHERE userId = 1
            """
            cursor.execute(sql, (
                ADMIN_USER['userName'],
                ADMIN_USER['avatar'],
                ADMIN_USER['email'],
                ADMIN_USER['password'],
                ADMIN_USER['realName'],
                ADMIN_USER['identify'],
                ADMIN_USER['rating'],
                ADMIN_USER['firstRace'],
                ADMIN_USER['firstPodium'],
                ADMIN_USER['firstPole'],
                ADMIN_USER['firstWin'],
                ADMIN_USER['totalRace'],
                ADMIN_USER['totalPodium'],
                ADMIN_USER['totalPole'],
                ADMIN_USER['totalWin'],
                ADMIN_USER['registerTime'],
                ADMIN_USER['lastConnected'],
                ADMIN_USER['type']
            ))
            print("已更新管理员用户 (userId=1)")

        # 删除除userId=1以外的其他用户
        cursor.execute("DELETE FROM users WHERE userId > 1")

        # 批量插入其他用户数据（从userId=2开始）
        batch_size = 1000
        default_time = '1970-01-01 00:00:00'

        # 用于确保用户名唯一的集合
        used_usernames = {ADMIN_USER['userName']}  # 确保不与管理员的用户名重复

        # 计算需要生成的实际用户数量（因为已经有一个管理员用户）
        actual_count = count - 1

        for batch_start in range(0, actual_count, batch_size):
            batch_end = min(batch_start + batch_size, actual_count)
            values = []

            for i in range(batch_start, batch_end):
                user_id = i + 2  # 从userId=2开始

                # 生成唯一用户名
                username = generate_username()
                while username in used_usernames:
                    username = generate_username()
                used_usernames.add(username)

                # 生成真实姓名
                real_name = generate_real_name()

                # 生成邮箱
                email = f"{username.lower()}@{random.choice(EMAIL_DOMAINS)}"

                # 生成身份证号
                identify = f"ID{random.randint(10000000, 99999999)}"

                # 随机注册时间 (过去2年内)
                register_days_ago = random.randint(1, 730)
                register_time = datetime.now() - timedelta(days=register_days_ago)

                # 随机最后登录时间 (过去30天内)
                last_connected_days_ago = random.randint(0, 30)
                last_connected = datetime.now() - timedelta(days=last_connected_days_ago)

                values.append((
                    user_id,  # userId
                    username,  # userName
                    f"/avatars/avatar{random.randint(1, 100)}.jpg",  # avatar
                    email,  # email
                    FIXED_PASSWORD,  # password
                    real_name,  # realName
                    identify,  # identify
                    0,  # rating
                    default_time,  # firstRace
                    default_time,  # firstPodium
                    default_time,  # firstPole
                    default_time,  # firstWin
                    0,  # totalRace
                    0,  # totalPodium
                    0,  # totalPole
                    0,  # totalWin
                    register_time.strftime('%Y-%m-%d %H:%M:%S'),  # registerTime
                    last_connected.strftime('%Y-%m-%d %H:%M:%S'),  # lastConnected
                    'USER'  # type
                ))

            # 批量插入
            sql = """
            INSERT INTO users (userId, userName, avatar, email, password, realName, identify, 
                              rating, firstRace, firstPodium, firstPole, firstWin, 
                              totalRace, totalPodium, totalPole, totalWin, 
                              registerTime, lastConnected, type) 
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """
            cursor.executemany(sql, values)
            conn.commit()
            print(f"已生成用户 {batch_end + 1}/{count}")  # +1 因为包含管理员用户

        print(f"成功插入 {count} 条用户数据（包含管理员用户）")
        return True
    except Exception as e:
        print(f"生成用户数据失败: {e}")
        conn.rollback()
        return False


def generate_team_members(conn, user_count=50000, team_count=10):
    """生成车队成员关联数据"""
    try:
        cursor = conn.cursor()

        # 清空现有车队成员数据
        cursor.execute("DELETE FROM team_members")

        # 生成车队成员关联数据
        # 假设70%的用户加入车队，30%的用户没有车队（teamId=0）
        batch_size = 1000
        values = []

        # 从userId=2开始（userId=1是管理员，不加入车队）
        for user_id in range(2, user_count + 1):
            if random.random() < 0.7:  # 70%的用户加入车队
                team_id = random.randint(1, team_count)  # 随机分配车队（1-10）
            else:  # 30%的用户没有车队
                team_id = 0  # 使用teamId=0表示"未加入车队"

            values.append((user_id, team_id))

            # 批量插入
            if len(values) >= batch_size or user_id == user_count:
                sql = "INSERT INTO team_members (userId, teamId) VALUES (%s, %s)"
                cursor.executemany(sql, values)
                conn.commit()
                values = []
                print(f"已生成车队成员关联数据 {user_id}/{user_count}")

        print("成功生成车队成员关联数据")
        return True
    except Exception as e:
        print(f"生成车队成员关联数据失败: {e}")
        conn.rollback()
        return False


def generate_ranks(conn, count=100000, user_count=50000, sub_event_count=8):
    """生成排名数据（确保同一分站赛同一类型中名次不重复）"""
    try:
        cursor = conn.cursor()

        # 清空现有排名数据
        cursor.execute("DELETE FROM ranks")

        # 排名类型
        rank_types = ['RACE', 'QUALIFY', 'PRACTICE']

        # 计算每个分站赛每个类型应该有多少条记录
        # 总记录数 = 分站数 * 类型数 * 每个分站每个类型的平均记录数
        # 每个分站每个类型的平均记录数 = 总记录数 / (分站数 * 类型数)
        avg_records_per_event_type = count // (sub_event_count * len(rank_types))
        remainder = count % (sub_event_count * len(rank_types))

        print(f"每个分站每个类型平均记录数: {avg_records_per_event_type}, 余数: {remainder}")

        # 批量插入排名数据
        batch_size = 1000
        values = []
        rank_id = 1

        # 为每个分站赛和每个类型生成排名数据
        for sub_event_id in range(1, sub_event_count + 1):
            for rank_type in rank_types:
                # 计算这个分站这个类型应该有多少条记录
                records_for_this_type = avg_records_per_event_type
                if remainder > 0:
                    records_for_this_type += 1
                    remainder -= 1

                # 如果记录数为0，跳过
                if records_for_this_type <= 0:
                    continue

                # 确保记录数不超过用户总数
                actual_records = min(records_for_this_type, user_count)

                # 随机选择用户（确保不重复）
                available_users = list(range(1, user_count + 1))
                random.shuffle(available_users)
                selected_users = available_users[:actual_records]

                # 为这些用户生成唯一的名次
                positions = list(range(1, actual_records + 1))
                random.shuffle(positions)  # 随机打乱名次

                # 为每个用户生成排名记录
                for i, user_id in enumerate(selected_users):
                    pos = positions[i]
                    pts = POINTS_SYSTEM.get(pos, 0)  # 根据名次获取积分

                    # 获取用户的teamId（从team_members表）
                    cursor.execute("SELECT teamId FROM team_members WHERE userId = %s", (user_id,))
                    team_result = cursor.fetchone()
                    team_id = team_result[0] if team_result else 0

                    values.append((
                        rank_id,  # rankId
                        pos,  # pos
                        pts,  # pts
                        rank_type,  # type
                        sub_event_id,  # sub_eventId
                        user_id,  # userId
                        team_id  # teamId
                    ))

                    rank_id += 1

                    # 批量插入
                    if len(values) >= batch_size:
                        sql = """
                        INSERT INTO ranks (rankId, pos, pts, type, sub_eventId, userId, teamId) 
                        VALUES (%s, %s, %s, %s, %s, %s, %s)
                        """
                        cursor.executemany(sql, values)
                        conn.commit()
                        values = []
                        print(f"已生成排名 {rank_id - 1}/{count}")

        # 插入剩余的数据
        if values:
            sql = """
            INSERT INTO ranks (rankId, pos, pts, type, sub_eventId, userId, teamId) 
            VALUES (%s, %s, %s, %s, %s, %s, %s)
            """
            cursor.executemany(sql, values)
            conn.commit()
            print(f"已生成排名 {rank_id - 1}/{count}")

        print(f"成功插入 {rank_id - 1} 条排名数据")
        return True
    except Exception as e:
        print(f"生成排名数据失败: {e}")
        conn.rollback()
        return False


def main():
    """主函数"""
    print("开始生成测试数据...")

    # 连接数据库
    conn = connect_db()
    if not conn:
        return

    try:
        # 生成车队数据
        print("生成车队数据...")
        if not generate_teams(conn):
            return

        # 生成用户数据
        print("生成用户数据...")
        if not generate_users(conn, 50000):
            return

        # 生成车队成员关联数据
        print("生成车队成员关联数据...")
        if not generate_team_members(conn, 50000, 10):
            return

        # 生成排名数据
        print("生成排名数据...")
        if not generate_ranks(conn, 100000, 50000, 8):  # sub_event_count改为8
            return

        print("所有测试数据生成完成！")

        # 验证数据
        cursor = conn.cursor()
        cursor.execute("SELECT COUNT(*) FROM teams")
        team_count = cursor.fetchone()[0]

        cursor.execute("SELECT COUNT(*) FROM users")
        user_count = cursor.fetchone()[0]

        cursor.execute("SELECT COUNT(*) FROM team_members")
        team_member_count = cursor.fetchone()[0]

        cursor.execute("SELECT COUNT(*) FROM ranks")
        rank_count = cursor.fetchone()[0]

        # 检查管理员用户
        cursor.execute("SELECT userId, userName, type FROM users WHERE userId = 1")
        admin_user = cursor.fetchone()

        # 检查teamId=0的车队
        cursor.execute("SELECT teamId, teamName FROM teams WHERE teamId = 0")
        team_zero = cursor.fetchone()

        # 验证排名数据的唯一性
        cursor.execute("""
            SELECT sub_eventId, type, pos, COUNT(*) as count 
            FROM ranks 
            GROUP BY sub_eventId, type, pos 
            HAVING COUNT(*) > 1
        """)
        duplicate_ranks = cursor.fetchall()

        if duplicate_ranks:
            print("警告：发现重复的排名数据：")
            for dup in duplicate_ranks:
                print(f"  分站 {dup[0]}, 类型 {dup[1]}, 名次 {dup[2]}: {dup[3]} 条记录")
        else:
            print("排名数据验证：同一分站同一类型中名次唯一")

        print(f"数据统计:")
        print(f"  车队数量: {team_count}")
        print(f"  用户数量: {user_count}")
        print(f"  车队成员关联: {team_member_count}")
        print(f"  排名记录: {rank_count}")

        if admin_user:
            print(f"  管理员用户: userId={admin_user[0]}, userName={admin_user[1]}, type={admin_user[2]}")

        if team_zero:
            print(f"  特殊车队: teamId={team_zero[0]}, teamName={team_zero[1]}")

        # 显示一些示例用户名
        cursor.execute("SELECT userId, userName, realName, type FROM users LIMIT 10")
        sample_users = cursor.fetchall()
        print("\n示例用户名:")
        for user_id, username, realname, user_type in sample_users:
            admin_marker = " (ADMIN)" if user_type == 'ADMIN' else ""
            print(f"  {username} -> {realname}{admin_marker}")

    except Exception as e:
        print(f"生成数据过程中出错: {e}")
    finally:
        conn.close()


if __name__ == "__main__":
    main()