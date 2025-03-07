#!/bin/bash

# Путь к файлу с переменными
ENV_FILE="./.env"

# Перевод контекста выполнения скрипта в корневую папку проекта
pushd ~/FileSharingBot/ || exit

# Переключение на ветку main
git checkout main

# Обновление ветки main
git pull origin main

# Остановка и удаление старых контейнеров (микросервисов)
docker compose -f docker-compose.yml --env-file $ENV_FILE down --timeout=60 --remove-orphans

# Сборка и запуск новых контейнеров (из docker-compose.yml)
docker compose -f docker-compose.yml --env-file $ENV_FILE up --build --detach

# Возвращение контекста к исходному каталогу
popd || exit