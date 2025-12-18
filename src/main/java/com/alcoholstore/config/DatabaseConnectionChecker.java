package com.alcoholstore.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@Component
@Order(1) // Запускаем первым
public class DatabaseConnectionChecker implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionChecker.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        logger.info("🔍 ПРОВЕРКА ПОДКЛЮЧЕНИЯ К БАЗЕ ДАННЫХ...");

        try {
            // 1. Проверяем подключение к DataSource
            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();

            logger.info("✅ ПОДКЛЮЧЕНИЕ УСПЕШНО!");
            logger.info("📊 Информация о БД:");
            logger.info("   - URL: {}", metaData.getURL());
            logger.info("   - User: {}", metaData.getUserName());
            logger.info("   - Database: {}", metaData.getDatabaseProductName());
            logger.info("   - Version: {}", metaData.getDatabaseProductVersion());
            logger.info("   - Driver: {}", metaData.getDriverName());
            logger.info("   - Driver Version: {}", metaData.getDriverVersion());

            // 2. Проверяем доступность таблицы users
            try {
                String sql = "SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'users'";
                Integer tableCount = jdbcTemplate.queryForObject(sql, Integer.class);

                if (tableCount != null && tableCount > 0) {
                    logger.info("✅ Таблица 'users' существует в базе");

                    // Проверяем количество записей
                    Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
                    logger.info("👥 Количество пользователей в таблице: {}", userCount);

                    // Выводим список пользователей, если они есть
                    if (userCount > 0) {
                        jdbcTemplate.query("SELECT id, username, email, role FROM users",
                                (rs, rowNum) -> {
                                    logger.info("   - ID: {}, Имя: {}, Email: {}, Роль: {}",
                                            rs.getLong("id"),
                                            rs.getString("username"),
                                            rs.getString("email"),
                                            rs.getString("role"));
                                    return null;
                                });
                    }
                } else {
                    logger.warn("⚠️ Таблица 'users' не найдена в базе");
                }
            } catch (Exception e) {
                logger.warn("⚠️ Не удалось проверить таблицу 'users': {}", e.getMessage());
            }

            connection.close();

        } catch (Exception e) {
            logger.error("❌ ОШИБКА ПОДКЛЮЧЕНИЯ К БАЗЕ ДАННЫХ!");
            logger.error("   Причина: {}", e.getMessage());
            logger.error("   Stack trace:", e);

            // Предлагаем возможные решения
            logger.error("   Возможные решения:");
            logger.error("   1. Проверьте, запущен ли PostgreSQL: sudo systemctl status postgresql");
            logger.error("   2. Проверьте, существует ли база alcoholstore_db: psql -U postgres -l");
            logger.error("   3. Создайте базу: CREATE DATABASE alcoholstore_db;");
            logger.error("   4. Проверьте пароль в application.properties");
            logger.error("   5. Проверьте доступность порта 5432: netstat -tulpn | grep 5432");

            throw new RuntimeException("Не удалось подключиться к базе данных", e);
        }

        logger.info("=========================================");
    }
}