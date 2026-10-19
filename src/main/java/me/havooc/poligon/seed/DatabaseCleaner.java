package me.havooc.poligon.seed;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(0)
public class DatabaseCleaner implements CommandLineRunner {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        jdbcTemplate.execute("""
            DO $$
            DECLARE
                r RECORD;
            BEGIN
                EXECUTE 'TRUNCATE TABLE ' || string_agg(quote_ident(table_name), ', ') || ' RESTART IDENTITY CASCADE'
                FROM information_schema.tables
                WHERE table_schema = 'public';
            END $$;
            """);
        System.out.println("Database cleared before seeding.");
    }
}