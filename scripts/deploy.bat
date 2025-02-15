:: Путь к файлу с переменными
set ENV_FILE="./.env"

:: Перевод контекста выполнения скрипта в корневую папку проекта
pushd "D:\DZHITS\Programming\_main\_Projects\FileSharingBot"

:: Переключение на ветку main
call git checkout main

:: Обновление ветки main
call git pull origin main

:: Остановка и удаление старых контейнеров (микросервисов)
call docker compose -f docker-compose.yml --env-file %ENV_FILE% down --timeout=60 --remove-orphans

:: Сборка и запуск новых контейнеров (из docker-compose.yml)
call docker compose -f docker-compose.yml --env-file %ENV_FILE% up --build --detach

:: Возвращение контекста к исходному каталогу
popd