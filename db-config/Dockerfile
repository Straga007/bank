FROM postgres:15.0

# Установка переменных окружения
ENV POSTGRES_DB=bankdb
ENV POSTGRES_USER=bankuser
ENV POSTGRES_PASSWORD=bankpass

# Копирование скрипта инициализации (если есть)
COPY init.sql /docker-entrypoint-initdb.d/ 2>/dev/null || true

# Открытие порта
EXPOSE 5432