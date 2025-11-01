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

# 排名类型
RANK_TYPES = ['RACE', 'QUALIFY', 'PRACTICE']

# 特殊名次（-1表示退赛，-2表示未参赛等）
SPECIAL_POSITIONS = [-1, -2]

# 分站ID范围 (9-16)
SUB_EVENT_IDS = list(range(9, 17))


def generate_username():
    """生成游戏风格的英文用户名"""
    if random.random() < 0.8:
        prefix = random.choice(USERNAME_PREFIXES)
        suffix = random.choice(USERNAME_SUFFIXES)
        if random.random() < 0.3:
            num = str(random.randint(1, 999))
            return prefix + num + suffix
        else:
            return prefix + suffix
    else:
        word = random.choice(USERNAME_PREFIXES + USERNAME_SUFFIXES)
        num = str(random.randint(1, 9999))
        return word + num


def generate_real_name():
    """生成真实姓名（中文或英文）"""
    if random.random() < 0.7:
        first_name = random.choice(FIRST_NAMES_CN)
        last_name = random.choice(LAST_NAMES_CN)
        return first_name + last_name
    else:
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


def get_max_user_id(conn):
    """获取当前最大的用户ID"""
    try:
        cursor = conn.cursor()
        cursor.execute("SELECT MAX(userId) FROM users")
        result = cursor.fetchone()
        return result[0] if result[0] is not None else 0
    except Exception as e:
        print(f"获取最大用户ID失败: {e}")
        return 0


def get_max_rank_id(conn):
    """获取当前最大的排名ID"""
    try:
        cursor = conn.cursor()
        cursor.execute("SELECT MAX(rankId) FROM ranks")
        result = cursor.fetchone()
        return result[0] if result[0] is not None else 0
    except Exception as e:
        print(f"获取最大排名ID失败: {e}")
        return 0


def generate_new_users(conn, count=30):
    """生成新的30个用户"""
    try:
        cursor = conn.cursor()

        # 获取当前最大用户ID
        max_user_id = get_max_user_id(conn)
        start_user_id = max_user_id + 1

        # 用于确保用户名唯一的集合
        cursor.execute("SELECT userName FROM users")
        existing_usernames = {row[0] for row in cursor.fetchall()}

        users_data = []

        for i in range(count):
            user_id = start_user_id + i

            # 生成唯一用户名
            username = generate_username()
            while username in existing_usernames:
                username = generate_username()
            existing_usernames.add(username)

            # 生成真实姓名和邮箱
            real_name = generate_real_name()
            email = f"{username.lower()}@{random.choice(EMAIL_DOMAINS)}"
            identify = f"ID{random.randint(10000000, 99999999)}"

            # 随机注册时间和最后登录时间
            register_days_ago = random.randint(1, 365)
            register_time = datetime.now() - timedelta(days=register_days_ago)
            last_connected_days_ago = random.randint(0, 30)
            last_connected = datetime.now() - timedelta(days=last_connected_days_ago)

            users_data.append((
                user_id,
                username,
                f"/avatars/avatar{random.randint(1, 100)}.jpg",
                email,
                FIXED_PASSWORD,
                real_name,
                identify,
                0,  # rating
                '1970-01-01 00:00:00',  # firstRace
                '1970-01-01 00:00:00',  # firstPodium
                '1970-01-01 00:00:00',  # firstPole
                '1970-01-01 00:00:00',  # firstWin
                0,  # totalRace
                0,  # totalPodium
                0,  # totalPole
                0,  # totalWin
                register_time.strftime('%Y-%m-%d %H:%M:%S'),
                last_connected.strftime('%Y-%m-%d %H:%M:%S'),
                'USER'
            ))

        # 插入新用户
        sql = """
        INSERT INTO users (userId, userName, avatar, email, password, realName, identify, 
                          rating, firstRace, firstPodium, firstPole, firstWin, 
                          totalRace, totalPodium, totalPole, totalWin, 
                          registerTime, lastConnected, type) 
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """
        cursor.executemany(sql, users_data)
        conn.commit()

        print(f"成功插入 {count} 个新用户，用户ID范围: {start_user_id} - {start_user_id + count - 1}")

        # 返回新生成的用户ID列表
        return list(range(start_user_id, start_user_id + count))

    except Exception as e:
        print(f"生成新用户失败: {e}")
        conn.rollback()
        return []


