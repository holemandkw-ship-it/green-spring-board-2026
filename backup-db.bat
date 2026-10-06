@echo off

"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe" -u root -p --databases green_board_2026 > db\green_board_2026.sql

git add db\green_board_2026.sql
git add backup-db.bat

git commit -m "Update database backup"

git push

pause