def assign_users_to_teams(conn, user_ids):
    """将用户分配到10个车队中，确保每个车队至少有2人，车队0不分配任何人"""
    try:
        cursor = conn.cursor()

        # 获取可用的车队ID（1-10）
        cursor.execute("SELECT teamId FROM teams WHERE teamId BETWEEN 1 AND 10 ORDER BY teamId")
        team_ids = [row[0] for row in cursor.fetchall()]

        if len(team_ids) < 10:
            print(f"警告：数据库中只有 {len(team_ids)} 个车队（需要10个）")
            return False

        # 确保每个车队至少有2人
        team_assignments = []
        user_index = 0

        # 第一轮分配：每个车队分配2人
        for team_id in team_ids:
            for _ in range(2):
                if user_index < len(user_ids):
                    team_assignments.append((user_ids[user_index], team_id))
                    user_index += 1

        # 第二轮分配：剩余用户随机分配到车队
        remaining_users = user_ids[user_index:]
        for user_id in remaining_users:
            team_id = random.choice(team_ids)
            team_assignments.append((user_id, team_id))

        # 插入车队成员关联数据
        sql = "INSERT INTO team_members (userId, teamId) VALUES (%s, %s)"
        cursor.executemany(sql, team_assignments)
        conn.commit()

        # 统计每个车队的分配人数
        team_counts = {}
        for _, team_id in team_assignments:
            team_counts[team_id] = team_counts.get(team_id, 0) + 1

        print("用户分配到车队完成：")
        for team_id in sorted(team_counts.keys()):
            print(f"  车队 {team_id}: {team_counts[team_id]} 人")

        return team_assignments

    except Exception as e:
        print(f"分配用户到车队失败: {e}")
        conn.rollback()
        return []


def generate_ranks_for_all_events(conn, user_team_assignments):
    """为所有分站赛（9-16）生成排名数据，每个分站的每种类型所有车手都有数据"""
    try:
        cursor = conn.cursor()

        # 获取当前最大排名ID
        max_rank_id = get_max_rank_id(conn)
        start_rank_id = max_rank_id + 1

        rank_data = []
        current_rank_id = start_rank_id

        # 为每个分站赛（9-16）生成数据
        for sub_event_id in SUB_EVENT_IDS:
            print(f"为分站 {sub_event_id} 生成排名数据...")

            # 为每个用户生成三种类型的排名数据
            for user_id, team_id in user_team_assignments:
                for rank_type in RANK_TYPES:
                    # 随机生成名次：80%概率正常名次，20%概率特殊名次
                    if random.random() < 0.8:
                        # 正常名次（1-30，允许重复）
                        pos = random.randint(1, 30)
                    else:
                        # 特殊名次（-1或-2）
                        pos = random.choice(SPECIAL_POSITIONS)

                    # 计算积分（特殊名次积分为0）
                    pts = POINTS_SYSTEM.get(pos, 0) if pos > 0 else 0

                    rank_data.append((
                        current_rank_id,
                        pos,
                        pts,
                        rank_type,
                        sub_event_id,
                        user_id,
                        team_id
                    ))

                    current_rank_id += 1

        # 插入排名数据
        sql = """
        INSERT INTO ranks (rankId, pos, pts, type, sub_eventId, userId, teamId) 
        VALUES (%s, %s, %s, %s, %s, %s, %s)
        """
        cursor.executemany(sql, rank_data)
        conn.commit()

        total_events = len(SUB_EVENT_IDS)
        total_users = len(user_team_assignments)
        total_rank_types = len(RANK_TYPES)
        total_records = total_events * total_users * total_rank_types

        print(f"成功为 {total_users} 个用户生成排名数据")
        print(f"  分站范围: {SUB_EVENT_IDS[0]} - {SUB_EVENT_IDS[-1]} (共{total_events}个分站)")
        print(f"  排名类型: {', '.join(RANK_TYPES)}")
        print(f"  总排名记录数: {total_records}")

        # 统计名次分布
        pos_counts = {}
        for _, pos, _, rank_type, sub_event_id, _, _ in rank_data:
            key = (sub_event_id, rank_type, pos)
            pos_counts[key] = pos_counts.get(key, 0) + 1

        print("\n名次分布统计 (按分站和类型):")
        for sub_event_id in SUB_EVENT_IDS:
            print(f"分站 {sub_event_id}:")
            for rank_type in RANK_TYPES:
                print(f"  {rank_type}类型:")
                type_positions = {pos: count for (s, t, pos), count in pos_counts.items()
                                  if s == sub_event_id and t == rank_type}
                # 只显示有记录的名次
                for pos in sorted(type_positions.keys()):
                    print(f"    名次 {pos}: {type_positions[pos]} 人")

        return True

    except Exception as e:
        print(f"生成排名数据失败: {e}")
        conn.rollback()
        return False


def main():
    """主函数"""
    print("开始为分站9-16生成30个新用户及其排名数据...")

    # 连接数据库
    conn = connect_db()
    if not conn:
        return

    try:
        # 1. 生成30个新用户
        print("步骤1: 生成30个新用户...")
        new_user_ids = generate_new_users(conn, 30)
        if not new_user_ids:
            print("生成新用户失败，程序终止")
            return

        # 2. 将用户分配到车队（确保每个车队至少2人，车队0不分配）
        print("\n步骤2: 将用户分配到车队...")
        user_team_assignments = assign_users_to_teams(conn, new_user_ids)
        if not user_team_assignments:
            print("分配用户到车队失败，程序终止")
            return

        # 3. 为这些用户生成所有分站（9-16）的排名数据
        print("\n步骤3: 生成排名数据（分站9-16）...")
        if not generate_ranks_for_all_events(conn, user_team_assignments):
            print("生成排名数据失败")
            return

        print("\n所有数据生成完成！")

        # 验证数据
        cursor = conn.cursor()

        # 验证用户数量
        cursor.execute("SELECT COUNT(*) FROM users")
        total_users = cursor.fetchone()[0]
        print(f"当前总用户数: {total_users}")

        # 验证车队成员分配
        cursor.execute("""
            SELECT t.teamId, t.teamName, COUNT(tm.userId) as member_count 
            FROM teams t 
            LEFT JOIN team_members tm ON t.teamId = tm.teamId 
            WHERE t.teamId BETWEEN 1 AND 10 
            GROUP BY t.teamId, t.teamName 
            ORDER BY t.teamId
        """)
        team_stats = cursor.fetchall()
        print("各车队成员统计:")
        for team_id, team_name, member_count in team_stats:
            print(f"  车队 {team_id} ({team_name}): {member_count} 人")

        # 验证排名数据
        for sub_event_id in SUB_EVENT_IDS:
            cursor.execute("SELECT COUNT(*) FROM ranks WHERE sub_eventId = %s", (sub_event_id,))
            rank_count = cursor.fetchone()[0]

            cursor.execute("""
                SELECT type, COUNT(DISTINCT userId) as user_count 
                FROM ranks 
                WHERE sub_eventId = %s 
                GROUP BY type
            """, (sub_event_id,))
            rank_types_stats = cursor.fetchall()

            print(f"\n分站{sub_event_id}的排名记录统计:")
            print(f"  总记录数: {rank_count}")
            for rank_type, user_count in rank_types_stats:
                print(f"  {rank_type}: {user_count} 个车手的数据")

        # 显示一些示例数据
        cursor.execute("""
            SELECT r.rankId, r.pos, r.pts, r.type, r.sub_eventId, u.userName, t.teamName 
            FROM ranks r 
            JOIN users u ON r.userId = u.userId 
            JOIN teams t ON r.teamId = t.teamId 
            WHERE r.sub_eventId BETWEEN 9 AND 16 
            ORDER BY r.sub_eventId, r.type, r.pos 
            LIMIT 20
        """)
        sample_ranks = cursor.fetchall()
        print("\n示例排名数据（分站9-16）:")
        for rank_id, pos, pts, rank_type, sub_event_id, username, teamname in sample_ranks:
            print(f"  分站{sub_event_id} {rank_type}: {username} ({teamname}) - 名次: {pos}, 积分: {pts}")

    except Exception as e:
        print(f"生成数据过程中出错: {e}")
    finally:
        conn.close()


if __name__ == "__main__":
    main()